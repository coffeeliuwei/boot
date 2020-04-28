## Eureka服务集群

### 一、学习目标
1. 搭建集群服务中心
2. 熟练操作linux系统

### 二、配置服务器
#### 代码沿用单服务不变

#### 配置properties文件
+ application-eureka1.profiles清单
```
# -----------------------------通用配置--------------------------------------------
# 应用名称，将会显示在Eureka界面的应用名称列
spring.application.name=eureka-server
# 应用端口，Eureka服务端默认为：8761
server.port=8761
# ------------------------eureka.server前缀的配置项--------------------------------
# 是否允许开启自我保护模式，缺省：true
# 当Eureka服务器在短时间内丢失过多客户端时，自我保护模式可使服务端不再删除失去连接的客户端
#eureka.server.enable-self-preservation = false
# Peer节点更新间隔，单位：毫秒
#eureka.server.peer-eureka-nodes-update-interval-ms =3000 
# Eureka服务器清理无效节点的时间间隔，单位：毫秒，缺省：60000，即60秒
#eureka.server.eviction-interval-timer-in-ms = 60000
#-------------------------eureka.instance前缀的配置项------------------------------
# 服务名，默认取 spring.application.name 配置值，如果没有则为 unknown
#eureka.instance.appname = eureka-server
# 实例ID
eureka.instance.instance-id = eureka1-server
# 应用实例主机名
eureka.instance.hostname=eureka1
# 客户端在注册时使用自己的IP而不是主机名，缺省：false
eureka.instance.prefer-ip-address = true
# 应用实例IP,若eureka.instance.prefer-ip-address为true并此选项没有的情况系统选择第一非环路IP
#eureka.instance.ip-address = 192.168.1.126
# 服务失效时间，失效的服务将被剔除。单位：秒，默认：90
#eureka.instance.lease-expiration-duration-in-seconds = 90
# 服务续约（心跳）频率，单位：秒，缺省30
#eureka.instance.lease-renewal-interval-in-seconds = 30
# 状态页面的URL，相对路径，默认使用 HTTP 访问，如需使用 HTTPS则要使用绝对路径配置，缺省：/info
#eureka.instance.status-page-url-path = /info
# 健康检查页面的URL，相对路径，默认使用 HTTP 访问，如需使用 HTTPS则要使用绝对路径配置，缺省：/health
#eureka.instance.health-check-url-path = /health
#---------------------------http basic安全认证---------------------------------------
security.basic.enabled=true  
security.user.name=user
security.user.password=111111
# ---------------------------eureka.client前缀------------------------------------
#设置服务注册中心地址,指向另一个注册中心
eureka.client.serviceUrl.defaultZone=http://${security.user.name}:${security.user.password}@eureka2:${server.port}/eureka/
```
+ application-eureka2.profiles清单
```
# -----------------------------通用配置--------------------------------------------
# 应用名称，将会显示在Eureka界面的应用名称列
spring.application.name=eureka-server
# 应用端口，Eureka服务端默认为：8761
server.port=8761
# ------------------------eureka.server前缀的配置项--------------------------------
# 是否允许开启自我保护模式，缺省：true
# 当Eureka服务器在短时间内丢失过多客户端时，自我保护模式可使服务端不再删除失去连接的客户端
#eureka.server.enable-self-preservation = false
# Peer节点更新间隔，单位：毫秒
#eureka.server.peer-eureka-nodes-update-interval-ms =3000 
# Eureka服务器清理无效节点的时间间隔，单位：毫秒，缺省：60000，即60秒
#eureka.server.eviction-interval-timer-in-ms = 60000
#-------------------------eureka.instance前缀的配置项------------------------------
# 服务名，默认取 spring.application.name 配置值，如果没有则为 unknown
#eureka.instance.appname = eureka-server
# 实例ID
eureka.instance.instance-id = eureka2-server
# 应用实例主机名
eureka.instance.hostname=eureka2
# 客户端在注册时使用自己的IP而不是主机名，缺省：false
#eureka.instance.prefer-ip-address = true
# 应用实例IP,若eureka.instance.prefer-ip-address为true并此选项没有的情况系统选择第一非环路IP
#eureka.instance.ip-address = 192.168.1.127
# 服务失效时间，失效的服务将被剔除。单位：秒，默认：90
#eureka.instance.lease-expiration-duration-in-seconds = 90
# 服务续约（心跳）频率，单位：秒，缺省30
#eureka.instance.lease-renewal-interval-in-seconds = 30
# 状态页面的URL，相对路径，默认使用 HTTP 访问，如需使用 HTTPS则要使用绝对路径配置，缺省：/info
#eureka.instance.status-page-url-path = /info
# 健康检查页面的URL，相对路径，默认使用 HTTP 访问，如需使用 HTTPS则要使用绝对路径配置，缺省：/health
#eureka.instance.health-check-url-path = /health
#---------------------------http basic安全认证---------------------------------------
security.basic.enabled=true  
security.user.name=user
security.user.password=111111
# ---------------------------eureka.client前缀------------------------------------
#设置服务注册中心地址,指向另一个注册中心
eureka.client.serviceUrl.defaultZone=http://${security.user.name}:${security.user.password}@eureka1:${server.port}/eureka/
```
如有更多中心可以此方式继续拓展形成集群网络
#### 对项目打包
![打jar包](https://github.com/coffeeliuwei/boot/blob/master/img/40.jpg?raw=true)

### 发布Eureka服务(centos版本)
+ 对多台服务器分别配置静态网络
```
cd /etc/sysconfig/network-scripts/
vi ifcfg-ens33
///在ifcfg-ens33文件中配置如下选项
//多台服务器请配置不同IP
BOOTPROTO=static
ONBOOT=yes
IPADDR=192.168.1.126
NETMASK=255.255.255.0
GETWAY=192.168.1.1
DNS1=114.114.114.114
```

+ 安装java环境

`yum list java*`
+ 选择需要的版本安装

`yum -y install java-1.8.0-openjdk-headless.x86_64`
+ 检查版本

`java -version`
+ 在服务器创建eureka目录

```
cd /
ls
cd usr
mkdir eureka
cd eureka
```

![拷jar包](https://github.com/coffeeliuwei/boot/blob/master/img/39.jpg?raw=true)

同样方法建第二台服务器
+ 安装vim/vi编辑器

`yum install vim`
+ 两台服务器同时修改主机名

```
vim /etc/hosts
//添加两个主机名
192.168.1.126 eureka1
192.168.1.127 eureka2
```
+ 查看是否修改成功

`cat /etc/hosts`
+ 安装perl等编译环境(可选)

```
yum install wget
yum install gcc
yum install perl* 
yum install cpan
wget http://www.cpan.org/src/5.0/perl-5.16.1.tar.gz
tar -zxvf perl-5.16.1.tar.gz
./Configure -des -Dprefix=/usr/local/perl
make
make test
make install
```
+ 配置批处理文件

`vi server.sh`
批处理文件代码与上一节课基本相同
将`SPRING_PROFILES_ACTIV="-Dspring.profiles.active=eureka1"`注释去掉其中192.168.1.126服务器指向eureka1配置文件，192.168.1.127服务器指向eureka2配置文件。以启动不同配置。
+ 添加可执行权限
`chmod -R 755 server.sh`
+ 启动服务
`./server.sh start`
### 执行结果
![eureka1](https://github.com/coffeeliuwei/boot/blob/master/img/41.jpg?raw=true)

![eureka2](https://github.com/coffeeliuwei/boot/blob/master/img/42.jpg?raw=true)
注意eureka.instance.prefer-ip-address = true对服务器名的影响