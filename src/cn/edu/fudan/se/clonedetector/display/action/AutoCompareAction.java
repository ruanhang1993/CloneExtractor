package cn.edu.fudan.se.clonedetector.display.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.*;
import cn.edu.fudan.se.clonedetector.display.service.*;

public class AutoCompareAction extends AbstractAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean successful;
	
	public String autoCompare(Commit preCommit, Commit commit){
		System.out.println("Start auto compare: ");

		
		int preCommitId,commitId;
		preCommitId = preCommit.getCommitId();
		commitId = commit.getCommitId();
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
		Date now = new Date();
		String compareDate = sdf.format( now ); 
		((DiffExtractService) this.getService()).extractDiff(preCommitId, commitId, compareDate);
		this.setSuccessful(true);
		return SUCCEED;
	}

	/**
	 * @return the successful
	 */
	public boolean isSuccessful() {
		return successful;
	}

	/**
	 * @param successful the successful to set
	 */
	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

}
