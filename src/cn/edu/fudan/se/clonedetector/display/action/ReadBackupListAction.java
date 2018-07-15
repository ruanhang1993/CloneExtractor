package cn.edu.fudan.se.clonedetector.display.action;

import java.util.List;

import cn.edu.fudan.se.clonedetector.display.service.BackupDatabaseService;

public class ReadBackupListAction extends AbstractAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean successful;
	private List<String> backupList;
	
	public String readBackupList(){
		System.out.println("get backup list...");
		setBackupList(((BackupDatabaseService) this.getService()).getBackupList());
		System.out.println("done");
		setSuccessful(true);
		return SUCCEED;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public List<String> getBackupList() {
		return backupList;
	}

	public void setBackupList(List<String> backupList) {
		this.backupList = backupList;
	}

}
