#!/bin/bash
# 
# Upgrade script - run once after unpacking upgrade.
# This script runs the upgrade worker script,
# redirecting output to a log file, before tidying up and rebooting.
ROOT_HOME=/home/pi/ARM-32bit/Root
UPGRADE_SCRIPT=${ROOT_HOME}/upgrade_worker.sh
UPGRADE_LOG=${ROOT_HOME}/upgrade.log

cd ${ROOT_HOME}
if [[ -e ${UPGRADE_LOG} ]]
then
	rm -f ${UPGRADE_LOG}
fi
${UPGRADE_SCRIPT} >${UPGRADE_LOG} 2>&1

# Remove upgrade scripts and data.
rm -rf ${ROOT_HOME}/upgrade_data
rm -f ${ROOT_HOME}/upgrade.sh
rm -f ${ROOT_HOME}/upgrade_worker.sh

sudo reboot
