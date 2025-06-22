#!/bin/bash

cd `dirname $0`

APP_NAME=homewk-trans
ENV=local

# 检查logs目录是否存在，如果不存在则创建
if [ ! -d "logs" ]; then
    mkdir -p logs
fi

STDOUT_FILE=logs/stdout.log


echo -e "\n================ Time: `date '+%Y-%m-%d %H:%M:%S'` ================"
APP_PID=`ps -ef -ww | grep "java" | grep "$APP_NAME" | awk '{print $2}'`
if [ -n "$APP_PID" ]; then
    echo "INFO: The $APP_NAME already started or maybe is starting!"
    echo "PID: $APP_PID"
    exit 0
fi


# JVM_MEM_OPTS="-XX:ParallelGCThreads=3 -Xms3072m -Xmx6144m -XX:MaxPermSize=512m"

echo  "java $JVM_MEM_OPTS -jar homewk-trans-*.jar   --spring.profiles.active=$ENV"
nohup  java $JVM_MEM_OPTS -jar homewk-trans-*.jar   --spring.profiles.active=$ENV > $STDOUT_FILE 2>&1 &

echo  "Starting the $APP_NAME ..."
sleep 3


APP_PID=`ps -ef -ww | grep "java" | grep "$APP_NAME" | awk '{print $2}'`
if [ -z "$APP_PID" ]; then
    echo "START APP FAIL!"
    exit 1
fi
