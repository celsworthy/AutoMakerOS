# AutoMakerOS
AutoMaker software for Robox 3D printers

This repository contains the source code for the software required to operate the Robox range of 3D printers from CEL-UK.

The open-source version of the software is "a work in progress". Notes on how to build and install the software are below.

AutoMaker Developer Notes

Building

Clone the git repositories

git clone https://github.com/CEL-UK/AutoMakerOS.git

Use NetBeans IDE to build.

The repository includes several NetBeans projects. Build them in this order:

	Configuration
	Stenographer
	Language
	RoboxBase
	CelTechCore
	AutoMaker
	RoboxRoot
	GRoot
	GCodeViewer

The installer directory contains all the additional resources required to run AutoMaker.

Installing AutoMaker
====================

In the past, the installable package was built using BitRock Installer. Until an open source install builder is implemented, installation must be done by hand. The easiest way to do this is to install an old version of AutoMaker that has an installer, which will create the required structure. Old versions of AutoMaker can be downloaded from the Robox web site:

	https://cel-robox.com/downloads

To replace AutoMaker with the new version copy the following files into the existing install. (The environment variables need to be suitably defined)

Windows:
	rmdir /Q /S %CEL_INSTALL%\AutoMaker\AutoMaker.jar 
	del -r %CEL_INSTALL%\AutoMaker\lib

	copy %AUTOMAKER_BUILD%\target\AutoMaker.jar %CEL_INSTALL%\AutoMaker
	xcopy /E /I %AUTOMAKER_BUIL%\target\lib %CEL_INSTALL%\AutoMaker

Linux:
	rm $CEL_INSTALL/AutoMaker/AutoMaker.jar 
	rm -r $CEL_INSTALL/AutoMaker//lib

	cp $AUTOMAKER_BUILD/target/AutoMaker.jar $CEL_INSTALL/AutoMaker
	cp -r $AUTOMAKER_BUILD/target/lib $CEL_INSTALL/AutoMaker

To replace GCodeViewer with the new version copy the following files into the existing install. (The environment variables need to be suitably defined)

Windows:
	rmdir /Q /S %CEL_INSTALL%\Common\GCodeViewer\GCodeViewer.jar 
	del -r %CEL_INSTALL%\Common\GCodeViewer\lib

	copy %GCODE_VIEWER_BUILD%\target\AutoMaker.jar %CEL_INSTALL%\Common\GCodeViewer
	xcopy /E /I %GCODE_VIEWER_BUILD%\target\lib %CEL_INSTALL%\Common\GCodeViewer
	
Linux:
	rm $CEL_INSTALL/Common/GCodeViewer/GCodeViewer.jar 
	rm -r $CEL_INSTALL/Common/GCodeViewer/lib

	cp $GCODE_VIEWER_BUILD/target/AutoMaker.jar $CEL_INSTALL/Common/GCodeViewer
	cp -r $GCODE_VIEWER_BUILD/target/lib $CEL_INSTALL/Common/GCodeViewer

About Root
==========

Raspberry Pi
------------

Root is a web server app using the DropWizard framework running on a Raspberry Pi with Raspbian OS. It listens on port 8080, so is accessed using localhost:8080. A browser interface is implemented in HTML, CSS and Javascript. 

For AutoMaker (and/or the Root web interface?) to work, the version of the software must match the version number in 

	/home/pi/ARM-32bit/Root/application.properties

on the Root Raspberry Pi. To match version "4.02.00" the file must contain:

	version=4.02.00

