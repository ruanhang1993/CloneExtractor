package cn.edu.fudan.se.clonedetector.display.action;

import cn.edu.fudan.se.clonedetector.bean.Project;
import cn.edu.fudan.se.clonedetector.display.service.GetOneProjectService;

public class GetOneProjectAction extends AbstractAction {
	private int projectId;
	private Project project;
	private boolean successful;

	public String getOneProject() {
		project = ((GetOneProjectService)this.getService()).getOneProject(projectId);
		successful = true;
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

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	
	
	
}
