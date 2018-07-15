package cn.edu.fudan.se.clonedetector.display.action;

import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.ChangeFile;
import cn.edu.fudan.se.clonedetector.bean.File;
import cn.edu.fudan.se.clonedetector.display.service.ChangeFilesExtractService;

public class ChangeFilesExtractAction extends AbstractAction {
	// private Map<String, List<FileDisplayInfo>> versionMap;
	private List<ChangeFile> files;
	private boolean successful;
	private static final long serialVersionUID = 1L;
	private int compareId;

	public String extractChangeFiles() {
		System.out.println("extracting changedfiles...");
		System.out.println("compareId is :" + this.compareId);
		files = (List<ChangeFile>) ((ChangeFilesExtractService) this.getService()).specificedChangesFiles(compareId);

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

	public List<ChangeFile> getFiles() {
		return files;
	}

	public void setFiles(List<ChangeFile> files) {
		this.files = files;
	}

	public int getCompareId() {
		return compareId;
	}

	public void setCompareId(int compareId) {
		this.compareId = compareId;
	}

	

}
