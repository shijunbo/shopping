#!/bin/bash
DIR=$(dirname $(cd `dirname $0`;pwd))
pid=$(ps uxf|grep ${DIR}/${pom.build.finalName}-server.jar|grep -v grep|awk '{print $2}')
if [ -z $pid ]; then
    echo "${pom.build.finalName}-server is not running"
else
    kill $pid
    echo "${pom.build.finalName}-server is shutting down"
    rm pid
fi
