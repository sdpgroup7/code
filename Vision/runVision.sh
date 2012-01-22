#!/bin/bash

export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:./lib

java -cp ./lib/v4l4j.jar:. RunVision
