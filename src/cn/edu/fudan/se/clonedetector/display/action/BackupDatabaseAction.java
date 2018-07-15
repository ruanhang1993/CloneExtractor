package cn.edu.fudan.se.clonedetector.display.action;

import cn.edu.fudan.se.clonedetector.display.service.BackupDatabaseService;

public class BackupDatabaseAction extends AbstractAction{
	private String username = "root";
	private String password = "root";
	//²¿ÊðÊ±ÇëÐÞ¸Ä
	private String mysqlBinPath="";
	private boolean successful;
	private String path;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String backupDatabase(){
		System.out.println("backup database...");
		successful = ((BackupDatabaseService) this.getService()).backup(mysqlBinPath,username,password,path);
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
