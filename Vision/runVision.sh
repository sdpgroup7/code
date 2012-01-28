#!/bin/bash

export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:./lib:/group/teaching/sdp/sdp7/OpenCV/OpenCV-2.3.1/release/lib

java -cp ./lib/v4l4j.jar:./lib/javacpp.jar:./lib/javacv.jar:./lib/javacv-linux-x86_64.jar:. TestVision
