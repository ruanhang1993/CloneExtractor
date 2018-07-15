package cn.edu.fudan.se.clonedetector.differ;

import cn.edu.fudan.se.clonedetector.bean.Change;
import cn.edu.fudan.se.clonedetector.ccfinder.evolution.EvolutionAnalyse;

public class ChangeFactory {
	private int commitId;

	private int preCommitId;

	public void setCommitId(int commitId) {
		this.commitId = commitId;
	}

	public void setPreCommitId(int preCommitId) {
		this.preCommitId = preCommitId;
	}

	public Change produceChange(int beginLine, int endLine, int changeType, String fragment, int fileId,
			int oldFileId) {
		Change change = new Change(changeType, beginLine, endLine, commitId, fileId, oldFileId);
		int res[] = new EvolutionAnalyse("", "./"+preCommitId+"_"+commitId).countIncompleteLineByString(beginLine, endLine, fragment);
		int bchloc = res[0];
		int cmchloc = res[1];
		int echloc = res[2];
		change.setEchloc(echloc);
		change.setBchloc(bchloc);
		change.setCmchloc(cmchloc);
		change.setPreCommitId(preCommitId);
		return change;
	}

	public Change produceChange(int beginLine, int endLine, int changeType, int fileId, int oldFileId) {
		Change change = new Change(changeType, beginLine, endLine, commitId, fileId, oldFileId);
		change.setPreCommitId(preCommitId);
		return change;
	}

	public Change produceChange(int beginLine, int type, int fileId, int oldFileId) {
		Change ret = new Change();
		ret.setBeginLine(beginLine);
		ret.setCommitId(commitId);
		ret.setPreCommitId(preCommitId);
		ret.setChangeType(type);
		ret.setFileId(fileId);
		ret.setOldFileId(oldFileId);
		return ret;
	}
}
