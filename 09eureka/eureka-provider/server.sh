#!/bin/bash
#cd $(dirname $0)
CUR_SHELL_DIR=$(pwd)
CUR_SHELL_NAME=$(basename ${BASH_SOURCE})
JAR_NAMES_STR="eureka-server-cluster-1.0.0-SNAPSHOT.jar,eureka-provider-1.0.0-SNAPSHOT.jar"
JAR_NAMES=(${JAR_NAMES_STR//,/ })
#JAVA_MEM_OPTS=" -server -Xms1024m -Xmx1024m -XX:PermSize=128m"
JAVA_MEM_OPTS=("" "")
SPRING_PROFILES_ACTIVS=(-Dspring.profiles.active=eureka1 "")

LOG_DIR=$CUR_SHELL_DIR/logs
echo_help() {
    echo -e "syntax: sh $CUR_SHELL_NAME start (1,2,null)|stop"
}

if [ -z $1 ]; then
    echo_help
    exit 1
fi

if [ ! -d "$LOG_DIR" ]; then
    mkdir -p "$LOG_DIR"
fi

startdo()
{
 # check server
        PIDS=$(ps --no-heading -C java -f --width 1000 | grep ${JAR_NAMES[$1-1]} | awk '{print $2}')
        if [ -n "$PIDS" ]; then
            echo -e "ERROR: The ${JAR_NAMES[$1-1]} already started and the PID is ${PIDS}."
            exit 1
        fi

        echo "${JAR_NAMES[$1-1]}开始启动----------------------"

        # 启动JAR_NAME
        nohup java ${JAVA_MEM_OPTS[$1-1]} -jar ${SPRING_PROFILES_ACTIVS[$1-1]} $CUR_SHELL_DIR/${JAR_NAMES[$1-1]} >>/dev/null 2>&1 &

        COUNT=0
        while [ $COUNT -lt 1 ]; do
            sleep 1
            COUNT=$(ps --no-heading -C java -f --width 1000 | grep "${JAR_NAMES[$1-1]}" | awk '{print $2}' | wc -l)
            if [ $COUNT -gt 0 ]; then
                break
            fi
        done
        PIDS=$(ps --no-heading -C java -f --width 1000 | grep "${JAR_NAMES[$1-1]}" | awk '{print $2}')
        echo "${JAR_NAMES[$1-1]} Started and the PID is ${PIDS}."
        echo "启动细节查看 $LOG_DIR/${JAR_NAMES[$1-1]}.log."

}
case "$1" in

start)
    if [ -n "$2" ]; then
        startdo $2
    else
     for(( i=0;i<${#JAR_NAMES[@]};i++)) 
 do 
 startdo i+1
 done
    fi
    ;;
stop)
       for(( i=0;i<${#JAR_NAMES[@]};i++)) 
 do 
 PID=$(ps -ef | grep -w $CUR_SHELL_DIR/${JAR_NAMES[i]} | grep -v "grep" | awk '{print $2}')
    if [ "$PID" == "" ]; then
        echo "${JAR_NAMES[i]}不存在，终止成功！"
    else
        kill -9 $PID
        echo -e "${JAR_NAMES[i]} 终止成功！ PID 为 ${PID}."
    fi
 done
    echo "所有程序终止成功！"
    ;;
esac
