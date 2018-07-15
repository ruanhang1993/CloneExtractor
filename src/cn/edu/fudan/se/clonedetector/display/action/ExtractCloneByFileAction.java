package cn.edu.fudan.se.clonedetector.display.action;

import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Clone;
import cn.edu.fudan.se.clonedetector.display.service.ExtractCloneByFileService;

public class ExtractCloneByFileAction extends AbstractAction {
	private List<Clone> clones;
	private boolean successful;
	private static final long serialVersionUID = 1L;
	private int fileId;
	public String extractCloneByFile() {
		System.out.println("In ExtractCloneByFileAction......");
		clones = (List<Clone>) ((ExtractCloneByFileService) this.getService()).specificedClones(fileId);
		System.out.println("End ExtractCloneByFileAction......"+clones.size());
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

	public List<Clone> getclones() {
		return clones;
	}

	public void setClones(List<Clone> clones) {
		this.clones = clones;
	}

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}
}