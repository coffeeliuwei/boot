package com.coffee.exceptions;


import com.coffee.codes.ResultCode;

import lombok.Data;


@Data
public class BusinessException extends RuntimeException {

	protected Integer code;

	protected String message;


	public BusinessException(ResultCode resultCode) {
		this.code = resultCode.code();
		this.message = resultCode.message();
	}

}
