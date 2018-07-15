package cn.edu.fudan.se.clonedetector.display.action;

import cn.edu.fudan.se.clonedetector.display.service.DeleteCommitService;

public class DeleteCommitAction extends AbstractAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean successful;
	private int commitId;
	
	public String deleteCommit(){
		System.out.println("delete commit...");
		((DeleteCommitService) this.getService()).deleteCommit(commitId);
		this.setSuccessful(true);
		return SUCCEED;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public int getCommitId() {
		return commitId;
	}

	public void setCommitId(int commitId) {
		this.commitId = commitId;
	}

}
