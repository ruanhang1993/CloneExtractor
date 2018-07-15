package cn.edu.fudan.se.clonedetector.display.action;

import java.util.ArrayList;
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Clone;
import cn.edu.fudan.se.clonedetector.display.service.GetClonesService;

public class GetClonesAction extends AbstractAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean successful;
	private int cloneId;
	private List<Clone> clones;
	private List<String> versions;
	private List<String> files;
	
	public String getCloneObjects(){
		System.out.println("get clone Objects...");
		clones = ((GetClonesService) this.getService()).getCloneObjectsById(cloneId);
		if(clones != null && clones.size()>0){
			files = new ArrayList<String>();
			versions = new ArrayList<String>();
			for(int i = 0; i < clones.size(); i++){
				String tempVersion = ((GetClonesService) this.getService()).getVersion(((Clone)clones.get(i)).getCommitId());
				String tempFilename = ((GetClonesService) this.getService()).getFilename(((Clone)clones.get(i)).getFileId());
				files.add(tempFilename);
				versions.add(tempVersion);
			}
		}
		this.setSuccessful(true);
		return SUCCEED;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public int getCloneId() {
		return cloneId;
	}

	public void setCloneId(int cloneId) {
		this.cloneId = cloneId;
	}

	public List<Clone> getClones() {
		return clones;
	}

	public void setClones(List<Clone> clones) {
		this.clones = clones;
	}

	public List<String> getVersions() {
		return versions;
	}

	public void setVersions(List<String> versions) {
		this.versions = versions;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

}
