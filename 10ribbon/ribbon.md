# Ribbon负载均衡

## 一、学习目标
+ 了解ribbon及其作用
+ 搭建ribbon服务
+ 理解七种负载均衡策略

## 二、Ribbon简介
1. Ribbon是一个基于Http和TCP的客服端负载均衡工具，它是基于Netflix Ribbon实现的。
2. 它不像spring cloud服务注册中心、配置中心、API网关那样独立部署，但是它几乎存在于每个spring cloud 微服务中。包括feign提供的声明式服务调用也是基于该Ribbon实现的。
3. ribbon默认提供很多种负载均衡算法，例如 轮询、随机 等等。甚至包含自定义的负载均衡算法。
4. 他解决并提供了微服务的负载均衡的问题。

#### 负载均衡解决方案的分类
1. 集中式负载均衡, 即在consumer和provider之间使用独立的负载均衡设施(可以是硬件，如F5, 也可以是软件，如nginx), 由该设施负责把 访问请求 通过某种策略转发至provider；
2. 进程内负载均衡，将负载均衡逻辑集成到consumer，consumer从服务注册中心获知有哪些地址可用，然后自己再从这些地址中选择出一个合适的provider。

Ribbon就属于后者，它只是一个类库，集成于consumer进程，consumer通过它来获取到provider的地址。

