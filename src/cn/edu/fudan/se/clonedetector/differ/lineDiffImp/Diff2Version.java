package cn.edu.fudan.se.clonedetector.differ.lineDiffImp;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.bean.Compare;
import cn.edu.fudan.se.clonedetector.bean.File;
import cn.edu.fudan.se.clonedetector.comparecloneinchange.CloneInChangeExtractor;
import cn.edu.fudan.se.clonedetector.comparecloneinchange.ICloneInChange;
import cn.edu.fudan.se.clonedetector.dao.IDataAccessor;
import cn.edu.fudan.se.clonedetector.dao.hibernate.HibernateDao;
import cn.edu.fudan.se.clonedetector.differ.ChangeSaveNotify;
import cn.edu.fudan.se.clonedetector.differ.IDifferDetector;
import cn.edu.fudan.se.clonedetector.differ.IDifferOutcomeNotify;

public class Diff2Version {
	private IDifferDetector detector;
//	IDataAccessor dao = HibernateDao.getInstance();

	public void changeOf2Version(int preCommitId, int commitId, IDataAccessor dao) {
		detector = new LineDiffDetectorImp();
		IDifferOutcomeNotify notify = new ChangeSaveNotify();
		detector.addOutcomeNotify(notify);
		Commit preCommit = dao.getCommitById(preCommitId);
		Commit commit = dao.getCommitById(commitId);
		if(preCommit.getCommitDate().compareTo(commit.getCommitDate()) > 0){
			int tmp = preCommitId;
			preCommitId = commitId;
			commitId = tmp;
		}
		List<File> preFiles = dao.getFilesOfCommitByIdCountFrom(preCommitId, -1, -1);
		List<File> files = dao.getFilesOfCommitByIdCountFrom(commitId, -1, -1);
		LinkedList<File> leftFile = new LinkedList<File>();
		LinkedList<File> rightFile = new LinkedList<File>();
		System.out.println("There are " + preFiles.size() + " files in the pre version");
		System.out.println("There are " + files.size() + " files in the post version");
		for (File file : preFiles) {
			leftFile.add(file);
		}
		for (File file : files) {
			rightFile.add(file);
		}
		System.out.println("There are " + leftFile.size() + " files in the pre version");
		System.out.println("There are " + rightFile.size() + " files in the post version");
		
		Compare existedCompare = dao.getCompareBetween2Version(preCommitId, commitId);
		if (existedCompare == null)
			detector.detectDiffer(leftFile, rightFile, preCommitId, commitId, dao);
		else if (existedCompare.getStatus() == null || existedCompare.getStatus() != 1) {
			dao.deleteClonesByCompareId(existedCompare.getCompareId());
			detector.detectDiffer(leftFile, rightFile, preCommitId, commitId, dao);
		}
	}
}
