package cn.edu.fudan.se.clonedetector.display.action;

import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.File;
import cn.edu.fudan.se.clonedetector.display.service.FilesExtractService;

public class FilesExtractAction extends AbstractAction {
	// private Map<String, List<FileDisplayInfo>> versionMap;
	private List<File> files;
	private boolean successful;
	private static final long serialVersionUID = 1L;
	private int commitId;
	private String relativePath;
	
	public String extractFiles() {
		System.out.println("extracting files...");
		// commits = (List<Commit>)
		// ((VersionExtractService)this.getService()).getVersions();
		System.out.println("commitId is :" + this.commitId) ;
		files = (List<File>) ((FilesExtractService) this.getService()).specificedFiles(commitId);
		relativePath = ((FilesExtractService) this.getService()).getRelativePath(commitId);
		
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

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}

	public int getCommitId() {
		return commitId;
	}

	public void setCommitId(int commitId) {
		this.commitId = commitId;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	
	
}
