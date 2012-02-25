#!/bin/sh
FILES=( net robot_simm world_state simulator )

rm *.o

for f in ${FILES[*]}
do
	echo "*** Compiling $f..."
	gcc -DHAVE_CONFIG_H -O2 -I. -g -MT $f.o -MD -MP -MF ".deps/$f.Tpo" -c -o $f.o $f.c
	ret=$?
	if [ $ret -ne 0 ]; then
		echo "*** Failed compiling $f, exit code $ret"
		exit 1
	fi
done

gcc -pthread -lm -o simulator *.o
