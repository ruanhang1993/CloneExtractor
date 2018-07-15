package cn.edu.fudan.se.clonedetector.display.action;

import java.util.Date;

import main.TestDao;
import cn.edu.fudan.se.clonedetector.display.service.DiffExtractService;

public class DiffExtractAction extends AbstractAction {
	private int preCommitId;
	private int commitId;
	private String compareDate;
	private boolean successful;

	public String extractDiff() {
//		TestDao.test();
		((DiffExtractService) this.getService()).extractDiff(preCommitId, commitId, compareDate);
		successful = true;
		return SUCCEED;
	}

	public int getPreCommitId() {
		return preCommitId;
	}

	public void setPreCommitId(int preCommitId) {
		this.preCommitId = preCommitId;
	}

	public int getCommitId() {
		return commitId;
	}

	public void setCommitId(int commitId) {
		this.commitId = commitId;
	}

	public boolean isSuccessful() {
		return successful;
	}

	/**
	 * @param isSuccessful
	 *            the isSuccessful to set
	 */
	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public String getCompareDate() {
		return compareDate;
	}

	public void setCompareDate(String compareDate) {
		this.compareDate = compareDate;
	}

}
