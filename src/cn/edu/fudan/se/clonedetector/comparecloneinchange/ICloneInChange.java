package cn.edu.fudan.se.clonedetector.comparecloneinchange;

import cn.edu.fudan.se.clonedetector.dao.IDataAccessor;

public interface ICloneInChange {
	public void CloneInChange(int preCommitId, int commitId, int compareId, IDataAccessor dao);

	public int returnFinalLine();

	public int returnEfinalLine();
}
