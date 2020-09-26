#!/bin/bash
origindir=`pwd`
installerdir=$(dirname ${origindir})
parentdir=$(dirname ${installerdir})
applicationdir=${parentdir}/RoboxRoot

FINAL_BUILD_LABEL=$1

doPackage()
{
        applicationname=$1
        packagename=$2
        javaversion=$3

        echo ------------------------------------------------
        echo Creating package named ${packagename} for ${applicationname} at version ${FINAL_BUILD_NAME} using ${javaversion}
		echo User is `id`
        echo Origin dir is ${origindir}
        echo Installer dir is ${installerdir}
        echo Application dir is ${applicationdir}
        packagedir=${installerdir}/${packagename}
        echo "Package dir is " ${packagedir}
        mkdir -p ${packagedir}
        cd ${packagedir}

        #
        #Common files
        #
        mkdir -p ${packagedir}/Common
        mkdir -p ${packagedir}/Common/bin
        mkdir -p ${packagedir}/Common/Language

        for binFile in $4; do
            cp ${installerdir}/Common/bin/${binFile} ${packagedir}/Common/bin
        done

        for binFile in ${origindir}/*${packagename}*.sh; do
			binFileNoPath=$(basename $binFile)
			newFile=`echo $binFileNoPath | sed -e "s/${packagename}\.//"`
			cp $binFile ${packagedir}/Common/bin/$newFile
        done

        cp -R ${installerdir}/Common/Filaments ${packagedir}/Common
        cp -R ${installerdir}/Common/Heads ${packagedir}/Common
        cp -R ${installerdir}/Common/Language/NoUI*.properties ${packagedir}/Common/Language
        cp -R ${installerdir}/Common/Macros ${packagedir}/Common
        cp -R ${installerdir}/Common/Printers ${packagedir}/Common
        cp -R ${installerdir}/Common/PrintProfiles ${packagedir}/Common

        #
        # App-specific files
        #
        mkdir -p ${packagedir}/${applicationname}
        cp ${origindir}/${applicationname}.configFile.xml ${packagedir}/${applicationname}
        cp ${origindir}/${applicationname}.yml ${packagedir}/${applicationname}
        cp ${origindir}/run${applicationname}.sh ${packagedir}/${applicationname}
        cp ${origindir}/checkFor${applicationname}Upgrade.sh ${packagedir}/${applicationname}
        cp ${origindir}/restart${applicationname}.sh ${packagedir}/${applicationname}
        cp ${origindir}/install${applicationname}.sh ${packagedir}/${applicationname}
        cp ${origindir}/uninstall${applicationname}.sh ${packagedir}/${applicationname}
        cp ${origindir}/startBrowser.sh ${packagedir}/${applicationname}
        cp ${origindir}/swapTSAxes.sh ${packagedir}/${applicationname}
		cp ${origindir}/takePhoto.sh ${packagedir}/${applicationname}
		cp ${origindir}/takeSnapshot.sh ${packagedir}/${applicationname}
		cp ${origindir}/cameraDetector.sh ${packagedir}/${applicationname}
		cp ${origindir}/findCameraInfo.sh ${packagedir}/${applicationname}
        cp ${origindir}/cel.xbm ${packagedir}/${applicationname}
        cp -R ${origindir}/www ${packagedir}/${applicationname}
        cp ${applicationdir}/target/${applicationname}.jar ${packagedir}/${applicationname}
        cp -R ${applicationdir}/target/lib ${packagedir}/${applicationname}
		
		chmod ugo+x ${packagedir}/${applicationname}/*.sh
		
        mkdir -p ${packagedir}/${applicationname}/java
        cp -R /var/jenkins_home/java/javaDistros11/${javaversion}/* ${packagedir}/${applicationname}/java

		# Copy GRoot (Gui for Root) application
        cp -R ${installerdir}/GRoot ${packagedir}/GRoot
		chmod ug+x ${packagedir}/GRoot/*.sh
		# Generate md5 checksums for GRoot.
		md5sum ${packagedir}/GRoot/GRoot.jar ${packagedir}/GRoot/lib/*.jar > ${packagedir}/GRoot/GRoot.md5
		
		# Upgrade files
        cp ${origindir}/upgrade.sh ${packagedir}/${applicationname}
        cp ${origindir}/upgrade_worker.sh ${packagedir}/${applicationname}
        cp -R ${origindir}/upgrade_data ${packagedir}/${applicationname}

		# Set the execute permission of all the scripts and libraries.		
		find ${packagedir}/${applicationname} -regex ".*\.s[ho]" -exec chmod ug+x '{}' \;
		# Set permissions of executables.		
		find ${packagedir}/${applicationname} -regex ".*/bin/.*" -exec chmod ug+x '{}' \;
		# Set permissions of cpmount.
		find ${packagedir}/${applicationname}/upgrade_data/usb_mount -name 'cpmount' -exec chmod ug+x '{}' \;

        # Version number
        echo 'version = '${FINAL_BUILD_LABEL} > ${packagedir}/${applicationname}/application.properties

		# Generate md5 checksums.
		pushd ${packagedir}/${applicationname}
		md5sum Root.jar lib/*.jar > Root.md5
		popd
				
        cd ${installerdir}
        zipfilename=${applicationname}${packagename}-${FINAL_BUILD_LABEL}.zip
        zip -r ${zipfilename} ${packagename}
        mv ${zipfilename} ${installerdir}

        echo ------------------------------------------------
}

# Only build the RPi version.
doPackage Root ARM-32bit arm32-hflt/jdk-11.0.2 RoboxDetector.linux.sh
#doPackage Root Windows-x64 java-windows-x64 "RoboxDetector.exe msvcp100.dll msvcr100.dll"
#doPackage Root MacOSX java-osx RoboxDetector.mac.sh
#doPackage Root Linux-x86 java-linux RoboxDetector.linux.sh
#doPackage Root Linux-x64 java-linux-x64 RoboxDetector.linux.sh
