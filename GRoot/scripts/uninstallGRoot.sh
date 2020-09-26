#!/bin/bash

if [ "$(id -u)" != "0" ]; then
    echo "The uninstaller must be run as root. Try sudo $0"
    exit 1
fi

serviceFinalDir=/etc/systemd/system
grootServiceFile=roboxgroot.service

systemctl stop ${grootServiceFile}
systemctl daemon-reload

rm -f ${serviceFinalDir}/${grootServiceFile}

echo Robox GRoot GUI service uninstalled
