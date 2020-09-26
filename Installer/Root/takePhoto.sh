#!/bin/bash

PRINTER_NAME=$1
JOB_ID=$2
VIDEO_DEVICE=$3
RESOLUTION=$4
shift
shift
shift
shift

#!/bin/bash
# Try to find a USB directory
USB_DIR=`find /media/ -maxdepth 1 -type d -name 'usb*' -print -quit`
SNAP_DIR=""
if [ ! -z "$USB_DIR" ]
then
    SNAP_DIR="$USB_DIR/$PRINTER_NAME/$JOB_ID"
else
    # If we don't have a USB then we make a project folder in the User folder
    SNAP_DIR="/home/pi/CEL Root/timelapse/$PRINTER_NAME/$JOB_ID"
fi
sudo mkdir -p "$SNAP_DIR"

# Take photo.
TIME_STAMP=`date +"%Y%m%d%H%M%S"`
sudo fswebcam -q -d $VIDEO_DEVICE -r $RESOLUTION "$@" --no-banner "$SNAP_DIR/$JOB_ID$TIME_STAMP.jpg"

