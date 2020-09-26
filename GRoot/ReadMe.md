GRoot is graphical user interface for the Root http server for remotely controlling Robox 3D printers, intended to run on a Raspberry Pi with a 800x480 touch screen. It will, however, run on other platforms such as Windows 10. It is included in the Root install package.

Building GRoot
==============

Download the GRoot project from GitHub:

    git clone https://github.com/celsworthy/GRoot.git

For development and debugging, use the NetBeans IDE (v9 or later).

Open the project in Netbeans. Set the compile java platform to a JDK which must be at least version 11.0.2 and include JavaFX 11.0.2 or later. Build it!
	
GRoot can also be built from the command line using Maven:

	rem GROOT_HOME should be set the GRoot project directory.
	cd %GROOT_HOME%
	set JAVA_HOME=<Path to JDK>
    mvn clean install
	
Running GRoot
=============

The following command will run GRoot (assuming a suitable version of java is on the path):

	cd <Project directory containing pom.xml>
	java -Dcom.sun.javafx.virtualKeyboard="none" -jar ./target/GRoot.jar -h <rootHostName> -i ./ -u 1000

It accepts the following arguments:

	-c <path>
	--config-directory <path>
    Directory in which application configuration is saved (default: install directory)
	 
    -h <host name or IP address of Root server>
	--host <host name or IP address of Root server>
    Host name of Root server (default localhost)
	
    -i <path>
	--install-directory <path>
    Directory in which application is installed (default: ./)
	
    -l tag
    --language-tag tag
	tag to indicate the interface language (default: en)
	
    --pin PIN
	Access PIN of Root server

	-p port
	--port port
	Port number of Root server (default: 8080)

    -s
	--show-splash-screen
	Show splash screen during startup (default: no splash screen)

    -u milliseconds
	Update interval in milliseconds (default: 2000)

Installing GRoot
================

The following files should be copied into the Installer project:

Windows:

	rem INSTALLER_HOME should be set to the Installer project directory.
	cd %INSTALLER_HOME%\GRoot
	rmdir /S /Q Language
	rmdir /S /Q lib
	del GRoot.jar
	
	rem GROOT_HOME should be set to the GRoot project directory.
	cd %GROOT_HOME%
	robocopy Language %INSTALLER_HOME%\GRoot\Language /MIR
	robocopy target\lib %INSTALLER_HOME%\GRoot\lib /MIR
	copy target\GRoot.jar %INSTALLER_HOME%\GRoot\GRoot.jar

Linux:

	# INSTALLER_HOME should be set to the Installer project directory.
	cd ${INSTALLER_HOME}/GRoot
	rm -rf Language
	rm -rf lib
	rm GRoot.jar
	
	# GROOT_HOME should be set to the GRoot project directory.
	cd ${GROOT_HOME}
	cp -r Language ${INSTALLER_HOME}/GRoot/Language
	cp -r target/lib ${INSTALLER_HOME}/GRoot/lib
	cp target/GRoot.jar ${INSTALLER_HOME}/GRoot/GRoot.jar
