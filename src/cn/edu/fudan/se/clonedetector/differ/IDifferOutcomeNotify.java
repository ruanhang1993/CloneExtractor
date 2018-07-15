/**
 * 
 */
package cn.edu.fudan.se.clonedetector.differ;

import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Change;
import cn.edu.fudan.se.clonedetector.dao.IDataAccessor;

/**
 * @author lyk
 *
 */
public interface IDifferOutcomeNotify {
	public void notifyChange(List<Change> changeList, int preCommitId, int commitId, IDataAccessor dataAccessor);
}