![负载均衡原理图](https://github.com/coffeeliuwei/boot/blob/master/img/50.jpg?raw=true)  

![ribbon负载均衡原理图](https://github.com/coffeeliuwei/boot/blob/master/img/51.jpg?raw=true)

## 三、搭建简单ribbon服务实验环境

### 将两个provider服务加载进linux服务器
这里分别将两个provider加载进两个eureka服务器，所以需对server.sh启动脚本做相应调整
+ 将server.sh脚本改写成多任务启动
具体脚本请参考`VScode远程调试linux`章节
+ 对脚本权限、格式等进行调整
```
chmod -R 755 server.sh
vim server.sh
set: ff=unix
:wq
./server.sh start
```
注意对`SPRING_PROFILES_ACTIVS=(-Dspring.profiles.active=eureka1 "")`启动文件的修改

![启动效果](https://github.com/coffeeliuwei/boot/blob/master/img/59.jpg?raw=true)

### 启动consumer服务

![启动效果](https://github.com/coffeeliuwei/boot/blob/master/img/60.jpg?raw=true)

![多次请求效果](https://github.com/coffeeliuwei/boot/blob/master/img/61.jpg?raw=true)

经多次请求可发现Ribbon默认采用轮询策略选择服务生产者响应来消费者的请求。

## 四、Ribbon轮询常用策略
1. 轮询策略（默认）RoundRobinRule：轮询策略表示每次都顺序取下一个provider，比如一共有5个provider，第1次取第1个，第2次取第2个，第3次取第3个，以此类推
1. 权重轮询策略 WeightedResponseTimeRule：
	+ 根据每个provider的响应时间分配一个权重，响应时间越长，权重越小，被选中的可能性越低。
	+ 原理：一开始为轮询策略，并开启一个计时器，每30秒收集一次每个provider的平均响应时间，当信息足够时，给每个provider附上一个权重，并按权重随机选择provider，高权越重的provider会被高概率选中。"
1. 随机策略 RandomRule：从provider列表中随机选择一个provider
1. 最少并发数策略 BestAvailableRule：选择正在请求中的并发数最小的provider，除非这个provider在熔断中。
1. 在“选定的负载均衡策略”基础上进行重试机制 RetryRule：
	+ “选定的负载均衡策略”这个策略是轮询策略RoundRobinRule
	+ 该重试策略先设定一个阈值时间段，如果在这个阈值时间段内当选择provider不成功，则一直尝试采用“选定的负载均衡策略：轮询策略”最后选择一个可用的provider
1. 可用性敏感策略 AvailabilityFilteringRule:过滤性能差的provider
	+ 过滤掉在eureka中处于一直连接失败provider
	+ 过滤掉高并发的provider
1. 区域敏感性策略 ZoneAvoidanceRule：
	+ 以一个区域为单位考察可用性，对于不可用的区域整个丢弃，从剩下区域中选可用的provider
	+ 如果这个ip区域内有一个或多个实例不可达或响应变慢，都会降低该ip区域内其他ip被选中的权重。

## 五、为consumer指定轮询策略
在10ribbon项目中新建eureka-consumer-lb项目，eureka-consumer-lb源码沿用eureka-consumer项目源码

![ribbon策略](https://github.com/coffeeliuwei/boot/blob/master/img/62.jpg?raw=true)

### 代码实现方式
```
@EnableEurekaClient
@SpringBootApplication
public class EurekaApplication {
    @Bean
	IRule ribbonIRule()
	{
		return new ZoneAvoidanceRule();
	}
	public static void main(String[] args) {
		SpringApplication.run(EurekaApplication.class, args);
	}
}
```
在启动类配置一个启动策略

### 通过配置文件实现方式

在application.properties配置中增加
```
eureka-provider.ribbon.NFLoadBalancerRuleClassName=\
     com.netflix.loadbalancer.WeightedResponseTimeRule
```

![采用时间加权的效果](https://github.com/coffeeliuwei/boot/blob/master/img/63.jpg?raw=true)

一般建议采用低并发策略BestAvailableRule。如项目分部于不同区域建议采用区域加权策略ZoneAvoidanceRule。


## 点对点直连
在10ribbon项目中新建eureka-consumer-pvp项目，eureka-consumer-pvp源码沿用eureka-consumer项目源码

![点对点直连原理图](https://github.com/coffeeliuwei/boot/blob/master/img/64.jpg?raw=true)

在开发阶段消费者需要与特定服务供应者进行联调，由于eureka的存在会导致消费者不能精确命中特定供应者。所以点对点直连在开发阶段抛开eureka服务是必要的。

+ 剔除eureka架包
由于eureka架包存在于10ribbon项目中，所以本项目父级不指向10ribbon。而直接指向更高级coffeeliu-boot总项目。
直接排除spring-cloud-starter-netflix-eureka-server架包
+ 单独添加ribbon架包
pom文件部分代码
```
<parent>
	<groupId>com.coffee</groupId>
	<artifactId>coffeeliu-boot</artifactId>
	<version>1.0.0-SNAPSHOT</version>
</parent>
<groupId>com.coffee.eureka</groupId>
<artifactId>eureka-consumer-pvp</artifactId>
<name>eureka-consumer-pvp</name>
<dependencies>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-web</artifactId>
	</dependency>
	<!-- https://mvnrepository.com/artifact/com.alibaba/fastjson -->
	<dependency>
		<groupId>com.alibaba</groupId>
		<artifactId>fastjson</artifactId>
		<version>1.2.68</version>
	</dependency>
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
		<version>2.2.2.RELEASE</version>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-actuator</artifactId>
	</dependency>
</dependencies>
```
这里保留了actuator所以要增加spring-boot-starter-actuator依赖（以前是eureka包含了）
+ application.properties配置
```
spring.application.name=eureka-consumer-pvp
server.port=9090
#调整”/actuator”路径到“/”方便调试
management.endpoints.web.base-path=/
#暴露shutdown端点
management.endpoints.web.exposure.include=health,info,shutdown
#启用shutdown
management.endpoint.shutdown.enabled=true
#在控制台使用 curl -X POST 127.0.0.1:9090/shutdown
#即可远程安全关掉程序

#指定具体的服务实例清单
eureka-provider.ribbon.listOfServers=192.168.1.127:8081
```

![运行结果](https://github.com/coffeeliuwei/boot/blob/master/img/65.jpg?raw=true)

![运行结果](https://github.com/coffeeliuwei/boot/blob/master/img/66.jpg?raw=true)

从运行结果和eureka管理界面发现pvp已经和192.168.1.127服务器上eureka-provider实现了直连。
若eureka-provider.ribbon.listOfServers增加多个地址（以逗号隔开）则实现脱离eureka托管的简单轮询服务。




