#!/bin/bash

cd `dirname $0`
cd ..

APP_NAME=homewk-trans

STDOUT_FILE=logs/stdout.log


PIDS=`ps -ef -ww | grep "java" | grep "$APP_NAME" | awk '{print $2}'`
if [ -z "$PIDS" ]; then
    echo "INFO: The $APP_NAME does not started!"
    exit 1
fi

echo -e "Stopping the $APP_NAME ...\c"
KILL_PS=""
for PID in $PIDS ; do
    kill $PID >/dev/null &
    KILL_PS="$! $KILL_PS"
done

# sleep 10s to recycle vm
sleep 10
for kPID in $KILL_PS; do
    wait $kPID
done

# force kill
for kPID in $PIDS; do
    kill -9 $kPID >/dev/null 2>&1
done

REMAIN_PIDS=`ps -ef -ww | grep "java" | grep "$APP_NAME" | awk '{print $2}'`
if [ -z "$REMAIN_PIDS" ]; then
    echo "OK!"
    echo "PID: $PIDS"
    exit 0
else
    echo "ERROR!"
    echo "PID: ${REMAIN_PIDS} is still alive."
    exit 1
fi