The web interface uses jQuery and works by running a function on the document ready event. It is setup in core.js:

	$(document).ready(function () {
		...
	}

This function does some internationalisation, then calls the page_initialiser function of the page, which does the page specific setup.

The script /home/pi/ARM-32bit/Root/installRoot.sh installs a service on the RPi to start Root.

Root is started on the Pi by systemd with the following service script:

	/etc/systemd/system/roboxroot.service
		[Unit]
		Description=Robox Root Node
		After=syslog.target network.target remote-fs.target nss-lookup.target
		[Service]
		Type=simple
		WorkingDirectory=/home/pi/ARM-32bit/Root
		ExecStart=/home/pi/ARM-32bit/Root/runRoot.sh
		Restart=always
		LimitNOFILE=10000
		User=pi
		[Install]
		WantedBy=multi-user.target

This runs the script

	/home/pi/ARM-32bit/Root/runRoot.sh
	
	#!/bin/bash
	# If checkForUpgrade.sh returns a non zero exit code, it upgraded root and the service needs to restart.
	# This is done by simply exiting, and letting the service manager restart the service.
	if ./checkForRootUpgrade.sh; then
		java/bin/java -Dglass.platform=Monocle -Dmonocle.platform=Headless -Djava.net.preferIPv4Stack=true -DlibertySystems.configFile=Root.configFile.xml -jar Root.jar server Root.yml
	fi


GRoot is the GUI client for Root. It is started on the Pi by systemd with the following service script:

	/etc/systemd/system/roboxgroot.service
		[Unit]
		Description=Robox Root Node
		After=syslog.target network.target remote-fs.target nss-lookup.target
		[Service]
		Type=simple
		WorkingDirectory=/home/pi/ARM-32bit/GRoot
		ExecStart=/home/pi/ARM-32bit/GRoot/runGRoot.sh
		Restart=always
		LimitNOFILE=10000
		User=pi
		[Install]
		WantedBy=multi-user.target

This runs the script

	/home/pi/ARM-32bit/GRoot/runGRoot.sh

		#!/bin/bash
		ROBOX_PRO_FILE=/boot/RoboxPro

		ROBOX_LARGE=/boot/RoboxLarge

		if [ -e "$ROBOX_LARGE" ]
		then
				# This is a Root with a large touch screen.
				X_MIN=800
				X_MAX=300
				Y_MIN=0
				Y_MAX=750
		elif [ -e "$ROBOX_PRO_FILE" ]
		then
			# This is a RoboxPro
			X_MIN=3895
			X_MAX=1675
			Y_MIN=300
			Y_MAX=6400

		else
			# This is a Root/Mote.
			X_MIN=300
			X_MAX=2500
			Y_MIN=4000
			Y_MAX=-2300
		fi

	cd /home/pi/ARM-32bit/GRoot
	sudo /home/pi/ARM-32bit/Root/java/bin/java -Djavafx.platform=monocle -Dmonocle.input.0/0/0/0.minX=$X_MIN -Dmonocle.input.0/0/0/0.maxX=$X_MAX -Dmonocle.input.0/0/0/0.minY=$Y_MIN -Dmonocle.input.0/0/0/0.maxY=$Y_MAX -Dmonocle.input.0/0/0/0.flipXY=true -Dcom.sun.javafx.virtualKeyboard="none" -jar ./GRoot.jar -u 500 -s -c ~/CEL\ Root -i ~/ARM-32bit/GRoot

If Root fails to start, look in the various logs: /var/log/syslog, /home/pi/CEL Root/Root.log, /home/pi/ARM-32bit/Root/dw.log and /home/pi/ARM-32bit/Root/jetty.log

Make sure that Configuration-x.x.jar, RoboxBasex.x.jar and Stenographer-x.x.jar are all in sync with Root.jar.

The following Windows PowerShell script will run Root from the build target directory "C:\Dev\RoboxRoot\target". The RoboxRoot.configFile.xml specify a fake install directory in the Application Configuration, e.g.

	<FakeInstallDirectory>C:\Program Files\CEL\Root\</FakeInstallDirectory>

The fake install directory must contain an applications.properties file. There must be a Common directory, as installed when AutoMaker or Root is properly installed, at the same level as Root (e.g. C:\Program Files\CEL\Common). It must contain bin\RoboxDetector.Exe (on Windows) or RoboxDetector.linux.sh (on Linux i.e. the RPi).
	
	$env:JAVA_HOME = "C:\Program Files\Java\jre.8.0_92"
	$env:CLASSPATH = ".;C:\Dev\RoboxRoot\target;D:\CEL\Dev\RoboxRoot\target\lib"
	cd C:\Dev\RoboxRoot
	 
	& 'C:\Program Files\Java\jre1.8.0_92\bin\java' '-Djava.net.preferIPv4Stack=true' '-DlibertySystems.configFile=RoboxRoot.configFile.xml' -jar 'target\Root.jar' server 'Root.yml'

Once root is running, it can be accessed from the browser using the URL http://localhost:8080/index.html.

It can also be run from the NetBeans IDE; run the Root project.

Installing Root on Raspbian
===========================

To create a new image:

1: Download a fresh installation of Raspbian.
2: Download a fresh build of RootARM-32bit-xxx.zip (where xxx is the git tag) from build server.
3: Unzip RootARM-32bit-xxx.zip into /home/pi. This should result in a directory /home/pi/ARM-32bit.
4: Set execute permissions: chmod ug+x /home/pi/ARM-32bit/Root/*.sh /home/pi/ARM-32bit/Root/bin/* /home/pi/ARM-32bit/Common/bin/*
5: To get the touch screen working, edit config.txt on the boot partition of the RPi SD Card (which is a FAT32 partition, so can be seen by Windows) to be the following:

	# Enable audio (loads snd_bcm2835)
	dtparam=audio=on

	###################################################
	#Setup for Root Screen
	max_usb_current=1
	hdmi_group=2
	hdmi_mode=87
	hdmi_cvt 800 480 59 6 0 0 0
    #For Root, set display_rotate to 3.
    #For Pro, set display_rotate to 1.
	display_rotate=1
	# fbtft_device.rotate=0
	hdmi_drive=1
	disable_splash=1

	#Touch screen device
	dtparam=spi=on
	dtparam=i2c_arm=on
	dtoverlay=ads7846,cs=1,penirq=25,penirq_pull=2,speed=50000,keep_vref_on=0,swapxy=0,pmax=255,xohms=150,xmin=200,xmax=3900,ymin=200,ymax=3900
	###################################################
	disable_overscan=1

7: run the upgrade script to setup Root:

	/home/pi/ARM-32bit/Root/upgrade.sh

With luck and a following wind, the system will reboot into Root.

Updating Root
======
When copying a development version onto the RPi, first delete the following:

	rm -rf /home/pi/ARM-32bit/Root/lib/
	rm /home/pi/ARM-32bit/Root/Root.jar

then copy the development versions in their place:

	cp -r $ROOT_DEV/lib /home/pi/ARM-32bit/Root/lib/
	cp $ROOT_DEV/Root.jar /home/pi/ARM-32bit/Root/Root.jar

and restart Root:

	/home/pi/ARM-32bit/Root/restartRoot.sh
	
Bootstrap Studio
================

The browser interface for Root was created in BootStrap (a proprietary tool) and exported. Use the Cosmo theme to get the correct fonts etc.

Root Upgrade Process
====================

Upgrades are triggered from AutoMaker when the Root version does not match the AutoMaker version.
The version numbers are stored in the application.properties files in the respective install directores for AutoMaker and Root
(e.g. C:\Program Files\CEL\Root\application.properties and C:\Program Files\CEL\AutoMaker\application.properties).

Contents of the application.properties is something like:

	version = 4.02.00
	language = en

If the version numbers do not match, AutoMaker downloads the file "RootARM-32bit-<version>.zip" from the Robox Website:

	"https://downloads.cel-uk.com/software/root/RootARM-32bit-<version>.zip"

storing it in the Temp directory of the user storage directory (usually something like 

	C:\Users\Name\Documents\CEL Robox\
	
AutoMaker then sends it to the Root via SSL,  storing it in the user temp directory on the Root Raspberry Pi.

Once the whole file has been downloaded, Root terminates.It is restarted by the service manager.

The start Root script unzips the downloaded file before running Root.

The upgrade will overwrite existing files and add new files, but not remove old files.

WiFi on PI
==========
Raspbian Stretch uses the dhcp 10-wpa_supplicant hook to handle wifi (see https://wiki.archlinux.org/index.php/Dhcpcd#10-wpa_supplicant).
Earlier versions did something else - maybe using the /etc/network/interfaces file.

The Wifi wpa_supplicant configuration file on the Pi is

	/etc/wpa_supplicatnt/wpa_supplicant.config

Wifi will not start up if no country has been specified. Either set it using raspi-config, or add the line

	country=GB

to wpa_supplicant.config.

To force the dameon to reload the config:

	# Bring down the interface.
	sudo ip link set wlan0 down > /dev/null 2>&1
	# Force wpa-supplicant to reload it's configuration.
	sudo wpa_cli -i wlan0 reconfigure > /dev/null 2>&1
	# Bring up the interface.
	sudo ip link set wlan0 up > /dev/null 2>&1
	# Allow time for changes to come into effect.
	
Documentation for wpa_supplicant.config is here:

	https://w1.fi/cgit/hostap/plain/wpa_supplicant/wpa_supplicant.conf

Upgrading Root Software
=======================

AutoMaker looks for "RootARM-32bit-<version>.zip" in

	C:\Users\<user>\Documents\CEL Robox\Temp
	
If it is not there, it downloads it from

	https://downloads.cel-uk.com/software/root/RootARM-32bit-<version>.zip
	
It is uploaded onto the Raspberry Pi into /tmp. Root automatically unpacks it when it next restarts. The file can be copied there manually, but don't restart the Pi, as /tmp is automatically emptied on a restart. Instead, run

	/home/pi/ARM-32bit/Root/restartRoot.sh

to do the upgrade.
	
Safe Mode
=========

The printer can be powered up in 'safe mode', in which the control board is powered through the USB, and everything else is off.

	Disconnect all leads including the mains power lead.
	Plug in the USB cable (which must be connected to a computer) while holding the printer eject button.

Amount of Filament to Extrude
=============================

Let diameter of filament = d.
Cross-sectional area of filament = pi.(d/2)^2 = (pi.d^2)/4 = a
Volume of length (l) of filament = a.l = pi.d^2.l/4

Volume to extrude = thickness x step length x nozzle width = t.s.n
Length to extrude = t.s.n / a = 4.t.s / (pi.d^2)

Nozzle and Material assignments per head type
=============================================
Dual Material, Dual nozzles, Valves: RBX01-DM

   Mat Ext       Left Right
    1   E --------------|
                        |
    2   D ---------|    |
           Heater  S    T
                   |    |
           Tool    T0   T1
		   Nozzle  N1   N2
			
Single Material, Dual nozzles, Valves: RBX01-SM, RBX01-S2

   Mat Ext       Left Right
    1   E --------------|
                        |
                        |
           Heater  |----S
                   |    |
           Tool    T0   T1
		   Nozzle  N1   N2

Single Material Head, Single nozzle, Without valves: RBXDV-S1

   Mat Ext       Left Right
    1   E --------------|
                        |
                        |
           Heater       S
                        |
           Tool         T0
		   Nozzle       N1

Upgrading AutoMaker
===================

From version 4.00.02, AutoMaker loads the URL

    https://downloads.cel-uk.com/software/update/0abc523fc24/AutoMaker-update.xml

to determine if there is an update available for 64 bit windows and non-windows platforms. For 32-bit windows it loads

   https://downloads.cel-uk.com/software/update/0abc523fc24_x86/AutoMaker-update.xml

Earlier versions of AutoMaker load the URL

   http://www.cel-robox.com/wp-content/uploads/Software/AutoMaker-update.xml

to determine if there is an update available for 64 bit windows and non-windows platforms. For 32-bit windows earlier versions load

   http://www.cel-robox.com/wp-content/uploads/Software/x86/AutoMaker-update.

The contents specifies the version, and the filenames and url from which to download the upgrade. it looks something like this:

    <installerInformation>
        <versionId>40001</versionId>
        <version>4.00.01</version>
        <platformFileList>
            <platformFile>
                <filename>AutoMaker-4.00.01-windows-x64-installer.exe</filename>
                <platform>windows</platform>
            </platformFile>
            <platformFile>
                <filename>AutoMaker-4.00.01-osx-installer.dmg</filename>
                <platform>osx</platform>
            </platformFile>
            <platformFile>
                <filename>AutoMaker-4.00.01-linux-installer.run</filename>
                <platform>linux</platform>
            </platformFile>
            <platformFile>
                <filename>AutoMaker-4.00.01-linux-x64-installer.run</filename>
                <platform>linux-x64</platform>
            </platformFile>
        </platformFileList>
        <downloadLocationList>
            <downloadLocation>
                <url>http://downloads.cel-uk.com/software/automaker/</url>
            </downloadLocation>
        </downloadLocationList>
    </installerInformation>

If an upgrade is available, AutoMaker starts a helper program AutoMaker-update-windows.exe (or something similar on other platforms) to download and install the upgrade. This program is generated by the install builder, based on the contents the file

	Installer\AutoMaker\AutoMaker_update.xml

It reads the file update.ini from the install directory to find out the version and url from which to download. The contents looks something like this:

    [Update]
    url = https://downloads.cel-uk.com/software/update/0abc523fc24/AutoMaker-update.xml
    version_id = 40001
    update_download_location = C:\Program Files\CEL/
    check_for_updates = 1
    [Proxy]
    enable = 0

Note that this is separate from the version read by AutoMaker itself from application.properties, also in the install directory. If AutoMaker says an update is available, but the update programs disagrees, then these two version numbers probably do not match.

The update executable was generated by the BitRock InstallBuilder. Documentation is here:

https://clients.bitrock.com/installbuilder/docs/installbuilder-userguide/ar01s23.html

Debug Log on Root
=================

To get DEBUG logging in Root, add the line

	log_level = DEBUG

to the file

	/home/pi/ARM-32bit/Root/application.properties

Disable desktop and login prompt on Raspberry Pi
================================================

To stop the lx desktop from starting, run raspi-config

    sudo raspi-config
    
Choose "Boot Options |Desktop / CLI | Console". To disable the login prompt that will appear on the screen run the following command:

    sudo systemctl disable getty@tty1.service
    
To enable the login prompt, run the following command:

    sudo systemctl enable getty@tty1.service

To suppress the message of the day, create a file .hushlogin in your home directory:

	touch ~/.hushlogin

Timelapse Camera on Raspberry Pi
================================

These are the control settings that can be set in a camera profile, which come from v4l2-ctl.

v4l2-ctl --list-ctrls
                     brightness (int)    : min=30 max=255 step=1 default=-8193 value=116
                       contrast (int)    : min=0 max=10 step=1 default=57343 value=5
                     saturation (int)    : min=0 max=200 step=1 default=57343 value=103
 white_balance_temperature_auto (bool)   : default=1 value=1
           power_line_frequency (menu)   : min=0 max=2 default=2 value=2
      white_balance_temperature (int)    : min=2500 max=10000 step=1 default=57343 value=4500 flags=inactive
                      sharpness (int)    : min=0 max=50 step=1 default=57343 value=25
         backlight_compensation (int)    : min=0 max=10 step=1 default=57343 value=0
                  exposure_auto (menu)   : min=0 max=3 default=0 value=3
              exposure_absolute (int)    : min=1 max=10000 step=1 default=156 value=156 flags=inactive
                   pan_absolute (int)    : min=-529200 max=529200 step=3600 default=0 value=0
                  tilt_absolute (int)    : min=-432000 max=432000 step=3600 default=0 value=0
                 focus_absolute (int)    : min=0 max=40 step=1 default=57343 value=0
                     focus_auto (bool)   : default=1 value=0
                  zoom_absolute (int)    : min=0 max=317 step=1 default=57343 value=0
					 
Useful options for fswebcam:

	--delay <delay>
		Inserts a delay after the source or device has been opened and initialised, and before the capture begins. Some devices need this delay to let the image settle after a setting has changed. The delay time is specified in seconds.

	--flip <direction[,direction]>
		Flips the image. Direction can be (h)orizontal or (v)ertical. Example:
			--flip h Flips the image horizontally.
			--flip h,v Flips the image both horizontally and vertically.

	--crop <dimensions[,offset]>
		Crop the image. With no offset the cropped area will be the center of the image. Example:
		--crop 320x240 Crops the center 320x240 area of the image.
		--crop 10x10,0x0 Crops the 10x10 area at the top left corner of the image.

	--scale <dimensions>
		Scale the image.
		Example: "--scale 640x480" scales the image up or down to 640x480.
		Note: The aspect ratio of the image is not maintained.

	--rotate <angle>
		Rotate the image in right angles (90, 180 and 270 degrees).
		Note: Rotating the image 90 or 270 degrees will swap the dimensions.

	--deinterlace
		Apply a simple deinterlacer to the image.

	--invert
		Invert all the colours in the image, creating a negative.

	--greyscale
		Remove all colour from the image.

List of USB Cams that have been used on Raspberry Pi:

		https://elinux.org/RPi_USB_Webcams

Using pishrink to create smallest, self-expanding Raspian image:
================================================================

To shrink an image (creating a new image RoboxRoot-4.01.02-Buster5Gb.img.gz) - note this takes a lot of space!

	sudo ./pishrink.sh -vz RoboxPro-4.01.02-Buster.img

