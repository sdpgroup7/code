#!/bin/sh
FILES=( net robot_simm world_state simulator )
SRC="src/uk/ac/ed/inf/sdp2012/group7/simulator/"
INC="$SRC"
OUT="bin/uk/ac/ed/inf/sdp2012/group7/simulator/"
TPO="$OUT"

for f in ${FILES[*]}
do
	echo "*** Compiling $f..."
	gcc -DHAVE_CONFIG_H -O2 -I$INC -g -MT "$OUT/$f.o" -MD -MP -MF "$TPO/$f.Tpo" -c -o "$OUT/$f.o" "$SRC/$f.c"
	ret=$?
	if [ $ret -ne 0 ]; then
		echo "*** Failed compiling $f, exit code $ret"
		exit 1
	fi
done

gcc -pthread -lm -o "$OUT/simulator" $OUT/*.o
