package cn.edu.fudan.se.clonedetector.display.action;
import cn.edu.fudan.se.clonedetector.display.service.DeleteCloneService;
public class DeleteCloneAction extends AbstractAction{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean successful;
	private int cloneId;
	public String deleteClone(){
		System.out.println("delete clone...");
		((DeleteCloneService)this.getService()).deleteClone(cloneId);
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

}
