#include "robot_simm.h"

#include <errno.h>
#include <math.h>
#include <sys/time.h>
#include <netdb.h>
#include <unistd.h>
#include <stdio.h>

#include "types.h"
#include "opcodes.h"
#include "macros.h"
#include "options.h"


void decode_command(opcode_t op, struct command * cmd) {
	cmd->instr = op & INSTR_MASK;
	cmd->arg = op ARG_SHIFT;
}

void robot_thread(void *args) {
	struct robot_thread_args *a = args;
	RT_SAY("started\n");

	if (a->is_dummy) {
		RT_SAY("I'm a dummy LOL\n");
	} else {
		RT_SAY("waiting for connection...\n");
		listen(a->socket, 1);
		struct sockaddr_storage client_addr;
		socklen_t sin_size = sizeof client_addr;
		socket_t socket = accept(a->socket, (struct sockaddr *) &client_addr, &sin_size);
		RT_SAY("connected.\n");
		close(a->socket); // no need to keep control socket up
		opcode_t buf;
		int recv_size;
		struct command cmd;
		cmd.instr = DO_NOTHING;
		pthread_t action_thread;
		struct robot_action_thread_args action_args;
		action_args.cmd = &cmd;
		action_args.rs = a->status;
		action_args.robot = a->robot;		
		pthread_create(&action_thread, NULL, action, &action_args);
		
		while ((recv_size = recv(socket, &buf, sizeof buf, 0)) > 0) {
			if (recv_size == sizeof(opcode_t)) {
				buf = ntohl(buf);
				RT_SAY2("received opcode %i\n", buf);
				decode_command(buf, &cmd);
				RT_SAY3("decoded as instr=%i arg=%i\n", cmd.instr, cmd.arg);
				
			} else {
				RT_SAY2("received opcode of wrong size (%i)\n", recv_size);
			}
		}

		cmd.instr = QUIT;
		close(socket);
	}

	RT_SAY("thread exit\n");
}


/* This thread will not exit voluntarily! It must be stopped manualy through opcode QUIT! */
void action(void* args) {
	struct robot_action_thread_args * a = args;
	int speed = 1;
	int just_kicked = 0;
	int angle_temp = 0;
	int distance = 0;

	TIMED_LOOP

		/* Protract kicker */
		if (just_kicked) {
			just_kicked = 0;
			a->rs->kicker = 0;
		}
		switch (a->cmd->instr) {
			case DO_NOTHING: /*AT_SAY("idling.\n");*/ break;
			case FORWARDS:
					 a->rs->x = a->rs->x - speed * cos(a->rs->angle);
					 a->rs->y = a->rs->y - speed * sin(a->rs->angle);
					 break;
			case BACKWARDS:  
					 a->rs->x = a->rs->x + speed * cos(a->rs->angle);
					 a->rs->y = a->rs->y + speed * sin(a->rs->angle);
					 break;
			case BACKWARDS_SLIGHTLY: AT_STUB("BACKWARDS_SLIGHTLY\n"); break;
			case STOP: AT_STUB("STOP\n"); break; /* I don't think this really needs to do anything. */
			case CHANGE_SPEED: speed = a->cmd->arg; break;
			case KICK:
					   just_kicked = 1;
					   a->rs->kicker = 1;
					   break;
			case ROTATE: 
					   angle_temp = a->rs->angle + a->cmd->arg;
					   if (angle_temp >= 360) angle_temp = angle_temp - 360;
					   a->rs->angle = angle_temp;
					   break;
			case ARC: AT_STUB("ARC\n"); break;
			case STEER_WITH_RATIO: AT_STUB("STEER_WITH_RATIO\n"); break;
			case BEEP: AT_SAY("bleep blop.\n"); a->cmd->instr = DO_NOTHING;  break;
			case CELEBRATE: AT_SAY("bleep blop win!\n"); break;
			case FORWARDS_WITH_DISTANCE:
					distance = a->cmd->arg; break;
			case QUIT: AT_SAY("quitting action thread.\n"); return 0;
		}

	TIMED_LOOP_END
}
