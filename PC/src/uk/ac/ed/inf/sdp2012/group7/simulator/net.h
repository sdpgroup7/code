#ifndef __net_h__
#define __net_h__

#include "types.h"

inline socket_t accept_client(socket_t control_socket);
int make_socket(char *host, char *port, int *error, int *gai_error);
int set_up_robot_conn(char * port, char * addr, struct robot_thread_args * thread_args);

#endif
