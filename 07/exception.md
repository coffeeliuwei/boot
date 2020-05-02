# Exception封装

## 一、本课程目标：
将**全局异常处理器**集成进**接口返回值统一标准格式**

**此课程通用功能全部封装在coffeeliu-boot-commons项目中**
## 二、springboot为什么需要全局异常处理器？
1. 先讲下什么是全局异常处理器  
全局异常处理器就是把整个系统的异常统一自动处理，程序员可以做到不用写try...catch
2. 那为什么需要全局异常呢
- 第一个原因：不用强制写try-catch,由全局异常处理器统一捕获处理
``` 
@PostMapping(value="/error1")
public void  error1(  ){
	int i=9/0;
}
```
如果不用try-catch捕获的话，客户端就会怎么样?
客户端报错信息：
``` 
{
  "timestamp": "2020-04-02T02:15:26.591+0000",
  "status": 500,
  "error": "Internal Server Error",
  "message": "/ by zero",
  "path": "/user/error1"
}
```
这种格式对于客户端来说，不友好，而一般程序员的try-catch也不够友好。
``` 
@PostMapping(value="/error11")
public String  error11(  ){
	try{
		int i=9/0;
	}catch (Exception ex){
		log.error("异常：{}",ex);
		return "no";
	}
	return "ok";
}
```


- 第二个原因：自定义异常，只能用全局异常来捕获。
``` 
@PostMapping(value="/error1")
public void  error1(  ){
	throw new RuntimeException("用户已存在！！");
}
```
客户端报错结果：
``` 
{
  "timestamp": "2020-04-02T02:18:26.843+0000",
  "status": 500,
  "error": "Internal Server Error",
  "message": "用户已存在！！",
  "path": "/user/error4"
}
```
报错结果过于粗糙不应直接暴露给客户端，所以需要使用**接口返回值统一标准格式**

- 第三个原因：JSR303规范的Validator参数校验器，参数校验不通过会抛异常，是无法使用try-catch语句直接捕获，
只能使用全局异常处理器了。

## 三、案例实战：编码实现一个springboot*全局异常处理器*

### 步骤1：封装异常内容，统一存储在枚举类中
把所有的未知运行是异常都用SYSTEM_ERROR(500, "系统异常，请稍后重试")来提示
**commons模块**
ResultCode.java
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
### 步骤2：封装异常结果类
期望返回给客户的统一格式：
``` 
{
  "status": 500,
  "message": "系统异常，请稍后重试",
  "exception": "java.lang.ArithmeticException"
}
```
建立错误结果类封装错误
**commons模块**
``` 
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorResult {
	/**
	 * 异常状态码
	 */
	private Integer status;
	/**
	 * 用户看得见的异常，例如 用户名重复！！,
	 */
	private String message;
	/**
	 * 异常的名字
	 */
	private String exception;
	/**
	 * 对自带异常提示语进行封装
	 */
	public static ErrorResult fail(ResultCode resultCode, Throwable e,String message) {
		ErrorResult result = ErrorResult.fail(resultCode, e);
		result.setMessage(message);
		return result;
	}
	/**
	 * 对ResultCode进行封装
	 */
	public static ErrorResult fail(ResultCode resultCode, Throwable e) {
		ErrorResult result = new ErrorResult();
		result.setMessage(resultCode.message());
		result.setStatus(resultCode.code());
		result.setException(e.getClass().getName());
		return result;
	}
}
```

### 步骤3：加全局异常处理器，对异常进行处理
GlobalExceptionHandler.java
**commons模块**
``` 
@RestControllerAdvice(basePackages = "com.coffee" )
//返回json格式错误让ResponseBodyAdvice调用
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionHandler  {
    /**
     * 500 - 处理所有内部异常
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value ={ Throwable.class } )
    public ErrorResult handleThrowable(Throwable ex, HttpServletRequest request) {
    	 log.error("URL:{} ,系统异常: :{}",request.getRequestURI(), ex);
        return ErrorResult.fail(ResultCode.SYSTEM_ERROR, ex); 
    }
}

```
handleThrowable方法的作用是：捕获内部异常，并把异常统一封装为ErrorResult对象。

