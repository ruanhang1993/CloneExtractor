package cn.edu.fudan.se.clonedetector.differ;

/**
 * @author lyk
 *
 */
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.File;
import cn.edu.fudan.se.clonedetector.dao.IDataAccessor;

public interface IDifferDetector {
	public void detectDiffer(List<File> olderFileList, List<File> newFileList, int preCommitId, int commitId, IDataAccessor dao);

	public void addOutcomeNotify(IDifferOutcomeNotify notify);
}
