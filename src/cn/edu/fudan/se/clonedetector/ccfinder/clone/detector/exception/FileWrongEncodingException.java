package cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.exception;

public class FileWrongEncodingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String fileName;
	public FileWrongEncodingException(String fileName){
		this.fileName = fileName;
	}
	@Override
	public String getMessage() {
		return fileName;		
	}
}
