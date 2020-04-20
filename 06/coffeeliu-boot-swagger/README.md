# 集成swagger

### 一、本课程目标：
1. 弄清楚，为什么要用swagger，它解决了什么问题？
2. 编码实现2个springboot接口，让swagger自动生成接口文档


### 二、Swagger简介
Swagger 是一套基于 OpenAPI 规范构建的开源工具，可以帮助我们设计、构建、记录以及使用 Rest API。Swagger 主要包含了以下三个部分：

+ Swagger Editor：基于浏览器的编辑器，我们可以使用它编写我们 OpenAPI 规范。
+ Swagger UI：它会将我们编写的 OpenAPI 规范呈现为交互式的 API 文档，后文我将使用浏览器来查看并且操作我们的 Rest API。
+ Swagger Codegen：它可以通过为 OpenAPI（以前称为 Swagger）规范定义的任何 API 生成服务器存根和客户端 SDK 来简化构建过程

### 三、为什么要使用swagger
当下很多公司都采取前后端分离的开发模式，前端和后端的工作由不同的工程师完成。在这种开发模式下，维持一份及时更新且完整的 Rest API 文档将会极大的提高我们的工作效率。传统意义上的文档都是后端开发人员手动编写的，相信大家也都知道这种方式很难保证文档的及时性，这种文档久而久之也就会失去其参考意义，反而还会加大我们的沟通成本。而 Swagger 给我们提供了一个全新的维护 API 文档的方式，下面我们就来了解一下它的优点：

+ 代码变，文档变。只需要少量的注解，Swagger 就可以根据代码自动生成 API 文档，很好的保证了文档的时效性。
+ 跨语言性，支持 40 多种语言。
+ Swagger UI 呈现出来的是一份可交互式的 API 文档，我们可以直接在文档页面尝试 API 的调用，省去了准备复杂的调用参数的过程。
+ 还可以将文档规范导入相关的工具（例如 SoapUI）, 这些工具将会为我们自动地创建自动化测试。
### 四、运行条件
+ Java 8
+ Apache Maven 3.0.4或更高版本
+ jackson 2.4.5或更高
+ [Springfox Swagger2下载地址](https://mvnrepository.com/artifact/io.springfox/springfox-swagger2)
### 五、SpringBoot项目中使用Swagger

#### 步骤1： pom文件加入依赖包
``` 
<!--swagger-->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>
<!--swagger-ui-->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.9.2</version>
</dependency>
```
#### 步骤2：修改配置文件
1. 添加开关配置
``` 
swagger.enabled=true
```
2. 增加一个swagger配置类
``` 
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Value(value = "${swagger.enabled}")
    private Boolean swaggerEnabled;
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(swaggerEnabled)
                .select()
                	//RequestHandlerSelectors.any() 默认值，任意
                	.apis(RequestHandlerSelectors.basePackage("com.coffee"))
                	.paths(PathSelectors.any())//url过滤
                	.build();
    }
    final ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("spring文档接口")
                .description("这是一个接口文档")
                .contact(contact)
                .version("API V1.0")
                .license("Apache")
                .licenseUrl("http://www.apache.org/")
                .build();
    }
    Contact contact=new Contact("刘伟", 
    		"https://github.com/coffeeliuwei/boot", "coffee.liu@gmail.com");
}

```
#### 步骤3：@api具体注解，详细案例
+ **controller注解类** @Api @ApiOpration @ApiImplicitParam
```
@Api(value = "用户操作接口", tags = "用户接口操作")
//替代logFactory生成的log
public class UserController {
	@ApiOperation(value = "/返回用户信息", tags = { "用户", "标签" }, 
			notes = "接口发布说明",response = User.class,
			httpMethod = "POST")
	@ApiImplicitParam(paramType ="query",name = "name",
			value = "用户名",required = true,dataType = "String" )
	@PostMapping("/user/{name}")
	public User user(String name) {
		User user = new User(name);
		user.setAge(18);
		user.setScore(30.1);
		// lombok泛型演示
		Exercise exercise = Exercise.of("liuT", 40);
		user.setTags(new String[] { "liu", exercise.toString() });
		log.trace(user.toString());
		return user;
	}

	@ApiOperation(value = "/返回复杂信息", tags = { "复杂", "标签" }, 
			response = HashMap.class)
	@GetMapping("/val")
	public Object Val() { // var修饰的对象不添加final修饰
		var example = new ArrayList<String>();
		example.add("coffeeliu!");
		// val修饰的对象都自动添加为final类型
		val map = new HashMap<>();
		map.put(1, "liu");
		map.put(2, "wei");
		map.put(3, example);
		return map;
	}
}
```
+ **Model相关注解** @ApiModel @ApiModelProperty
```
@ApiModel("用户实体")
public class User {
	// lombok会将final修饰属性作为构造参数
	@ApiModelProperty(value = "用户名",required =true )
	private final String name;
	@Setter(AccessLevel.PACKAGE)
	@ApiModelProperty(value = "年龄",example = "18")
	private int age;
	@ApiModelProperty("成绩")
	private double score;
	@ApiModelProperty(value = "标签",hidden = true,allowEmptyValue = true)
	private String[] tags;

	@ToString(includeFieldNames = true)
	@Data(staticConstructor = "of")
	// staticConstructor增加对泛型构造函数的支持
	@ApiModel("lombok泛型演示")
	public static class Exercise<T> {
		@ApiModelProperty(value = "用户名",required = true)
		private final String name;
		@ApiModelProperty(value = "泛型value",allowableValues = "Object")
		private final T value;
	}
}
```

#### 效果图及体验地址
http://127.0.0.1:8080/swagger-ui.html
![如图设置](https://github.com/coffeeliuwei/boot/blob/master/img/27.jpg?raw=true)


















