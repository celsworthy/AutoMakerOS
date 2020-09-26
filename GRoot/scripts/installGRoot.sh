#!/bin/bash
serviceFinalDir=/etc/systemd/system
grootServiceFile=roboxgroot.service
installDir=/home/pi/ARM-32bit/GRoot

echo ${installDir}

if [ "$(id -u)" != "0" ]; then
    echo "The installer must be run as root. Try sudo $0"
    exit 1
fi

rm -f ${serviceFinalDir}/${grootServiceFile}

echo '[Unit]' >> ${serviceFinalDir}/${grootServiceFile}
echo 'Description=Robox Root Node' >> ${serviceFinalDir}/${grootServiceFile}
echo 'After=syslog.target network.target remote-fs.target nss-lookup.target' >> ${serviceFinalDir}/${grootServiceFile}
echo '[Service]' >> ${serviceFinalDir}/${grootServiceFile}
echo 'Type=simple' >> ${serviceFinalDir}/${grootServiceFile}
echo "WorkingDirectory=${installDir}" >> ${serviceFinalDir}/${grootServiceFile}
echo "ExecStart=${installDir}/runGRoot.sh" >> ${serviceFinalDir}/${grootServiceFile}
echo 'Restart=always' >> ${serviceFinalDir}/${grootServiceFile}
echo 'LimitNOFILE=10000' >> ${serviceFinalDir}/${grootServiceFile}
echo 'User=pi' >> ${serviceFinalDir}/${grootServiceFile}
echo '[Install]' >> ${serviceFinalDir}/${grootServiceFile}
echo 'WantedBy=multi-user.target' >> ${serviceFinalDir}/${grootServiceFile}

systemctl daemon-reload
systemctl enable roboxgroot
systemctl start roboxgroot

echo Installed Robox GRoot GUI service