说明：
  
1. @RestControllerAdvice：A convenience annotation that is itself annotated with @ControllerAdvice and @ResponseBody

2. @ExceptionHandler为统一处理某一类异常，从而能够减少代码重复率和复杂度，@ExceptionHandler(Throwable.class)指处理Throwable的异常。

3. @ResponseStatus指定客户端收到的http状态码，这里配置500错误，客户端就显示500错误，

### 步骤4：体验效果
``` 
    @PostMapping(value="/error")
    public void  error1(  ){
        int i=9/0;
    }
```
结果
``` 
{
  "status": 500,
  "message": "系统异常，请稍后重试",
  "exception": "java.lang.ArithmeticException"
}
```

## 四、案例实战：把自定义异常集成进全局异常处理器

### 步骤1：封装一个自定义异常

自定义异常通常是集成RuntimeException
**commons模块**
``` 
@Data
public class BusinessException extends RuntimeException {
	protected Integer code;
	protected String message;
	public BusinessException(ResultCode resultCode) {
		this.code = resultCode.code();
		this.message = resultCode.message();
	}
}
```

### 步骤2：把自定义异常集成进全局异常处理器
添加一个自定义异常处理。
**commons模块**
GlobalExceptionHandler.java
``` 
	/**
     * 处理自定义异常
     */
	@ExceptionHandler(BusinessException.class)
	public ErrorResult handleBusinessException(BusinessException e, HttpServletRequest request) {
        ErrorResult error = ErrorResult.builder().status(e.code)
                .message(e.message)
                .exception(e.getClass().getName())
                .build();
        log.warn("URL:{} ,业务异常:{}", request.getRequestURI(),error);
        return error;
	}
```
### 步骤3：体验效果
``` 
    @PostMapping(value="/error1")
    public void  error3(  ){
        throw new BusinessException(ResultCode.USER_HAS_EXISTED);
    }
```
结果
``` 
{
  "status": 20001,
  "message": "用户名已存在",
  "exception": "com.coffee.boot.exceptions.BusinessException"
}
```

## 五、案例实战：把全局异常处理器集成进接口返回值统一标准格式
目标：把全局异常处理器的json格式转换为接口返回值统一标准格式格式
原形式：
``` 
{
  "status": 20001,
  "message": "用户名已存在",
  "exception": "com.coffee.boot.exceptions.BusinessException"
}
```
期望形式：
``` 
{
   "status":20001,
   "desc":"用户名已存在",
   "data":null
}
```
### 步骤1：改造ResponseHandler
**commons模块**
``` 
@ControllerAdvice(basePackages = "com.coffee")
@Order(Ordered.LOWEST_PRECEDENCE)
public class ResponseHandler implements ResponseBodyAdvice<Object> {

    /**
     * 是否支持advice功能
     * treu=支持，false=不支持
     */
    @Override
    public boolean supports(MethodParameter methodParameter, 
		Class<? extends HttpMessageConverter<?>> aClass) {
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
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, 
		MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, 
		ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (o instanceof ErrorResult) {
            ErrorResult errorResult = (ErrorResult) o;
            return Result.fail(errorResult.getStatus(),errorResult.getMessage());
        } else if (o instanceof String) {
            return JsonUtil.object2Json(Result.suc(o));
        }
        return Result.suc(o);
    }
}
```
## 六：课后练习题
自己手写一个全局异常处理器和接口返回值统一标准格式  

  1. 模拟一个空指针异常，然后返回以下接口返回值统一标准格式：
``` 
{
  "code": 10000,
  "msg": "系统异常请稍后...",
  "data": null
}
```

  2.  模拟用户登录，自定义异常，提示以下内容
``` 
{
  "code": 20003,
  "msg": "用户名或密码错误，请重试",
  "data": null
}
```
