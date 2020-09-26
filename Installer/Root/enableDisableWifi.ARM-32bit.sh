#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

command=down

if [ $1 == 'on' ]
then
	command=up
	sudo ip link set wlan0 up > /dev/null 2>&1 
else
	$DIR/setupWifi.sh clear
fi

#sudo ip link set wlan0 ${command} > /dev/null 2>&1
