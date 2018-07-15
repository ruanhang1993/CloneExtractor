package cn.edu.fudan.se.clonedetector.display.action;

import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.bean.Project;
import cn.edu.fudan.se.clonedetector.display.service.VersionExtractService;

public class VersionExtractAction extends AbstractAction {
	// private Map<String, List<FileDisplayInfo>> versionMap;
	private List<Commit> commits;
	private boolean successful;
	private static final long serialVersionUID = 1L;
	private int projectId;
	private String projectName;
	private Project thisProject;

	public String extractVersions() {
		System.out.println("extracting versions...");
		// commits = (List<Commit>)
		// ((VersionExtractService)this.getService()).getVersions();
		System.out.println("projectId is :" + this.projectId);
		commits = (List<Commit>) ((VersionExtractService) this.getService()).specificedCommits(projectId);
		projectName = ((VersionExtractService) this.getService()).getProjectName(projectId);
		thisProject = ((VersionExtractService) this.getService()).getProject(projectId);

		this.setSuccessful(true);
		System.out.println("done");
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

	public List<Commit> getCommits() {
		return commits;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Project getThisProject() {
		return thisProject;
	}

	public void setThisProject(Project thisProject) {
		this.thisProject = thisProject;
	}
	
}
