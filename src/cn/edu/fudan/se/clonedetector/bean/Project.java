package cn.edu.fudan.se.clonedetector.bean;

// Generated 2016-5-20 10:49:33 by Hibernate Tools 3.4.0.CR1

/**
 * Project generated by hbm2java
 */
public class Project implements java.io.Serializable {

	private Integer projectId;
	private String projectNameCh;
	private String projectNameEn;
	private String developCompany;
	private String projectTeam;
	private String projectPath;
	private String projectLanguage;
	private Integer commitId;
	private Integer repositoryId;
	private String stream;
	private String pvob;
	private String component;
	private String view;
	private String viewLocalPath;

	public Project() {
	}

	public Project(String projectNameCh,String projectNameEn,String developCompany, String projectPath,
			String projectLanguage, Integer commitId) {
		this.projectNameCh = projectNameCh;
		this.projectNameEn = projectNameEn;
		this.developCompany = developCompany;
		this.projectPath = projectPath;
		this.projectLanguage = projectLanguage;
		this.commitId = commitId;
	}

	public Integer getProjectId() {
		return this.projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getProjectTeam() {
		return projectTeam;
	}

	public void setProjectTeam(String projectTeam) {
		this.projectTeam = projectTeam;
	}

	public String getProjectPath() {
		return this.projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public String getProjectLanguage() {
		return this.projectLanguage;
	}

	public void setProjectLanguage(String projectLanguage) {
		this.projectLanguage = projectLanguage;
	}

	public Integer getCommitId() {
		return this.commitId;
	}

	public void setCommitId(Integer commitId) {
		this.commitId = commitId;
	}

	public Integer getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(Integer repositoryId) {
		this.repositoryId = repositoryId;
	}

	public String getProjectNameCh() {
		return projectNameCh;
	}

	public void setProjectNameCh(String projectNameCh) {
		this.projectNameCh = projectNameCh;
	}

	public String getProjectNameEn() {
		return projectNameEn;
	}

	public void setProjectNameEn(String projectNameEn) {
		this.projectNameEn = projectNameEn;
	}

	public String getDevelopCompany() {
		return developCompany;
	}

	public void setDevelopCompany(String developCompany) {
		this.developCompany = developCompany;
	}

	public String getStream() {
		return stream;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}

	public String getPvob() {
		return pvob;
	}

	public void setPvob(String pvob) {
		this.pvob = pvob;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getViewLocalPath() {
		return viewLocalPath;
	}

	public void setViewLocalPath(String viewLocalPath) {
		this.viewLocalPath = viewLocalPath;
	}
	
	
}
