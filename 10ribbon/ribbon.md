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

## 三、搭建ribbon服务实验环境

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