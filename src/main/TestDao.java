package main;

import java.util.Date;

import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.bean.Compare;
import cn.edu.fudan.se.clonedetector.dao.IDataAccessor;
import cn.edu.fudan.se.clonedetector.dao.hibernate.HibernateDao;

public class TestDao {
	public static void test() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		IDataAccessor dao = (HibernateDao)Class.forName("cn.edu.fudan.se.clonedetector.dao.hibernate.HibernateDao").newInstance();
		int preCommitId = 30;
		int commitId = 31;
		System.out.println("preCommitId is " + preCommitId);
		System.out.println("CommitId is " + commitId);
		Compare compare = new Compare();
		Commit prec = dao.getCommitById(preCommitId);
		Commit commit = dao.getCommitById(commitId);
		int r1 = Integer.parseInt(prec.getRevisionId().substring(1));
		int r2 = Integer.parseInt(commit.getRevisionId().substring(1));
		int versionSpan = r2-r1;
		String preRevisionId = prec.getRevisionId();
		String reviionId = commit.getRevisionId();
		int projectId = commit.getProjectId();
		long timeSpan = commit.getCommitDate().getTime() - prec.getCommitDate().getTime();
		Date date = new Date();
		compare.setVersionSpan(versionSpan);		
		compare.setPreRevisionId(preRevisionId);
		compare.setRevisionId(reviionId);
		compare.setCommitId(commitId);
		compare.setPreCommitId(preCommitId);
		compare.setProjectId(projectId);		
		compare.setTimeSpan(""+timeSpan);
		compare.setCompareDate(date);
		System.out.println("save Compare...");
		dao.saveCompare(compare);
		System.out.println("done!");
	}
}
