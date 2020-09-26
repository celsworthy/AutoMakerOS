#!/bin/bash
# If checkForUpgrade.sh returns a non zero exit code, it upgraded root and the service needs to restart.
# This is done by simply exiting, and letting the service manager restart the service.
if ./checkForRootUpgrade.sh; then
	java/bin/java -Dglass.platform=Monocle -Dmonocle.platform=Headless -Djava.net.preferIPv4Stack=true -DlibertySystems.configFile=Root.configFile.xml -jar Root.jar server Root.yml
fi


