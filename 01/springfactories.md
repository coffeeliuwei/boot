## 一、本课程目标：
1. 搞明白spring 的ImportSelector的原理和spring的spring.factories文件的作用
2. @EnableAutoConfiguration 如何通过 spring.factories 来实现bean的注册
3. 动手编码练习：自定义一个的spring.factories文件



## 二、从ImportSelector到spring.factories分析
上一章节我们已经知道AutoConfigurationImportSelector继承了DeferredImportSelector接口，
而DeferredImportSelector接口又是继承ImportSelector接口的。Spring处理引入配置的时候，
遇到实现了ImportSelector接口的类，会调用接口的selectImports方法来拿到需要引入的类名数组进行解析引入。
+ ImportSelector的selectImports接口方法

```
@Override
	public String[] selectImports(AnnotationMetadata annotationMetadata) {
	...
```
而如果对于实现了DeferredImportSelector接口类，Spring是会在处理完其他所有配置类都解析完成后，再解析这个类（这便于处理条件注解@ConditionalOnBean、ConditionalOnMissingBean等）
+ DeferredImportSelector接口

```
public interface DeferredImportSelector extends ImportSelector {
	@Nullable
	default Class<? extends Group> getImportGroup() {
		return null;
	}
	interface Group {
		void process(AnnotationMetadata metadata, DeferredImportSelector selector);
		Iterable<Entry> selectImports();
		class Entry {
			private final AnnotationMetadata metadata;
			...
```
DeferredImportSelector这个接口在Spring5增加了内部接口Group，
Spring5处理DeferredImportSelector的时候会先调用getImportGroup拿到Group类型的类，
然后实例化这个类，接着调用process方法，再调用selectImports拿到要引入的配置集合（Entry类型的集合），
最后遍历这个集合逐个解析配置类。

上面说过，会先调用Group类型的process方法，再调用其selectImports方法，来看AutoConfigurationGroup类对这两个方法的实现
+ process方法

``` 
@Override
public void process(AnnotationMetadata annotationMetadata, DeferredImportSelector deferredImportSelector) {
	//限制deferredImportSelector的实际类型是AutoConfigurationImportSelector
	Assert.state(deferredImportSelector instanceof AutoConfigurationImportSelector,
			() -> String.format("Only %s implementations are supported, got %s",
					AutoConfigurationImportSelector.class.getSimpleName(),
					deferredImportSelector.getClass().getName()));
	//AutoConfigurationEntry里有需要引入配置类和排除掉的配置类，最终只要返回需要配置的配置类
	AutoConfigurationEntry autoConfigurationEntry = ((AutoConfigurationImportSelector) deferredImportSelector)
			.getAutoConfigurationEntry(getAutoConfigurationMetadata(), annotationMetadata);
	//加入缓存,List<AutoConfigurationEntry>类型
	this.autoConfigurationEntries.add(autoConfigurationEntry);
	for (String importClassName : autoConfigurationEntry.getConfigurations()) {
		//加入缓存，Map<String, AnnotationMetadata>类型
		this.entries.putIfAbsent(importClassName, annotationMetadata);
	}
}
```
在 process方法中调用了一个 getAutoConfigurationEntry() 方法。

##### 调用链：getAutoConfigurationEntry() -> getCandidateConfigurations() -> loadFactoryNames()

+ getAutoConfigurationEntry

```
protected AutoConfigurationEntry getAutoConfigurationEntry(AutoConfigurationMetadata autoConfigurationMetadata,
		AnnotationMetadata annotationMetadata) {
	...
	AnnotationAttributes attributes = getAttributes(annotationMetadata);
	List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
	...
```
+ getCandidateConfigurations

```
protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
	List<String> configurations = SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(),
			getBeanClassLoader());
	Assert.notEmpty(configurations, "No auto configuration classes found in META-INF/spring.factories. If you "
			+ "are using a custom packaging, make sure that file is correct.");
	return configurations;
}
//在这里 loadFactoryNames() 方法传入了 EnableAutoConfiguration.class 这个参数。
protected Class<?> getSpringFactoriesLoaderFactoryClass() {
	return EnableAutoConfiguration.class;
}
```
+ loadFactoryNames

