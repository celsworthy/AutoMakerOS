if [ $# -ne 1 -o "$1" = "" ]; then
	echo "ERROR - Wrong number of arguments"
else
	comport=`ioreg -p IOService -n $1 -rl | grep IOCalloutDevice | sed 's/[^\/]*\([^\"]*\).*/\1/'`

	if [ "$comport" = "" ]; then
        	echo "NOT_CONNECTED"
	else
        	echo $comport
	fi
fi
