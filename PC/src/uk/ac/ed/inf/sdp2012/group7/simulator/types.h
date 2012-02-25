#ifndef __types_h__
#define __types_h__

#include <inttypes.h>

#include "opcodes.h"

#define opcode_t uint32_t
#define socket_t int
#define java_int int32_t
#define java_uint uint32_t

struct command {
	enum opcodes instr;
	int arg;
};

struct robot_status {
	java_uint x;
	java_uint y;
	java_uint angle;
	int kicker;
};

struct robot_thread_args {
	socket_t socket;
	int is_dummy;
	int robot;
	struct robot_status * status;
};

struct robot_action_thread_args {
	struct command * cmd;
	struct robot_status * rs;
	int robot;
};

struct ball {
	java_uint x;
	java_uint y;
	int angle;
	int speed;
};

struct world_state {
	struct robot_status * blue;
	struct robot_status * yellow;
	struct ball * ball;
};

struct ws_thread_args {
	struct world_state * ws;
	socket_t socket;
	int is_dummy;
};

struct ws_packet {
	java_uint blue_x;
	java_uint blue_y;
	java_uint blue_a;

	java_uint yellow_x;
	java_uint yellow_y;
	java_uint yellow_a;

	java_uint ball_x;
	java_uint ball_y;
};

#endif
