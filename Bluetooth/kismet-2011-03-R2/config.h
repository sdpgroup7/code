/* config.h.  Generated from config.h.in by configure.  */
/* config.h.in.  Generated from configure.in by autoheader.  */

/* Define if building universal (internal helper macro) */
/* #undef AC_APPLE_UNIVERSAL_BUILD */

/* system binary directory */
#define BIN_LOC "/usr/local/bin"

/* system data directory */
#define DATA_LOC "/usr/local/share"

/* libairpcap header */
/* #undef HAVE_AIRPCAP_H */

/* Define to 1 if you have the <asm/types.h> header file. */
/* #undef HAVE_ASM_TYPES_H */

/* BSD radiotap packet headers */
/* #undef HAVE_BSD_SYS_RADIOTAP */

/* kernel capability support */
/* #undef HAVE_CAPABILITY */

/* Define to 1 if you have the <errno.h> header file. */
#define HAVE_ERRNO_H 1

/* Define to 1 if you have the <getopt.h> header file. */
#define HAVE_GETOPT_H 1

/* system defines getopt_long */
#define HAVE_GETOPT_LONG 1

/* Define to 1 if you have the `gettimeofday' function. */
#define HAVE_GETTIMEOFDAY 1

/* GPS support will be built. */
#define HAVE_GPS 1

/* inttypes.h is present */
#define HAVE_INTTYPES_H 1

/* libairpcap win32 control lib */
/* #undef HAVE_LIBAIRPCAP */

/* Define to 1 if you have the `cap' library (-lcap). */
/* #undef HAVE_LIBCAP */

/* Curses terminal lib */
/* #undef HAVE_LIBCURSES */

/* NCurses terminal lib */
#define HAVE_LIBNCURSES 1

/* libnl netlink library */
/* #undef HAVE_LIBNL */

/* libnl-2.0 netlink library */
/* #undef HAVE_LIBNL20 */

/* Panel terminal lib */
#define HAVE_LIBPANEL 1

/* libpcap packet capture lib */
#define HAVE_LIBPCAP 1

/* libpcre regex support */
/* #undef HAVE_LIBPCRE */

/* Define to 1 if you have the <libutil.h> header file. */
/* #undef HAVE_LIBUTIL_H */

/* Define to 1 if you have the <linux/if_arp.h> header file. */
/* #undef HAVE_LINUX_IF_ARP_H */

/* Linux wireless iwfreq.flag */
/* #undef HAVE_LINUX_IWFREQFLAG */

/* Netlink works */
/* #undef HAVE_LINUX_NETLINK */

/* Define to 1 if you have the <linux/nl80211.h> header file. */
/* #undef HAVE_LINUX_NL80211_H */

/* Linux wireless extentions present */
/* #undef HAVE_LINUX_WIRELESS */

/* Define to 1 if you have the <linux/wireless.h> header file. */
/* #undef HAVE_LINUX_WIRELESS_H */

/* local radiotap packet headers */
#define HAVE_LOCAL_RADIOTAP 1

/* Define to 1 if you have the <memory.h> header file. */
#define HAVE_MEMORY_H 1

/* Define to 1 if you have the `memset' function. */
#define HAVE_MEMSET 1

/* Define to 1 if you have the <netdb.h> header file. */
#define HAVE_NETDB_H 1

/* Netlink headers are there */
/* #undef HAVE_NETLINKHEADERS */

/* Define to 1 if you have the <netlink/attr.h> header file. */
/* #undef HAVE_NETLINK_ATTR_H */

/* Define to 1 if you have the <netlink/genl/ctrl.h> header file. */
/* #undef HAVE_NETLINK_GENL_CTRL_H */

/* Define to 1 if you have the <netlink/genl/family.h> header file. */
/* #undef HAVE_NETLINK_GENL_FAMILY_H */

/* Define to 1 if you have the <netlink/genl/genl.h> header file. */
/* #undef HAVE_NETLINK_GENL_GENL_H */

/* Define to 1 if you have the <netlink/msg.h> header file. */
/* #undef HAVE_NETLINK_MSG_H */

/* pcap/pcap.h */
/* #undef HAVE_PCAPPCAP_H */

/* pcapfileno-capable libwpcap */
/* #undef HAVE_PCAP_FILENO */

/* Selectablefd-capable libpcap */
#define HAVE_PCAP_GETSELFD 1

/* libpcap header */
#define HAVE_PCAP_H 1

/* Nonblocking-capable libpcap */
#define HAVE_PCAP_NONBLOCK 1

/* libpcap supports PPI */
#define HAVE_PPI 1

/* Define to 1 if you have the `pstat' function. */
/* #undef HAVE_PSTAT */

/* Define to 1 if you have the `select' function. */
#define HAVE_SELECT 1

/* Define to 1 if you have the `setproctitle' function. */
/* #undef HAVE_SETPROCTITLE */

/* Define to 1 if you have the `socket' function. */
#define HAVE_SOCKET 1

/* accept() takes type socklen_t for addrlen */
#define HAVE_SOCKLEN_T 1

/* Define to 1 if `stat' has the bug that it succeeds when given the
   zero-length file name argument. */
/* #undef HAVE_STAT_EMPTY_STRING_BUG */

/* stdint.h is present */
#define HAVE_STDINT_H 1

/* Define to 1 if you have the <stdlib.h> header file. */
#define HAVE_STDLIB_H 1

/* Define to 1 if you have the `strcasecmp' function. */
#define HAVE_STRCASECMP 1

