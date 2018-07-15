package cn.edu.fudan.se.clonedetector.differ;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
/**
 * @author lyk
 *
 */
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Change;
import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.bean.Compare;
import cn.edu.fudan.se.clonedetector.bean.File;
import cn.edu.fudan.se.clonedetector.dao.IDataAccessor;

public abstract class AbstractDiffDetector implements IDifferDetector {

	private LinkedList<IDifferOutcomeNotify> outcomeNotifyList = new LinkedList<IDifferOutcomeNotify>();
	protected LinkedList<Change> changeList;
	protected ChangeFactory changeFactory;
	protected int commitId;
	protected int preCommitId;

	@Override
	public void detectDiffer(List<File> oldFileList, List<File> newFileList, int preCommitId, int commitId, IDataAccessor dao) {
		changeList = new LinkedList<Change>();
		this.preCommitId = preCommitId;
		this.commitId = commitId;
		this.getBasicInfomation(oldFileList, newFileList);
		this.matchFile(oldFileList, newFileList);

		this.notifyChangeOutcome(dao);
	}

	private void getBasicInfomation(List<File> oldFileList, List<File> newFileList) {

		changeFactory = new ChangeFactory();
		changeFactory.setCommitId(commitId);
		changeFactory.setPreCommitId(preCommitId);
	}

	// match old file to new file by file name
	private void matchFile(List<File> oldFileList, List<File> newFileList) {

		Comparator<File> fileComparator = new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {

				return o1.getValidFileName().compareTo(o2.getValidFileName());
			}
		};

		oldFileList.sort(fileComparator);
		newFileList.sort(fileComparator);

		Iterator<File> olderFileIterator = oldFileList.iterator();
		Iterator<File> newFileIterator = newFileList.iterator();
		File oldFile = null;
		File newFile = null;
		if (olderFileIterator.hasNext() && newFileIterator.hasNext()) {
			oldFile = olderFileIterator.next();
			newFile = newFileIterator.next();
		}

		LinkedList<File> deleteFileList = new LinkedList<File>();
		LinkedList<File> addFileList = new LinkedList<File>();
		LinkedHashMap<File, File> fileMap = new LinkedHashMap<File, File>();

		while (oldFile != null && newFile != null) {//comment by junyi:get files which have been deleted and add
			int outcome = fileComparator.compare(oldFile, newFile);
			if (outcome < 0) {
				deleteFileList.add(oldFile);
				if (olderFileIterator.hasNext()) {
					oldFile = olderFileIterator.next();
				} else {
					addFileList.add(newFile);
					break;
				}
			} else if (outcome == 0) {//add by junyi:the same file
				fileMap.put(oldFile, newFile);
				if (olderFileIterator.hasNext() && newFileIterator.hasNext()) {
					oldFile = olderFileIterator.next();
					newFile = newFileIterator.next();
				} else {
					break;
				}
			} else {
				addFileList.add(newFile);
				if (newFileIterator.hasNext()) {
					newFile = newFileIterator.next();
				} else {
					deleteFileList.add(oldFile);
					break;
				}
			}
		}

		while (newFileIterator.hasNext()) {
			addFileList.add(newFileIterator.next());
		}

		while (olderFileIterator.hasNext()) {
			deleteFileList.add(olderFileIterator.next());
		}

		this.handleTotalFileChange(addFileList, Change.INSERT);
		this.handleTotalFileChange(deleteFileList, Change.DELETE);
		this.handleUpdateFile(fileMap);

	}

	// implemented in children
	protected void handleTotalFileChange(List<File> totalChangeFileList, int type) {

		int oldFileId = -1;
		int newFileId = -1;
		for (File file : totalChangeFileList) {
			if (type == Change.INSERT) {
				newFileId = file.getFileId();

			} else {
				oldFileId = file.getFileId();
			}

			Change c = changeFactory.produceChange(1, this.convertFileToStringArray(file.getContent()).length, type,
					newFileId, oldFileId);
			changeList.add(c);
		}
	}

	abstract protected void handleUpdateFile(LinkedHashMap<File, File> fileMap);

	@Override
	public void addOutcomeNotify(IDifferOutcomeNotify notify) {
		outcomeNotifyList.add(notify);
	}

	protected void notifyChangeOutcome(IDataAccessor dao) {
		for (IDifferOutcomeNotify notify : outcomeNotifyList) {
			notify.notifyChange(changeList, preCommitId, commitId, dao);
		}
	}

	protected String[] convertFileToStringArray(byte[] fileContent) {
		String all = new String(fileContent);
		return all.split("\n");
	}

	public int getCommitId() {
		return commitId;
	}

	public void setCommitId(int commitId) {
		this.commitId = commitId;
	}

	public int getPreCommitId() {
		return preCommitId;
	}

	public void setPreCommitId(int preCommitId) {
		this.preCommitId = preCommitId;
	}

}
