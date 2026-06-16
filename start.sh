#!/bin/bash
# Primary English Vocabulary Learning System 啟動腳本
pkill -9 -f "java.*primary-english" >/dev/null 2>&1
APP_NAME="primary-english"
JAR_FILE="primary-english-1.0.0.jar"
APP_HOME="$(cd "$(dirname "$0")" && pwd)"
LOG_FILE="$APP_HOME/app.log"
PID_FILE="$APP_HOME/app.pid"

# 設定 JVM 參數
JAVA_OPTS="-Xms256m -Xmx512m"
JAVA_OPTS="$JAVA_OPTS --enable-native-access=ALL-UNNAMED"
JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF-8"

# 設定 Spring Boot 參數（可選）
SPRING_OPTS="--server.port=8077"

cd "$APP_HOME" || exit 1

echo "啟動 $APP_NAME ..."
echo "JAR: $JAR_FILE"
echo "Log: $LOG_FILE"

nohup java $JAVA_OPTS -jar "target/$JAR_FILE" $SPRING_OPTS > "$LOG_FILE" 2>&1 &
PID=$!
echo $PID > "$PID_FILE"

echo "應用程式已啟動 (PID: $PID)"
echo "監聽日誌: tail -f $LOG_FILE"
