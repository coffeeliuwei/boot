# ubuntu上安装RabbitMQ步骤
1. 安装erlang  
```
sudo apt-get install erlang-nox
```
2. 安装Rabbitmq  
```
sudo apt-get update
sudo apt-get install rabbitmq-server
```
3. 启动、停止、重启、状态rabbitMq命令

```
sudo rabbitmq-server start
 
sudo rabbitmq-server stop
 
sudo rabbitmq-server restart
 
sudo rabbitmqctl status
```
4. 防火墙设置

```
sudo ufw enable
sudo ufw allow 80
sudo ufw allow 22
sudo ufw allow 25672
sudo ufw allow 15672
sudo ufw allow 5672
sudo ufw allow 4369
sudo ufw reload
```
5. 添加管理员


```
sudo rabbitmqctl add_user  admin  admin 
sudo rabbitmqctl set_user_tags admin administrator
sudo rabbitmqctl  set_permissions -p / admin '.*' '.*' '.*'
```

6. 安装Web管理界面插件

```
rabbitmq-plugins enable rabbitmq_management
rabbitmqctl list_users
// http://54.180.65.30:15672/#/
// http://3.132.215.12:15672/#/
```

# centos上安装RabbitMQ步骤
### 准备

```
yum update
yum install epel-release
yum install gcc gcc-c++ glibc-devel make ncurses-devel openssl-devel autoconf java-1.8.0-openjdk-devel git wget wxBase.x86_64
```  
### 安装erlang

```
yum install -y erlang
```
### 安装rabbitmq

```
wget https://github.com/rabbitmq/rabbitmq-server/releases/download/v3.7.15/rabbitmq-server-3.7.15-1.el7.noarch.rpm
```
### 导入秘钥

```
rpm --import https://www.rabbitmq.com/rabbitmq-release-signing-key.asc
```
### 安装
```
yum install rabbitmq-server-3.7.15-1.el7.noarch.rpm
```
### 启动服务
```
systemctl start rabbitmq-server
# 设置开机启动
systemctl enable rabbitmq-server
```
### 防火墙设置
```
--查看状态
firewall-cmd --state //running 表示运行

--重新加载
firewall-cmd --reload 

--开放端口 permanent 永久
firewall-cmd --permanent --zone=public --add-port=15672/tcp
firewall-cmd --permanent --zone=public --add-port=5672/tcp
firewall-cmd --permanent --zone=public --add-port=25672/tcp
firewall-cmd --permanent --zone=public --add-port=4369/tcp
firewall-cmd --permanent --zone=public --add-port=80/tcp
firewall-cmd --permanent --zone=public --add-port=22/tcp
firewall-cmd --permanent --zone=public --add-port=8080/tcp

--查看所有开放服务
firewall-cmd --permanent --zone=public --list-services 

--查看所有开放端口
firewall-cmd --permanent --zone=public --list-ports
```
### 安装Web管理界面插件
```
rabbitmq-plugins enable rabbitmq_management
```
### 添加管理员同上
![如图设置](aws.jpg)
# 记得打开vps相应的端口策略
[亚马逊服务器安装成品](http://3.132.215.12:15672/#/)


