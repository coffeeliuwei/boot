# response统一格式封装

## 一、本课程目标：
1. 弄清楚为什么要对springboot,所有Controller的response做统一格式封装？
1. 学会用ResponseBodyAdvice接口 和 @ControllerAdvice注

**此课程通用功能全部封装在coffeeliu-boot-commons项目中**
## 二、为什么要对springboot的接口返回值统一标准格式?
我们先来看下，springboot默认情况下的response是什么格式的
### 第一种格式：response为String 
``` 
@GetMapping(value="/getStr")
public String  getStr(  ){
    return  "test";
}
```
以上springboot的返回值为
``` 
test
```
### 第二种格式：response为Objct 
``` 
@GetMapping(value="/getObject")
public User  getObject(  ){
    User user=new User();
    user.setUsername("liuwei");
    return  user;
}
```
以上springboot的返回值为
``` 
{
  "id": null,
  "username": "liuwei",
  "password": null,
  "email": null,
  "phone": null,
  "sex": null,
}
```
### 第三种格式：response为void 
``` 
@GetMapping(value="/empty")
public void  empty(  ){

}
```
以上springboot的返回值为空

### 第四种格式：response为异常 
``` 
@GetMapping(value="/error")
public void  error(  ){
    int i=9/0;
}
```
以上springboot的返回值为系统默认json形式
``` 
{
  "timestamp": "2020-04-07T05:24:16.120",
  "status": 500,
  "error": "Internal Server Error",
  "message": "/ by zero",
  "path": "/user/error"
} 
```
以上4种情况，如果你和客户端（app h5）开发人联调接口，返回数据格式很混乱，因为给他们的接口没有一个统一的格式，客户端开发人员，不知道如何处理返回值。
因此，我们应该统一response的标准格式。

## 三、定义response的标准格式
一般的response的标准格式包含3部分：
  1. status状态值：代表本次请求response的状态结果。
  2. response描述：对本次状态码的描述。
  3. data数据：本次返回的数据。
``` 
{
   "status":0,
   "desc":"成功",
   "data":"test"
}
```
## 四、初级程序员对response代码封装
对response的统一封装，是有一定的技术含量的，我们先来看下，初级程序员的封装。
### 步骤1:把标准格式转换为代码
``` 
{
   "status":0,
   "desc":"成功",
   "data":"test"
}
```
把以上格式转换为Result代码
**commons模块**
``` 
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Result<T> {
    /**
     * 1.status状态值：代表本次请求response的状态结果。
     */
    private Integer status;
    /**
     * 2.response描述：对本次状态码的描述。
     */
    private String desc;
    /**
     * 3.data数据：本次返回的数据。
     */
    private T data;

    /**
     * 成功，创建ResResult：没data数据
     */
    public static Result suc() {
        Result result = new Result();
        result.setResultCode(ResultCode.SUCCESS);
        return result;
    }

    /**
     * 成功，创建ResResult：有data数据
     */
    public static Result suc(Object data) {
        Result result = new Result();
        result.setResultCode(ResultCode.SUCCESS);
        result.setData(data);
        return result;
    }

    /**
     * 失败，指定status、desc
     */
    public static Result fail(Integer status, String desc) {
        Result result = new Result();
        result.setStatus(status);
        result.setDesc(desc);
        return result;
    }

    /**
     * 失败，指定ResultCode枚举
     */
    public static Result fail(ResultCode resultCode) {
        Result result = new Result();
        result.setResultCode(resultCode);
        return result;
    }

    /**
     * 把ResultCode枚举转换为ResResult
     */
    private void setResultCode(ResultCode code) {
        this.status = code.code();
        this.desc = code.message();
    }
}
```
### 步骤2:把状态码存在枚举类里面
**commons模块**
``` 
public enum ResultCode  {
	/* 成功状态码 */
	SUCCESS(0, "成功"),
	/* 系统500错误*/
	SYSTEM_ERROR(500, "系统异常，请稍后重试"),
	/*前端400错误*/
	UN_AUTHORIZED(401, "签名验证失败"),
    MISS_PARAMETER(4001,"缺少请求参数"),
    NOT_READABLE(4002,"参数解析失败"),
    NOT_BIND(4003,"参数绑定失败"),
    NOT_FOUND(404,"找不到页面"),
    NOT_ALLOWED(405,"不支持当前请求方法"),
    UN_SPPORTEDTYPE(415,"不支持当前媒体类型"),
	PARAM_NOT_VALID(4004, "Json参数验证失败"),
	PARAM1_NOT_VALID(4005, "实体参数验证失败"),
	VALID(4006, "参数验证失败"),
	/* 用户错误：20001-29999*/
	USER_HAS_EXISTED(20001, "用户名已存在"),
	USER_NOT_FIND(20002, "用户名不存在");
	private Integer code;

	private String message;

	ResultCode(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	public Integer code() {
		return this.code;
	}

	public String message() {
		return this.message;
	}
}
```

### 步骤3:加一个体验类
``` 
@Api(description = "用户接口")
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @GetMapping(value="/getResult")
    public Result getResult(  ){
        return Result.suc("test");
    }
}
```
结论：看到这里，此封装代码弊端很大。因为今后每写一个接口，都要手工指定Result.suc()。
## 五、高级程序员对response代码封装
如果在公司推广以上编码规范，将会对团队协作造成干扰。
因此我们的目标为：使用增强器统一封装返回值，无需对每个接口指定Result返回值即无需调用Result.suc()。

