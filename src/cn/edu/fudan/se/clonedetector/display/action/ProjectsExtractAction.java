package cn.edu.fudan.se.clonedetector.display.action;

import java.util.Date;
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Project;
import cn.edu.fudan.se.clonedetector.display.service.ProjectsExtractService;

public class ProjectsExtractAction extends AbstractAction {
	// private Map<String, List<FileDisplayInfo>> versionMap;
	private List<Project> Projects;
	private boolean successful;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String extractProjects() {
		System.out.println("extracting projects...");
		// Projects = (List<Project>)
		// ((VersionExtractService)this.getService()).getVersions();
		Projects = (List<Project>) ((ProjectsExtractService) this.getService()).specificedProjects();
		System.out.println(Projects.size());
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
	// /**
	// * @return the version map
	// */
	// public Map<String, List<FileDisplayInfo>> getVersions() {
	// return versionMap;
	// }

	public List<Project> getProjects() {
		return Projects;
	}

}
