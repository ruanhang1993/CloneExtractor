package cn.edu.fudan.se.clonedetector.display.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.display.service.CommitExtractService;

public class CommitExtractAction extends AbstractAction {
	// private Map<String, List<FileDisplayInfo>> versionMap;
	private boolean successful;
	private String projects;
	private List<Commit> lastCommits = new ArrayList<>();
	
	public String getProjects() {
		return projects;
	}

	public void setProjects(String projects) {
		this.projects = projects;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String extractLastCommit() {
		System.out.println("extracting project last commit...");
		int projectId = -1;
		String[] projectIds = projects.replaceAll("\\s","").split(",");
		for(int i=0;i<projectIds.length;i++){
			try{
				projectId = Integer.parseInt(projectIds[i]);
			}catch(Exception e){
				e.printStackTrace();
			}
			System.out.format("projectId : %d%n", projectId);
			
			Commit lastCommit = ((CommitExtractService) this.getService()).lastCommit(projectId);
			System.out.format("last commit id : %d%n",lastCommit.getCommitId());
			lastCommits.add(lastCommit);
		}
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

	public List<Commit> getLastCommits() {
		return lastCommits;
	}

}
