#!/bin/sh
SERVER_NAME=dingtalk
nohup java -jar ../lib/$SERVER_NAME.jar > ../logs/console.log 2>&1 &
