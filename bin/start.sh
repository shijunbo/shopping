#!/bin/bash
DIR=$(dirname $(cd `dirname $0`;pwd))
pid=$(ps uxf|grep ${DIR}/${pom.build.finalName}-server.jar|grep -v grep|awk '{print $2}')
if [ -z $pid ]; then
    nohup $JAVA_HOME_7/bin/java -Xms512m -Xmx512m \
    -server \
    -XX:+AggressiveOpts \
    -XX:+UseParNewGC \
    -XX:+UseConcMarkSweepGC \
    -XX:+CMSParallelRemarkEnabled \
    -XX:+UseCMSCompactAtFullCollection \
    -XX:+UseFastAccessorMethods \
    -XX:SurvivorRatio=8 \
    -XX:MaxTenuringThreshold=1 \
    -XX:CMSInitiatingOccupancyFraction=75 \
    -XX:+UseCMSInitiatingOccupancyOnly \
    -XX:+HeapDumpOnOutOfMemoryError \
    -Xloggc:gc.log \
    -XX:+PrintGCDetails \
    -XX:+PrintGCTimeStamps \
    -XX:+PrintGCApplicationStoppedTime \
    -XX:+PrintGCApplicationConcurrentTime \
    -XX:+CMSClassUnloadingEnabled \
    -XX:+CMSPermGenSweepingEnabled \
    -XX:PermSize=128M -XX:MaxPermSize=512m \
    -Dlogback.configurationFile=${DIR}/conf/logback.xml \
    -jar ${DIR}/${pom.build.finalName}-server.jar >/dev/null 2>&1 &
    echo $! > pid
    echo "${pom.build.finalName}-server started"
else
    echo "${pom.build.finalName}-server is already running"
fi
