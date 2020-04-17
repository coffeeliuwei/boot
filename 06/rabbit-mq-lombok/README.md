#在springboot中使用lombok

## 一、目标：
1. 学会安装lombok插件，并学会用lombok。
2. 掌握lombok的核心@Data注解
3. 掌握lombok的核心@log注解


## 二、为什么要使用lombok,它解决了什么问题？
Lombok 是一个 IDEA 插件,也是一个依赖jar 包。
它解决了开发人员少写代码，提升开发效率。
它使开发人员不要去写javabean的getter/setter方法，写构造器、equals等方法；最方便的是你对javabean的属性增删改，
你不用再重新生成getter/setter方法。省去一大麻烦事。

## 三、安装lombok插件
+ 双击lombok.jar（可从此站点或maven存储库下载；它是同一jar）。这将启动Eclipse安装程序，该安装程序将找到Eclipse（以及上面列出的Eclipse变体），并提供将Lombok安装到这些Eclipse安装中的功能。相同的工具也可以卸载lombok：
![如图设置](https://github.com/coffeeliuwei/boot/blob/master/img/25.jpg?raw=true)
+ 或者将Lombok添加到Pom文件中
```
<dependency>
		<groupId>org.projectlombok</groupId>
		<artifactId>lombok</artifactId>
		<version>1.18.12</version>
		<scope>provided</scope>
	</dependency>
```


## 四、体验lombok核心注解
+ 配置文件
```
server.port=8080
logging.level.com.coffee=trace
logging.file.path=output/logs
logging.pattern.console=%d{yyyy-MM-dd} %-5level %logger{10} -%msg%n
logging.pattern.file=%d{yyyy/MM/dd} = [%thread] =%-5level = %logger{50} = %msg%n
```
+ User.java
```
@Data
/**
 * 一锅端涵盖：
 * @ToString，
 * @EqualsAndHashCode，
 * @Getter 在所有领域，
 * @Setter 所有非final字段， 
 * @RequiredArgsConstructor
 **/
public class User {
	// lombok会将final修饰属性作为构造参数
	private final String name;
	@Setter(AccessLevel.PACKAGE)
	private int age;
	private double score;
	private String[] tags;

	@ToString(includeFieldNames = true)
	@Data(staticConstructor = "of")
	// staticConstructor增加对泛型构造函数的支持
	public static class Exercise<T> {
		private final String name;
		private final T value;
	}
}
```
+ UserController.java
```
@RestController
@Slf4j
//替代logFactory生成的log
public class UserController {

	@RequestMapping("/user")
	public User user()
	{
		User user=new User("liuwei");
		user.setAge(18);
		user.setScore(30.1);
		
		//lombok泛型演示
	 Exercise exercise=Exercise.of("liuT", 40);
	 user.setTags(new String[]{"liu",exercise.toString()});
	 log.trace(user.toString());
		return user;
	}
	@RequestMapping("/val")
	public Object Val()
	{   //var修饰的对象不添加final修饰
		var example = new ArrayList<String>();
	    example.add("coffeeliu!");
		//val修饰的对象都自动添加为final类型
		val map=new HashMap<>();
		map.put(1, "liu");
		map.put(2, "wei");
		map.put(3, example);
		return map;
		
	}
}
```
## 五、运行结果
![如图设置](https://github.com/coffeeliuwei/boot/blob/master/img/23.jpg?raw=true)
![如图设置](https://github.com/coffeeliuwei/boot/blob/master/img/24.jpg?raw=true)

## 六、课后练习题
```
public class City {

    private Long id;

    private Long provinceId;

    private String cityName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
```
请把以上City类，转换为lombok对象。
然后建个测试类，执行以下代码
```
City city=new City();
city.setId(100);
city.setCityName("深圳");
city.setProvinceId(200);
log.error("-----------error-------------"+city.toString());
```
















