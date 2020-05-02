# Assert断言封装

### 一、本课程目标：
  
  1. 如何使用Assert参数校验？
  
  2. 为什么用了Validator参数校验，还必须再用Assert参数校验？
  
**此课程通用功能全部封装在coffeeliu-boot-commons项目中**

### 二、什么是Assert参数校验？
Assert翻译为中文为"断言".它是spring的一个util类，org.springframework.util.Assert
一般用来断定某一个实际的值是否为自己预期想得到的,如果不一样就抛出异常.

### 三、为什么用了Validator参数校验，还必须再用Assert参数校验？
1. 因为validator只解决了参数自身的数据校验，解决不了参数和业务数据之间校验。
   例如以下代码，validator是搞不定的
``` 
public void test1(int userId) {
    User user = userDao.selectById(userId);
    if (user == null) {
        throw new IllegalArgumentException("用户不存在！");
    }
}
```
2. 采用Assert能使代码更优雅
下以上代码可以转变为以下优雅代码
``` 
public void test2(int userId) {
    User user = userDao.selectById(userId);
    Assert.notNull(user, "用户不存在！");
}
```

### 四、案例实战：修改用户信息时，用Assert校验用户是否存在

#### 步骤1：校验用户是否存在
``` 
    @PostMapping(value = "/user/update")
    public void updateUser(@RequestBody @Validated User use) {
        User user = null;
        //user = userDao.selectById(userId);
        Assert.notNull(user, "用户不存在！");
    }
```
#### 测试结果：
``` 
{
  "timestamp": "2020-10-03T07:40:29.416+0000",
  "status": 500,
  "error": "Internal Server Error",
  "message": "用户不存在！",
  "path": "/user/user/update"
}
```
从以上测试结果可以知道：
  1. 参数校验，一般都是validator和assert 一起结合使用的，validator只解决了参数自身的数据校验，解决不了参数和业务数据之间校验。
  2. 从测试结果看，assert抛出异常是一个json，这个json 不是我们想要的，所以必须和全局异常处理器一起使用封装。

### 五、常用的Assert场景
- 逻辑断言

	1. isTrue()  
	如果条件为假抛出IllegalArgumentException 异常。

	1. state()  
	该方法与isTrue一样，但抛出IllegalStateException异常。

- 对象和类型断言

	1. notNull()  
	通过notNull()方法可以假设对象不null：

	1. isNull()  
	用来检查对象为null:

	1. isInstanceOf()  
	使用isInstanceOf()方法检查对象必须为另一个特定类型的实例

	1. isAssignable()  
	使用Assert.isAssignable()方法检查类型

- 文本断言

	1. hasLength()    
	如果检查字符串不是空符串，意味着至少包含一个空白，可以使用hasLength()方法。

	1. hasText()  
	我们能增强检查条件，字符串至少包含一个非空白字符，可以使用hasText()方法。

	1. doesNotContain()    
	我们能通过doesNotContain()方法检查参数不包含特定子串。

- Collection和map断言  

	1. Collection应用notEmpty()  
	如其名称所示，notEmpty()方法断言collection不空，意味着不是null并包含至少一个元素。

	1. map应用notEmpty()  
	同样的方法重载用于map，检查map不null，并至少包含一个entry（key，value键值对）。

- 数组断言

	1. notEmpty()  
	notEmpty()方法可以检查数组不null，且至少包括一个元素：

	1. noNullElements()  
	noNullElements()方法确保数组不包含null元素：

### 六、将Assert异常加入全局异常处理器
GlobalExceptionHandler.java
**commons模块**
``` 
  /**
     * 400 Assert断言异常封装
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        ErrorResult error = ErrorResult.builder().status(4007)
                .message(e.getMessage())
                .exception(e.getClass().getName())
                .build();
        log.warn("URL:{} ,断言异常:{}", request.getRequestURI(),e);
        return error;
    }
```

### 七：课后练习题
编码体验Assert的isTrue() 、notEmpty()的效果，结合全局异常处理器来实现。
