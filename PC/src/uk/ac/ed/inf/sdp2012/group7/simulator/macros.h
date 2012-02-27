#ifndef __macros_h__
#define __macros_h__

#include <sys/time.h>

#define not_dummy(x) strncmp(x, "-1", 2)
#define port_dummy(x) not_dummy(x)?x:"dummy"
#define is_dummy(x) not_dummy(x)==0?1:0 // Don't use in ?: expressions

#ifdef IPV4
#define DEFAULT_AI_FAMILY AF_INET
#endif
#ifdef IPV6
#define DEFAULT_AI_FAMILY AF_INET6
#endif

#define ROBOT_BLUE 1
#define ROBOT_YELLOW 2

#define RT_SAY(x) printf("robot_thread %i: "x, a->robot);
#define RT_SAY2(x,y) printf("robot_thread %i: "x, a->robot, y)
#define RT_SAY3(x,y,z) printf("robot_thread %i: "x, a->robot, y, z)
#define AT_SAY(x) printf("robot_thread %i action_thread: "x, a->robot)
#define AT_STUB(x) AT_SAY("STUB: "x);
#define WS_SAY(x) printf("world_state_thread: "x)
#define WS_SAY_ printf("world_state_thread: "


#define TIMED_LOOP       struct timeval t_start; struct timeval t_end; struct timeval t_left; while(1) { gettimeofday(&t_start, NULL);
#define TIMED_LOOP_END 	 gettimeofday(&t_end, NULL); timersub(&t_end, &t_start, &t_left); usleep(USEC_PER_ACTION - t_left.tv_usec); }

#endif
