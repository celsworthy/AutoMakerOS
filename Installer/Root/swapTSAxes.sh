#!/bin/bash
#This version is for the SPI touch interface
#export DISPLAY=:0.0
#export XAUTHORITY=~/.Xauthority
export ROBOX_PRO_FILE=/boot/RoboxPro

if [ -e "$ROBOX_PRO_FILE" ]
then
	# This is a RoboxPro
	# Default calibration for GPIO-based screen
	#xinput --set-prop 'ADS7846 Touchscreen' --type=float 'Coordinate Transformation Matrix' 0 1 0 -1 0 1 0 0 1
	xinput --set-prop 'ADS7846 Touchscreen' --type=float 'Coordinate Transformation Matrix' 0.0 1.047 -0.026 -1.076 0.0 1.041 0 0 1
	xinput --set-prop 'eGalax Inc. eGalaxTouch EXC3000-0783-45.00.00' 'Coordinate Transformation Matrix' 0 1 0 -1 0 1 0 0 1
	xinput --set-prop 'FT5406 memory based driver' 'Coordinate Transformation Matrix' 0 1 0 -1 0 1 0 0 1
else
	# This is a Root/Mote.
	# Default calibration for GPIO-based screen
	xinput --set-prop 'ADS7846 Touchscreen' --type=float 'Coordinate Transformation Matrix' 0 -1.1 1.06 1.1 0 -0.05 0 0 1
	xinput --set-prop 'eGalax Inc. eGalaxTouch EXC3000-0783-45.00.00' 'Coordinate Transformation Matrix' 0 -1 1 1 0 0 0 0 1
	xinput --set-prop 'FT5406 memory based driver' 'Coordinate Transformation Matrix' 0 -1 1 1 0 0 0 0 1
fi
