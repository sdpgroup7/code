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
	cmd->instr = op INSTR_SHIFT;
	cmd->arg = op & ARG_MASK;
	cmd->kicker = op KICKER_SHIFT;
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
		action_args.socket = socket;
		pthread_create(&action_thread, NULL, action, &action_args);
		
		while ((recv_size = recv(socket, &buf, sizeof buf, 0)) > 0) {
			if (recv_size == sizeof(opcode_t)) {
				buf = ntohl(buf);
				RT_SAY2("received opcode %i\n", buf);
				decode_command(buf, &cmd);
				RT_SAY3("decoded as instr=%i arg=%i\n", cmd.instr, cmd.arg);
				RT_SAY2("kicker=%i\n", cmd.kicker);
				
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
	int angle_temp = 0;
	int distance = 0;


	TIMED_LOOP

		a->rs->kicker = 0;

		switch (a->cmd->instr) {
			case CONTINUE:
			case DO_NOTHING: break;
			case START_MATCH:
			case FORWARDS:
					 a->rs->x = a->rs->x + speed * cos(a->rs->angle);
					 a->rs->y = a->rs->y + speed * sin(a->rs->angle);
					 break;
			case BACKWARDS:  
					 a->rs->x = a->rs->x - speed * cos(a->rs->angle);
					 a->rs->y = a->rs->y - speed * sin(a->rs->angle);
					 break;
			case BACKWARDS_WITH_DISTANCE:
					if (!distance)
						distance = a->cmd->arg;
					else {
						distance -= speed;
						a->rs->x = a->rs->x - speed * cos(a->rs->angle);
						a->rs->y = a->rs->y - speed * sin(a->rs->angle);
					}
					if (distance < 0) {
						distance == 0;
						a->cmd->instr = DO_NOTHING;
					}
					break;
			case STOP: AT_STUB("STOP\n"); break; /* I don't think this really needs to do anything. */
			case CHANGE_SPEED: speed = SPEED(a->cmd->arg); break;
			case ROTATE_LEFT: 
			case ROTATE_BLOCK_LEFT:
					   a->rs->angle = a->rs->angle - degtorad(a->cmd->arg);
					   break;
			case ROTATE_RIGHT:
			case ROTATE_BLOCK_RIGHT:
					   a->rs->angle = a->rs->angle + degtorad(a->cmd->arg);
					   break;
			case ARC_LEFT: AT_STUB("ARC_LEFT\n"); break;
			case ARC_RIGHT: AT_STUB("ARC_RIGHT\n"); break;
			case BEEP: AT_SAY("bleep blop.\n"); a->cmd->instr = DO_NOTHING;  break;
			case FORWARDS_WITH_DISTANCE:
					if (!distance)
						distance = a->cmd->arg;
					else {
						distance -= speed;
						a->rs->x = a->rs->x + speed * cos(a->rs->angle);
						a->rs->y = a->rs->y + speed * sin(a->rs->angle);
					}
					if (distance < 0) {
						distance == 0;
						a->cmd->instr = DO_NOTHING;
					}
					break;
			case STOP_MATCH: AT_STUB("STOP_MATCH\n"); break;
			case QUIT: AT_SAY("quitting action thread.\n"); return 0;
		}

		if (a->cmd->kicker) {
			a->rs->kicker = 1;
			a->cmd->kicker = 0;
		}

		send(a->socket, &a->cmd->instr, sizeof a->cmd->instr, 0);		

		if (a->rs->x < PITCH_X1+ROBOT_SIZE/2) {
			send(a->socket, BUMP_ON, sizeof a->cmd->instr, 0);
			a->rs->x = PITCH_X1+ROBOT_SIZE/2+ROBOT_SIZE;
			send(a->socket, BUMP_OFF, sizeof a->cmd->instr, 0);
		}
		if (a->rs->y < PITCH_Y1+ROBOT_SIZE/2) {
			send(a->socket, BUMP_ON, sizeof a->cmd->instr, 0);
			a->rs->y = PITCH_Y1+ROBOT_SIZE/2+ROBOT_SIZE;
			send(a->socket, BUMP_OFF, sizeof a->cmd->instr, 0);
		}
		if (a->rs->x > PITCH_X2-ROBOT_SIZE/2) {
			send(a->socket, BUMP_ON, sizeof a->cmd->instr, 0);
			a->rs->x = PITCH_X2-ROBOT_SIZE/2-ROBOT_SIZE;
			send(a->socket, BUMP_OFF, sizeof a->cmd->instr, 0);
		}
		if (a->rs->y > PITCH_Y2-ROBOT_SIZE/2) {
			send(a->socket, BUMP_ON, sizeof a->cmd->instr, 0);
			a->rs->y = PITCH_Y2-ROBOT_SIZE/2-ROBOT_SIZE;
			send(a->socket, BUMP_OFF, sizeof a->cmd->instr, 0);
		}

	TIMED_LOOP_END
}
