package cn.edu.fudan.se.clonedetector.display.action;

import java.util.ArrayList;
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Clone;
import cn.edu.fudan.se.clonedetector.display.service.ExtractCloneByCommitService;

public class ExtractCloneByCommitAction extends AbstractAction {
	private List<Clone> clones;
	private List<String> files;
	private boolean successful;
	private static final long serialVersionUID = 1L;
	private int commitId;
	
	public String extractCloneByCommit() {
		System.out.println("In ExtractCloneByCommitAction......");
		clones = (List<Clone>) ((ExtractCloneByCommitService) this.getService()).specificedClones(commitId);
		if(clones != null && clones.size()>0){
			files = new ArrayList<String>();
			for(int i = 0; i < clones.size(); i++){
				String tempFilename = ((ExtractCloneByCommitService) this.getService()).getFilename(((Clone)clones.get(i)).getFileId());
				files.add(tempFilename);
			}
		}
		System.out.println("End ExtractCloneByCommitAction......"+clones.size());
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

	public int getCommitId() {
		return commitId;
	}

	public void setCommitId(int commitId) {
		this.commitId = commitId;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}
	
	
}