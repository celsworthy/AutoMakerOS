#!/bin/bash
export DISPLAY=:0.0
# Display CEL logo.
if [ -e /usr/bin/xsetroot ] && [ -e /home/pi/ARM-32bit/Root/cel.xbm ]
then
    xsetroot -bitmap /home/pi/ARM-32bit/Root/cel.xbm
fi

# Clear the Chromium cache.
rm -fr ~/.cache/chromium/*
sed -i 's/\("exited_cleanly": *\)false,/\1true,/' /home/pi/.config/chromium/Default/Preferences
sed -i 's/\("exit_type": *"\)[^"]*",/\1Normal",/' /home/pi/.config/chromium/Default/Preferences

# Start Chromium in kiosk mode on the loading static page.
chromium-browser --kiosk --noerrdialogs file:///home/pi/ARM-32bit/Root/www/loading.html

