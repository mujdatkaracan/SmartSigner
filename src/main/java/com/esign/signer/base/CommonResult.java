package com.esign.signer.base;

import com.esign.signer.enums.CommonResultType;

public class CommonResult<T> {
	 
	private T data;
	 
	private CommonResultType resultType;
	 
	private Exception exception;
 
	public CommonResult(T data) {
		this.data = data;
		resultType = CommonResultType.SUCCESS;
	}

	public CommonResult(Exception exception) {
		this.exception = exception;
		resultType = CommonResultType.ERROR;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public CommonResultType getResultType() {
		return resultType;
	}

	public void setResultType(CommonResultType resultType) {
		this.resultType = resultType;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

}
