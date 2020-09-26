#!/bin/bash
ssid=$(echo "$1" | cut -d : -f1)
pw=$(echo "$1" | cut -d : -f2)

networksetup -setairportnetwork en0 $ssid $pw
