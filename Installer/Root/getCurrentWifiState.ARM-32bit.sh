#!/bin/bash
#poweredInput=$(sudo ifdown -n wlan0 2>&1 | sed 's/^[ \t]*//;s/[ \t]*$//' | awk '{print tolower($0)}' )
poweredInput=$(sudo ip link show wlan0 2>&1 | sed -ne 's/.*wlan0: <\(.*\)>.*/\1/p')
associatedInput=$(iwconfig wlan0 2>/dev/null | cut -d ' ' -f15 | sed 's/^[ \t]*//;s/[ \t]*$//' )
ssid=$(iwgetid wlan0 | cut -d : -f2 | sed 's/^[ \t]*//;s/[ \t]*$//')
if [[ -z ${ssid} ]]
then
   ssid=\"\"
fi

powered='true'
if [[ ${poweredInput} != *"UP"* ]]
then
   powered='false'	
fi

associated='false'
if [[ ${associatedInput} != *"Not-Associated"* ]]
then
   associated='true'	
fi

echo {\"poweredOn\":${powered}, \"associated\":${associated}, \"ssid\":${ssid}}
