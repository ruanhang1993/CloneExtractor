package cn.edu.fudan.se.clonedetector.bean;

import java.util.Date;

/**
 * @author echo
 *
 */
public class Repository implements java.io.Serializable {
	private Integer repositoryId;
	private String url;
	private String username;
	private String password;
	private String language;
	private Date processDate;
	private Date commitDate;
	private Integer analyzeType;
	public Repository() {
	}

	public Repository(Integer repositoryId, String url, String username,
			String password, String language) {
		this.repositoryId = repositoryId;
		this.url = url;
		this.username = username;
		this.password = password;
		this.language = language;
	}

	public Integer getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(Integer repositoryId) {
		this.repositoryId = repositoryId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Date getProcessDate() {
		return processDate;
	}

	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}

	public Date getCommitDate() {
		return commitDate;
	}

	public void setCommitDate(Date commitDate) {
		this.commitDate = commitDate;
	}

	public Integer getAnalyzeType() {
		return analyzeType;
	}

	public void setAnalyzeType(Integer analyzeType) {
		this.analyzeType = analyzeType;
	}
	
}