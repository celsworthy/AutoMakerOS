#!/bin/bash
wifion=$(networksetup -getairportpower en0 | cut -d : -f2 | sed 's/^[ \t]*//;s/[ \t]*$//' | awk '{print tolower($0)}' )
ssid=$(networksetup -getairportnetwork en0 | cut -d : -f2 | sed 's/^[ \t]*//;s/[ \t]*$//')

associated='false'
if [ ${ssid} != "You are not associated with an AirPort network." ]
then
   associated='true'	
fi

echo {\"poweredOn\":\"${wifion}\", \"associated\":\"${associated}\", \"ssid\":\"${ssid}\"}
