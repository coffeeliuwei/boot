package com.coffee.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
