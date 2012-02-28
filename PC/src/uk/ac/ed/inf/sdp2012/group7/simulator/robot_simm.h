#ifndef __robot_simm_h__
#define __robot_simm_h__

#include "types.h"

void decode_command(opcode_t op, struct command * cmd);
void robot_thread(void *args);
void action(void* args);

#endif
