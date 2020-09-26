#!/bin/bash

if [ "$(id -u)" != "0" ]; then
    echo "The uninstaller must be run as root. Try sudo $0"
    exit 1
fi

serviceFinalDir=/etc/systemd/system
rootServiceFile=roboxroot.service

systemctl stop ${rootServiceFile}
systemctl daemon-reload

rm -f ${serviceFinalDir}/${rootServiceFile}

echo Robox Root Node service uninstalled
