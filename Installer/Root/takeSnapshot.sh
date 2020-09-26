#!/bin/bash
VIDEO_DEVICE=$1
RESOLUTION=$2
shift
shift
#echo Camera parameters = $VIDEO_DEVICE -r $RESOLUTION "$@" > /home/pi/dump.txt
fswebcam -q -d $VIDEO_DEVICE -r $RESOLUTION "$@" --no-banner -