```
public static List<String> loadFactoryNames(Class<?> factoryType, @Nullable ClassLoader classLoader) {
	String factoryTypeName = factoryType.getName();
	return loadSpringFactories(classLoader).getOrDefault(factoryTypeName, Collections.emptyList());
}

private static Map<String, List<String>> loadSpringFactories(@Nullable ClassLoader classLoader) {
	MultiValueMap<String, String> result = cache.get(classLoader);
	if (result != null) {
		return result;
	}

	try {
		Enumeration<URL> urls = (classLoader != null ?
				classLoader.getResources(FACTORIES_RESOURCE_LOCATION) :
				ClassLoader.getSystemResources(FACTORIES_RESOURCE_LOCATION));
		result = new LinkedMultiValueMap<>();
		...
```
loadFactoryNames方法的作用
+ 从当前项目的类路径中获取 FACTORIES_RESOURCE_LOCATION 这个文件下的信息。
+ 将上面获取到的信息封装成一个 Map 返回。
+ 从返回的 Map 中通过刚才传入的 EnableAutoConfiguration.class 参数，获取该 key 下的所有值
+ FACTORIES_RESOURCE_LOCATION的定义

```
/**
 * The location to look for factories.
 * <p>Can be present in multiple JAR files.
 */
public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";
```
+ selectImports方法

``` 
@Override
public Iterable<Entry> selectImports() {
	//根据缓存的成员变量判断是不是空
	if (this.autoConfigurationEntries.isEmpty()) {
		return Collections.emptyList();
	}
	//拿到所有排除类
	Set<String> allExclusions = this.autoConfigurationEntries.stream()
			.map(AutoConfigurationEntry::getExclusions).flatMap(Collection::stream).collect(Collectors.toSet());
	//拿到需要配置的类
	Set<String> processedConfigurations = this.autoConfigurationEntries.stream()
			.map(AutoConfigurationEntry::getConfigurations).flatMap(Collection::stream)
			.collect(Collectors.toCollection(LinkedHashSet::new));
	//这里移除排除的类
	processedConfigurations.removeAll(allExclusions);
	//对配置类排序（根据注解AutoConfigureOrder、AutoConfigureBefore、AutoConfigureAfter），
	//最后封装成Entry装入集合返回
	return sortAutoConfigurations(processedConfigurations, getAutoConfigurationMetadata()).stream()
			.map((importClassName) -> new Entry(this.entries.get(importClassName), importClassName))
			.collect(Collectors.toList());
}
```
这个方法会最终会把需要配置的类封装成Entry，装入集合最后返回出去，交由Spring解析处理。

##### 综上分析打开第三方依赖的META-INF/spring.factories

```
# Auto Configure
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration,\
org.springframework.boot.autoconfigure.aop.AopAutoConfiguration,\
org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration,\
org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration,\
org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration,\
```
将类路径下 META-INF/spring.factories 里面配置的所有 EnableAutoConfiguration 的值加入到 Spring 容器中

### 三、案例实战：自己动手编码实现的spring.factories文件
只要在src/main/resource目录下的META-INF创建spring.factories文件即可

#### 步骤1：新建一个@Configuration配置类
在package liu.wei包中

``` 
package liu.wei;
public class Person {
    public String say(){
        return " I am coffeeliu";
    }
}

package liu.wei;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class PersonConfig {
    @Bean
    public Person person(){
        return new Person();
    }
}
```
#### 步骤2：新建spring.factories
在src/main/resource目录下的META-INF创建spring.factories文件即可

``` 
org.springframework.boot.autoconfigure.EnableAutoConfiguration=liu.wei.PersonConfig
```
#### 步骤3：体验类

``` 
package com.coffee;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import liu.wei.Person;
@SpringBootApplication
public class CoffeeliuBootSpringfactoriesApplication {
	public static void main(String[] args) {
	Person person=	SpringApplication.run(CoffeeliuBootSpringfactoriesApplication.class, args)
						.getBean(Person.class);
	System.out.println(person.say());
	}
```
结果：

``` 
 I am coffeeliu
```


### 四、总结@SpringBootApplication启动原理
以上所有注解就只干一件事：把bean注册到spring ioc容器。
通过4种方式来实现：

1. @SpringBootConfiguration 通过@Configuration 与@Bean结合，注册到Spring ioc 容器。
2. @ComponentScan  通过范围扫描的方式，扫描特定注解类，将其注册到Spring ioc 容器。
3. @Import 通过导入的方式，将指定的class注入到spring ioc容器里面 。
4. @EnableAutoConfiguration 通过spring.factories的配置，来实现bean的注册到Spring ioc 容器。


###五：课后练习题
参考本课程的代码，建2个maven工程，一个是school工程，另一个是student工程
1.student工程，只有一个类，如下：

``` 
package com.student.demo;

public class Student {
    public String info(){
        return "student";
    }
}
```
2.school工程就一个启动类

``` 
package com.school.demo;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {

		ConfigurableApplicationContext run = SpringApplication.run(Application.class, args);
		Student bean = run.getBean(Student.class);
		System.out.println(bean.info());
	}

```
把以上2个工程，采用spring.factories整合在一起，使school工程能正常运行。










