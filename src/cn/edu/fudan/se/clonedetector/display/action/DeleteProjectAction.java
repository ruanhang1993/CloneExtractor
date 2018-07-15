package cn.edu.fudan.se.clonedetector.display.action;

import cn.edu.fudan.se.clonedetector.display.service.DeleteProjectService;

public class DeleteProjectAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private boolean successful;
	private int projectId;
	
	public String deleteProject() {
		System.out.println("delete project...");
		((DeleteProjectService) this.getService()).deleteProject(projectId);
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
