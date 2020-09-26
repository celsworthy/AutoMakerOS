#!/bin/bash
serviceFinalDir=/etc/systemd/system
rootServiceFile=roboxroot.service
installDir=`pwd`

echo ${installDir}

if [ "$(id -u)" != "0" ]; then
    echo "The installer must be run as root. Try sudo $0"
    exit 1
fi

rm -f ${serviceFinalDir}/${rootServiceFile}

echo '[Unit]' >> ${serviceFinalDir}/${rootServiceFile}
echo 'Description=Robox Root Node' >> ${serviceFinalDir}/${rootServiceFile}
echo 'After=syslog.target network.target remote-fs.target nss-lookup.target' >> ${serviceFinalDir}/${rootServiceFile}
echo '[Service]' >> ${serviceFinalDir}/${rootServiceFile}
echo 'Type=simple' >> ${serviceFinalDir}/${rootServiceFile}
echo "WorkingDirectory=${installDir}" >> ${serviceFinalDir}/${rootServiceFile}
echo "ExecStart=${installDir}/runRoot.sh" >> ${serviceFinalDir}/${rootServiceFile}
echo 'Restart=always' >> ${serviceFinalDir}/${rootServiceFile}
echo 'LimitNOFILE=10000' >> ${serviceFinalDir}/${rootServiceFile}
echo 'User=pi' >> ${serviceFinalDir}/${rootServiceFile}
echo '[Install]' >> ${serviceFinalDir}/${rootServiceFile}
echo 'WantedBy=multi-user.target' >> ${serviceFinalDir}/${rootServiceFile}

systemctl daemon-reload
systemctl enable ${rootServiceFile}
systemctl start ${rootServiceFile}

echo Installed Robox Root Node service
