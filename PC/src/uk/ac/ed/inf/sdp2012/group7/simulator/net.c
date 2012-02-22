#include "net.h"


#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/socket.h>
#include <netdb.h>
#include <stdio.h>

#include "options.h"
#include "types.h"
#include "macros.h"


inline socket_t accept_client(socket_t control_socket) {
	listen(control_socket, 1);
	struct sockaddr_storage client_addr;
	socklen_t sin_size = sizeof client_addr;
	socket_t socket;
	socket = accept(control_socket, (struct sockaddr *) &client_addr, &sin_size);
	close(control_socket);
	return socket;
}


socket_t make_socket(char *host, char *port, int *error, int *gai_error) {
	*error = 0;
	*gai_error = 0;

	struct addrinfo hints;
	struct addrinfo *res, *ai_p;

	socket_t sock;

	memset(&hints, 0, sizeof hints);
	hints.ai_family = DEFAULT_AI_FAMILY;
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_flags = AI_PASSIVE;

	if ((*gai_error = getaddrinfo(NULL, port, &hints, &res)) != 0) 
		return -1;
	
	for (ai_p = res; ai_p != NULL; ai_p = ai_p->ai_next) {
		/* Don't care if these fail, we'll just try the next res, in the end we should have a working socket */
		if ((sock = socket(ai_p->ai_family, ai_p->ai_socktype, ai_p->ai_protocol)) == -1) 
			continue;
		int yes = 1;
		if (setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof yes) == -1) 
			continue;
		if (bind(sock, ai_p->ai_addr, ai_p->ai_addrlen) == -1) {
			close(sock); // clean up
			continue;
		}
		break;
	}
	if (ai_p == NULL) {
		*error = errno;
		return -1;
	}

	void *_addr;
	if (ai_p->ai_family == AF_INET)
		_addr = &(((struct sockaddr_in *)ai_p->ai_addr)->sin_addr);
	if (ai_p->ai_family == AF_INET6)
		_addr = &(((struct sockaddr_in6 *)ai_p->ai_addr)->sin6_addr);
	
	inet_ntop(ai_p->ai_family, _addr, host, INET6_ADDRSTRLEN);

	freeaddrinfo(res);

	return sock;
}

int set_up_robot_conn(char * port, char * addr, struct robot_thread_args * thread_args) {
	if (not_dummy(port)) {
		int error;
		int gai_error;
		socket_t socket = make_socket(addr, port, &error, &gai_error);
		if (socket == -1) {
			printf("error: ");
			if (error)
				printf("failed to open socket on [%s]:%s, errno %i", addr, port, error);
			if (gai_error)
				printf("getaddrinfo on port %s failed: %i %s", port, gai_error, gai_strerror(gai_error));
			return -1;
		}
		printf("Opened socket %i on [%s]:%s\n", socket, addr, port);
	}

	thread_args->socket = socket;
	thread_args->is_dummy = is_dummy(port);
	return 0;
}

