### 一、课程目标
将validator异常加入全局异常处理器

### 二、 为什么要用Validator参数校验器，它解决了什么问题？
  背景：在日常的接口开发中，经常要对接口的参数做校验，例如，登录的时候要校验用户名 密码是否为空。但是这种日常的接口参数校验太烦锁了，代码繁琐又多。
Validator框架就是为了解决开发人员在开发的时候少写代码，提升开发效率的；它专门用来做接口参数的校验的，例如 email校验、用户名长度必须位于6到12之间 等等。  
  
  原理：spring 的validator校验框架遵循了JSR-303验证规范（参数校验规范）,JSR是Java Specification Requests的缩写。
在默认情况下，Spring Boot会引入Hibernate Validator机制来支持JSR-303验证规范。

spring boot的validator校验框架有3个特性：
  1. JSR303特性： JSR303是一项标准,只提供规范不提供实现，规定一些校验规范即校验注解，如@Null，@NotNull，@Pattern，位于javax.validation.constraints包下。
  2. hibernate validation特性：hibernate validation是对JSR303规范的实现，并增加了一些其他校验注解，如@Email，@Length，@Range等等
  3. spring validation：spring validation对hibernate validation进行了二次封装，在springmvc模块中添加了自动校验，并将校验信息封装进了特定的类中。

### 三、案例实战：实现一个SpringBoot的参数校验功能
#### 步骤1：pom文件加入依赖包
springboot天然支持validator数据校验
``` 
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
#### 步骤2：创建一个实体类
``` 
@Data
@ApiModel("用户表")
public class User {
    private Integer id;
    @NotEmpty(message="用户名不能为空")
    @Pattern(regexp ="([\\w]|[-_]){4,16}$",message = "请规范填写用户名")
    private String username;
    @NotEmpty(message="密码不能为空")
    @Length(min=6,message="密码长度不能小于6位")
    private String password;
    @Email(message="请输入正确的邮箱")
    private String email;
    @Phone
    private String phone;
}
```

#### 步骤3：在控制器中添加测试函数
``` 
    @PostMapping(value="/user/create")
    public void  createUser( @RequestBody @Validated User user ){
        log.info("---------------crreteUser------------------");
    }
```
注意：注意只有加上@Validated验证功能才起作用，用来校验User的参数是否正确


### 四、Validation常用注解

- @Null 限制只能为null
- @NotNull 限制必须不为null
- @AssertFalse 限制必须为false
- @AssertTrue 限制必须为true
- @DecimalMax(value) 限制必须为一个不大于指定值的数字
- @DecimalMin(value) 限制必须为一个不小于指定值的数字
- @Digits(integer,fraction) 限制必须为一个小数，且整数部分的位数不能超过integer，小数部分的位数不能超过fraction
- @Future 限制必须是一个将来的日期
- @Max(value) 限制必须为一个不大于指定值的数字
- @Min(value) 限制必须为一个不小于指定值的数字
- @Past 限制必须是一个过去的日期
- @Pattern(value) 限制必须符合指定的正则表达式
- @Size(max,min) 限制字符长度必须在min到max之间
- @Past 验证注解的元素值（日期类型）比当前时间早
- @NotEmpty 验证注解的元素值不为null且不为空（字符串长度不为0、集合大小不为0）
- @NotBlank 验证注解的元素值不为空（不为null、去除首位空格后长度为0），不同于@NotEmpty，@NotBlank只应用于字符串且在比较时会去除字符串的空格
- @Email 验证注解的元素值是Email，也可以通过正则表达式和flag指定自定义的email格式


### 五、自定义validator注解
为什么要自定义validator注解呢？
因为validator框架支持的注解有限，不可能方方面面都支持，故需要我们自定义注解。
我们以手机号码为例，自定义validator注解。
#### 步骤1：创建一个@interface的手机校验注解
``` 
@Documented
// 指定注解的实现类
@Constraint(validatedBy = PhoneValidator.class)
@Target( { METHOD, FIELD })
@Retention(RUNTIME)
public @interface Phone {
    String message() default "请输入正确的手机号码";
    boolean isValidetor() default true;
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
```

#### 步骤2：手机号码校验注解实现类
``` 
public class PhoneValidator implements ConstraintValidator<Phone, String> {
	 private String phoneValid ="^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
	 boolean isValidetor;
	 @Override
    public void initialize(Phone phone) {
		 isValidetor = phone.isValidetor();
    }
    /**
     *
     * 返回true表示验证通过，false则不通过，提示错误信息message()
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
    	if(isValidetor){
    	if ( value == null || value.length() == 0 ) {
            return false;
        }
        return Pattern.matches(phoneValid,value);
        }else{
            return true;
        }
    }
}

```

#### 步骤3：给实体类中加入手机号码校验注解
``` 
    @Phone
    private String phone;
```

### 六、把validator异常加入全局异常处理器
那为什么要把validator异常加入全局异常处理器呢？
因为validator异常返回的内容是json比较复杂.不利于客户端联调，而且提示也不友好。

#### 步骤1：全局异常处理器加入validator异常处理

``` 
 /**
     * 400 validator Json约束异常封装
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = this.handle(e.getBindingResult().getFieldErrors());
        ErrorResult error = ErrorResult.fail(ResultCode.PARAM_NOT_VALID, e,  message);
        log.warn("URL:{} ,Json约束参数校验异常:{}", request.getRequestURI(),message);
        return error;
    }
    private String handle(List<FieldError> fieldErrors) {
        StringBuilder sb = new StringBuilder();
        for (FieldError obj : fieldErrors) {
            sb.append(obj.getField());
            sb.append("=[");
            sb.append(obj.getDefaultMessage());
            sb.append("]  ");
        }
		return sb.toString();
    }
    /**
     * 400 - validator 实体约束异常封装
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResult handleServiceException(ConstraintViolationException e, HttpServletRequest request) { 
    	 String message = this.handle(e.getConstraintViolations());
    	 ErrorResult error = ErrorResult.fail(ResultCode.PARAM1_NOT_VALID, e,  message);
        log.warn("URL:{} ,实体校验异常:{}", request.getRequestURI(),message);
        return error;
    }
    private String handle(Set<ConstraintViolation<?>> violations) {
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<?> obj : violations) {
            sb.append(obj.getMessage());
        }
		return sb.toString();
    }
    /**
     * 400 - validator 通用异常封装
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ErrorResult handleValidationException(ValidationException e, HttpServletRequest request) {
    	 log.error("URL:{} ,违反约束:{}", request.getRequestURI(),e);
         return ErrorResult.fail(ResultCode.VALID, e);
    }
```
#### 步骤2：结果
``` 
{
  "status": 10001,
  "desc": "email=[请输入正确的邮箱]  phone=[请输入正确的手机号码]  ",
  "data": null
}
```
### 七、课后练习题
本课程的User,还遗留了一个sex没有校验，请为该字段设计一个自定义注解；
注解的校验规则：0=女，1=男，输入错误提示：性别输入错误！
