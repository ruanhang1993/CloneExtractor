package cn.edu.fudan.se.clonedetector.display.action;

import cn.edu.fudan.se.clonedetector.display.service.BackupDatabaseService;

public class RestoreDatabaseAction  extends AbstractAction{
	private String username = "root";
	private String password = "root";
	//²¿ÊðÊ±ÇëÐÞ¸Ä
	private String mysqlBinPath="";
	private boolean successful;
	private String fileName;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String restoreDatabase(){
		System.out.println("restore database...");
		successful = ((BackupDatabaseService) this.getService()).restore(mysqlBinPath,username,password,fileName);;
		System.out.println("done");
		return SUCCEED;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the mysqlBinPath
	 */
	public String getMysqlBinPath() {
		return mysqlBinPath;
	}
	/**
	 * @param mysqlBinPath the mysqlBinPath to set
	 */
	public void setMysqlBinPath(String mysqlBinPath) {
		this.mysqlBinPath = mysqlBinPath;
	}
	/**
	 * @return the successful
	 */
	public boolean isSuccessful() {
		return successful;
	}
	/**
	 * @param successful the successful to set
	 */
	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
