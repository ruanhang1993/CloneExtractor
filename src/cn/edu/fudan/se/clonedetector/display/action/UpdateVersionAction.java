package cn.edu.fudan.se.clonedetector.display.action;

import cn.edu.fudan.se.clonedetector.display.service.UpdateVersionService;

public class UpdateVersionAction extends AbstractAction {
	private String id;
	private String val;
	private boolean successful;

	public String updateVersion() {
		if(val != null){
			String[] ids = id.replaceAll("\\s","").split(",");
			String[] vals = val.replaceAll("\\s","").split(",");
			
			successful = ((UpdateVersionService) this.getService()).updateVersion(ids, vals);
		}else{
			successful = false;
		}
		return SUCCEED;
	}

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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}
	
	
}
