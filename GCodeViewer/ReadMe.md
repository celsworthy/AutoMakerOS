GCodeViewer is GCode previewer with special features to support Robox 3D printers. It is used by AutoMaker, the Robox control software, to preview the results of slicing a model.

Building GCodeViewer
====================

GCodeViewer depends on two other projects, and is required for the AutoMaker installer. Download the projects from GitHub:

    git clone https://github.com/celsworthy/Configuration.git
    git clone https://github.com/celsworthy/Stenographer.git
    git clone https://github.com/celsworthy/GCodeViewer.git
    git clone https://github.com/celsworthy/Installer.git

For development and debugging, use the NetBeans IDE (v9 or later).

Open all the projects except Installer in Netbeans. Set the compile java platform for each project to the same JDK, which must be at least version 11.0.2 and include JavaFX 11.0.2 or later.

Build the projects in the following order:

    Configuration
    Stenographer
    GCodeViewer
	
Each project can be built from the command line using Maven:

    cd <Project directory containing pom.xml>
	set JAVA_HOME=<Path to JDK>
    mvn clean install
	
Running GCodeViewer
===================

The following command will run GCodeViewer (assuming a suitable version of java is on the path):

	cd %GCODE_VIEWER_HOME%
	java -DlibertySystems.configFile="GCodeViewerFake.configFile.xml" -jar ./target/GCodeViewer.jar [arguments] [gcode file]

It accepts the following command line arguments:

    -l <tag>
	--language-tag <tag>
	Tag to indicate the interface language. (Default: en)
    
	-xd <letter>
	--extruder-letter-d <letter>
	Letter to use as D extruder (Default: D)
	
    -xe <letter>
	--extruder-letter-e <letter>
	Letter to use as E extruder (Default: E)

	-nv <mode>
	--nozzle-valves <mode>
	Mode value of "ON" indicates that nozzle valves are present. A value of "OFF" indicates they are not present. (Default: OFF)
	
    -sa
	--show-advanced-options
	Flag to indicate advanced options should be shown (Default: false)
	
	-p <printer type>
	--printer-type <printer type>
	Printer type to define the printer volume. (Defalt: "RBX01")
	
    -pd <directory path>
	--project-directory <directory path>
	Directory in which the interface configuration is stored. (Default: .)
	
    -cd <directory path>
	--config-directory  <directory path>
	Directory in which the viewer configuration file is stored. (Default: .)
	
    -wt
	--always-on-top
	Flag to indicate window should always be above other desktop windows. (Default: false)
	
	-wc
	--centered
	Flag to indicate window should be centered on screen. (Default: false)
    
	-wd
	--decorated
	Flag to indicate window should be decorated with title, close box etc. (Default: true)
    
	-wh <height>
	--window-height <height>
	Window height on screen. (Default = half screen height)
	
    -wn
	--normalised-window
	Flag to indicate window sizes are normalised. (Default: false)
	
    -wr
	--resizeable
	Flag to indicate window should be resizable (Default: true)
	
    -ww <width>
	--window-width <width>
	Window width on screen. (Default: half screen width)
    
	-wx <x coord>
	--window-x <x coordinate>
	Window X position on screen (Default: centred in x)
	
    -wy <y coordinate>
	--window-y <y coordinate>
	Window Y position on screen. (Default: centred in y)
	
It uses three configuration files:

	GCodeViewer.configFile.xml
	Used to specify the language, location of language files and log files. The name of this file is specified by the Java system property "libertySystems.configFile" and is set with the command line option -DlibertySystems.configFile=<Path>.
	
	GCodeViewer.json
	Used to specify configuration parameters for the viewer such as printer types and colours. The location of this files is specified by the --config-directory command line option.
	
	GCodeViewerGUI.json
	Used to specify GUI parameters such as the expanded state of the panels. The location of this files is specified by the --project-directory command line option.

When run from the command line, GCodeViewer can be controlled from the terminal. When started, it responds with something like this:

	2020-02-03 15:28:45,876 [main] INFO - StenographerFactory initialised with logfile= ...
	Hello!
	
