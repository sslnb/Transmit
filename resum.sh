#!/bin/bash


pid=`ps -ef | grep Transmit-0.0.1-SNAPSHOT.jar |grep -v color |grep -v grep | awk '{print $2}'`


echo "java pid is ${pid}"




if [ "${pid}" = "" ]
then
        echo "no java is alive"
else
        kill -s -9 ${pid}
fi


cd /home/app/java/bin
./startup.sh