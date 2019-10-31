package com.esign.signer.enums;

/**
 * ServiceResultType.java Service işlem sonuc enum bilgileridir. İşlem
 * Başarılı(SUCCESS) ise 200 ve başarılı mesajı döner. İşlem Başarısız
 * (ERROR)ise 500 ve hata mesajı döner.
 * 
 * @author mujdat.karacan
 *
 */
public enum ServiceResultType {
	/**
	 * İşlem Başarılı bilgisi
	 */
	SUCCESS(200, "İşlem Başarılı"),
	/**
	 * Işlem Başarısız veya hata bilgisi
	 */
	ERROR(500, "İşlem Başarısız");

	private int type;
	private String message;

	ServiceResultType(int resultType, String message) {
		this.type = resultType;
		this.message = message;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
