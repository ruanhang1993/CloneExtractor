package cn.edu.fudan.se.clonedetector.display.action;

import cn.edu.fudan.se.clonedetector.display.service.UpdateProjectService;

public class UpdateProjectAction extends AbstractAction {
	private int projectId;
	private String projectNameCh;
	private String projectNameEn;
	private String projectTeam;
	private String developCompany;
	
	private String address;
	private String username;
	private String password;
	
	private String stream;
	private String pvob;
	private String component;
	
	private boolean successful;

	public String updateProject() {
		if(stream==null||stream.equals("")){
			successful = ((UpdateProjectService) this.getService()).updateSvnProject(projectId, developCompany, projectTeam, projectNameEn, projectNameCh, address, username, password);
		}else{
			successful = ((UpdateProjectService) this.getService()).updateCcProject(projectId, developCompany, projectTeam, projectNameEn, projectNameCh, stream, pvob, component);
		}
		
		return SUCCEED;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public String getProjectTeam() {
		return projectTeam;
	}

	public void setProjectTeam(String projectTeam) {
		this.projectTeam = projectTeam;
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

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	/**
	 * @param isSuccessful
	 *            the isSuccessful to set
	 */
	public void setSuccessful(boolean isSuccessful) {
		this.successful = isSuccessful;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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
}
