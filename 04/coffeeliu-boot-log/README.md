## 一：目标：
学习springboot日志的框架，学完后会设置日志级别、设置日志的存储路径、设置日志的格式等等。

## 二：剖析springboot的日志框架
  slf4j
 logback、log4j  
 从springboot的底层框架spring-boot-starter-logging 可以看出，它依赖了3个框架分别为；slf4j、logback、log4j
 
### 分析1：slf4j、logback、log4j的区别？
1.logback、log4j：是日志实现框架，就是实现怎么记录日志的。  
2.slf4j：提供了java中所有的日志框架的简单抽象（日志的门面设计模式），说白了就是一个日志API（没有实现类），它不能单独使用
故：必须结合logback或log4j日志框架来实现。
 
### 分析2：springboot的日志搭配
springboot2.0默认采用了slf4f+logback的日志搭配。  
在开发过程中，我们都是采用了slf4j的api去记录日志，底层的实现就是根据配置logback或log4j日志框架。

##为什么控制台的日志只输出了 info  warn error?
因为springboot默认是info级别的  
```
logging.level.com.coffee=trace

```

## 三：配置日志的生成存储路径和日志名称
在实际的开发中，你不可能一直看着控制台，而且日志会非常大，瞬间就丢失。
故，我们要把日志存储在指定的目录下；
``` 

#一下配置的效果为：项目根目录下/output/logs/spring.log,默认的日志名为spring.log
logging.file.path=output/logs

# 如果不想要把日志存放在longging.path默认的根目录下，那就采用自定义的目录和文件名
logging.file.path=c:/data/logs/springboot.log
```
## 四：配置日志的内容格式

``` 
# %d-时间格式、%thread-线程、%-5level-从左5字符宽度、%logger{50}-日志50个字符、%msg-信息、%n-换行
# 设置在控制台输出的日志格式
logging.pattern.console=%d{yyyy-MM-dd} [%thread] %-5level %logger{50} -%msg%n
# 设置输出到文件的日志格式
logging.pattern.file=%d{yyyy/MM/dd} === [%thread] == %-5level == %logger{50} == %msg%n
```


## 五：课后练习
自己搭建一个springboot项目,实现以下2个功能:  
1.用try...catch捕获以下代码异常
```
int i=0/9; 
```
把异常日志存储在d:/log/springboot.log

2.参考以下的日志内容，配置出日志格式
``` 
2020-04-14 [http-nio-8080-exec-1] TRACE com.coffee.controller.configcontroller -------------trace-----------
2020-04-14 [http-nio-8080-exec-1] DEBUG com.coffee.controller.configcontroller -------------debug-----------
2020-04-14 [http-nio-8080-exec-1] INFO  com.coffee.controller.configcontroller -------------info-----------
2020-04-14 [http-nio-8080-exec-1] WARN  com.coffee.controller.configcontroller -------------warn-----------
2020-04-14 [http-nio-8080-exec-1] ERROR com.coffee.controller.configcontroller -------------error-----------
```

