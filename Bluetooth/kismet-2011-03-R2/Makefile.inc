INSTGRP 	?= "wheel"
MANGRP 		?= "wheel"
INSTUSR 	?= "root"

ZAURUS 		= @zaurus@

PCAPLNK 	= -lpcap

THREADL 	= 

BLDHOME		= .
CXX			= g++
CC			= gcc
LD			= g++
LDFLAGS		=  -framework Foundation -framework CoreFoundation -F/System/Library/PrivateFrameworks -framework Apple80211 -framework IOKit -rdynamic
INSTALL		= /usr/bin/install -c
INSTALL_DATA = ${INSTALL} -m 644
INSTALL_PROGRAM = ${INSTALL}
INSTALL_SCRIPT = ${INSTALL}
INSTALL_STRIP_PROGRAM = @INSTALL_STRIP_PROGRAM@
LIBS		=  -ldl
CLIBS   	= 
CXXLIBS		=  -lstdc++
KSLIBS		= 
CFLAGS		=  -I/usr/include/ncurses
CLIENTLIBS 	=  -ldl -lncurses -lpanel
CAPLIBS		= 
CLIENTFLAGS = 
CXXFLAGS 	= -Wall -g -O2
CPPFLAGS 	=  -I/usr/include/ncurses
SUIDGROUP 	= staff

prefix 		= /usr/local
exec_prefix = ${prefix}
ETC			= ${DESTDIR}${prefix}/etc
BIN			= ${DESTDIR}${exec_prefix}/bin
SHARE 		= ${DESTDIR}${prefix}/share/kismet/
MAN 		= ${DESTDIR}${prefix}/share/man
WAV 		= ${SHARE}/wav/

PLUGINLDFLAGS = -bundle -flat_namespace -undefined suppress

