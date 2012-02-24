#!/bin/sh
FILES=( net robot_simm world_state simulator )

for f in ${FILES[*]}
do
	echo "*** Compiling $f..."
	gcc -DHAVE_CONFIG_H -O2 -I. -g -pthread -MT $f.o -MD -MP -MF ".deps/$f.Tpo" -c -o $f.o $f.c
	ret=$?
	if [ $ret -ne 0 ]; then
		echo "*** Failed compiling $f, exit code $ret"
		exit 1
	fi
done

gcc -o simulator *.o
