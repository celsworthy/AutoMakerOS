#!/bin/bash
camera_devices="/dev/video*"

for device in $camera_devices
do
	# If there are no camera devices this will loop once with device equal to /dev/video*
	if [[ $device = "$camera_devices" ]]
	then
		echo NOT_CONNECTED
		exit
	fi
	# Check that device has video capture capability, which is so when
	# the least significant bit of the device capabilities is set.
	devcaps=`v4l2-ctl --device=$device -D | grep "Device Caps" | cut -d 'x' -f2`
	l=${devcaps: -1} # Get the last digit in the capabilites number - note space is required
	if [[ "$((l%2))" != 0 ]]; then
		echo $device > /dev/stdout
		echo " "
	fi
done
