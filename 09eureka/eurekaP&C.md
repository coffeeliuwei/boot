## Eureka服务生产者与消费者

### 一、课程目标
1. 掌握Provider构造原理
2. 掌握Consumer构造原理
3. 掌握RestTemplate运用

### 二、Spring Boot Actuator
Spring Boot Actuator 模块提供了生产级别的功能，比如健康检查，审计，指标收集，HTTP 跟踪等，帮助我们监控和管理Spring Boot 应用。这个模块是一个采集应用内部信息暴露给外部的模块，上述的功能都可以通过HTTP 和 JMX 访问。

因为暴露内部信息的特性，Actuator 也可以和一些外部的应用监控系统整合（Prometheus, Graphite, DataDog, Influx, Wavefront, New Relic等）。这些监控系统提供了出色的仪表板，图形，分析和警报，可帮助你通过一个统一友好的界面，监视和管理你的应用程序。

Actuator使用Micrometer与这些外部应用程序监视系统集成。这样一来，只需很少的配置即可轻松集成外部的监控系统。

#### Endpoints
actuator的核心部分，它用来监视应用程序及交互，spring-boot-actuator中已经内置了非常多的 Endpoints（health、info、beans、httptrace、shutdown等等），同时也允许我们自己扩展自己的端点。
+ 内置Endpoints
  + auditevents:显示当前应用程序的审计事件信息
  + beans:显示应用Spring Beans的完整列表
  + caches:显示可用缓存信息
  + conditions:显示自动装配类的状态及及应用信息
  + configprops:显示所有@ConfigurationProperties列表
  + env:显示ConfigurableEnvironment中的属性
  + flyway:显示Flyway 数据库迁移信息
  + health:显示应用的健康信息（未认证只显示status，认证显示全部信息详情）
  + info:显示任意的应用信息（在资源文件写info.xxx即可）
  + liquibase:展示Liquibase 数据库迁移
  + metrics:展示当前应用的metrics信息
  + mappings:显示所有@RequestMapping 路径集列表
  + scheduledtasks:显示应用程序中的计划任务
  + sessions:允许从Spring会话支持的会话存储中检索和删除用户会话。
  + shutdown:允许应用以优雅的方式关闭（默认情况下不启用）
  + threaddump:执行一个线程dump
  + httptrace:显示HTTP跟踪信息（默认显示最后100个HTTP请求-响应交换）
由于Actuator默认只对外暴露info/health端点所以首先需通过`management.endpoints.web.exposure.include=相应服务`向外界暴露所需端点服务。

### 三、Provider-服务提供者
provider负责与数据库进行交互，实现数据持久化，并给consumer提供服务


#### eureka-provider项目的pom清单
由于本课程采用总包版本控制所以无需添加多余设置，consumer配置基本相同
actuator模块在spring cloud中以被默认依赖无需再手动添加
```
<modelVersion>4.0.0</modelVersion>
<parent>
	<groupId>com.coffee</groupId>
	<artifactId>09eureka</artifactId>
	<version>1.0.0-SNAPSHOT</version>
</parent>
<groupId>com.coffee.eureka</groupId>
<artifactId>eureka-provider</artifactId>
<name>eureka-provider</name>
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
</dependencies>
<build>
	<plugins>
		<plugin>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-maven-plugin</artifactId>
		</plugin>
	</plugins>
</build>
```
基于中国人对json的使用习惯，加入FastJson依赖，用以替换掉系统默认Jackson

#### 启动类-增加EurekaClient注解

```
@EnableEurekaClient
@SpringBootApplication
public class EurekaApplication {
	public static void main(String[] args) {
		SpringApplication.run(EurekaApplication.class, args);
	}
}
```
注意与Eureka server服务器的配置区别
#### 实体类
Product进行简单模拟

```
public class Product {
	private int id;
	private String name;
	...
```

#### 控制类
```
@RestController
public class ProductController {
	@RequestMapping(value="/list",method=RequestMethod.GET)
			//,produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String,Object> listProduct(){
		Map<String,Object> map=new HashMap<String, Object>();
		List<Product> list=new ArrayList<Product>();
		list.add(new Product(1,"刘伟"));
		map.put("一", list);
		map.put("二", new Date());
		return  map;
	}
}
```
这里指明使用get方法以备消费者后续采用RestTemplate.getForObject调用

#### 配置类
```
@Configuration
public class WebConfig  implements WebMvcConfigurer {
    @Override
	//添加内容裁决器
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		//WebMvcConfigurer.super.configureContentNegotiation(configurer);
    	configurer.ignoreAcceptHeader(true).defaultContentType(
              MediaType.APPLICATION_JSON,MediaType.ALL);
	}
    @Override
	//添加fastjson转换器
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    	
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        		//格式化日期
        fastJsonConfig.setDateFormat("yyyy-MM-dd");
        fastJsonConfig.setSerializerFeatures(
                // 防止循环引用
                SerializerFeature.DisableCircularReferenceDetect,
                //序列化时写入类型信息,为以后反射用
                SerializerFeature.WriteClassName,
                // 空集合返回[],不返回null
                SerializerFeature.WriteNullListAsEmpty, 
                // 空字符串返回"",不返回null
                SerializerFeature.WriteNullStringAsEmpty,
                //是否输出值为null的字段
                SerializerFeature.WriteMapNullValue
        );
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);

        //处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);

        converters.add(0,fastJsonHttpMessageConverter);
        for (HttpMessageConverter<?> messageConverter : converters) {
            System.out.println(messageConverter);
        }
    }
}
```
WebMvcConfigurer配置类其实是Spring内部的一种配置方式，
采用JavaBean的形式来代替传统的xml配置文件形式进行针对框架个性化定制，
可以自定义一些Handler，Interceptor，ViewResolver，MessageConverter。  

