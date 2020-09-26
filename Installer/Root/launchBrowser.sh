#!/bin/bash
#export DISPLAY=:0.0
#export XAUTHORITY=~/.Xauthority
export ROOT_HOME=/home/pi/ARM-32bit/Root

# Display the animated loading  image on the X root window.
animate -window root -delay 10 ${ROOT_HOME}/RoboxLoading.gif &

# Save the process ID so it can be easily killed later.
pid=`echo $!`

# Wait until Root is running.
until curl http://localhost:8080/index.html > /dev/null 2>&1
do
	/bin/sleep 1
done

# Start the browser
${ROOT_HOME}/startBrowser.sh &

# Kill the animated loading image.
kill -9 $pid

# This is just incase the root screen is displayed again.
# Load the static screen after 1 minute. The delay is to
# let the browser start, as otherwise the splash screen flashes
# up briefly.
sleep 60
xsetroot -bitmap ${ROOT_HOME}/cel.xbm

