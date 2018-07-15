package cn.edu.fudan.se.clonedetector.display.action;

import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Compare;
import cn.edu.fudan.se.clonedetector.display.service.CompareExtractService;

public class CompareExtractAction extends AbstractAction {
	// private Map<String, List<FileDisplayInfo>> versionMap;
	private List<Compare> compares;
	private boolean successful;
	private static final long serialVersionUID = 1L;
	private int projectId;
	private String projectName;

	public String extractCompares() {
		System.out.println("extracting compares...");
		System.out.println("projectId is :" + this.projectId);
		compares = (List<Compare>) ((CompareExtractService) this.getService()).specificedCompares(projectId);
		projectName = ((CompareExtractService) this.getService()).getProjectName(projectId);
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

	public List<Compare> getCompares() {
		return compares;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public void setCompares(List<Compare> compares) {
		this.compares = compares;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}
