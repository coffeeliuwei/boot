# 简介
+ 微服务：一个项目 可以由多个 小型服务构成（微服务）  
+ spring boot可以快速开发 微服务模块  
	*	简化j2ee开发
	*	整个spring技术栈的整合（整合springmvc  spring）	
	*	整个j2ee技术的整合（整合mybatis redis）
	
+ 准备：  
	+ jdk:
	JAVA_HOME： jdk根目录  
	path:jdk根目录\bin  
	classpath: .;jdk根目录\lib  
	+ maven:  
	MAVEN_HOME： maven根目录  
	path: maven根目录\bin  
	配置Maven本地仓库:  
	```mvn根目录/conf/setting.xml```  
	```<localRepository>D:/mvnlib</localRepository> ```   
	在IDE中配置mvn：
		window->preference->搜maven ,installations/user settings
+ spring boot开发工具：  
	Eclipse(STS插件) >>STS  
	IntelliJ IDEA
+ 目录结构resources：  
	static:静态资源（js css 图片 音频 视频）  
	templates：模板文件（模版引擎freemarker ,thymeleaf；默认不支持jsp）   
	application.properties： 配置文件  
+ spring boot内置了tomcat，并且不需要打成war再执行。  
可以在appication.properties对端口号等服务端信息进行配置

+ spring boot将各个应用/三方框架 设置成了一个个“场景”stater，
 以后要用哪个，只需要引入那个场景即可。  
选完之后，spring boot就会将 该场景所需要的所有依赖 自动注入。 
例如 选择 “web”,spring boot就会将web相关的依赖（tomcat  json） 全部引入本项目
