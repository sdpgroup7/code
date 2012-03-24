#include <stdio.h>

#include "types.h"
#include "net.h"
#include "macros.h"
#include "options.h"

#include <errno.h>

/* This won't close (unless on error) but who cares */
void ws_thread(void * args) {
	struct ws_thread_args * a = args;
	WS_SAY("started, waiting for connection...\n");
	socket_t socket;
	if ((socket = accept_client(a->socket)) != -1) {
		WS_SAY("connected.\n");
	} else {
		WS_SAY_ "failed to connect client socket %i %i!\n", errno, a->socket);
		return -1;
	}
	
	/* Send packets! */
	struct ws_packet p;

	int frame = 0;

	TIMED_LOOP

		p.blue_x = a->ws->blue->x;
		p.blue_y = a->ws->blue->y;
		p.blue_a = radtodeg(a->ws->blue->angle);

		p.yellow_x = a->ws->yellow->x;
		p.yellow_y = a->ws->yellow->y;
		p.yellow_a = radtodeg(a->ws->yellow->angle);

		p.blue_kick = a->ws->blue->kicker;
		p.yellow_kick = a->ws->yellow->kicker;
		
		WS_SAY_ "[%02i] bx=%i by=%i ba=%i yx=%i yy=%i ya=%i bk=%i yk=%i\r", frame, p.blue_x, p.blue_y, p.blue_a, p.yellow_x, p.yellow_y, p.yellow_a, p.blue_kick, p.yellow_kick);

		if (send(socket, &p, sizeof p, 0) == -1)
			WS_SAY("send failed\n");

		++frame;
		if (frame > 24)
				  frame = 0;

	TIMED_LOOP_END
}
