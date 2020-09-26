# Installer
The installer repository contains the required files to build the install packages for [AutoMaker](https://github.com/celsworthy/AutoMaker) and [RoboxRoot](https://github.com/celsworthy/RoboxRoot).

> Note: The information on the files in the Installer repository is often refering to the copies of these files in the AutoMaker installation or the Root installation. In order to test changes to these files in a development environment it is often useful to edit the files within the installation directory.

## AutoMaker
The AutoMaker directory contains files for the AutoMaker installation directory, some files are OS specific and are sorted by the install builder.

## Common
The common directory contains several other directories

#### bin
Various scripts that AutoMaker makes use of for, detecting printers and killing the external slicing process.

#### Filaments
```.roboxfilamet``` files that contain the configuration information for particular filaments.

#### Heads
```.roboxhead``` files that contain the configuration information for particular heads

#### Language
Various language files. A particular translation string found within the code will map to these files, which file depends on either the default system language or the languge the user has selected from the settings within AutoMaker.

#### Macros
These are the macros used to instruct the printers to perform various routines such as homing the print head or purging.

#### Models
Some example files including the STLs for the Robox Robot.

#### Printers
```.roboxprinter``` files that contain configuration information for the robox printers.

#### PrintProfiles
Print profile settings information. There is a few things going on here which is described <SOMEWHERE>

#### windows_drivers
The windows driver files for USB to printer connection.
