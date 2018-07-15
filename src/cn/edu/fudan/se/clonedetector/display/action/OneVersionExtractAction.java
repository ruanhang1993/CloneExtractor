package cn.edu.fudan.se.clonedetector.display.action;

import java.util.ArrayList;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.NEW;

import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.bean.CommitLanguage;
import cn.edu.fudan.se.clonedetector.display.service.OneVersionExtractService;

public class OneVersionExtractAction extends AbstractAction {
	// private Map<String, List<FileDisplayInfo>> versionMap;
	private Commit commit;
	private List<CommitLanguage> commitLs; 

	private boolean successful;
	private static final long serialVersionUID = 1L;
	private int commitId;

	private String projectName;

	public String extractOneVersion() {
		System.out.println("extracting commit..." + commitId);
		commit = ((OneVersionExtractService) this.getService()).specifiedCommit(commitId);
		commitLs = ((OneVersionExtractService) this.getService()).involvedCommitLs(commitId);
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

	public int getCommitId() {
		return commitId;
	}

	public void setCommitId(int commitId) {
		this.commitId = commitId;
	}

	public Commit getCommit() {
		return commit;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public List<CommitLanguage> getCommitLs() {
		return commitLs;
	}


}