### 步骤1：采用ResponseBodyAdvice技术来实现response的统一格式
springboot提供了ResponseBodyAdvice来帮我们处理
ResponseBodyAdvice的作用：拦截Controller方法的返回值，统一处理返回值/响应体，一般用来做response的统一格式、加解密、签名等等。
+ ResponseBodyAdvice这个接口的源码:

``` 
public interface ResponseBodyAdvice<T> {
    /**
	 * Whether this component supports the given controller method return type
	 * and the selected {@code HttpMessageConverter} type.
	 * @param returnType the return type
	 * @param converterType the selected converter type
	 * @return {@code true} if {@link #beforeBodyWrite} should be invoked;
	 * {@code false} otherwise
	 */
	boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType);
    /**
	 * Invoked after an {@code HttpMessageConverter} is selected and just before
	 * its write method is invoked.
	 * @param body the body to be written
	 * @param returnType the return type of the controller method
	 * @param selectedContentType the content type selected through content negotiation
	 * @param selectedConverterType the converter type selected to write to the response
	 * @param request the current request
	 * @param response the current response
	 * @return the body that was passed in or a modified (possibly new) instance
	 */
   @Nullable
	T beforeBodyWrite(@Nullable T body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response);
   }

```
### 步骤2：写一个ResponseBodyAdvice实现类
**commons模块**
``` 
@ControllerAdvice(basePackages = "com.coffee")
public class ResponseHandler implements ResponseBodyAdvice<Object> {

    /**
     * 是否支持advice功能
     * treu=支持，false=不支持
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        /**
		 String methodName=returnType.getMethod().getName(); 
        String method= "coffeeliu"; 
		return method.equals(methodName);
		//举例说明：如果方法名为coffeeliu则执行write方法，直接返回true表示不进行方法筛选
		**/
		return true;
    }

    /**
     *
     * 处理response的具体业务方法
     */
    @Override
     public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
       // if (o instanceof ErrorResult) {
       //     ErrorResult errorResult = (ErrorResult) o;
       //     return Result.fail(errorResult.getStatus(),errorResult.getMessage());
      //  } else 
		if (o instanceof String) {
            return JsonUtil.object2Json(Result.suc(o));
        }
        return Result.suc(o);
    }
}

```

#### 说明：
1. @ControllerAdvice 注解：
@ControllerAdvice这是一个非常有用的注解，它的作用是增强Controller的扩展功能类。那@ControllerAdvice对Controller增强了哪些扩展功能呢？主要体现在2方面： 

	1. 对Controller全局数据统一处理。

	1. 对Controller全局异常统一处理。

	在使用@ControllerAdvice时，还要特别注意，加上basePackages,
	@ControllerAdvice(basePackages = "com.coffee"),因为如果不加的话，它可是对整个系统的Controller做了扩展功能，
	它会对某些特殊功能产生冲突，例如**不加的话，在使用swagger时会出现空白页异常**。


2. beforeBodyWrite方法体的response类型判断
``` 
if (o instanceof String) {
            return JsonUtil.object2Json(ResResult.suc(o));
}
```
以上代码一定要加，因为Controller的返回值为String的时候，它是直接返回String,不是json，故我们要手工做下json转换处理

3. 此项目需在pom文件中引入我们自定义的**commons模块**依赖
```
<dependency>
	<groupId>com.coffee</groupId>
	<artifactId>coffeeliu-boot-commons</artifactId>
	<version>${project.parent.version}</version>
</dependency>
```



## 六：总结

#### @ControllerAdvice
  + @ControllerAdvice，是spring3.2提供的新注解。@ControllerAdvice注解内部使用@ExceptionHandler、@InitBinder、@ModelAttribute注解的方法应用到所有的 ***@RequestMapping***注解的方法。
  + 我们要做的就是创建一个用@ControllerAdvice注释的类，并创建相应的三个方法，这三个方法分别用@ExceptionHandler注释以进行全局异常处理，@InitBinder用于全局init绑定，而@ModelAttribute用于全局model属性添加。
  + 当请求达到Controller类中带@RequestMapping注解的方法时，如果没有本地定义的@ExceptionHandler，@InitBinder和@ModelAttribute时，将使用由@ControllerAdvice注解标记的类中的相应方法。
  + 默认情况下，在@ControllerAdvice的方法会应用到所有的Controller中，但是你可以使用@ControllerAdvice的basePackages属性来限制应用到特定包路径下的controller。

#### ResponseBodyAdvice<T>
  + @ResponseBody或者ResponseEntity的控制器方法可以在执行完成之后，response写入输出流（通过HttpMessageConverter）之前，完成对response的拦截

  + 原文重点：Implementations may be registered directly with RequestMappingHandlerAdapter(requestmapping方法适配器) and ExceptionHandlerExceptionResolver(异常方法解析器) or more likely `annotated with @ControllerAdvice` in which case they will be auto-detected by both.

#### @RestController
  + @RestController = @Controller + @ResponseBody

#### @RestControllerAdvice
  + A convenience annotation that is itself annotated with @ControllerAdvice and @ResponseBody

