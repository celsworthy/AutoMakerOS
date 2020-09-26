#!/bin/bash
# 
# Post upgrade script - run once after upgrade.

ARM_DIR="ARM-32bit"
ROOT_DIR="Root"
GROOT_DIR="GRoot"
PI_HOME="/home/pi"

ARM_HOME=${PI_HOME}/${ARM_DIR}
ROOT_HOME=${ARM_HOME}/${ROOT_DIR}
GROOT_HOME=${ARM_HOME}/${GROOT_DIR}

echo "Upgrading Root ..."

# Remove the roboxbrowser service if present.
SERVICE_DIR=/etc/systemd/system
BROWSER_SERVICE_FILE=roboxbrowser.service
BROWSER_SERVICE_PATH=${SERVICE_DIR}/${BROWSER_SERVICE_FILE}
if [[ -e ${BROWSER_SERVICE_PATH} ]]
then
	echo "Removing the roboxbrowser service ..."
	sudo systemctl stop ${BROWSER_SERVICE_FILE}
	sudo rm -f ${BROWSER_SERVICE_PATH}
	sudo systemctl daemon-reload
fi
pkill chromium

if [[ ! -e /usr/bin/unclutter ]]
then
	echo "Installing unclutter ..."
	pushd ${ROOT_HOME}/upgrade_data/offline/unclutter
	${ROOT_HOME}/upgrade_data/offline/unclutter/install.sh
	popd
fi

#==============================
# pmount and all files needed for mounting of USB drives
#==============================
if [[ ! -e /usr/bin/pmount ]]
then
	echo "Installing pmount ..."
	pushd ${ROOT_HOME}/upgrade_data/offline/pmount
	${ROOT_HOME}/upgrade_data/offline/pmount/install.sh
	popd
fi

if [[ ! -e /etc/udev/rules.d/usbstick.rules ]]
then
	echo "Installing usbstick.rules ..."
	sudo cp -f ${ROOT_HOME}/upgrade_data/usb_mount/usbstick.rules /etc/udev/rules.d
fi

if [[ ! -e /lib/systemd/system/usbstick-handler@.service ]]
then
	echo "Installing usbstick-handler service ..."
	sudo cp -f ${ROOT_HOME}/upgrade_data/usb_mount/usbstick-handler@.service /lib/systemd/system
fi

if [[ ! -e /usr/local/bin/cpmount ]]
then
	echo "Installing cpmount ..."
	sudo cp -f ${ROOT_HOME}/upgrade_data/usb_mount/cpmount /usr/local/bin
fi

#=================
# install fswebcam
#=================
if [ ! -e /usr/bin/fswebcam ]
then
	echo "Installing fswebcam ..."
	pushd ${ROOT_HOME}/upgrade_data/offline/fswebcam
	${ROOT_HOME}/upgrade_data/offline/fswebcam/install.sh
	popd
fi


# Remove the old touch screen calibration scripts
rm -rf ${PI_HOME}/scripts

# Replace lxsession autostart.
if [[ ! -e ${PI_HOME}/.config/lxsession/LXDE-pi ]]
then
	mkdir -p ${PI_HOME}/.config/lxsession/LXDE-pi
fi
cp -bf ${ROOT_HOME}/upgrade_data/autostart ${PI_HOME}/.config/lxsession/LXDE-pi

# Add robox device
if [[ ! -e /etc/udev/rules.d/robox.rules ]]
then
	echo "Adding robox device ..."
	sudo cp -f ${ROOT_HOME}/upgrade_data/robox.rules /etc/udev/rules.d
fi

# Add CEL boot splash screen.
if [[ ! -e /usr/share/plymouth/themes/celrobox/splash.png ]]
then
	echo "Adding CEL boot splash screen ..."
	sudo cp -r ${ROOT_HOME}/upgrade_data/celrobox /usr/share/plymouth/themes
	sudo plymouth-set-default-theme celrobox

	# Get the PARTUUID for the rootfs partition and substitute into the rootoption in cmdline.txt
	# Create a backup of the working cmdline.txt in case it fails.
	sudo cp -f /boot/cmdline.txt /boot/cmdline-backup.txt
	rootfs_dev=`blkid -L rootfs`
	partuuid=`blkid $rootfs_dev -s PARTUUID | sed -e 's/.*=\"\(.*\)\"/\1/'`
	sudo sed -i -e "s/root=PARTUUID=\(.*\) rootfs/root=PARTUUID=${partuuid} rootfs/" ${ROOT_HOME}/upgrade_data/cmdline.txt
	sudo cp -f ${ROOT_HOME}/upgrade_data/cmdline.txt /boot
fi

# Add RoboxPro file if required. Detect this by checking if the display_rotate value is 1
if [[ ! -e /boot/RoboxPro ]]
then
	dr=`sed -n -e 's/display_rotate=\(.*\)/\1/p' /boot/config.txt`
	if [[ "${dr}" == "1" ]]
	then
		echo "Adding RoboxPro file ..."
		sudo touch /boot/RoboxPro
	fi
fi

# Enable and start the SSH server if it is not active.
if service ssh status | grep -q inactive; then
	echo "Enabling and starting the SSH server ..."
	sudo update-rc.d ssh enable
    sudo invoke-rc.d ssh start
fi

# Copy the ssh public key if it is not already there.
mkdir -p /home/pi/.ssh
if [[ -e /home/pi/.ssh/authorized_keys ]]
then
	# Backup the original keys.
	cp /home/pi/.ssh/authorized_keys /home/pi/.ssh/authorized_keys~
	# Remove any existing key.
	sed -i /automaker-root/d /home/pi/.ssh/authorized_keys
fi

# Append new key.
cat ${ROOT_HOME}/upgrade_data/authorized_keys >> /home/pi/.ssh/authorized_keys
chmod 600 /home/pi/.ssh/authorized_keys

if [[ -e ${GROOT_HOME} ]]
then
	echo "Updating GRoot ..."
	if [[ -e /etc/systemd/system/roboxgroot.service ]]
	then
		# Restart GRoot service.
		sudo ${GROOT_HOME}/restartGRoot.sh
		
		# Assume it is already booting to command line.
	else
		# Install GRoot service.
		sudo ${GROOT_HOME}/installGRoot.sh
	
		# Boot to command line with auto-login.
		# The desktop GUI is not started, and hence 
		# neither is the Chromium Kiosk that displayed
		# the Web-based interface to Root.
		sudo raspi-config nonint do_boot_behaviour B2
		
		# Suppress text output to the screen during bootup
		# by disabling the login service
		systemctl --quiet is-enabled getty@tty1.service
		if [[ $? -eq 0 ]]
		then
			sudo systemctl --quiet disable getty@tty1.service
		fi

		# Calling script will reboot.
	fi
fi
echo "Upgrade complete :-)"
