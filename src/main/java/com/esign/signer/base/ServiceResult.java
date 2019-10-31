package com.esign.signer.base;

import com.esign.signer.enums.ServiceResultType;

/**
 * ServiceResult.java servis cevabını barındıran sınıftır.
 * 
 * T tipinde generic olarak aldığı dataya göre işlem sonucu başarılı,başarısız
 * olduğunu ve nedenin açıklamasını barındaran sınıftır.
 * @param <T> generic data type
 * 
 * @author mujdat.karacan 
 */
public class ServiceResult<T> {
	 
	private T data;
	 
	private ServiceResultType resultType;
	 
	private Exception exception;
 
	public ServiceResult(T data) {
		this.data = data;
		resultType = ServiceResultType.SUCCESS;
	}

	public ServiceResult(Exception exception) {
		this.exception = exception;
		resultType = ServiceResultType.ERROR;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public ServiceResultType getResultType() {
		return resultType;
	}

	public void setResultType(ServiceResultType resultType) {
		this.resultType = resultType;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

}
