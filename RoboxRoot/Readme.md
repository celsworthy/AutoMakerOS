Root is an http server for remotely controlling Robox 3D printers. It is intended to run on a Raspberry Pi with an optional 800x480 touch screen, but can be run on another platform, such as Windows 10.

Building Root
=============

Root depends on a number of other projects. Download each project from GitHub:

    git clone https://github.com/celsworthy/Configuration.git
    git clone https://github.com/celsworthy/Stenographer.git
    git clone https://github.com/celsworthy/Licence.git
    git clone https://github.com/celsworthy/Language.git
    git clone https://github.com/celsworthy/RoboxBase.git
    git clone https://github.com/celsworthy/RoboxRoot.git
    git clone https://github.com/celsworthy/GRoot.git
    git clone https://github.com/celsworthy/Installer.git

For development and debugging, use the NetBeans IDE (v9 or later).

Open all the projects except Installer in Netbeans. Set the compile java platform for each project to the same JDK, which must be at least version 11.0.2 and include JavaFX 11.0.2 or later.

Build the projects in the following order:

    Configuration
    Stenographer
    Licence
    Language
    RoboxBase
    RoboxRoot
    GRoot
	
Each project can be built from the command line using Maven:

    cd <Project directory containing pom.xml>
	set JAVA_HOME=<Path to JDK>
    mvn clean install

Debugging Root
==============

It is possible to debug Root by running it in NetBeans, but it requires the location of the Root installation to be specified the file RoboxRoot.configFile.xml:

	<FakeInstallDirectory>C:\Program Files\CEL\Root/</FakeInstallDirectory>

The structure of the installation is as follows:

	%CEL_HOME%
	    \Common
	        \bin
	        \Filaments
	        \Heads
	        \Language
	        \Macros
	        \Printers
	        \PrintProfiles
	    \Root

Installing AutoMaker will create the contents of the common directory.

To create the contents of the Common directory manually, copy the required directories from the Installer project. On linux and macos, , rename the appropriate shell scripts, removing the platform name and give the scripts execute permissions. On Linux:

	mv KillCuraEngine.linux.sh KillCuraEngine.sh
	mv RoboxDetector.linux.sh RoboxDetector.sh
	rm *.macos.* *.dll
	chmod ug+x *.sh

On MacOS:

	mv KillCuraEngine.macos.sh KillCuraEngine.sh
	mv RoboxDetector.macos.sh RoboxDetector.sh
	rm *.linux.* *.dll
	chmod ug+x *.sh

Either copy

    %CEL_HOME%\AutoMaker\application.properties
	
to

    %CEL_HOME%\Root\application.properties

or create the latter file containing text:

	version = <version number>
	
where <version number> is replaced by the current version, e.g.

	version = 4.01.00


To set the logging level of Root to DEBUG, add the following to the Root application.properties file:

	log_level = DEBUG
	
Access Root from a browser with the following URL:

	http://localhost:8080/home.html

Creating The Root Install Package
=================================

The Root install package is a zip file containing everything needed to run Root on a Raspberry Pi. Root itself can be built and run (and hence debugged) on Windows, but the install package is best built on a linux machine, to avoid problems in scripts caused by Windows-style line endings.

If obfuscation by Proguard is required, run the following:

	echo Run Proguard
	cd ${INSTALLER_HOME}
	${PROGUARD_HOME}/bin/proguard.sh @Root/Root_proguard.config
    cp Root/rootObfuscated.jar ../Root/target/Root.jar;
    cp Root/Licence-1.1Obfuscated.jar ../Root/target/lib/Licence-1.1.jar';
    cp Root/RoboxBase-1.3Obfuscated.jar ../Root/target/lib/RoboxBase-1.3.jar;
    cp Root/Configuration-1.3Obfuscated.jar ../Root/target/lib/Configuration-1.3.jar;
    cp Root/Stenographer-1.10Obfuscated.jar ../Root/target/lib/Stenographer-1.10.jar;
    cp proguard.mapping proguard.mapping.Root.${RELEASE_VERSION}

where the environment variable PROGUARD_HOME is set to the install directory for proguard. (Sed is used to modify some directory paths in Root_proguard.config, which is intended to be run by Jenkins, which, for historical reasons, checks out the Root project into the directory "application" instead of "Root".)

GRoot should have been built and copied into the install project directory. (See the GRoot project ReadMe.md for details.)
Once built, the Root installation package is built by the following script:

	cd ${INSTALLER_HOME}/Root
	./createRootPackages.sh ${RELEASE_VERSION} 

where the environment variable INSTALLER_HOME is set to the directory of the Installer project, and RELEASE_VERSION is set to a version number:

    export INSTALLER_HOME=/dev/Installer
    export RELEASE_VERSION=4.01.00

Installing Root on a Raspberry Pi
=================================

To install Root on a clean version of Raspian, transfer the install package to /home/pi on the Raspberry Pi. Unzip it:

	cd /home/pi
	unzip -o RootArm-32bit-4.01.00.zip

Run the upgrade script (note that the RPi will reboot):

	ARM-32bit/Root/upgrade.sh

Install the Root service:

	ARM-32bit/Root/installRoot.sh

To upgrade an existing Root installation, transfer the install package to /tmp on the Raspberry Pi. Restart Root without rebooting (which would empty the /tmp folder):

	/home/pi/ARM-32bit/Root/restartRoot.sh

The RPi should upgrade and restart.


	