主要功能：
+ 重写内容裁决器替换掉客户端默认Accpet参数设置。控制其不直接输出xml格式数据。
+ 重写configureMessageConverters方法加入fastjson解析器。
+ `converters.add(0,fastJsonHttpMessageConverter)`将其加载在converters列表头部阻止系统采用Jackson去解析json数据。

![加入后的converters列表状态](https://github.com/coffeeliuwei/boot/blob/master/img/46.jpg?raw=true)
#### application.properties文件配置
```
spring.application.name=eureka-provider
server.port=8081
eureka.client.serviceUrl.defaultZone=\
	http://user:111111@192.168.1.126:8761/eureka/
	#http://user:111111@192.168.1.127:8761/eureka/
eureka.instance.prefer-ip-address = true

#调整”/actuator”路径到“/”方便调试
management.endpoints.web.base-path=/
#暴露shutdown端点
management.endpoints.web.exposure.include=health,info,shutdown
#启用shutdown
management.endpoint.shutdown.enabled=true
```
defaultZone只连接126服务器以测试Eureka集群的数据同步是否正常。
actuator向外暴露health、info、shutdown端点。
将远程shutdown命令生效，既可通过控制台命令`curl -X POST 127.0.0.1:8081/shutdown`远程安全关闭程序。
安全下线机制保证Eureka服务器不会将已经下线的服务提供者分配给服务消费者。这样不会导致服务消费者因调用不存在的服务而应用出错。
![下线后Eureka服务端状态](https://github.com/coffeeliuwei/boot/blob/master/img/44.jpg?raw=true)

### 三、Consumer-服务消费者
服务消费者主要完成：发现服务和消费服务。其中服务的发现主要由Eureka的客户端完成，而消费的任务由Ribbon完成。

#### eureka-consumer项目的pom清单
```
<modelVersion>4.0.0</modelVersion>
<parent>
<groupId>com.coffee</groupId>
<artifactId>09eureka</artifactId>
<version>1.0.0-SNAPSHOT</version>
</parent>
<groupId>com.coffee.eureka</groupId>
<artifactId>eureka-consumer</artifactId>
<name>eureka-consumer</name>
<dependencies>
<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-web</artifactId>
	</dependency>
	
</dependencies>
<build>
	<plugins>
		<plugin>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-maven-plugin</artifactId>
		</plugin>
	</plugins>
</build>
```
#### 启动类与Provider相同

#### 实体类与Provider相同

#### 服务类
```
@Service
public class ProductService {
	@Autowired
	private LoadBalancerClient loadBalancerClient;//ribbon 负载均衡客户端
	
	public  Map<String,Object> listProduct(){
		ServiceInstance serviceInstance=loadBalancerClient.choose("eureka-provider");
		String url="http://" + serviceInstance.getHost() + ":"
				+serviceInstance.getPort()+"/list";
		System.out.println(url);
		RestTemplate rt=new RestTemplate();
		Map<String,Object> plist=rt.getForObject(url, Map.class);
		return plist;
	}
}
```
#### 控制类
```
@RestController
public class ProductController {
	
	@Autowired
	private ProductService productService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Map<String,Object> listProduct() {
		Map<String,Object> map = this.productService.listProduct();
		return map;
	}
}

```

#### 配置类与Provider相同

#### 注意点及说明
由于本项目父级工程导入了spring-cloud-starter-netflix-eureka-server，其依赖jackson-dataformat-xml，其作用是将数据解析成xml格式。
而客户端报文头Request Headers中`Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3`，
服务器会优先以xml格式输出数据。

解决方案：
+ 可以通过修改响应头的方式强制生成json格式。在控制器的每个@RequestMapping注解增加`produces = "application/json;charset=UTF-8"`属性。
+ 可以通过设置内容裁决器的方式`configurer.ignoreAcceptHeader(true)`禁用对Accept检查`.defaultContentType(MediaType.APPLICATION_JSON,MediaType.ALL)`设置默认MediaTypes其中将json设为第一匹配的方式进行处理。

### 四、整体效果
![Provider端自测效果](https://github.com/coffeeliuwei/boot/blob/master/img/47.jpg){:height="500" width="400"}
![Consumer调用服务效果](https://github.com/coffeeliuwei/boot/blob/master/img/48.jpg?raw=true)

![Eureka服务状态](https://github.com/coffeeliuwei/boot/blob/master/img/49.jpg?raw=true )
