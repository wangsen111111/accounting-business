package com.ywjs.jcsy.exception;


import com.ywjs.jcsy.common.result.Result;
import com.ywjs.jcsy.common.result.ResultCode;

public class GlobalException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	Result result;

	public GlobalException(ResultCode rc) {
		super(rc.toString());
		result = ResultCode.enumToResult(rc);
//		this.cm = cm;
	}

	public Result getResult() {
		return result;
	}

}
