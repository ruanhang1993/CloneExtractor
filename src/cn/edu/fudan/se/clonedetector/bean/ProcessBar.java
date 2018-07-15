package cn.edu.fudan.se.clonedetector.bean;

import java.io.Serializable;
import java.util.Date;

public class ProcessBar implements Serializable{
	private String stage;
	private String projectname;
	private String projectnameen;
	private String versionId;
	private int percent;
	private Date scanDate;
	private int commitId;
	
	public String getProjectnameen() {
		return projectnameen;
	}
	public void setProjectnameen(String projectnameen) {
		this.projectnameen = projectnameen;
	}
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
	}
	public String getVersionId() {
		return versionId;
	}
	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}
	public int getPercent() {
		return percent;
	}
	public void setPercent(int percent) {
		this.percent = percent;
	}
	public String getProjectname() {
		return projectname;
	}
	public void setProjectname(String projectname) {
		this.projectname = projectname;
	}
	public Date getScanDate() {
		return scanDate;
	}
	public void setScanDate(Date scanDate) {
		this.scanDate = scanDate;
	}
	public int getCommitId() {
		return commitId;
	}
	public void setCommitId(int commitId) {
		this.commitId = commitId;
	}
	
	
}
