#!/bin/bash
# Robox detector. Works with or without the presence of robox.rules
if [ -e /etc/udev/rules.d/robox.rules ]
then
	robox_device="/dev/robox*"
	is_ttyACM=
else
	robox_device="/dev/ttyACM*"
	is_ttyACM=1
fi

name=$1
id=$2

for device in $robox_device
do
	if [[ $device = "$robox_device" ]]
	then
		echo NOT_CONNECTED
		exit
	fi
	if [[ $is_ttyACM ]]
	then
		poss=`udevadm info --query=symlink --name=$device | grep -i $name | grep -i $id`
	else
		poss=1
	fi
	if [[ $poss ]]
	then
		echo $device >/dev/stdout
		echo " "
	fi
done