/* Define to 1 if you have the `strftime' function. */
#define HAVE_STRFTIME 1

/* Define to 1 if you have the <strings.h> header file. */
#define HAVE_STRINGS_H 1

/* Define to 1 if you have the <string.h> header file. */
#define HAVE_STRING_H 1

/* Define to 1 if you have the `strstr' function. */
#define HAVE_STRSTR 1

/* System headers are there */
#define HAVE_SYSHEADERS 1

/* Define to 1 if you have the <sys/pstat.h> header file. */
/* #undef HAVE_SYS_PSTAT_H */

/* Define to 1 if you have the <sys/socket.h> header file. */
#define HAVE_SYS_SOCKET_H 1

/* Define to 1 if you have the <sys/stat.h> header file. */
#define HAVE_SYS_STAT_H 1

/* Define to 1 if you have the <sys/time.h> header file. */
#define HAVE_SYS_TIME_H 1

/* Define to 1 if you have the <sys/types.h> header file. */
#define HAVE_SYS_TYPES_H 1

/* Define to 1 if you have the <sys/wait.h> header file. */
#define HAVE_SYS_WAIT_H 1

/* Define to 1 if you have the <unistd.h> header file. */
#define HAVE_UNISTD_H 1

/* Define to 1 if you have the <Win32-Extensions.h> header file. */
/* #undef HAVE_WIN32_EXTENSIONS_H */

/* Define to 1 if you have the <windows.h> header file. */
/* #undef HAVE_WINDOWS_H */

/* __PROGNAME glibc macro available */
/* #undef HAVE___PROGNAME */

/* system library directory */
#define LIB_LOC "/usr/local/lib"

/* system state directory */
#define LOCALSTATE_DIR "/usr/local/var"

/* Define to 1 if `lstat' dereferences a symlink specified with a trailing
   slash. */
/* #undef LSTAT_FOLLOWS_SLASHED_SYMLINK */

/* Define to the address where bug reports for this package should be sent. */
#define PACKAGE_BUGREPORT ""

/* Define to the full name of this package. */
#define PACKAGE_NAME ""

/* Define to the full name and version of this package. */
#define PACKAGE_STRING ""

/* Define to the one symbol short name of this package. */
#define PACKAGE_TARNAME ""

/* Define to the home page for this package. */
#define PACKAGE_URL ""

/* Define to the version of this package. */
#define PACKAGE_VERSION ""

/* writeable argv type */
#define PF_ARGV_TYPE PF_ARGV_WRITEABLE

/* Define as the return type of signal handlers (`int' or `void'). */
#define RETSIGTYPE void

/* Define to 1 if you have the ANSI C header files. */
#define STDC_HEADERS 1

/* system config directory */
#define SYSCONF_LOC "/usr/local/etc"

/* Compiling for Cygwin */
/* #undef SYS_CYGWIN */

/* Compiling for OSX/Darwin */
#define SYS_DARWIN 1

/* Compiling for FreeBSD */
/* #undef SYS_FREEBSD */

/* Compiling for Linux OS */
/* #undef SYS_LINUX */

/* Compiling for NetBSD */
/* #undef SYS_NETBSD */

/* Compiling for OpenBSD */
/* #undef SYS_OPENBSD */

/* Define to 1 if you can safely include both <sys/time.h> and <time.h>. */
#define TIME_WITH_SYS_TIME 1

/* Define to 1 if your <sys/time.h> declares `struct tm'. */
/* #undef TM_IN_SYS_TIME */

/* Define WORDS_BIGENDIAN to 1 if your processor stores words with the most
   significant byte first (like Motorola and SPARC, unlike Intel). */
#if defined AC_APPLE_UNIVERSAL_BUILD
# if defined __BIG_ENDIAN__
#  define WORDS_BIGENDIAN 1
# endif
#else
# ifndef WORDS_BIGENDIAN
/* #  undef WORDS_BIGENDIAN */
# endif
#endif

/* Number of bits in a file offset, on hosts where this is settable. */
/* #undef _FILE_OFFSET_BITS */

/* Define for large files, on AIX-style hosts. */
/* #undef _LARGE_FILES */

/* Define to empty if `const' does not conform to ANSI C. */
/* #undef const */
/* proftpd argv stuff */
#define PF_ARGV_NONE        0
#define PF_ARGV_NEW     	1
#define PF_ARGV_WRITEABLE   2
#define PF_ARGV_PSTAT       3
#define PF_ARGV_PSSTRINGS   4

/* Maximum number of characters in the status line */
#define STATUS_MAX 1024

/* Maximum number of channels - I've only ever heard of 14 being used. */
#define CHANNEL_MAX 14

/* Stupid ncurses */
#define NCURSES_NOMACROS

/* Number of hex pairs in a key */
#define WEPKEY_MAX 32

/* String length of a key */
#define WEPKEYSTR_MAX ((WEPKEY_MAX * 2) + WEPKEY_MAX)

/* Number of past alerts to queue for new clients */
#define ALERT_BACKLOG 50

/* system min isn't reliable */
#define kismin(x,y) ((x) < (y) ? (x) : (y))
#define kismax(x,y) ((x) > (y) ? (x) : (y))

// Timer slices per second
#define SERVER_TIMESLICES_SEC 10

// Max chars in SSID
#define MAX_SSID_LEN    255

/* Namespace (on non-obj-c files) */
#ifndef __IN_OBJC_FILE__
using namespace std;
#define __STL_USE_NAMESPACES
#endif

#ifndef _
#define _(x) x
#endif

