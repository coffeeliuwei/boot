package com.coffee.exceptions;


import lombok.extern.slf4j.Slf4j;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.coffee.codes.ResultCode;
import com.coffee.response.ErrorResult;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import java.util.List;
import java.util.Set;

@RestControllerAdvice(basePackages = "com.coffee" )
//返回json格式错误让ResponseBodyAdvice调用
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionHandler  {
	
	 /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ErrorResult handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.error("URL:{} ,缺少请求参数:{}", request.getRequestURI(),e);
        return ErrorResult.fail(ResultCode.MISS_PARAMETER, e);
    }
    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResult handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.error("URL:{} ,参数解析失败:{}",request.getRequestURI(), e);
        return ErrorResult.fail(ResultCode.NOT_READABLE, e);
    }
    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ErrorResult handleBindException(BindException e, HttpServletRequest request) {
        String message = this.handle(e.getBindingResult().getFieldErrors());
        log.error("URL:{} ,参数绑定失败:{}", request.getRequestURI(),e);
        return ErrorResult.fail(ResultCode.NOT_BIND, e,message);
    }
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

    /**
     * 404 - Not Found
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ErrorResult noHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.error("URL:{} ,Not Found:{}", request.getRequestURI(),e);
        return ErrorResult.fail(ResultCode.NOT_FOUND, e);
    }
    /**
     * 405 - Method Not Allowed
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResult handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.error("URL:{} ,不支持当前请求方法:{}",request.getRequestURI(), e);
        return ErrorResult.fail(ResultCode.NOT_ALLOWED, e);
    }

    /**
     * 415 - Unsupported Media Type
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ErrorResult handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e, HttpServletRequest request) {
        log.error("URL:{} ,不支持当前媒体类型:{}",request.getRequestURI(), e);
        return ErrorResult.fail(ResultCode.UN_SPPORTEDTYPE, e);
    }

    /**
     * 500 - 处理所有内部异常
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value ={ Throwable.class } )
    public ErrorResult handleThrowable(Throwable ex, HttpServletRequest request) {
    	 log.error("URL:{} ,系统异常: :{}",request.getRequestURI(), ex);
        return ErrorResult.fail(ResultCode.SYSTEM_ERROR, ex); 
    }

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

}
