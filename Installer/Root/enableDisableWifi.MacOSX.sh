#!/bin/bash
command=off

if [ $1 == 'on' ]
then
   command=on 
fi

networksetup -setairportpower en0 $command
