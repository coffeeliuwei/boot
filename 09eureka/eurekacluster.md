## Eureka原理及搭建服务集群

### 一、学习目标
1. 了解Eureka原理
1. 搭建集群服务中心
2. 熟练操作linux系统

### 二、Eureka原理
![原理图](https://github.com/coffeeliuwei/boot/blob/master/img/43.jpg?raw=true)

上面的架构描述了Eureka在Netflix上的部署方式。每个区域至少有一个eureka服务器来处理区域故障。

服务在Eureka注册，然后每30秒发送一次心跳续订租约。如果客户端几次无法续签租约，则大约90秒后会将其从服务器注册表中删除。注册信息和续订将复制到集群中的所有eureka节点。任何区域的客户端都可以查找注册表信息（每30秒发生一次）以查找其服务（可能在任何区域）并进行远程调用。

#### 功能说明
1. Register(服务注册)： 把自己的IP port注册给eureka
2. renew(服务续约)：发送心跳 30秒发送一次心跳，告诉eureka自己还活着。
3. eviction(剔除)：超过90秒，eureka认为app死亡，从注册表剔除。
4. cancel(服务下线)：provider停止关闭，调用eureka，把自己从注册表剔除，防止consumer调用不存在的服务。
5. get registry(获取注册列表)
6. replicate(复制)：eureka集群自己的数据同步和复制。

#### 对非Java服务的支持
对于非基于Java的服务，可以选择以该服务的语言实现eureka的客户端部分，也可以运行“ side car”，它实际上是带有嵌入式eureka客户端的Java应用程序，用于处理注册和心跳。对于Eureka客户端支持的所有操作，还将公开基于REST的端点。非Java客户端可以使用REST端点查询有关其他服务的信息。

#### 弹性(Resilience)
处于AWS云中，很难不考虑我们构建的所有内容的弹性。Eureka受益于我们获得的经验，客户端和服务器都内置了弹性。

如一台或多台Eureka服务器出现故障。由于Eureka客户端中具有注册表缓存信息，因此即使所有eureka服务器都关闭了，它们也可以正常运行。

在同级别Eureka服务器出现故障时Eureka服务器也具有弹性。即使在客户端和服务器之间进行网络分区时，服务器也具有内置的弹性以防止大规模中断。

#### Eureka自我保护机制
如果Eureka服务器检测到数量超过预期的注册客户端已以不正当的方式终止了它们的连接，并且同时正等待驱逐退出，则它们将进入自我保存模式。这样做是为了确保灾难性的网络事件不会清除eureka注册表数据，并将其传播到下游的所有客户端。

Eureka协议要求客户端永久离开时执行明确的注销操作。例如，在提供的Java客户端中，这是通过shutdown（）方法完成的。任何连续3次心跳续订失败的客户端将被视为不正常的终止，并且将由后台驱逐过程逐出。只有当前注册表丢失数> 15％处于此更高状态时，才会启用自我保存。

处于自我保留模式时，eureka服务器将停止逐出所有实例，直到发生以下情况之一：

+ 它看到的心跳续订次数又回到了预期的阈值之上，或者
+ 自我保护功能已禁用
默认情况下会启用自我保留，并且启用自我保留的默认阈值>当前注册表大小的15％。
配置自我保护阈值`eureka.server.renewal-percent-threshold=0.8`
状态开关`eureka.server.enable-self-preservation = false`默认true启动自我保护

#### CAP原理介绍
+ C：Consistency
即一致性，访问所有的节点得到的数据应该是一样的。注意，这里的一致性指的是强一致性，也就是数据更新完，访问任何节点看到的数据完全一致，要和弱一致性，最终一致性区分开来。
+ A：Availability
即可用性，所有的节点都保持高可用性。注意，这里的高可用还包括不能出现延迟，比如如果节点B由于等待数据同步而阻塞请求，那么节点B就不满足高可用性。
也就是说，任何没有发生故障的服务必须在有限的时间内返回合理的结果集。
+ P：Partiton tolerence
即分区容忍性，这里的分区是指网络意义上的分区。由于网络是不可靠的，所有节点之间很可能出现无法通讯的情况，在节点不能通信时，要保证系统可以继续正常服务。

CAP原理说，一个数据分布式系统不可能同时满足C和A和P这3个条件。所以系统架构师在设计系统时，不要将精力浪费在如何设计能满足三者的完美分布式系统，而是应该进行取舍。由于网络的不可靠性质，大多数开源的分布式系统都会实现P，也就是分区容忍性，之后在C和A中做抉择。

##### CAP原理简单证明
假设有节点data1和节点data2，一开始有个数据number=1。之后向data1提交更新，将数据number设置为2。接着data1就需要将更新推送给data2，让data2也更新number数据。

接下来我们分3个场景分析：

1. 在保证C和P的情况下
为了保证数据一致性，data1需要将数据复制给data2，即data1和data2需要进行通信。但是由于网络是不可靠的，我们系统有保证了分区容忍性，也就是说这个系统是可以容忍网络的不可靠的。这时候data2就不一定能及时的收到data1的数据复制消息，当有请求向data2访问number数据时，为了保证数据的一致性，data2只能阻塞等待数据真正同步完成后再返回，这时候就没办法保证高可用性了。所以，在保证C和P的情况下，是无法同时保证A的。

2. 在保证A和P的情况下
为了保证高可用性，data1和data2都有在有限时间内返回。同样由于网络的不可靠，在有限时间内，data2有可能还没收到data1发来的数据更新消息，这时候返回给客户端的可能是旧的数据，和访问data1的数据是不一致的，也就是违法了C。也就是说，在保证A和P的情况下，是无法同时保证C的。

3. 在保证A和C的情况下
如果要保证高可用和一致性，只有在网络情况良好且可靠的情况下才能实现。这样data1才能立即将更新消息发送给data2。但是我们都知道网络是不可靠的，是会存在丢包的情况的。所以要满足即时可靠更新，只有将data1和data2放到一个区内才可以，也就丧失了P这个保证。其实这时候整个系统也不能算是一个分布式系统了。理解CAP理论的最简单方式是想象两个节点分处分区两侧。允许至少一个节点更新状态会导致数据不一致，即丧失了C性质。如果为了保证数据一致性，将分区一侧的节点设置为不可用，那么又丧失了A性质。除非两个节点可以互相通信，才能既保证C又保证A，这又会导致丧失P性质。

### 三、配置服务器

#### 启动基本网络安全认证
创建一个继承于WebSecurityConfigurerAdapter的类
```
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf()
				.disable()	//关闭跨域保护
			.httpBasic() //为了实现通过url传递用户名和密码进行验证，这里启用基本http服务
			.and()
			.authorizeRequests()
			.anyRequest()
			.authenticated();
	}	
}
```
在配置文件中添加用户名和密码
```
spring.security.user.name=user
spring.security.user.password=111111
```

#### 全部配置文件清单及详细说明
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
#自我保护启动阈值
eureka.server.renewal-percent-threshold=0.8
# Peer节点更新间隔，单位：毫秒
eureka.server.peer-eureka-nodes-update-interval-ms =3000 
# Eureka服务器清理无效节点的时间间隔，单位：毫秒，缺省：60000，即60秒
eureka.server.eviction-interval-timer-in-ms = 60000
#-------------------------eureka.instance前缀的配置项------------------------------
# 服务名，默认取 spring.application.name 配置值，如果没有则为 unknown
#eureka.instance.appname = eureka-server
# 实例ID
eureka.instance.instance-id = eureka1-server:${random.value}
# 应用实例主机名
eureka.instance.hostname=eureka1
# 客户端在注册时使用自己的IP而不是主机名，缺省：false
eureka.instance.prefer-ip-address = true
# 应用实例IP,若eureka.instance.prefer-ip-address为true并此选项没有的情况系统选择第一非环路IP
#eureka.instance.ip-address = 192.168.1.126
# 服务失效时间，失效的服务将被剔除。单位：秒，默认：90
eureka.instance.lease-expiration-duration-in-seconds = 90
# 服务续约（心跳）频率，单位：秒，缺省30
eureka.instance.lease-renewal-interval-in-seconds = 30
# 状态页面的URL，相对路径，默认使用 HTTP 访问，
#如需使用 HTTPS则要使用绝对路径配置，缺省：/info
eureka.instance.status-page-url-path = /info
# 健康检查页面的URL，相对路径，默认使用 HTTP 访问，
#如需使用 HTTPS则要使用绝对路径配置，缺省：/health
eureka.instance.health-check-url-path = /health
#---------------------------http basic安全认证---------------------------------------
#需要启动WebSecurity模块验证
spring.security.user.name=user
spring.security.user.password=111111
# ---------------------------eureka.client前缀------------------------------------
#设置服务注册中心地址,指向另一个注册中心，如果多个用逗号隔开
eureka.client.serviceUrl.defaultZone=\
	http://${spring.security.user.name}:\
	${spring.security.user.password}@eureka2:\
	${server.port}/eureka/
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
#自我保护启动阈值
eureka.server.renewal-percent-threshold=0.8
# Peer节点更新间隔，单位：毫秒
eureka.server.peer-eureka-nodes-update-interval-ms =3000 
# Eureka服务器清理无效节点的时间间隔，单位：毫秒，缺省：60000，即60秒
eureka.server.eviction-interval-timer-in-ms = 60000
#-------------------------eureka.instance前缀的配置项------------------------------
# 服务名，默认取 spring.application.name 配置值，如果没有则为 unknown
#eureka.instance.appname = eureka-server
# 实例ID
eureka.instance.instance-id = eureka2-server:${random.value}
# 应用实例主机名
eureka.instance.hostname=eureka2
# 客户端在注册时使用自己的IP而不是主机名，缺省：false
eureka.instance.prefer-ip-address = true
# 应用实例IP,若eureka.instance.prefer-ip-address为true并此选项没有的情况系统选择第一非环路IP
#eureka.instance.ip-address = 192.168.1.127
# 服务失效时间，失效的服务将被剔除。单位：秒，默认：90
eureka.instance.lease-expiration-duration-in-seconds = 90
# 服务续约（心跳）频率，单位：秒，缺省30
eureka.instance.lease-renewal-interval-in-seconds = 30
# 状态页面的URL，相对路径，默认使用 HTTP 访问，如需使用 HTTPS则要使用绝对路径配置，缺省：/info
eureka.instance.status-page-url-path = /info
# 健康检查页面的URL，相对路径，默认使用 HTTP 访问，如需使用 HTTPS则要使用绝对路径配置，缺省：/health
eureka.instance.health-check-url-path = /health
#---------------------------http basic安全认证---------------------------------------
#需要启动WebSecurity模块验证
spring.security.user.name=user
spring.security.user.password=111111
# ---------------------------eureka.client前缀------------------------------------
#设置服务注册中心地址,指向另一个注册中心，如果多个用逗号隔开
eureka.client.serviceUrl.defaultZone=\
	http://${spring.security.user.name}:\
	${spring.security.user.password}@eureka1:\
	${server.port}/eureka/
```
如有更多中心可以此方式两两互联继续拓展形成集群网络

#### 对项目打包
![打jar包](https://github.com/coffeeliuwei/boot/blob/master/img/40.jpg?raw=true)

### 四、发布Eureka服务(centos版本)
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