The viewer accepts the following commands on the terminal:

	bottom | b  <layer>
	Set bottom layer to render
	
	clear | cl
	Clear GCode, so viewer is showing nothing.
	
	colour | co  type | ty 
	Set colour mode to type.

	colour | co  tool | to   <red>  <green>  <blue>
	Set colour for tool.

	colour | co  data | d  a|b|d|e|f|x|y|z 
	Set colour mode to data from axis.

	extruder-letter | ex  D | E  <letter>
	Set the letter to use for the D or E extruder.

	first | fi  <line number>
	Set the first selected line number.
	
	focus | fo
	Set window focus to the GCodeViewer window.

	hide | h  angles | a | moves | m | stylus | s | window | w
	hide | h  tool | t  <tool number>
	Hide item

	iconify | i
	Iconfiy the GCodeViewer window.
	
	last | la  <line number>
	Set the first selected line number.

	load | lo <gcode file path>
	Load the specified GCode file.

	nozzle-valves | nv on | off
	Enable or disable nozzle valve display for B axis.
	
	printer | p  <printer>
	Specify the target printer, which must be one specified in the GCodeViewer.json configuration file.

	quit | q
	terminate the program.
	
	restore
	r
	Restore GCodeViewer window from iconised state.

	show | s  angles | a | moves | m | stylus | s | window | w
	show | s  tool | t  <tool number>
	Show item

	tool | to  <tool number> show | s | hide | h
	Show or hide tool
	
	tool | to  <tool number> colour | co  <red>  <green>  <blue>
	Set colour for tool.

	top | t  <layer number>
	Set top layer to render
	
Installing GCodeViewer into Installer Project
=============================================

Windows:

	rem INSTALLER_HOME should be set to the Installer project directory.
	cd %INSTALLER_HOME%\GCodeViewer
	rmdir /S /Q Language
	rmdir /S /Q lib
	del GCodeViewer.*
	
	rem GCODEVIEWER_HOME should be set to the GCodeViewer project directory.
	cd %GCODEVIEWER_HOME%
	robocopy Language %INSTALLER_HOME%\GCodeViewer\Language /MIR
	robocopy target\lib %INSTALLER_HOME%\GCodeViewer\lib /MIR
	copy target\GCodeViewer.jar %INSTALLER_HOME%\GCodeViewer\GCodeViewer.jar
	copy GCodeViewer.configFile.xml %INSTALLER_HOME%\GCodeViewer\GCodeViewer.configFile.xml
	copy GCodeViewer.json %INSTALLER_HOME%\GCodeViewer\GCodeViewer.json

Linux:

	# INSTALLER_HOME should be set to the Installer project directory.
	cd ${INSTALLER_HOME}/GCodeViewer
	rm -rf Language
	rm -rf lib
	rm GCodeViewer.*
	
	# GCODEVIEWER_HOME should be set to the GCodeViewer project directory.
	cd ${GCODEVIEWER_HOME}
	cp -r Language ${INSTALLER_HOME}/GCodeViewer/Language
	cp -r target/lib ${INSTALLER_HOME}/GCodeViewer/lib
	cp GCodeViewer.configFile.xml ${INSTALLER_HOME}/GCodeViewer/GCodeViewer.configFile.xml
	cp GCodeViewer.json ${INSTALLER_HOME}/GCodeViewer/GCodeViewer.json

Obfuscating with Proguard
=========================

If required, the GCodeViewer can be obfuscated with ProGuard.

Windows:

	# GCODEVIEWER_HOME should be set to the GCodeViewer project directory.
	# RELEASE_VERSION should be set to the latest version number, e.g. 4.01.00
	# PROGUARD_HOME should be set to the proguard install directory, e.g. /home/ubuntu/proguard
	cd %GCODE_VIEWER_HOME%
	.\gcodeviewer_proguard.bat

Linux:

	# GCODEVIEWER_HOME should be set to the GCodeViewer project directory.
	# RELEASE_VERSION should be set to the latest version number, e.g. 4.01.00
	# PROGUARD_HOME should be set to the proguard install directory, e.g. /home/ubuntu/proguard
	cd %GCODE_VIEWER_HOME%
	export RELEASE_VERSION=4.01.00
	export PROGUARD_HOME=/home/ubuntu/proguard
	./gcodeviewer_proguard.sh

THe script will create two directories:

	%GCODE_VIEWER_HOME%/proguard
	%GCODE_VIEWER_HOME%/release

The contents of the release directory should be copied to the installer GCodeViewer directory:

Windows:

	rem INSTALLER_HOME should be set to the Installer project directory.
	rem GCODEVIEWER_HOME should be set to the GCodeViewer project directory.
	rmdir /S /Q %INSTALLER_HOME%\GCodeViewer
	robocopy %GCODE_VIEWER_HOME%\Release %INSTALLER_HOME%\GCodeViewer /MIR

Linux:

	# INSTALLER_HOME should be set to the Installer project directory.
	# GCODEVIEWER_HOME should be set to the GCodeViewer project directory.
	rm -rf ${INSTALLER_HOME}/GCodeViewer
	cp -r ${GCODE_VIEWER_HOME}/release ${INSTALLER_HOME}/GCodeViewer /MIR


