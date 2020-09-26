#!/bin/bash
cameraName=`udevadm info -a --name=$1 | grep -m 1 ATTR{name} | cut -d '"' -f2`
cameraNumber=`echo $1 | grep -o -E '[0-9]+'`
echo $cameraName
echo $cameraNumber
