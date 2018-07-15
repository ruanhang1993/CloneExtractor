package cn.edu.fudan.se.clonedetector.display.action;

import cn.edu.fudan.se.clonedetector.display.service.FreshProjectService;

public class FreshProjectAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private boolean successful;
	private int projectId;
	
	public String freshProject() {
		System.out.println("fresh project...");
		((FreshProjectService) this.getService()).freshProject(projectId);
		this.setSuccessful(true);
		return SUCCEED;
	}
	
	/**
	 * @return the isSuccessful
	 */
	public boolean isSuccessful() {
		return successful;
	}

	/**
	 * @param isSuccessful
	 *            the isSuccessful to set
	 */
	public void setSuccessful(boolean isSuccessful) {
		this.successful = isSuccessful;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

}
