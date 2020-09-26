# AutoMaker
AutoMaker is the software interface used for interacting with the Robox 3D printers.
## Building AutoMaker
AutoMaker uses [Maven](http://maven.apache.org/download.cgi). You will either want to build the project directly using the maven command line, or use and IDE such as [Netbeans](http://netbeans.apache.org/download/index.html).

You will need JDK of 11.0.2 or later that contains a version of JavaFX 11.0.2 or later e.g. [BellSoft Liberica 11.0.2](https://bell-sw.com/pages/java-11.0.2/)

AutoMaker depends on several other projects to run. Each can be cloned from GitHub:

	git clone https://github.com/celsworthy/Configuration.git
	git clone https://github.com/celsworthy/Stenographer.git
	git clone https://github.com/celsworthy/Licence.git
	git clone https://github.com/celsworthy/Language.git
	git clone https://github.com/celsworthy/RoboxBase.git
	git clone https://github.com/celsworthy/CELTechCore.git
	
The projects should be built in the following order:

	Configuration
	Stenographer
	Licence
	Language
	RoboxBase
	CELTechCore
	AutoMaker

### Maven Build
To build using maven
	
	cd <Project directory containing pom.xml>
	mvn clean install

Maven will build each jar and put it into a target folder within each directory as well as in the local maven repository.

### Netbeans Build
Import each project into netbeans, set the compile java platform for each project to the same JDK. Clean and build each project in order.

## Running AutoMaker
You will need to have the install files of AutoMaker in place in order to run the software, the recommended method is to simply install the latest version of [AutoMaker](http://cel-robox.com/downloads)

### Running from Maven
AutoMaker needs a VM system property "libertySystems.configFile" set to the full path of the AutoMaker.configFile.xml file, which can be found in the AutoMaker repository. So in order to run the AutoMaker.jar from the command line use:
 
	java -DlibertySystems.configFile=<Path to repository>\AutoMaker\AutoMaker.configFile.xml -jar AutoMaker.jar

### Running/Debugging from Netbeans
The config file needs to be specified as a VM argument. Go to project *properties > Run* and add ```-DlibertySystems.configFile=<Path to repository>\AutoMaker\AutoMaker.configFile.xml``` to the VM arguments. You can then either run/debug the AutoMaker project.
