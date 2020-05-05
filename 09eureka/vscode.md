# VScode远程调试linux

## 一、课程目标
掌握VScode远程调试环境搭建
掌握linux基础语法(不展开，自学)

## 二、VScode调试环境搭建-以图为主快速入门
### 扩展插件清单一览

![插件](https://github.com/coffeeliuwei/boot/blob/master/img/52.jpg?raw=true)

### 与linux建立远程连接
服务器安装SSH Server与客户端安装SSH Client，由于本实验环境已安装XShell所以此步骤略过。
使用`Ctrl+Shift+p`打开Remote-SSH:Add SSH Host连接linux服务器流程入下图：
![如图](https://github.com/coffeeliuwei/boot/blob/master/img/53.jpg?raw=true)

![如图](https://github.com/coffeeliuwei/boot/blob/master/img/54.jpg?raw=true)

打开config配置进行linux参数配置
![如图](https://github.com/coffeeliuwei/boot/blob/master/img/55.jpg?raw=true)

![如图](https://github.com/coffeeliuwei/boot/blob/master/img/56.jpg?raw=true)

设置完毕点击vscode左下角打开远程端口，输入登录密码登录linux。
打开远程文件夹，切换至所需目录如图：

![如图](https://github.com/coffeeliuwei/boot/blob/master/img/57.jpg?raw=true)

在VScode中通过`运行-打开配置`对所需项目进行启动配置
启动配置文件launch.json代码：
```
{
// 使用 IntelliSense 了解相关属性。 
// 悬停以查看现有属性的描述。
// 欲了解更多信息，请访问: https://go.microsoft.com/fwlink/?linkid=830387
"version": "0.2.0",
"configurations": [
	{
		"type": "bashdb",
		"request": "launch",
		"name": "Bash-Debug (select script from list of sh files)",
		"cwd": "${workspaceFolder}",
		"program": "${command:SelectScriptName}",
		"args": ["${input:arg1}","${input:arg2}"]
	}
],
"inputs": [
	{
	  "type": "pickString",
	  "id": "arg1",
	  "description": "What type of component do you want to create?",
	  "options": [
		"start",
		"stop",
		"restart"
	  ],
	  "default": "start"
	},
	{
		"type": "pickString",
		"id": "arg2",
		"description": "What type of component do you want to create?",
		"options": [
		  "1",
		  "2",
		 ""
		],
		"default":""
	  }
  ]
}
```
功能说明：
+ 在"configurations"中添加"type": "bashdb"启动bash-debug配置模块
+ 启动调试选择脚本功能 "program": "${command:SelectScriptName}"
+ 启动函数输入双参数设置 "args": ["\$\{input:arg1}","\$\{input:arg2}"]
+ 使用inputs设置arg1与arg2的详细配置， "type": "pickString"在运行期启动选择列表。
+ 具体参数含义请查看微软文档

## 三、编辑代码及调试

#### server.sh代码清单
```
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
```
#### 调试

![调试界面](https://github.com/coffeeliuwei/boot/blob/master/img/58.jpg?raw=true)
同时可通过下方`终端`直接与linux进行命令交互


