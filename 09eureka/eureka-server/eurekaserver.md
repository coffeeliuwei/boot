## Eureka简介及快速搭建单服务
### 学习目标
1. 搭建单服务中心
2. 熟练操作linux系统

### 一、简介

![如图设置](https://github.com/coffeeliuwei/boot/blob/master/img/38.jpg?raw=true)

#### 什么是Eureka
Eureka是一项基于REST（代表性状态转移）的服务，主要在AWS云中用于定位服务，以实现负载均衡和中间层服务器的故障转移。我们称此服务为Eureka Server。Eureka还带有一个基于Java的客户端组件Eureka Client，它使与服务的交互更加容易。客户端还具有一个内置的负载均衡器，可以执行基本的循环负载均衡。在Netflix中更复杂的负载均衡器将Eureka包装起来，以基于流量，资源使用，错误条件等多种因素提供加权负载均衡，以提供出色的弹性。

#### Eureka的作用
在AWS云中，由于其固有的性质，服务器来来往往。与使用具有已知IP地址和主机名的服务器的传统负载均衡器不同，在AWS中，负载均衡需要在使用负载均衡器动态注册和注销服务器时更加复杂。由于AWS尚未提供中间层负载均衡器，因此Eureka填补了中间层负载均衡领域的巨大空白。

#### Eureka包含两个组件：Eureka Server和Eureka Client。
+ 调用关系：
  1. 服务提供者在启动时，向注册中心注册自己提供的服务。
  2. 服务消费者在启动时，向注册中心订阅自己所需的服务。
  3. 注册中心返回服务提供者地址给消费者。
  4. 服务消费者从提供者地址中调用消费者。
+ Eureka Server：提供服务注册服务，各个节点启动后，会在Eureka Server中进行注册，包括主机与端口号、服务版本号、通讯协议等。这样EurekaServer中的服务注册表中将会存储所有可用服务节点的信息，服务节点的信息可以在界面中直观的看到。
+ Eureka服务端支持集群模式部署，首尾相连形成一个闭环即可，集群中的的不同服务注册中心通过异步模式互相复制各自的状态，这也意味着在给定的时间点每个实例关于所有服务的状态可能存在不一致的现象。
+ Eureka客户端，主要处理服务的注册和发现。客户端服务通过注册和参数配置的方式，嵌入在客户端应用程序的代码中。在应用程序启动时，Eureka客户端向服务注册中心注册自身提供的服务，并周期性的发送心跳来更新它的服务租约。同时，他也能从服务端查询当前注册的服务信息并把它们缓存到本地并周期行的刷新服务状态。
+ 服务调用,服务消费者在获取服务清单后，通过服务名可以获取具体提供服务的实例名和该实例的元数据信息。因为有这些服务实例的详细信息，所以客户端可以根据自己的需要决定具体调用哪个实例，在Ribbon中会默认采用轮询的方式进行调用，从而实现客户端的负载均衡。

### 二、单服务中心搭建

#### 在coffee-boot总项目pom文件中添加Eureka依赖
```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-dependencies</artifactId>
	<version>Hoxton.SR4</version>
	<type>pom</type>
	<scope>import</scope>
</dependency>
```
由于spring cloud版本更新很快，请注意版本选择。这里使用最新稳定版。

#### 在总项目下建立Eureka子模块并添加启动服务依赖
```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    <version>2.2.2.RELEASE</version>
</dependency>
```
本案例通过对以上两个pom配置对Eureka项目进行版本控制。也可直接在总项目下建立各Eureka项目，以上两项配置直接配置在总项目中。或者直接将配置分别写在各Eureka项目中。

#### 通过maven建立Eureka-server单服务子工程
对application.properties进行全局配置
```
spring.application.name=eureka-server
server.port=8761

#是否将自己注册到eureka-server 默认为true
eureka.client.registerWithEureka=false 
#是否从eureka-server获取注册信息，默认为true
eureka.client.fetchRegistry=false
```
以上配置关闭服务发现功能使得服务成为单服务

#### 为项目添加日志功能
在resources文件夹中添加logback.xml文件
```
<?xml version="1.0" encoding="UTF-8"?>
<!--默认每隔一分钟扫描此配置文件的修改并重新加载-->
<configuration>
    <!--定义日志文件的存储地址 勿在LogBack的配置中使用相对路径-->
    <property name="LOG_HOME" value="./logs"/>
    <!--控制台输出-->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!-- encoders are assigned the type
        ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->
        <encoder>
            <pattern>%date{yyyy-MM-dd HH:mm:ss.SSS}|%t|%highlight(%-5level)|%green(%X{threadId})|%cyan(%C{0}#%M:%L)|%msg%n</pattern>
        </encoder>
    </appender>
    <!--输出日志到文件中-->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_HOME}/info.log</file>
        <!--不输出ERROR级别的日志-->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>DENY</onMatch>
            <onMismatch>ACCEPT</onMismatch>
        </filter>
        <!--根据日期滚动输出日志策略-->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_HOME}/info.log.%d{yyyy-MM-dd}</fileNamePattern>
            <!--日志保留天数-->
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%date{yyyy-MM-dd HH:mm:ss.SSS}|%t|%-5level|%X{threadId}|%C{0}#%M:%L|%msg%n</pattern>
        </encoder>
    </appender>
    <!--错误日志输出文件-->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_HOME}/error.log</file>
        <!--只输出ERROR级别的日志-->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <!--根据日期滚动输出日志策略-->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_HOME}/error.log.%d{yyyy-MM-dd}</fileNamePattern>
            <!--日志保留天数-->
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%date{yyyy-MM-dd HH:mm:ss.SSS}|%t|%-5level|%X{threadId}|%C{0}#%M:%L|%msg%n</pattern>
        </encoder>
    </appender>
    <!--异步打印日志,任务放在阻塞队列中，如果队列达到80%，将会丢弃TRACE,DEBUG,INFO级别的日志任务，对性能要求不是太高的话不用启用-->
    <appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
        <queueSize>1000</queueSize>
        <!--设为0表示队列达到80%，也不丢弃任务,-1默认-->
        <discardingThreshold>-1</discardingThreshold>
        <!--日志上下文关闭后，AsyncAppender继续执行写任务的时间，单位毫秒-->
        <maxFlushTime>1000</maxFlushTime>
        <!--队列满了直接丢弃要写的消息-->
        <neverBlock>true</neverBlock>
        <!--是否包含调用方的信息，false则无法打印类名方法名行号等-->
        <includeCallerData>true</includeCallerData>
        <!--One and only one appender may be attached to AsyncAppender，添加多个的话后面的会被忽略-->
        <appender-ref ref="FILE"/>
    </appender>
    <appender name="ERROR_ASYNC" class="ch.qos.logback.classic.AsyncAppender">
        <queueSize>256</queueSize>
        <!--设为0表示队列达到80%，也不丢弃任务-->
        <discardingThreshold>-1</discardingThreshold>
        <!--日志上下文关闭后，AsyncAppender继续执行写任务的时间，单位毫秒-->
        <maxFlushTime>1000</maxFlushTime>
        <!--队列满了直接丢弃要写的消息，不阻塞写入队列-->
        <neverBlock>true</neverBlock>
        <!--是否包含调用方的信息，false则无法打印类名方法名行号等-->
        <includeCallerData>true</includeCallerData>
        <!--One and only one appender may be attached to AsyncAppender，添加多个的话后面的会被忽略-->
        <appender-ref ref="ERROR_FILE"/>
    </appender>

    <!--指定一些依赖包的日志输出级别,所有的logger会继承root，为了避免日志重复打印，需指定additivity="false",将不会继承root的append-ref-->
<!--    <logger name="com.xxx" level="ERROR" additivity="false">
        <appender-ref ref="STDOUT"/>
        &lt;!&ndash;<appender-ref ref="ERROR_FILE"/>&ndash;&gt;
    </logger>-->

    <root level="INFO">
        <!--<appender-ref ref="STDOUT"/>-->
      <!--   <appender-ref ref="FILE"/>
        <appender-ref ref="ERROR_FILE"/> -->
        <!--使用异步打印日志-->
    	<appender-ref ref="ASYNC"/>
        <appender-ref ref="ERROR_ASYNC"/>
    </root>
</configuration>
```

#### 为主启动文件添加Eureka服务
```
@EnableEurekaServer
@SpringBootApplication
public class EurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaApplication.class, args);
	}
}
```
#### 打包jar文件

![如图设置](https://github.com/coffeeliuwei/boot/blob/master/img/28.jpg?raw=true)

![如图设置](https://github.com/coffeeliuwei/boot/blob/master/img/29.jpg?raw=true)

### 三、eureka发布-Ubuntu版本
+ 创建eureka目录

```
root@ip-172-31-20-54:/home/ubuntu# cd /
root@ip-172-31-20-54:/# ll
root@ip-172-31-20-54:/# cd /usr/
root@ip-172-31-20-54:/usr# ll
root@ip-172-31-20-54:/usr# mkdir eureka
root@ip-172-31-20-54:/usr# ll
```
+ 安装lrzsz进行文件上传如文件过大安装ftp服务

```
sudo apt-get install lrzsz
```
+ 安装jdk

```
javac
sudo apt install openjdk-8-jdk-headless
```
+ 修改环境变量

`sudo vim /etc/profile`
+ 增加参数：

```
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

export JRE_HOME=$JAVA_HOME/jre

export CLASSPATH=$JAVA_HOME/lib:$JRE_HOME/lib:$CLASSPATH

export PATH=$JAVA_HOME/bin:$JRE_HOME/bin:$PATH
```

![如图设置](https://github.com/coffeeliuwei/boot/blob/master/img/30.jpg?raw=true)

+ 查看版本，如显示版本则表示安装成功

`javac -version`

+ 修改eureka文件夹权限以便ftp上传

`chmod 777 eureka/`

+ 安装xftp服务（可选）

```
apt install vsftpd
service vsftpd status
service vsftpd start
```
+ 上传jar文件到eureka目录

![如图设置](https://github.com/coffeeliuwei/boot/blob/master/img/31.jpg?raw=true)

+ 上传server.sh文件到eureka目录中
脚本代码：

```
#!/bin/bash
cd `dirname $0`
CUR_SHELL_DIR=`pwd`
CUR_SHELL_NAME=`basename ${BASH_SOURCE}` 
JAR_NAME="eureka-server-1.0.0-SNAPSHOT.jar"
JAR_PATH=$CUR_SHELL_DIR/$JAR_NAME
#JAVA_MEM_OPTS=" -server -Xms1024m -Xmx1024m -XX:PermSize=128m"
JAVA_MEM_OPTS=""
#SPRING_PROFILES_ACTIV="-Dspring.profiles.active=eureka"
#SPRING_PROFILES_ACTIV=""
LOG_DIR=$CUR_SHELL_DIR/logs
LOG_PATH=$LOG_DIR/${JAR_NAME%..log
echo_help()
{
    echo -e "syntax: sh $CUR_SHELL_NAME start|stop"
}
 
if [ -z $1 ];then
    echo_help
    exit 1
fi
 
if [ ! -d "$LOG_DIR" ];then
    mkdir "$LOG_DIR"
fi
 
if [ ! -f "$LOG_PATH" ];then
    touch "$LOG_DIR"
fi
 
if [ "$1" == "start" ];then
 
    # check server
    PIDS=`ps --no-heading -C java -f --width 1000 | grep $JAR_NAME | awk '{print $2}'`
    if [ -n "$PIDS" ]; then
        echo -e "ERROR: The $JAR_NAME already started and the PID is ${PIDS}."
        exit 1
    fi
 
    echo "Starting the $JAR_NAME..."
 
    # start
    nohup java $JAVA_MEM_OPTS -jar $SPRING_PROFILES_ACTIV $JAR_PATH >> $LOG_PATH 2>&1 &
 
    COUNT=0
    while [ $COUNT -lt 1 ]; do
        sleep 1
        COUNT=`ps  --no-heading -C java -f --width 1000 | grep "$JAR_NAME" | awk '{print $2}' | wc -l`
        if [ $COUNT -gt 0 ]; then
            break
        fi
    done
    PIDS=`ps  --no-heading -C java -f --width 1000 | grep "$JAR_NAME" | awk '{print $2}'`
    echo "${JAR_NAME} Started and the PID is ${PIDS}."
    echo "You can check the log file in ${LOG_PATH} for details."
 
elif [ "$1" == "stop" ];then
 
    PIDS=`ps --no-heading -C java -f --width 1000 | grep $JAR_NAME | awk '{print $2}'`
    if [ -z "$PIDS" ]; then
        echo "ERROR:The $JAR_NAME does not started!"
        exit 1
    fi
 
    echo -e "Stopping the $JAR_NAME..."
 
    for PID in $PIDS; do
        kill $PID > /dev/null 2>&1
    done
 
    COUNT=0
    while [ $COUNT -lt 1 ]; do
        sleep 1
        COUNT=1
        for PID in $PIDS ; do
            PID_EXIST=`ps --no-heading -p $PID`
            if [ -n "$PID_EXIST" ]; then
                COUNT=0
                break
            fi
        done
    done
 
    echo -e "${JAR_NAME} Stopped and the PID is ${PIDS}."
else
    echo_help
    exit 1
fi

```

`chmod 755 server.sh`
赋予server.sh执行权限

![如图设置](https://github.com/coffeeliuwei/boot/blob/master/img/32.jpg?raw=true)

+ 如果在win机器上修改过server.sh则需在服务器上改变文件格式

```
vi server.sh
:set ff=unix
```
+ 启动服务

`./server.sh start`

+ 进入logs目录查看系统日志

```
cd logs/
tail server.log
```

![如图设置](https://github.com/coffeeliuwei/boot/blob/master/img/33.jpg?raw=true)

+ 放开亚马逊服务器8761端口

![如图设置](https://github.com/coffeeliuwei/boot/blob/master/img/34.jpg?raw=true)

+ 同时放开Ubuntu8761端口

```
 ufw allow 8761
 ufw reload
 ```
+ 发布完成

![如图设置](https://github.com/coffeeliuwei/boot/blob/master/img/35.jpg?raw=true)

 ### 四、作业
 独立完成Eureka服务器配置