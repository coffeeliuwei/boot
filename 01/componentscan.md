# @ComponentScan

## 一：本课程目标：

1. 理解springboot的@ComponentScan注解作用
2. 学会用@ComponentScan

## 二、剖析springboot的@ComponentScan注解
```
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, 
		classes = TypeExcludeFilter.class),
		@Filter(type = FilterType.CUSTOM, 
		classes = AutoConfigurationExcludeFilter.class) })
```
+ excludeFilters：过滤不需要扫描的类型。
  + @Filter 过滤注解
  + FilterType.CUSTOM 过滤类型为自定义规则，即指定特定的class
  + classes :过滤指定的class，即剔除了TypeExcludeFilter.class、AutoConfigurationExcludeFilter.class

从以上源码，我们可以得出结论：

1. @SpringBootApplication的源码包含了@ComponentScan，
只要@SpringBootApplication注解的所在的包及其下级包，都会讲class扫描到并装入spring ioc容器
2. 如果你自定义的定义一个Spring bean，不在@SpringBootApplication注解的所在的包及其下级包，
都必须手动加上@ComponentScan注解并指定那个bean所在的包。

## 三、为什么要用@ComponentScan？它解決什么问题？

1. 为什么要用@ComponentScan ？
定义一个Spring bean 一般是在类上加上注解 @Service 或@Controller 或 @Component就可以，
但是，spring怎么知道有你这个bean的存在呢？所以我们必须告诉spring去哪里找这个bean类。
@ComponentScan就是用来告诉spring去哪里找bean类。

2. @componentscan的作用
通知Spring去扫描@componentscan指定包下所有的注解类，然后将扫描到的类装入spring bean容器。
例如：@ComponentScan("com.coffee.test")，就只能扫描com.coffee.test包下的注解类。
如果不写？就像@SpringBootApplication的@ComponentScan没有指定路径名？它去哪里找？
@SpringBootApplication注解的所在的包及其下级包，将class扫描并装入spring ioc容器中

## 四、 案例实战：体验@ComponentScan的作用

### 步骤1：在包名为com.coffee.testscan，新建一个TestScan测试类
``` 
package com.coffee.testscan;
import org.springframework.stereotype.Service;
@Service
public class TestScan {
}
```
### 步骤2：在com.coffee.test下面建个启动类
``` 
package com.coffee.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.coffee.testscan.TestScan;

@SpringBootApplication
//@ComponentScan("com.coffee.testscan")
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(Application.class, args);
        TestScan componentScan = run.getBean(TestScan.class);
        System.out.println(componentScan.toString());
    }
}
```
启动报错：
``` 
Exception in thread "main" org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'com.coffee.testscan.TestScan' available
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.getBean(DefaultListableBeanFactory.java:351)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.getBean(DefaultListableBeanFactory.java:342)
	at org.springframework.context.support.AbstractApplicationContext.getBean(AbstractApplicationContext.java:1126)
	at com.coffee.app.Application.main(Application.java:15)
```
- 去掉以上注释
``` 
@ComponentScan("com.coffee.testscan")
```
正常启动

