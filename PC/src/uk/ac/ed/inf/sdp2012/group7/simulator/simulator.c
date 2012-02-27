#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <getopt.h>
#include <netdb.h>
#include <pthread.h>

#include "options.h"
#include "macros.h"
#include "opcodes.h"
#include "types.h"
#include "net.h"
#include "robot_simm.h"
#include "world_state.h"

void help() {
	printf(
		"Usage: simulator [OPTIONS]\n\n"
		" General options:\n"
		"  -h, --help              Display this help\n\n"
		" Control options:\n"
		"  -b, --blue PORT         Blue robot control port (default: "DEFAULT_BLUE_PORT")\n"
		"  -y, --yellow PORT       Yellow robot control port (default: "DEFAULT_YELLOW_PORT")\n"
		" Specifying -1 as one of the ports will attach dummy to that robot.\n\n"
		" Vision options:\n"
		"  -w, --world-state PORT  World state port (default: "DEFAULT_WORLD_STATE_PORT")\n"
	);

}

int main(int argc, char **argv) {
	char blue_port[6] = DEFAULT_BLUE_PORT;
	char yellow_port[6] = DEFAULT_YELLOW_PORT;
	char world_state_port[6] = DEFAULT_WORLD_STATE_PORT;

	/* Options */
	static struct option long_options[] = {
		{"help", 0, 0, 'h'},
		{"blue", 1, 0, 'b'},
		{"yellow", 1, 0, 'y'},
		{"world-state", 1, 0, 'w'},
		{NULL, 0, NULL, 0}
	};
	int option_index = 0;
	int option;
	while ((option = getopt_long(argc, argv, "hb:y:w:", long_options, &option_index)) != -1) {
		switch (option) {
			case 'h':
				help();
				return 0;
			case '?':
				help();
	  			return 0;

			case 'b':
				strncpy(blue_port, optarg, 5);
				blue_port[5] = '\0';
				break;
			case 'y':
				strncpy(yellow_port, optarg, 5);
				yellow_port[5] = '\0';
				break;
			case 'v':
				strncpy(world_state_port, optarg, 5);
				world_state_port[5] = '\0';
				break;

		}
	}

	char blue_addr[INET6_ADDRSTRLEN];
	char yellow_addr[INET6_ADDRSTRLEN];
	char world_state_addr[INET6_ADDRSTRLEN];
	
	socket_t blue_socket = -1;
	socket_t yellow_socket = -1;
	socket_t vision_socket = -1;

	struct robot_status blue_status;
	struct robot_status yellow_status;
	struct world_state world_state;
	struct ball ball_state;
	world_state.blue = &blue_status;
	world_state.yellow = &yellow_status;
	world_state.ball = &ball_state;
	memset(&blue_status, 0, sizeof blue_status);
	memset(&yellow_status, 0, sizeof yellow_status);
	memset(&ball_state, 0, sizeof ball_state);
	blue_status.x = BLUE_START_X;
	blue_status.y = BLUE_START_Y;
	yellow_status.x = YELLOW_START_X;
	yellow_status.y = YELLOW_START_Y;
	ball_state.x = BALL_START_X;
	ball_state.y = BALL_START_Y;


	printf("Blue port: %s\nYellow port: %s\nWorld state port: %s\n", port_dummy(blue_port), port_dummy(yellow_port), world_state_port);

	/* Set up servers */
	printf("Starting servers...\n");

	struct robot_thread_args blue_thread_args;
	if (set_up_robot_conn(blue_port, blue_addr, &blue_thread_args) < 0) {
		printf("Failed to open blue socket!\n");
		return 1;
	}
	pthread_t blue_thread;
	blue_thread_args.robot = ROBOT_BLUE;
	blue_thread_args.status = &blue_status;
	pthread_create(&blue_thread, NULL, robot_thread, &blue_thread_args);

	struct robot_thread_args yellow_thread_args;
	if (set_up_robot_conn(yellow_port, yellow_addr, &yellow_thread_args) < 0) {
		printf("Failed to open yellow socket!\n");
		return 1;
	}
	pthread_t yellow_thread;
	yellow_thread_args.robot = ROBOT_YELLOW;
	yellow_thread_args.status = &yellow_status;
	pthread_create(&yellow_thread, NULL, robot_thread, &yellow_thread_args);

	struct ws_thread_args ws_thread_args;
	if (set_up_ws_conn(world_state_port, world_state_addr, &ws_thread_args) < 0) {
		printf("Failed to open world state socket!\n");
		return 1;
	}
	pthread_t world_state_thread;
	ws_thread_args.ws = &world_state;
	pthread_create(&world_state_thread, NULL, ws_thread, &ws_thread_args);
	

	pthread_join(blue_thread, NULL);
	pthread_join(yellow_thread, NULL);
	pthread_join(world_state_thread, NULL);	


	return 0;
}
