package cn.edu.fudan.se.clonedetector.comparecloneinchange;

import java.util.HashMap;
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Change;
import cn.edu.fudan.se.clonedetector.bean.Clone;
import cn.edu.fudan.se.clonedetector.bean.CloneInChange;
import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.bean.File;
import cn.edu.fudan.se.clonedetector.ccfinder.evolution.EvolutionAnalyse;
import cn.edu.fudan.se.clonedetector.dao.IDataAccessor;
import cn.edu.fudan.se.clonedetector.dao.hibernate.HibernateDao;

public class CloneInChangeExtractor implements ICloneInChange {
//	protected IDataAccessor dao;
	double[][] weight = { { 1.05, 1.1, 1.15, 1.2, 1.25, 1.28, 1.29, 1.30 },
			{ 1.1, 1.15, 1.2, 1.25, 1.28, 1.29, 1.30 } };
	public int finalAddedLine = 0;
	public int finalDeletedLine = 0;
	public int efinalAddedLine = 0;
	public int efinalDeletedLine = 0;

//	public static void main(String[] args) {
//		ICloneInChange icic = new CloneInChangeExtractor();
//		icic.CloneInChange(4, 1, 1000);
//
//	}

//	public void test() {
//		dao = HibernateDao.getInstance();
//		Commit commits = dao.getCommitById(1);
//		System.out.println(commits.getPreRevisionId());
//
//	}

	@Override
	public void CloneInChange(int preCommitId, int commitId, int compareId, IDataAccessor dao) {
		List<Change> deletedChangeSet = dao.getChangesOfCommitIdAndType(preCommitId, commitId, Change.DELETE);
		List<Change> addedChangeSet = dao.getChangesOfCommitIdAndType(preCommitId, commitId, Change.INSERT);
		System.out.println("increase change size is " + addedChangeSet.size());
		int deleteLine = returnCodeLine(deletedChangeSet, preCommitId, compareId, Change.DELETE,dao);
		int addLine = returnCodeLine(addedChangeSet, commitId, compareId, Change.INSERT,dao);
		System.out.printf("delete Line %d\nadd Line %d\n", deleteLine, addLine);
	}

	public int returnCodeLine(List<Change> changeSet, int preCommitId, int compareId, int changeType, IDataAccessor dao) {
		HashMap<Integer, Integer> deleteId2Size = new HashMap<Integer, Integer>();
		int finalLine = 0;
		int efinalLine = 0;
		int base = 0;
		for (Change change : changeSet) {
			int changeSL = change.getBeginLine();
			int changeEL = change.getEndLine();
			int fileId = -1;
			if (changeType == change.DELETE) {
				fileId = change.getOldFileId();
				base = 1;
			} else if (changeType == change.INSERT) {
				fileId = change.getFileId();
				base = 0;
			}
			List<Clone> prevCloneSet = dao.getClonesOfCommitIdAndFileId(preCommitId, fileId);
			for (Clone clone : prevCloneSet) {

				int cloneSL = clone.getBeginLine();
				int cloneEL = clone.getEndLine();
				if (Math.min(changeEL, cloneEL) - Math.max(changeSL, cloneSL) < 0)
					continue;
				int cloneLen = Math.min(changeEL, cloneEL) - Math.max(changeSL, cloneSL) + 1;
				// double w =
				// weight[clone.getCloneType()][clone.getCloneSize()];
				int begin = Math.max(changeSL, cloneSL);
				int end = Math.min(changeEL, cloneEL);
				int cloneClassId = clone.getCloneClassId();
				int size = 0;
				if (deleteId2Size.containsKey(cloneClassId)) {
					size = deleteId2Size.get(cloneClassId);
					deleteId2Size.put(clone.getCloneClassId(), size - 1);
				} else {
					size = clone.getCloneSize();
					deleteId2Size.put(clone.getCloneClassId(), clone.getCloneSize());
				}

				CloneInChange clich = new CloneInChange();
				clich.setBeginLine(begin);
				clich.setEndLine(end);
				clich.setChangeId(change.getChangeId());
				clich.setChangeType(change.getChangeType());
				clich.setCloneType(clone.getCloneType());
				clich.setCloneClassId(clone.getCloneClassId());
				clich.setCloneId(clone.getCloneId());
//				int cloneInChangeId = dao.getLastCloneInChangeId() + 1;
//				clich.setCloneInChangeId(cloneInChangeId);
				clich.setFileId(change.getFileId());
				clich.setCompareId(compareId);
				File realFile = dao.getFileById(fileId);
				String fileContent = new String(realFile.getContent());
				int res[] = new EvolutionAnalyse("", ""+compareId).countIncompleteLineByString(begin, end, fileContent);
				int bcloc = res[0];
				int cmcloc = res[1];				
				int ecloc = res[2];
				int cloc = end-begin+1;
				int repeatTime = clone.getRepeatTime();
				int ccloc = 0,eccloc =0;
				int repeatType=dao.getRepeatType(repeatTime);
				double[]lengthScore={0,0.5,0.6,0.7,0.8,0.9};
				double[]repeatScore={0,0.1,0.3,0.5,0.8,1};
				ccloc = (int) (cloc*(1-lengthScore[dao.getLengthType(cloc)]*repeatScore[repeatType]));
				eccloc = (int) (ecloc*(1-lengthScore[dao.getLengthType(ecloc)]*repeatScore[repeatType]));
				int extent = getLevel(size);
				if (extent >= 0) {
					double w = weight[0][extent] - base;
					finalLine += (int) (w * cloneLen);
					efinalLine += (int) (w * ecloc);
				}
				clich.setEcloc(ecloc);
				clich.setBcloc(bcloc);
				clich.setCmcloc(cmcloc);
				clich.setCcloc(ccloc);
				clich.setEccloc(eccloc);
				dao.saveCloneInChange(clich);
				System.out.format("save clone_id(%d) successfully %n" ,clone.getCloneId());
				// CloneClass cc = getCloneClass(clone.getClassId());
				// cc.setSize(cc.getSize - 1);
			}
		}
		if (changeType == Change.DELETE) {
			this.finalDeletedLine = finalLine;
			this.efinalDeletedLine = efinalLine;
		} else if (changeType == Change.INSERT) {
			this.finalAddedLine = finalLine;
			this.efinalAddedLine = efinalLine;
		}
		return finalLine;
	}

	public int getLevel(int size) {
		int result = 0;
		if (size <= 3)
			result = size - 1;
		else if (size < 6)
			result = 3;
		else if (size < 15)
			result = 4;
		else if (size < 30)
			result = 5;
		else
			result = 6;
		return result;
	}

	@Override
	public int returnFinalLine() {
		return this.finalDeletedLine - this.finalAddedLine;
	}

	@Override
	public int returnEfinalLine() {
		return this.efinalDeletedLine - this.efinalAddedLine;
	}
}
