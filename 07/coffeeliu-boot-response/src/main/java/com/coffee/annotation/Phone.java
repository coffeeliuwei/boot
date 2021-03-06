package com.coffee.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

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
