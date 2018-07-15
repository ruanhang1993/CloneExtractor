package cn.edu.fudan.se.clonedetector.display.service;

import java.util.ArrayList;
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.bean.ProcessBar;
import cn.edu.fudan.se.clonedetector.bean.Project;
import cn.edu.fudan.se.clonedetector.dao.IDataAccessor;
import cn.edu.fudan.se.clonedetector.dao.hibernate.HibernateDao;

public class ProcessBarService extends AbstractService {

	public List<ProcessBar> getProjectsBar() throws Exception{
//		IDataAccessor dao = HibernateDao.getInstance();
		List<ProcessBar> processBars = new ArrayList<>();
		List<Project> projects = dao.getProjects();
		for (Project project : projects) {
			List<Commit> commits = dao.getUnfinishedCommit(project.getProjectId());
			int number = 0;
			if (commits != null && (number = commits.size()) > 0) {
				for (Commit commit : commits) {
					ProcessBar pb = new ProcessBar();
					int stage = commit.getStage();
					float percent = getFinishPercent(stage);
					pb.setPercent((int)(percent*100));
					pb.setProjectname(project.getProjectNameCh());
					pb.setProjectnameen(project.getProjectNameEn());
					String versionId = commit.getSelfVersionId();
					if (versionId == null) {
						versionId = commit.getRevisionId();
					}
					pb.setVersionId(versionId);
					if (stage < 0) {
						pb.setStage(commit.STAGES[commit.STAGES.length-1]);
					}else {
						pb.setStage(commit.STAGES[stage]);
					}					
					pb.setScanDate(commit.getSubmitDate());
					pb.setCommitId(commit.getCommitId());
					processBars.add(pb);
				}				
			}
		}
		return processBars;
	}

	private float getFinishPercent(int stage) {
		switch (stage) {
		case Commit.FAILED:
			return Commit.FAILED_PERCENT;
		case Commit.WAIT:
			return Commit.WAIT_PERCENT;
		case Commit.ALL_FINISH:
			return Commit.ALL_FINISH_PERCENT;
		case Commit.START:
			return Commit.START_PERCENT;
		case Commit.CHECKOUT_FINISH:
			return Commit.CHECKOUT_FINISH_PERCENT;
		case Commit.FILE_A_FINISH:
			return Commit.FILE_A_FINISH_PERCENT;
		case Commit.CCFINDER_FINISH:
			return Commit.CCFINDER_FINISH_PERCENT;
		case Commit.UPDATE_FILE_CLOC_FINISH:
			return Commit.UPDATE_FILE_CLOC_FINISH_PERCENT;
		default:
			return 0;
		}
	}
}
