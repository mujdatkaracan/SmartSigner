package com.esign.signer.model;

/**
 * FileProperties.java sınıfı dosya bilgilerini tutar.
 * @author mujdat.karacan
 *
 */
public class FilePropertiesModel {

	private String filePath;

	private String fileName;
	private byte[] content;
	private boolean isExist;
	
	public FilePropertiesModel() {
	} 
	 
	
	public FilePropertiesModel(String filePath, String fileName, byte[] content, boolean isExist) {
		this.filePath = filePath;
		this.fileName = fileName;
		this.content = content;
		this.isExist = isExist;
	}


	public boolean isExist() {
		return isExist;
	}


	public void setExist(boolean isExist) {
		this.isExist = isExist;
	}


	public byte[] getContent() {
		return content;
	}
	public String getFileName() {
		return fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
