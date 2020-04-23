package com.coffee.codes;


import java.util.List;

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
