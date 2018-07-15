package cn.edu.fudan.se.clonedetector.dao.hibernate;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import cn.edu.fudan.se.clonedetector.bean.Author;
import cn.edu.fudan.se.clonedetector.bean.CCStreamProperty;
import cn.edu.fudan.se.clonedetector.bean.Change;
import cn.edu.fudan.se.clonedetector.bean.ChangeFile;
import cn.edu.fudan.se.clonedetector.bean.Clone;
import cn.edu.fudan.se.clonedetector.bean.CloneClass;
import cn.edu.fudan.se.clonedetector.bean.CloneInChange;
import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.bean.CommitLanguage;
import cn.edu.fudan.se.clonedetector.bean.Compare;
import cn.edu.fudan.se.clonedetector.bean.CompareLanguage;
import cn.edu.fudan.se.clonedetector.bean.File;
import cn.edu.fudan.se.clonedetector.bean.ProgramLanguage;
import cn.edu.fudan.se.clonedetector.bean.Project;
import cn.edu.fudan.se.clonedetector.bean.Repository;
import cn.edu.fudan.se.clonedetector.bean.Team;
import cn.edu.fudan.se.clonedetector.dao.IDataAccessor;
/**
 * 不完全的单例模式，因为框架在初始化的时候会自动调用无参数构造函数，如果仍按照原来的单例模式实际上会得到两个实例（一个来自框架，实际是在xml中指定，一个是代码中创建的）
 * 所以直接将getInstance方法注释掉，由初始化的类加载器保证只有一个实例，并且在所有服务中只能使用这个实例
 * 按xml的写法我们本来不需要在构造函数中给sessionFactory赋值，可能是因为classpath位置不正确导致没有正确赋值，所以在构造函数中显式赋值（留待后面解决）
 * @author zhanghr
 * @date 2016年11月30日
 */
public class HibernateDao extends HibernateDaoSupport implements IDataAccessor {

	private final static Object saveAuthorClock = new Object();
	private final static Object saveChangeClock = new Object();
	private final static Object saveFileClock = new Object();
	private final static Object saveCloneClock = new Object();
	private final static Object saveProjectClock = new Object();
	private final static Object saveRepoClock = new Object();
	private final static Object saveCommitClock = new Object();
	private final static Object saveCompareClock = new Object();
	private final static Object saveTeamClock = new Object();
	private final static Object saveCloneClassClock = new Object();
	private final static Object saveCloneInChangeClock = new Object();
	private final static Object saveChangeFileClock = new Object();
	private final static Object saveCCSTREAMClock = new Object();
	private SessionFactory sessionFactory;
//	private static HibernateDao dao = null;
	private HibernateDao() {
		Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
		sessionFactory = cfg.buildSessionFactory();
		setSessionFactory(sessionFactory);
	}

	public static void main(String args[]) {
		HibernateDao dao = new HibernateDao();
	}

//	public static HibernateDao getInstance() {
//		if (dao == null) {
//			synchronized (HibernateDao.class) {
//				if (dao == null) {
//					dao = new HibernateDao();
//				}
//				return dao;
//			}
//		}
//		return dao;
//	}

	@Override
	public <T> void save(List<T> list) {
		list.forEach((obj) -> {
			this.getHibernateTemplate().save(obj);
		});
	}

	@Override
	public <T> void save(T obj) {
		this.getHibernateTemplate().save(obj);
	}

//	public int getLastCommitId() {
//		HibernateDao inst = getInstance();
//		Session session = inst.getSession();
//
//		String query = "select commitId from Commit order by commitId DESC";
//		List<Integer> commitList = session.createQuery(query).list();
//		session.close();
//		inst.getSessionFactory().close();
//		if (commitList != null && commitList.size() > 0)
//			return commitList.get(0);
//		else
//			return 0;
//	}

//	public int getLastCompareId() {
//		HibernateDao inst = getInstance();
//		Session session = inst.getSession();
//		List<Integer> compareList = session.createQuery("select compareId from Compare order by compareId DESC").list();
//		session.close();
//		inst.getSessionFactory().close();
//		if (compareList != null && compareList.size() > 0)
//			return compareList.get(0);
//		else
//			return 0;
//	}

//	public int getLastFileId() {
//		HibernateDao inst = getInstance();
//		Session session = inst.getSession();
//		List<Integer> fileList = session.createQuery("select fileId from File order by fileId DESC").list();
//		session.close();
//		inst.getSessionFactory().close();
//		if (fileList != null && fileList.size() > 0)
//			return fileList.get(0);
//		else
//			return 0;
//	}

	public List<Commit> getCommits() {
		Session session = sessionFactory.openSession();
		List<Commit> commitList = session.createQuery("from Commit").list();
		session.close();
		return commitList;
	}

	@Override
	public Commit getCommitByProjectAndRevisionId(int projectId, String revisionId) {
		Session session = sessionFactory.openSession();
		List<Commit> commitList = session
				.createQuery("from Commit where projectId = " + projectId + " and revisionId = '" + revisionId + "'")
				.list();
		session.close();
		if (commitList != null && commitList.size() > 0)
			return commitList.get(0);
		else
			return null;
	}

	public List<String> getRevisionIDs() {
		Session session = sessionFactory.openSession();
		List<String> revisionIDs = session.createQuery("select c.revisionId from Commit as c").list();
		session.close();
		return revisionIDs;
	}

	public Commit getCommitById(int id) {
		Session session = sessionFactory.openSession();
		List<Commit> commitList = session.createQuery("from Commit where commitId = " + id).list();
		session.close();
		if (commitList != null && commitList.size() > 0)
			return commitList.get(0);
		return null;
	}
	
	public List<CommitLanguage> getCommitLanguageById(int id) {
		Session session = sessionFactory.openSession();
		List<CommitLanguage> list = session.createQuery("from CommitLanguage where commitId = " + id + " group by language").list();
		session.close();
		if (list != null && list.size() > 0)
			return list;
		return null;
	}
	
	public boolean hasCommitLanguage(int id, String language) {
		Session session = sessionFactory.openSession();
		List<CommitLanguage> list = session.createQuery("from CommitLanguage where commitId = " + id + " and language ='"+language+"'").list();
		session.close();
		if (list != null && list.size() > 0)
			return true;
		return false;
	}
	
	public Commit getCommitByIdAndLanguage(int id, String language) {
		Session session = sessionFactory.openSession();
		List<Commit> commitList = session.createQuery("from Commit where commitId = " + id).list();
		session.close();
		if (commitList != null && commitList.size() > 0)
			return commitList.get(0);
		return null;
	}

	public List<File> getFilesOfCommitByIdCountFrom(int id, int index, int count) {
		Session session = sessionFactory.openSession();
		List<File> fileList = session.createQuery("from File as f where f.commitId = " + id).list();
		session.close();
		if (index == -1)
			return fileList;
		return fileList.subList(index, index + count);
	}

	public List<Change> getChangesOfCommit(Commit commit) {
		int commitId = commit.getCommitId();
		Session session = sessionFactory.openSession();
		List<Change> changeList = session.createQuery("from Change as c where c.commitId = " + commitId).list();
		session.close();
		return changeList;
	}

	public List<Change> getChangesOfFileName(String fileName) {
		Session session = sessionFactory.openSession();
		List<Change> changeList = session.createQuery(
				"select c from Change as c, File as f where c.fileId = f.fileId and f.fileName = '" + fileName + "'")
				.list();
		session.close();
		return changeList;
	}

	public int getCommitCount() {
		Session session = sessionFactory.openSession();
		int commitCount = ((Number) session.createQuery("select count(*) from Commit").iterate().next()).intValue();
		session.close();
		return commitCount;
	}

	public List<Commit> getCommitsFromTo(int start, int end) {
		Session session = sessionFactory.openSession();
		Query query = session
				.createQuery("from Commit as c where c.commitId >= " + start + " and c.commitId <= " + end);
		List<Commit> commitList = query.list();
		session.close();
		return commitList;
	}

	public File getFileOfCommitByName(Commit currentCommit, String fileName) {
		int commitId = currentCommit.getCommitId();
		Session session = sessionFactory.openSession();
		List<File> fileList = session
				.createQuery("from File as f where f.commitId = " + commitId + " and f.fileName = '" + fileName + "'")
				.list();
		session.close();
		return fileList.get(0);
	}

	public File getFileById(int fileId) {
		Session session = sessionFactory.openSession();
		List<File> fileList = session.createQuery("from File as f where f.fileId = " + fileId).list();
		session.close();
		if (fileList != null && fileList.size() != 0)
			return fileList.get(0);
		else
			return null;
	}

	public File getFileOfCommitByIdByName(int id, String fileName) {
		Session session = sessionFactory.openSession();
		List<File> fileList = session
				.createQuery("from File as f where f.commitId = " + id + " and f.fileName = '" + fileName + "'").list();
		session.close();
		if (fileList != null && fileList.size() != 0)
			return fileList.get(0);
		else
			return null;
	}

	public List<Author> getAuthors() {
		Session session = sessionFactory.openSession();
		List<Author> authorList = session.createQuery("from Author").list();
		session.close();
		return authorList;
	}

	public Author getAuthorById(int id) {
		Session session = sessionFactory.openSession();
		List<Author> authorList = session.createQuery("from Author as a where a.id = " + id).list();
		session.close();
		return authorList.get(0);
	}

	@Override
	public Author getAuthorByName(String name) {
		Session session = sessionFactory.openSession();
		List<Author> authorList = session.createQuery("from Author where authorName = '" + name + "'").list();
		session.close();
		if (authorList != null && authorList.size() > 0)
			return authorList.get(0);
		else
			return null;
	}

	public List<File> getFiles() {
		Session session = sessionFactory.openSession();
		List<File> fileList = session.createQuery("from File").list();
		session.close();
		return fileList;
	}

	public List<Commit> getCommitByAuthorAndDate(Author author, Date t1, Date t2) {
		int authorId = author.getAuthorId();

		Session session = sessionFactory.openSession();
		List<Commit> commitList = session.createQuery("from Commit as c where c.authorId = " + authorId
				+ " and c.commitDate >= '" + t1 + "' and c.commitDate <= '" + t2 + "'").list();
		session.close();
		return commitList;
	}

	public void deleteAllAbout(Commit c) {
		int commitId = c.getCommitId();
		Session session = sessionFactory.openSession();

		String hql1 = "delete Author where commitId = " + commitId;
		String hql2 = "delete Change where commitId = " + commitId;
		String hql3 = "delete Clone where commitId = " + commitId;
		String hql4 = "delete CloneClass where commitId = " + commitId;
		String hql5 = "delete Commit where commitId = " + commitId;
		String hql6 = "delete File where commitId = " + commitId;
		String hql7 = "delete Project where commitId = " + commitId;

		Query query = session.createQuery(hql1);
		query.executeUpdate();
		session.beginTransaction().commit();
		query = session.createQuery(hql2);
		query.executeUpdate();
		session.beginTransaction().commit();
		query = session.createQuery(hql3);
		query.executeUpdate();
		session.beginTransaction().commit();
		query = session.createQuery(hql4);
		query.executeUpdate();
		session.beginTransaction().commit();
		query = session.createQuery(hql5);
		query.executeUpdate();
		session.beginTransaction().commit();
		query = session.createQuery(hql6);
		query.executeUpdate();
		session.beginTransaction().commit();
		query = session.createQuery(hql7);
		query.executeUpdate();
		session.beginTransaction().commit();

		session.close();

	}

	public Commit getEarliestCommit() {
		Session session = sessionFactory.openSession();
		List<Commit> commitList = session.createQuery("from Commit order by commitDate asc").list();
		session.close();
		return commitList.get(0);
	}

	public List<Change> getChangesOfOldFileName(String fileName) {
		Session session = sessionFactory.openSession();
		List<Change> changeList = session
				.createQuery(
						"from Change as c, File as f where c.oldFileId = f.fileId and f.fileName = '" + fileName + "'")
				.list();
		session.close();
		return changeList;
	}

	public List<Clone> getClones() {
		Session session = sessionFactory.openSession();
		List<Clone> cloneList = session.createQuery("from Clone").list();
		session.close();
		
		return cloneList;
	}

	public List<Clone> getClonesOfCommit(Commit commit) {
		int commitId = commit.getCommitId();
		Session session = sessionFactory.openSession();
		List<Clone> cloneList = session.createQuery("from Clone where commitId = " + commitId).list();
		session.close();
		
		return cloneList;
	}

	public List<Clone> getClonesOfCommitId(int commitId) {
		Session session = sessionFactory.openSession();
		List<Clone> cloneList = session.createQuery("from Clone where commitId = " + commitId).list();
		session.close();
		
		return cloneList;
	}

	@Override
	public List<Clone> getClonesOfCommitIdAndFileId(int commitId, int fileId) {
		Session session = sessionFactory.openSession();
		List<Clone> cloneList = session
				.createQuery("from Clone where commitId = " + commitId + " and fileId = " + fileId).list();
		session.close();
		
		return cloneList;
	}

	public List<CloneClass> getCloneClassOfCommit(Commit commit) {
		int commitId = commit.getCommitId();
		Session session = sessionFactory.openSession();
		List<CloneClass> cloneClassList = session.createQuery("from CloneClass where commitId = " + commitId).list();
		session.close();
		
		return cloneClassList;
	}

	public void saveAuthor(Author author) {
		synchronized (saveAuthorClock) {
			Session session = sessionFactory.openSession();
			session.beginTransaction();
			session.save(author);
			session.getTransaction().commit();
			session.close();
			
		}
	}

	public void saveChange(Change change) {
		synchronized (saveChangeClock) {

		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(change);
		session.getTransaction().commit();
		session.close();
		
		}
	}

	public void saveClone(Clone clone) {
		synchronized (saveCloneClock) {

		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(clone);
		session.getTransaction().commit();
		session.close();
		
		}
	}

	public void saveCloneClass(CloneClass cloneClass) {
		synchronized (saveCloneClassClock) {

		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(cloneClass);
		session.getTransaction().commit();
		session.close();
		
		}
	}

	public void saveCommit(Commit commit) {
		synchronized (saveCommitClock) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(commit);
		session.getTransaction().commit();
		session.close();
		
		}
	}

	public void saveFile(File file) {
		synchronized (saveFileClock) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(file);
		session.getTransaction().commit();
		session.close();
		
		}
	}

	public void saveProject(Project project) {
		synchronized (saveProjectClock) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(project);
		session.getTransaction().commit();
		session.close();
		
		}
	}

	public void updateAuthor(Author author) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(author);
		session.getTransaction().commit();
		session.close();
		
	}

	public void updateChange(Change change) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(change);
		session.getTransaction().commit();
		session.close();
		
	}

	public void updateClone(Clone clone) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(clone);
		session.getTransaction().commit();
		session.close();
		
	}

	public void updateCloneClass(CloneClass cloneClass) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(cloneClass);
		session.getTransaction().commit();
		session.close();
		
	}

	public void updateCommit(Commit commit) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(commit);
		session.getTransaction().commit();
		session.close();
		
	}

	public void updateFile(File file) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(file);
		session.getTransaction().commit();
		session.close();
		
	}

	public void updateProject(Project project) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(project);
		session.getTransaction().commit();
		session.close();
		
	}

//	@Override
//	public int getLastChangeId() {
//		HibernateDao inst = getInstance();
//		Session session = inst.getSession();
//		List<Integer> changeList = session.createQuery("select changeId from Change order by changeId DESC").list();
//		session.close();
//		inst.getSessionFactory().close();
//		if (changeList != null && changeList.size() > 0)
//			return changeList.get(0);
//		else
//			return 0;
//	}

//	@Override
//	public int getLastAuthorId() {
//		HibernateDao inst = getInstance();
//		Session session = inst.getSession();
//		List<Integer> authorList = session.createQuery("select authorId from Author order by authorId DESC").list();
//		session.close();
//		inst.getSessionFactory().close();
//		if (authorList != null && authorList.size() > 0)
//			return authorList.get(0);
//		else
//			return 0;
//	}

//	@Override
//	public int getLastCloneClassId() {
//		HibernateDao inst = getInstance();
//		Session session = inst.getSession();
//		List<Integer> cloneClassList = session
//				.createQuery("select cloneClassId from CloneClass order by cloneClassId DESC").list();
//		session.close();
//		inst.getSessionFactory().close();
//		if (cloneClassList != null && cloneClassList.size() > 0)
//			return cloneClassList.get(0);
//		else
//			return 0;
//	}

//	@Override
//	public int getLastCloneId() {
//		HibernateDao inst = getInstance();
//		Session session = inst.getSession();
//		List<Integer> cloneList = session.createQuery("select cloneId from Clone order by cloneId DESC").list();
//		session.close();
//		inst.getSessionFactory().close();
//		if (cloneList != null && cloneList.size() > 0)
//			return cloneList.get(0);
//		else
//			return 0;
//	}

//	@Override
//	public int getLastRepositoryId() {
//		HibernateDao inst = getInstance();
//		Session session = inst.getSession();
//		List<Integer> repositoryList = session
//				.createQuery("select repositoryId from Repository order by repositoryId DESC").list();
//		session.close();
//		inst.getSessionFactory().close();
//		if (repositoryList != null && repositoryList.size() > 0)
//			return repositoryList.get(0);
//		else
//			return 0;
//	}

//	@Override
//	public int getLastProjectId() {
//		HibernateDao inst = getInstance();
//		Session session = inst.getSession();
//		List<Integer> projectList = session.createQuery("select projectId from Project order by projectId DESC").list();
//		session.close();
//		inst.getSessionFactory().close();
//		if (projectList != null && projectList.size() > 0)
//			return projectList.get(0);
//		else
//			return 0;
//	}

	@Override
	public List<Change> getChangesOfCommitIdAndType(int preCommitId, int commitId, int type) {
		Session session = sessionFactory.openSession();
		List<Change> changeList = session.createQuery("from Change as c where preCommitId =  " + preCommitId
				+ " and c.commitId = " + commitId + " and c.changeType = " + type).list();
		session.close();
		
		return changeList;
	}

//	@Override
//	public int getLastCloneInChangeId() {
//		HibernateDao inst = getInstance();
//		Session session = inst.getSession();
//		List<Integer> cloneInChangeIdList = session
//				.createQuery("select cloneInChangeId from CloneInChange order by cloneInChangeId DESC").list();
//		session.close();
//		inst.getSessionFactory().close();
//		if (cloneInChangeIdList != null && cloneInChangeIdList.size() > 0)
//			return cloneInChangeIdList.get(0);
//		else
//			return 0;
//	}

	@Override
	public void saveCloneInChange(CloneInChange cloneInClass) {
		synchronized (saveCloneInChangeClock) {

		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(cloneInClass);
		session.getTransaction().commit();
		session.close();
		
		}
	}

	@Override
	public void saveRepository(Repository repo) {
		synchronized (saveRepoClock) {
			Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(repo);
		session.getTransaction().commit();
		session.close();
		
		}

	}

	@Override
	public void saveCompare(Compare compare) {
		synchronized (saveCompareClock) {
			Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(compare);
		session.getTransaction().commit();
		session.close();
		
		}

	}

	@Override
	public void updateCloneInChange(CloneInChange cloneInClass) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(cloneInClass);
		session.getTransaction().commit();
		session.close();
		

	}

	@Override
	public void updateRepository(Repository repo) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(repo);
		session.getTransaction().commit();
		session.close();
		

	}

	@Override
	public List<Project> getProjects() {
		Session session = sessionFactory.openSession();
		List<Project> projectList = session.createQuery("from Project").list();
		session.close();
		
		return projectList;
	}

	@Override
	public List<Commit> getCommitsByProjectId(int projectId) {
		Session session = sessionFactory.openSession();
		List<Commit> commitList = session.createQuery("from Commit where projectId=" + projectId).list();
		System.out.println(commitList.size() + "commit size ");
		session.close();
		
		return commitList;
	}

	@Override
	public void updateCommitCLoc(Commit commit) {
		int commitId = commit.getCommitId();
		Session session = sessionFactory.openSession();
		Integer cloc = 0, ecloc = 0;
		List<Integer> clocList = (List<Integer>) session
				.createQuery("select sum(endLine - beginLine + 1) from Clone where commitId =" + commitId).list();
		List<Integer> eclocList = (List<Integer>) session
				.createQuery("select sum(endLine - beginLine + 1) from Clone where commitId =" + commitId).list();

		if (clocList != null) {
			Number num = (Number) clocList.get(0);
			if (num != null)
				cloc = num.intValue();

		}
		if (eclocList != null) {
			Number num = (Number) clocList.get(0);
			if (num != null)
				ecloc = num.intValue();

		}
		commit.setCloc(cloc);
		commit.setEcloc(ecloc);
		session.beginTransaction();
		session.update(commit);
		session.getTransaction().commit();
		session.close();
		
	}

	@Override
	public void updateFileCLoc(int fileId) {
		Session session = sessionFactory.openSession();
		Integer cloc = 0, ecloc = 0,bcloc = 0,cmcloc =0,ccloc=0,eccloc=0;
		Integer cloneNum = 0,cloneClassNum = 0;

		File file = this.getFileById(fileId);
		List<Integer> clocList = (List<Integer>) session
				.createQuery("select sum(endLine - beginLine + 1) from Clone where fileId =" + fileId).list();

		List<Integer> eclocList = (List<Integer>) session
				.createQuery("select sum(ecloc) from Clone where fileId =" + fileId).list();
		List<Integer> bclocList = (List<Integer>) session
				.createQuery("select sum(bcloc) from Clone where fileId =" + fileId).list();
		List<Integer> cmclocList = (List<Integer>) session
				.createQuery("select sum(cmcloc) from Clone where fileId =" + fileId).list();
		List<Integer> cclocList = (List<Integer>) session
				.createQuery("select sum(ccloc) from Clone where fileId =" + fileId).list();

		List<Integer> ecclocList = (List<Integer>) session
				.createQuery("select sum(eccloc) from Clone where fileId =" + fileId).list();
		List<Integer> cloneList = (List<Integer>) session
				.createQuery("from Clone where fileId =" + fileId).list();
		List<Integer> cloneClassList = (List<Integer>) session
				.createQuery("from Clone where fileId =" + fileId+" group by clone_class_id").list();
		if (clocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) clocList.get(0);
			if (num != null) {
				cloc = num.intValue();
			}
		}
		if (eclocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) eclocList.get(0);
			if (num != null) {
				ecloc = num.intValue();
			}
		}
		if (bclocList != null) {
			// System.out.println("bloc of file is " + clocList.get(0));
			Number num = (Number) bclocList.get(0);
			if (num != null) {
				bcloc = num.intValue();
			}
		}
		if (cmclocList != null) {
			// System.out.println("cmloc of file is " + clocList.get(0));
			Number num = (Number) cmclocList.get(0);
			if (num != null) {
				cmcloc = num.intValue();
			}
		}
		if (cclocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) cclocList.get(0);
			if (num != null) {
				ccloc = num.intValue()+file.getLoc()-cloc;
			}
		}
		else{
			ccloc = file.getLoc();
		}
		if (ecclocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) ecclocList.get(0);
			if (num != null) {
				eccloc = num.intValue()+file.getEloc()-ecloc;
			}
		}
		else{
			eccloc = file.getEloc();
		}
		if (cloneList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) cloneList.size();
			if (num != null) {
				cloneNum = num.intValue();
			}
		}
		if (cloneClassList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) cloneList.size();
			if (num != null) {
				cloneClassNum = num.intValue();
			}
		}

		file.setCloc(cloc);
		file.setEcloc(ecloc);
		file.setBcloc(bcloc);
		file.setCmcloc(cmcloc);
		file.setCcloc(ccloc);
		file.setEccloc(eccloc);
		file.setCloneNum(cloneNum);
		file.setCloneClassNum(cloneClassNum);
		session.beginTransaction();
		session.update(file);
		session.getTransaction().commit();
		session.close();
		
	}

	@Override
	public void updateCommitLoc(Commit commit) {
		Session session = sessionFactory.openSession();
		Integer loc = 0, eloc = 0, cloc = 0, ecloc = 0, fileNum = 0, cloneFileNum = 0;
		int commitId = commit.getCommitId();
		List<Integer> locList = (List<Integer>) session
				.createQuery("select sum(loc) from File where commitId =" + commitId).list();
		List<Integer> elocList = (List<Integer>) session
				.createQuery("select sum(eloc) from File where commitId =" + commitId).list();
		List<Integer> clocList = (List<Integer>) session
				.createQuery("select sum(cloc) from File where commitId =" + commitId).list();
		List<Integer> eclocList = (List<Integer>) session
				.createQuery("select sum(ecloc) from File where commitId =" + commitId).list();

		List<File> fileList = (List<File>) session.createQuery("from File where commitId =" + commitId).list();

		List<File> cloneFileList = (List<File>) session
				.createQuery("from File where cloc > 0 and commitId = " + commitId).list();

		if (locList != null) {
			System.out.println("loc of commit is " + locList.get(0));
			Number num = (Number) locList.get(0);
			if (num != null)
				loc = num.intValue();

		}
		if (elocList != null) {
			System.out.println("loc of commit is " + elocList.get(0));
			Number num = (Number) elocList.get(0);
			if (num != null)
				eloc = num.intValue();

		}
		if (clocList != null) {
			System.out.println("loc of commit is " + clocList.get(0));
			Number num = (Number) clocList.get(0);
			if (num != null)
				cloc = num.intValue();

		}
		if (eclocList != null) {
			System.out.println("loc of commit is " + eclocList.get(0));
			Number num = (Number) eclocList.get(0);
			if (num != null)
				ecloc = num.intValue();

		}
		if (fileList != null) {
			fileNum = fileList.size();
			System.out.println("file size is : " + fileList.size());

		}
		if (cloneFileList != null) {
			cloneFileNum = cloneFileList.size();
			System.out.println("clone file size is : " + cloneFileList.size());
		}
		commit.setLoc(loc);
		commit.setEloc(eloc);
		commit.setCloc(cloc);
		commit.setEcloc(ecloc);
		commit.setFileNum(fileList.size());
		commit.setCloneFileNum(cloneFileNum);
		commit.setStage(Commit.ALL_FINISH);
		commit.setSubmitDoneDate(new Date());
		session.beginTransaction();
		session.update(commit);
		session.getTransaction().commit();
		session.close();
		
	}
	
	@Override
	public void updateCommitLoc2(Commit commit) {
		Session session = sessionFactory.openSession();
		Integer loc = 0, eloc = 0, cloc = 0, ecloc = 0, bloc =0,cmloc = 0,bcloc=0,cmcloc=0,ccloc=0,eccloc=0,fileNum = 0, cloneFileNum = 0;
		int commitId = commit.getCommitId();
		List<Integer> locList = (List<Integer>) session
				.createQuery("select sum(loc) from CommitLanguage where commitId =" + commitId + " group by commit_id").list();
		List<Integer> elocList = (List<Integer>) session
				.createQuery("select sum(eloc) from CommitLanguage where commitId =" + commitId + " group by commit_id").list();
		List<Integer> clocList = (List<Integer>) session
				.createQuery("select sum(cloc) from CommitLanguage where commitId =" + commitId + " group by commit_id").list();
		List<Integer> eclocList = (List<Integer>) session
				.createQuery("select sum(ecloc) from CommitLanguage where commitId =" + commitId + " group by commit_id").list();
		List<Integer> blocList = (List<Integer>) session
				.createQuery("select sum(bloc) from CommitLanguage where commitId =" + commitId + " group by commit_id").list();
		List<Integer> cmlocList = (List<Integer>) session
				.createQuery("select sum(cmloc) from CommitLanguage where commitId =" + commitId + " group by commit_id").list();
		List<Integer> bclocList = (List<Integer>) session
				.createQuery("select sum(bcloc) from CommitLanguage where commitId =" + commitId + " group by commit_id").list();
		List<Integer> cmclocList = (List<Integer>) session
				.createQuery("select sum(cmcloc) from CommitLanguage where commitId =" + commitId + " group by commit_id").list();
		List<Integer> cclocList = (List<Integer>) session
				.createQuery("select sum(ccloc) from CommitLanguage where commitId =" + commitId + " group by commit_id").list();
		List<Integer> ecclocList = (List<Integer>) session
				.createQuery("select sum(eccloc) from CommitLanguage where commitId =" + commitId + " group by commit_id").list();
		List<Integer> fileList = (List<Integer>) session
				.createQuery("select sum(fileNum) from CommitLanguage where commitId =" + commitId + " group by commit_id").list();
		List<Integer> cloneFileList = (List<Integer>) session
				.createQuery("select sum(cloneFileNum) from CommitLanguage where commitId =" + commitId + " group by commit_id").list();

		if (locList != null && locList.size()>0) {
			System.out.println("loc of commit is " + locList.get(0));
			Number num = (Number) locList.get(0);
			if (num != null)
				loc = num.intValue();

		}
		if (elocList != null && elocList.size()>0) {
			System.out.println("eloc of commit is " + elocList.get(0));
			Number num = (Number) elocList.get(0);
			if (num != null)
				eloc = num.intValue();

		}
		if (clocList != null && clocList.size()>0) {
			System.out.println("cloc of commit is " + clocList.get(0));
			Number num = (Number) clocList.get(0);
			if (num != null)
				cloc = num.intValue();

		}
		if (eclocList != null && eclocList.size()>0) {
			System.out.println("ecloc of commit is " + eclocList.get(0));
			Number num = (Number) eclocList.get(0);
			if (num != null)
				ecloc = num.intValue();

		}
		if (blocList != null && blocList.size()>0) {
			System.out.println("bloc of commit is " + blocList.get(0));
			Number num = (Number) blocList.get(0);
			if (num != null)
				bloc = num.intValue();

		}
		if (cmlocList != null && cmlocList.size()>0) {
			System.out.println("cmloc of commit is " + cmlocList.get(0));
			Number num = (Number) cmlocList.get(0);
			if (num != null)
				cmloc = num.intValue();

		}
		if (bclocList != null && bclocList.size()>0) {
			System.out.println("bcloc of commit is " + bclocList.get(0));
			Number num = (Number) bclocList.get(0);
			if (num != null)
				bcloc = num.intValue();

		}
		if (cmclocList != null && cmclocList.size()>0) {
			System.out.println("cmcloc of commit is " + cmclocList.get(0));
			Number num = (Number) cmclocList.get(0);
			if (num != null)
				cmcloc = num.intValue();

		}
		if (fileList != null && fileList.size()>0) {
			System.out.println("file size is : " + fileList.get(0));
			Number num = (Number) fileList.get(0);
			if (num != null)
				fileNum = num.intValue();

		}
		if (cloneFileList != null && cloneFileList.size()>0) {
			System.out.println("clone file size is : " + cloneFileList.get(0));
			Number num = (Number) cloneFileList.get(0);
			if (num != null)
				cloneFileNum = num.intValue();
		}
		if (cclocList != null && cclocList.size()>0) {
			System.out.println("ccloc of commit is " + cclocList.get(0));
			Number num = (Number) cclocList.get(0);
			if (num != null)
				ccloc = num.intValue();

		}
		if (ecclocList != null && ecclocList.size()>0) {
			System.out.println("eccloc of commit is " + ecclocList.get(0));
			Number num = (Number) ecclocList.get(0);
			if (num != null)
				eccloc = num.intValue();

		}
		commit.setLoc(loc);
		commit.setEloc(eloc);
		commit.setCloc(cloc);
		commit.setEcloc(ecloc);
		commit.setBcloc(bcloc);
		commit.setCmloc(cmloc);
		commit.setBloc(bloc);
		commit.setCmcloc(cmcloc);
		commit.setCcloc(ccloc);
		commit.setEccloc(eccloc);
		commit.setFileNum(fileNum);
		commit.setCloneFileNum(cloneFileNum);
		commit.setStage(Commit.ALL_FINISH);
		commit.setSubmitDoneDate(new Date());
		session.beginTransaction();
		session.update(commit);
		session.getTransaction().commit();
		session.close();
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public CommitLanguage saveCommitLanguage(CommitLanguage cl, Collection<String> suffixes) {
		int commitId = cl.getCommitId();
//		String selectSuffix = " and fileName REGEXP '%."+language+"'";
		StringBuilder sb = new StringBuilder();
		sb.append(" and regexp(fileName,'");
		for (Iterator iterator = suffixes.iterator(); iterator.hasNext();) {
			String suffix = (String) iterator.next();
			sb.append("(\\."+suffix+"$)|");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("')=1");
		String selectSuffix = sb.toString();
		Session session = sessionFactory.openSession();
		Integer loc = 0, eloc = 0, bloc = 0, cmloc = 0,fileNum = 0;
		
		List<File> fileList = (List<File>) session.createQuery("from File  where commitId =" + commitId + selectSuffix).list();
		if (fileList != null && fileList.size()>0) {
			fileNum = fileList.size();
			System.out.println("file size is : " + fileList.size());

		}
		if (fileNum == 0) {
			return null;
		}
		
		List<Integer> locList = (List<Integer>) session
				.createQuery("select sum(loc) from File where commitId =" + commitId + selectSuffix).list();
		List<Integer> elocList = (List<Integer>) session
				.createQuery("select sum(eloc) from File where commitId =" + commitId + selectSuffix).list();
		List<Integer> blocList = (List<Integer>) session
				.createQuery("select sum(bloc) from File where commitId =" + commitId + selectSuffix).list();
		List<Integer> cmlocList = (List<Integer>) session
				.createQuery("select sum(cmloc) from File where commitId =" + commitId + selectSuffix).list();

		if (locList != null && locList.size()>0) {
			System.out.println("loc of commit is " + locList.get(0));
			Number num = (Number) locList.get(0);
			if (num != null)
				loc = num.intValue();

		}
		if (elocList != null && elocList.size()>0) {
			System.out.println("eloc of commit is " + elocList.get(0));
			Number num = (Number) elocList.get(0);
			if (num != null)
				eloc = num.intValue();

		}
		if (blocList != null && blocList.size()>0) {
			System.out.println("bloc of commit is " + blocList.get(0));
			Number num = (Number) blocList.get(0);
			if (num != null)
				bloc = num.intValue();

		}
		if (cmlocList != null && cmlocList.size()>0) {
			System.out.println("cmloc of commit is " + cmlocList.get(0));
			Number num = (Number) cmlocList.get(0);
			if (num != null)
				cmloc = num.intValue();

		}
		cl.setLoc(loc);
		cl.setEloc(eloc);
		cl.setBloc(bloc);
		cl.setCmloc(cmloc);
		cl.setFileNum(fileList.size());
		session.beginTransaction();
		session.save(cl);
		session.getTransaction().commit();
		session.close();
		
		return cl;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void updateCommitLanguageCloc(CommitLanguage cl, Collection<String> suffixes) {
		int commitId = cl.getCommitId();
//		String selectSuffix = " and fileName REGEXP '%."+language+"'";
		StringBuilder sb = new StringBuilder();
		sb.append(" and regexp(fileName,'");
		for (Iterator iterator = suffixes.iterator(); iterator.hasNext();) {
			String suffix = (String) iterator.next();
			sb.append("(\\."+suffix+"$)|");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("')=1");
		String selectSuffix = sb.toString();
		Session session = sessionFactory.openSession();
		Integer cloc = 0, ecloc = 0, bcloc = 0,cmcloc=0,cloneFileNum = 0,ccloc=0,eccloc=0;
		
		List<Integer> clocList = (List<Integer>) session
				.createQuery("select sum(cloc) from File where commitId =" + commitId + selectSuffix).list();
		List<Integer> eclocList = (List<Integer>) session
				.createQuery("select sum(ecloc) from File where commitId =" + commitId + selectSuffix).list();
		List<Integer> bclocList = (List<Integer>) session
				.createQuery("select sum(bcloc) from File where commitId =" + commitId + selectSuffix).list();
		List<Integer> cmclocList = (List<Integer>) session
				.createQuery("select sum(cmcloc) from File where commitId =" + commitId + selectSuffix).list();
		List<Integer> cclocList = (List<Integer>) session
				.createQuery("select sum(ccloc) from File where commitId =" + commitId + selectSuffix).list();
		List<Integer> ecclocList = (List<Integer>) session
				.createQuery("select sum(eccloc) from File where commitId =" + commitId + selectSuffix).list();
		List<File> cloneFileList = (List<File>) session
				.createQuery("from File where cloc > 0 and commitId = " + commitId + selectSuffix).list();

		if (clocList != null && clocList.size()>0) {
			System.out.println("cloc of commit is " + clocList.get(0));
			Number num = (Number) clocList.get(0);
			if (num != null)
				cloc = num.intValue();
		}
		if (eclocList != null && eclocList.size()>0) {
			System.out.println("ecloc of commit is " + eclocList.get(0));
			Number num = (Number) eclocList.get(0);
			if (num != null)
				ecloc = num.intValue();
		}
		if (bclocList != null && bclocList.size()>0) {
			System.out.println("bcloc of commit is " + bclocList.get(0));
			Number num = (Number) bclocList.get(0);
			if (num != null)
				bcloc = num.intValue();
		}
		if (cmclocList != null && cmclocList.size()>0) {
			System.out.println("cmcloc of commit is " + cmclocList.get(0));
			Number num = (Number) cmclocList.get(0);
			if (num != null)
				cmcloc = num.intValue();
		}
		if (cclocList != null && cclocList.size()>0) {
			System.out.println("ccloc of commit is " + cclocList.get(0));
			Number num = (Number) cclocList.get(0);
			if (num != null)
				ccloc = num.intValue();
		}
		if (ecclocList != null && ecclocList.size()>0) {
			System.out.println("eccloc of commit is " + ecclocList.get(0));
			Number num = (Number) ecclocList.get(0);
			if (num != null)
				eccloc = num.intValue();
		}
		if (cloneFileList != null && cloneFileList.size()>0) {
			cloneFileNum = cloneFileList.size();
			System.out.println("clone file size is : " + cloneFileList.size());
		}
		
		cl.setCloc(cloc);
		cl.setEcloc(ecloc);
		cl.setBcloc(bcloc);
		cl.setCmcloc(cmcloc);
		cl.setCcloc(ccloc);
		cl.setEccloc(eccloc);
		cl.setCloneFileNum(cloneFileNum);
		session.beginTransaction();
		session.update(cl);
		session.getTransaction().commit();
		session.close();
		
	}

	@Override
	public List<Integer> getCloneFileIdByCommitId(int commitId) {
		Session session = sessionFactory.openSession();
		List<Integer> commitList = session
				.createQuery("select distinct(file_id) from clone where commit_id=" + commitId).list();
		// System.out.println(commitList.size() + "commit size ");

		session.close();
		
		return commitList;
	}

	@Override
	public List<Integer> getDistinctFileFromChange(int compareId) {
		Session session = sessionFactory.openSession();
		List<Integer> fileList = session
				.createQuery("select distinct(fileId) from Change where compareId = " + compareId).list();
		session.close();
		
		return fileList;

	}

	@Override
	public List<Compare> getComparesByProjectId(int projectId) {
		Session session = sessionFactory.openSession();
		List<Compare> compareList = session.createQuery("from Compare where projectId=" + projectId).list();
		// List<Compare> commitList = session.createQuery("from
		// Compare").list();
		 System.out.println(compareList.size() + "compare size ");
		session.close();
		
		return compareList;
	}

	@Override
	public void saveChangeFile(ChangeFile cf) {
		synchronized (saveChangeFileClock) {
			Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(cf);
		session.getTransaction().commit();
		session.close();
		
		}

	}

	@Override
	public void updateChangeFile(ChangeFile cf) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(cf);
		session.getTransaction().commit();
		session.close();
		

	}

//	@Override
//	public int getLastChangeFileId() {
//		HibernateDao inst = getInstance();
//		Session session = inst.getSession();
//		List<Integer> changeFileList = session
//				.createQuery("select changeFileId from ChangeFile order by changeFileId DESC").list();
//		session.close();
//		inst.getSessionFactory().close();
//		if (changeFileList != null && changeFileList.size() > 0)
//			return changeFileList.get(0);
//		else
//			return 0;
//	}

	@Override
	public void updateCompare(Compare compare) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(compare);
		session.getTransaction().commit();
		session.close();
		
	}

	@Override
	public void updateChangeFileLoc(ChangeFile cf) {
		Session session = sessionFactory.openSession();
		Integer cloc = 0, ecloc = 0, dcloc = 0, edcloc = 0, chloc = 0, echloc = 0,ccloc = 0,eccloc = 0;
		Integer bcloc = 0, cmcloc = 0, bdcloc = 0, cmdcloc = 0, bchloc = 0, cmchloc = 0;
		Integer aloc=0,ealoc=0,dloc=0,edloc=0;
		int fileId = cf.getFileId();
		int compareId = cf.getCompareId();
		/*comment by junyi
		 * cloc 代表增加的克隆代码行
		 * dcloc 代表删除的克隆代码行
		 * chloc 代表变更的代码行*
		 * ccloc 代表折算变更克隆代码行*/
		List<Integer> clocList = (List<Integer>) session
				.createQuery("select sum(endLine - beginLine + 1) from CloneInChange where fileId =" + fileId
						+ " and compareId = " + compareId + " and changeType = " + Change.INSERT)
				.list();

		List<Integer> eclocList = (List<Integer>) session
				.createQuery("select sum(ecloc) from CloneInChange where fileId =" + fileId + " and compareId = "
						+ compareId + " and changeType = " + Change.INSERT)
				.list();
		List<Integer> bclocList = (List<Integer>) session
				.createQuery("select sum(bcloc) from CloneInChange where fileId =" + fileId
						+ " and compareId = " + compareId + " and changeType = " + Change.INSERT)
				.list();

		List<Integer> cmclocList = (List<Integer>) session
				.createQuery("select sum(cmcloc) from CloneInChange where fileId =" + fileId + " and compareId = "
						+ compareId + " and changeType = " + Change.INSERT)
				.list();
		List<Integer> dclocList = (List<Integer>) session
				.createQuery("select sum(endLine - beginLine + 1) from CloneInChange where fileId =" + fileId
						+ " and compareId = " + compareId + " and changeType = " + Change.DELETE)
				.list();
		List<Integer> edclocList = (List<Integer>) session
				.createQuery("select sum(ecloc) from CloneInChange where fileId =" + fileId + " and compareId = "
						+ compareId + " and changeType = " + Change.DELETE)
				.list();
		List<Integer> bdclocList = (List<Integer>) session
				.createQuery("select sum(bcloc) from CloneInChange where fileId =" + fileId
						+ " and compareId = " + compareId + " and changeType = " + Change.DELETE)
				.list();
		List<Integer> cmdclocList = (List<Integer>) session
				.createQuery("select sum(cmcloc) from CloneInChange where fileId =" + fileId + " and compareId = "
						+ compareId + " and changeType = " + Change.DELETE)
				.list();
		List<Integer> chlocList = (List<Integer>) session
				.createQuery("select sum(endLine - beginLine + 1) from Change where fileId =" + fileId
						+ " and compareId = " + compareId)
				.list();

		List<Integer> echlocList = (List<Integer>) session
				.createQuery("select sum(echloc) from Change where fileId =" + fileId + " and compareId = " + compareId)
				.list();
		List<Integer> bchlocList = (List<Integer>) session
				.createQuery("select sum(bchloc) from Change where fileId =" + fileId
						+ " and compareId = " + compareId)
				.list();

		List<Integer> cmchlocList = (List<Integer>) session
				.createQuery("select sum(cmchloc) from Change where fileId =" + fileId + " and compareId = " + compareId)
				.list();
		List<Integer> cclocList = (List<Integer>) session
				.createQuery("select sum(ccloc) from CloneInChange where fileId =" + fileId
						+ " and compareId = " + compareId)
				.list();

		List<Integer> ecclocList = (List<Integer>) session
				.createQuery("select sum(eccloc) from CloneInChange where fileId =" + fileId + " and compareId = "
						+ compareId )
				.list();
		List<Integer> alocList = (List<Integer>) session
				.createQuery("select sum(endLine - beginLine + 1)  from Change where compareId =" + compareId
						+ "and changeType = " + Change.INSERT).list();
		List<Integer> ealocList = (List<Integer>) session
				.createQuery("select sum(echloc)  from Change where compareId =" + compareId
						+ "and changeType = " + Change.INSERT).list();
		List<Integer> dlocList = (List<Integer>) session
				.createQuery("select sum(endLine - beginLine + 1)  from Change where compareId =" + compareId
						+ "and changeType = " + Change.DELETE).list();
		List<Integer> edlocList = (List<Integer>) session
				.createQuery("select sum(echloc)  from Change where compareId =" + compareId
						+ "and changeType = " + Change.DELETE).list();


		if (alocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) alocList.get(0);
			if (num != null) {
				aloc = num.intValue();

			}
		}
		if (ealocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) ealocList.get(0);
			if (num != null) {
				ealoc = num.intValue();

			}
		}
		if (dlocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) dlocList.get(0);
			if (num != null) {
				dloc = num.intValue();

			}
		}
		if (edlocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) edlocList.get(0);
			if (num != null) {
				edloc = num.intValue();

			}
		}
		if (clocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) clocList.get(0);
			if (num != null) {
				cloc = num.intValue();

			}
		}
		if (eclocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) eclocList.get(0);
			if (num != null) {
				ecloc = num.intValue();

			}
		}
		if (bclocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) bclocList.get(0);
			if (num != null) {
				bcloc = num.intValue();

			}
		}
		if (cmclocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) cmclocList.get(0);
			if (num != null) {
				cmcloc = num.intValue();

			}
		}
		if (dclocList != null) {
			// System.out.println("dcloc of file is " + dclocList.get(0));
			Number num = (Number) dclocList.get(0);
			if (num != null) {
				dcloc = num.intValue();

			}
		}
		if (edclocList != null) {
			// System.out.println("dcloc of file is " + dclocList.get(0));
			Number num = (Number) edclocList.get(0);
			if (num != null) {
				edcloc = num.intValue();

			}
		}
		if (bdclocList != null) {
			// System.out.println("dcloc of file is " + dclocList.get(0));
			Number num = (Number) bdclocList.get(0);
			if (num != null) {
				bdcloc = num.intValue();

			}
		}
		if (cmdclocList != null) {
			// System.out.println("dcloc of file is " + dclocList.get(0));
			Number num = (Number) cmdclocList.get(0);
			if (num != null) {
				cmdcloc = num.intValue();

			}
		}
		if (chlocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) chlocList.get(0);
			if (num != null) {
				chloc = num.intValue();

			}
		}
		if (echlocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) echlocList.get(0);
			if (num != null) {
				echloc = num.intValue();

			}
		}
		if (bchlocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) bchlocList.get(0);
			if (num != null) {
				bchloc = num.intValue();

			}
		}
		if (cmchlocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) cmchlocList.get(0);
			if (num != null) {
				cmchloc = num.intValue();

			}
		}
		if (cclocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) cclocList.get(0);
			if (num != null) {
				ccloc = num.intValue()+chloc-cloc-dcloc;

			}
		}
		if (ecclocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) ecclocList.get(0);
			if (num != null) {
				eccloc = num.intValue()+echloc-ecloc-edcloc;

			}
		}
		cf.setCloc(cloc);
		cf.setEcloc(ecloc);
		cf.setDcloc(dcloc);
		cf.setEdcloc(edcloc);
		cf.setChloc(chloc);
		cf.setEchloc(echloc);
		cf.setBcloc(bcloc);
		cf.setCmcloc(cmcloc);
		cf.setBdcloc(bdcloc);
		cf.setCmdcloc(cmdcloc);
		cf.setBchloc(bchloc);
		cf.setCmchloc(cmchloc);
		cf.setCcloc(ccloc);
		cf.setEccloc(eccloc);
		cf.setAloc(aloc);
		cf.setEaloc(ealoc);
		cf.setDloc(dloc);
		cf.setEdloc(edloc);
		session.beginTransaction();
		session.save(cf);
		session.getTransaction().commit();
		session.close();
		
	}

	@Override
	public void updateCompareLoc(Compare compare) {
		Session session = sessionFactory.openSession();
		Integer loc = 0, eloc = 0, cloc = 0, ecloc = 0, dcloc = 0, edcloc = 0, chloc = 0, echloc = 0,ccloc = 0,eccloc = 0,aloc=0,dloc=0,ealoc=0,edloc=0,
				changeFileNum = 0,cloneChangeFileNum = 0;
		Integer bloc = 0,cmloc = 0, bcloc = 0, cmcloc = 0, bdcloc = 0, cmdcloc = 0, bchloc = 0, cmchloc = 0;
		int compareId = compare.getCompareId();
		List<Integer> locList = (List<Integer>) session
				.createQuery("select sum(loc) from ChangeFile where compareId =" + compareId).list();
		List<Integer> blocList = (List<Integer>) session
				.createQuery("select sum(bloc) from ChangeFile where compareId =" + compareId).list();
		List<Integer> cmlocList = (List<Integer>) session
				.createQuery("select sum(cmloc) from ChangeFile where compareId =" + compareId).list();
		List<Integer> clocList = (List<Integer>) session
				.createQuery("select sum(endLine - beginLine + 1) from CloneInChange where compareId =" + compareId
						+ "and changeType = " + Change.INSERT)
				.list();
		List<Integer> bclocList = (List<Integer>) session
				.createQuery("select sum(bcloc) from CloneInChange where compareId =" + compareId
						+ "and changeType = " + Change.INSERT)
				.list();
		List<Integer> cmclocList = (List<Integer>) session
				.createQuery("select sum(cmcloc) from CloneInChange where compareId =" + compareId
						+ "and changeType = " + Change.INSERT)
				.list();
		List<Integer> dclocList = (List<Integer>) session
				.createQuery("select sum(endLine - beginLine + 1) from CloneInChange where compareId =" + compareId
						+ "and changeType = " + Change.DELETE)
				.list();
		List<Integer> bdclocList = (List<Integer>) session
				.createQuery("select sum(bcloc) from CloneInChange where compareId =" + compareId
						+ "and changeType = " + Change.DELETE)
				.list();
		List<Integer> cmdclocList = (List<Integer>) session
				.createQuery("select sum(cmcloc) from CloneInChange where compareId =" + compareId
						+ "and changeType = " + Change.DELETE)
				.list();
		List<Integer> chlocList = (List<Integer>) session
				.createQuery("select sum(endLine - beginLine + 1)  from Change where compareId =" + compareId).list();
		List<Integer> alocList = (List<Integer>) session
				.createQuery("select sum(endLine - beginLine + 1)  from Change where compareId =" + compareId
						+ "and changeType = " + Change.INSERT).list();
		List<Integer> dlocList = (List<Integer>) session
				.createQuery("select sum(endLine - beginLine + 1)  from Change where compareId =" + compareId
						+ "and changeType = " + Change.DELETE).list();
		List<Integer> bchlocList = (List<Integer>) session
				.createQuery("select sum(bchloc)  from Change where compareId =" + compareId).list();
		List<Integer> cmchlocList = (List<Integer>) session
				.createQuery("select sum(cmchloc)  from Change where compareId =" + compareId).list();
		List<Integer> elocList = (List<Integer>) session
				.createQuery("select sum(eloc) from ChangeFile where compareId =" + compareId).list();
		List<Integer> ealocList = (List<Integer>) session
				.createQuery("select sum(echloc) from Change where compareId =" + compareId
						+ "and changeType = " + Change.INSERT).list();
		List<Integer> edlocList = (List<Integer>) session
				.createQuery("select sum(echloc) from Change where compareId =" + compareId
						+ "and changeType = " + Change.DELETE).list();
		List<Integer> eclocList = (List<Integer>) session
				.createQuery("select sum(ecloc) from CloneInChange where compareId =" + compareId + "and changeType = "
						+ Change.INSERT)
				.list();
		List<Integer> edclocList = (List<Integer>) session
				.createQuery("select sum(ecloc) from CloneInChange where compareId =" + compareId + "and changeType = "
						+ Change.DELETE)
				.list();
		List<Integer> echlocList = (List<Integer>) session
				.createQuery("select sum(echloc)  from Change where compareId =" + compareId).list();
		List<Integer> cclocList = (List<Integer>) session
				.createQuery("select sum(ccloc)  from ChangeFile where compareId =" + compareId).list();
		List<Integer> ecclocList = (List<Integer>) session
				.createQuery("select sum(eccloc)  from ChangeFile where compareId =" + compareId).list();
		List<ChangeFile> fileList = (List<ChangeFile>) session
				.createQuery("from ChangeFile where chloc > 0 and compareId = " + compareId).list();

		List<ChangeFile> cloneFileList = (List<ChangeFile>) session
				.createQuery("from ChangeFile where cloc > 0 and compareId = " + compareId).list();

		if (locList != null) {
			System.out.println("loc of compare is " + locList.get(0));
			Number num = (Number) locList.get(0);
			if (num != null)
				loc = num.intValue();

		}
		if (clocList != null) {
			System.out.println("cloc of compare is " + clocList.get(0));
			Number num = (Number) clocList.get(0);
			if (num != null)
				cloc = num.intValue();

		}
		if (dclocList != null) {
			System.out.println("dcloc of compare is " + dclocList.get(0));
			Number num = (Number) dclocList.get(0);
			if (num != null)
				dcloc = num.intValue();

		}
		if (chlocList != null) {
			System.out.println("chloc of compare is " + chlocList.get(0));
			Number num = (Number) chlocList.get(0);
			if (num != null)
				chloc = num.intValue();

		}
		if (elocList != null) {
			System.out.println("eloc of compare is " + elocList.get(0));
			Number num = (Number) elocList.get(0);
			if (num != null)
				eloc = num.intValue();

		}
		if (eclocList != null) {
			System.out.println("celoc of compare is " + eclocList.get(0));
			Number num = (Number) eclocList.get(0);
			if (num != null)
				ecloc = num.intValue();

		}
		if (edclocList != null) {
			System.out.println("celoc of compare is " + edclocList.get(0));
			Number num = (Number) edclocList.get(0);
			if (num != null)
				edcloc = num.intValue();

		}
		if (echlocList != null) {
			System.out.println("echloc of compare is " + echlocList.get(0));
			Number num = (Number) echlocList.get(0);
			if (num != null)
				echloc = num.intValue();

		}
		if (blocList != null) {
			System.out.println("bloc of compare is " + blocList.get(0));
			Number num = (Number) blocList.get(0);
			if (num != null)
				bloc = num.intValue();

		}
		if (bclocList != null) {
			System.out.println("bcloc of compare is " + bclocList.get(0));
			Number num = (Number) bclocList.get(0);
			if (num != null)
				bcloc = num.intValue();

		}
		if (bdclocList != null) {
			System.out.println("bdcloc of compare is " + bdclocList.get(0));
			Number num = (Number) bdclocList.get(0);
			if (num != null)
				bdcloc = num.intValue();

		}
		if (bchlocList != null) {
			System.out.println("bchloc of compare is " + bchlocList.get(0));
			Number num = (Number) bchlocList.get(0);
			if (num != null)
				bchloc = num.intValue();

		}
		if (cmlocList != null) {
			System.out.println("cmloc of compare is " + cmlocList.get(0));
			Number num = (Number) cmlocList.get(0);
			if (num != null)
				cmloc = num.intValue();

		}
		if (cmclocList != null) {
			System.out.println("cmcloc of compare is " + cmclocList.get(0));
			Number num = (Number) cmclocList.get(0);
			if (num != null)
				cmcloc = num.intValue();

		}
		if (cmdclocList != null) {
			System.out.println("cmdcloc of compare is " + cmdclocList.get(0));
			Number num = (Number) cmdclocList.get(0);
			if (num != null)
				cmdcloc = num.intValue();

		}
		if (cmchlocList != null) {
			System.out.println("cmchloc of compare is " + cmchlocList.get(0));
			Number num = (Number) cmchlocList.get(0);
			if (num != null)
				cmchloc = num.intValue();

		}
		if (cclocList != null) {
			System.out.println("ccloc of compare is " + cclocList.get(0));
			Number num = (Number) cclocList.get(0);
			if (num != null)
				ccloc = num.intValue();

		}
		if (ecclocList != null) {
			System.out.println("eccloc of compare is " + ecclocList.get(0));
			Number num = (Number) ecclocList.get(0);
			if (num != null)
				eccloc = num.intValue();

		}
		if (alocList != null) {
			System.out.println("aloc of compare is " + alocList.get(0));
			Number num = (Number) alocList.get(0);
			if (num != null)
				aloc = num.intValue();

		}
		if (ealocList != null) {
			System.out.println("ealoc of compare is " + ealocList.get(0));
			Number num = (Number) ealocList.get(0);
			if (num != null)
				ealoc = num.intValue();

		}
		if (dlocList != null) {
			System.out.println("dloc of compare is " + dlocList.get(0));
			Number num = (Number) dlocList.get(0);
			if (num != null)
				dloc = num.intValue();

		}
		if (edlocList != null) {
			System.out.println("edloc of compare is " + edlocList.get(0));
			Number num = (Number) edlocList.get(0);
			if (num != null)
				edloc = num.intValue();

		}
		if (fileList != null && fileList.size() > 0){
			System.out.println("changeFileNum of compare is " + fileList.size());
			changeFileNum = fileList.size();			
		}
		if (cloneFileList != null && cloneFileList.size() > 0){
			System.out.println("cloneChangeFileNum of compare is " + cloneFileList.size());
			cloneChangeFileNum = cloneFileList.size();
		}

		compare.setLoc(loc);
		compare.setCloc(cloc);
		compare.setChloc(chloc);
		compare.setEloc(eloc);
		compare.setEcloc(ecloc);
		compare.setEchloc(echloc);
		compare.setBchloc(bchloc);
		compare.setBcloc(bcloc);
		compare.setBdcloc(bdcloc);
		compare.setBloc(bloc);
		compare.setCmchloc(cmchloc);
		compare.setCmcloc(cmcloc);
		compare.setCmdcloc(cmdcloc);
		compare.setCmloc(cmloc);
		compare.setChangeFileNum(changeFileNum);
		compare.setCloneChangeFileNum(cloneChangeFileNum);
		compare.setDcloc(dcloc);
		compare.setEdcloc(edcloc);
		compare.setCcloc(ccloc);
		compare.setEccloc(eccloc);
		compare.setAloc(aloc);
		compare.setEaloc(ealoc);
		compare.setDloc(dloc);
		compare.setEdloc(edloc);
		compare.setStatus(1);
		compare.setProcessDoneDate(new Date());
		session.beginTransaction();
		session.update(compare);
		session.getTransaction().commit();
		session.close();

	}

	@Override
	public Compare getCompreById(int compareId) {
		Session session = sessionFactory.openSession();
		List<Compare> compareList = session.createQuery("from Compare where compareId = " + compareId).list();
		session.close();
		
		return compareList.get(0);
	}

	@Override
	public List<ChangeFile> getChangeFilesOfCompareByIdCountFrom(int compareId, int index, int count) {
		Session session = sessionFactory.openSession();
		List<ChangeFile> fileList = session.createQuery("from ChangeFile as f where f.compareId = " + compareId).list();
		session.close();
		
		if (index == -1 || count == -1)
			return fileList;
		return fileList.subList(index, index + count);
	}

	@Override
	public List<Project> getProjectByRepositoryId(int repository) {
		Session session = sessionFactory.openSession();
		List<Project> projectList = session.createQuery("from Project where repositoryId = " + repository).list();
		session.close();
		
		return projectList;
	}

	@Override
	public String getFileNameById(int fileId) {
		Session session = sessionFactory.openSession();
		List<String> fileList = session.createQuery("select fileName from File where fileId = " + fileId).list();
		session.close();
		
		if (fileList != null && fileList.size() > 0)
			return fileList.get(0);
		return "";
	}

	@Override
	public void updateChangeFileLoc(int fileId) {
		Session session = sessionFactory.openSession();
		Integer cloc = 0, ecloc = 0, chloc = 0, echloc = 0;

		ChangeFile file = this.getChangeFileById(fileId);
		int compareId = file.getCompareId();
		List<Integer> clocList = (List<Integer>) session
				.createQuery("select sum(endLine - beginLine + 1) from CloneInChange where fileId =" + file.getFileId()
						+ " and compareId = " + compareId)
				.list();

		List<Integer> eclocList = (List<Integer>) session
				.createQuery("select sum(ecloc) from CloneInChange where fileId =" + file.getFileId()
						+ " and compareId = " + compareId)
				.list();

		List<Integer> chlocList = (List<Integer>) session
				.createQuery("select sum(endLine - beginLine + 1) from Change where fileId =" + file.getFileId()
						+ " and compareId = " + compareId)
				.list();

		List<Integer> echlocList = (List<Integer>) session.createQuery(
				"select sum(echloc) from Change where fileId =" + file.getFileId() + " and compareId = " + compareId)
				.list();

		if (clocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) clocList.get(0);
			if (num != null) {
				cloc = num.intValue();

			}
		}
		if (eclocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) eclocList.get(0);
			if (num != null) {
				ecloc = num.intValue();

			}
		}
		if (chlocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) clocList.get(0);
			if (num != null) {
				chloc = num.intValue();

			}
		}
		if (echlocList != null) {
			// System.out.println("cloc of file is " + clocList.get(0));
			Number num = (Number) eclocList.get(0);
			if (num != null) {
				echloc = num.intValue();

			}
		}
		file.setCloc(cloc);
		file.setEcloc(ecloc);
		file.setChloc(chloc);
		file.setEchloc(echloc);
		session.beginTransaction();
		session.update(file);
		session.getTransaction().commit();
		session.close();
		

	}

	@Override
	public ChangeFile getChangeFileById(int changeFileId) {
		Session session = sessionFactory.openSession();
		List<ChangeFile> fileList = session.createQuery("from changeFile where changeFileId = " + changeFileId).list();
		session.close();
		
		if (fileList != null && fileList.size() > 0)
			return fileList.get(0);
		return null;
	}

	@Override
	public Compare getCompareBetween2Version(int preCommitId, int commitId) {
		Session session = sessionFactory.openSession();
		List<Compare> compareList = session
				.createQuery("from Compare where preCommitId = " + preCommitId + " and commitId = " + commitId).list();
		session.close();
		
		if (compareList != null && compareList.size() > 0)
			return compareList.get(0);
		return null;
	}

	@Override
	public Project getProjectById(int id) {
		Session session = sessionFactory.openSession();
		List<Project> projectList = session.createQuery("from Project where projectId = " + id).list();
		session.close();
		
		if (projectList != null && projectList.size() > 0)
			return projectList.get(0);
		return null;
	}

	@Override
	public Repository getRepositoryByAddress(String address) {
		Session session = sessionFactory.openSession();
		List<Repository> repositoryList = session.createQuery("from Repository where url = '" + address + "'").list();
		session.close();
		
		if (repositoryList != null && repositoryList.size() > 0)
			return repositoryList.get(0);
		else
			return null;
	}

	@Override
	public void deleteFilesByCommitId(int id) {
		int commitId = id;
		Session session = sessionFactory.openSession();

		String hql1 = "delete Clone where commitId = " + commitId;
		String hql2 = "delete CloneClass where commitId = " + commitId;
		String hql3 = "delete File where commitId = " + commitId;
		String hql4 = "delete CommitLanguage where commitId = " + commitId;

		Query query = session.createQuery(hql1);
		query.executeUpdate();
		session.beginTransaction().commit();
		System.out.println("clone clear");
		query = session.createQuery(hql2);
		query.executeUpdate();
		session.beginTransaction().commit();
		System.out.println("clone class clear");
		query = session.createQuery(hql3);
		query.executeUpdate();
		session.beginTransaction().commit();
		System.out.println("file clear");
		query = session.createQuery(hql4);
		query.executeUpdate();
		session.beginTransaction().commit();
		System.out.println("commitLanguage clear");
		session.close();
		

	}

	@Override
	public void deleteClonesByCompareId(int id) {
		Session session = sessionFactory.openSession();

		String hql1 = "delete Change where compareId = " + id;
		String hql2 = "delete CloneInChange where compareId = " + id;
		String hql3 = "delete ChangeFile where compareId = " + id;
		String hql4 = "delete Compare where compareId = " + id;

		Query query = session.createQuery(hql1);
		query.executeUpdate();
		session.beginTransaction().commit();
		System.out.println("change clear");
		query = session.createQuery(hql2);
		query.executeUpdate();
		session.beginTransaction().commit();
		System.out.println("clone in change clear");
		query = session.createQuery(hql3);
		query.executeUpdate();
		session.beginTransaction().commit();
		System.out.println("change file clear");
		query = session.createQuery(hql4);
		query.executeUpdate();
		session.beginTransaction().commit();
		System.out.println("compare clear");
		session.close();
		
	}

	@Override
	public Commit getLastCommit(int projectId) {
		Session session = sessionFactory.openSession();

		String query = "from Commit where projectId = "+projectId+" order by commitDate DESC ";
		List<Commit> commitList = session.createQuery(query).list();
		session.close();
		
		if (commitList != null && commitList.size() > 0)
			return commitList.get(0);
		else{
			System.err.format("this project has no commit, please check your projectId%n");
			return new Commit();
		}
	}

	@Override
	public void deleteProjectById(int id) {
		int projectId = id;
		Session session = sessionFactory.openSession();

		String hql1 = "delete Project where projectId = " + projectId;

		Query query = session.createQuery(hql1);
		query.executeUpdate();
		session.beginTransaction().commit();
		
		session.close();
		
	}

	@Override
	public void deleteRepositoryById(int id) {
		int repositoryId = id;
		Session session = sessionFactory.openSession();

		String hql1 = "delete Repository where repositoryId = " + repositoryId;

		Query query = session.createQuery(hql1);
		query.executeUpdate();
		session.beginTransaction().commit();
		
		session.close();
		
		
	}

	//add by junyi
	@Override
	public void deleteThingsByCommitId(int id) {
		// TODO Auto-generated method stub
		int commitId = id;
		Session session = sessionFactory.openSession();

		String hql1 = "delete Clone where commitId = " + commitId;
		String hql2 = "delete CloneClass where commitId = " + commitId;
		String hql3 = "delete File where commitId = " + commitId;
		String hql4 = "delete Diff where commitId = " + commitId;
//		String hql5 = "delete Commit where commitId = " + commitId;

		Query query = session.createQuery(hql1);
		query.executeUpdate();
		session.beginTransaction().commit();
		System.out.println("clone clear");
		query = session.createQuery(hql2);
		query.executeUpdate();
		session.beginTransaction().commit();
		System.out.println("clone class clear");
		query = session.createQuery(hql3);
		query.executeUpdate();
		session.beginTransaction().commit();
		System.out.println("file clear");
//		query = session.createQuery(hql4);
//		query.executeUpdate();
//		session.beginTransaction().commit();
//		System.out.println("diff clear");
//		query = session.createQuery(hql5);
//		query.executeUpdate();
//		session.beginTransaction().commit();
//		System.out.println("commit clear");

		session.close();
		

	}

	@Override
	public void deleteCommitByProjectId(int id) {
		// TODO Auto-generated method stub
		int projectId = id;
		Session session = sessionFactory.openSession();

		String hql1 = "delete Commit where projectId = " + projectId;

		Query query = session.createQuery(hql1);
		query.executeUpdate();
		session.beginTransaction().commit();
		
		session.close();
		
	}

	@Override
	public List<Clone> getClonesByFileId(int fileId) {
		Session session = sessionFactory.openSession();
		List<Clone> cloneList = session.createQuery("from Clone where fileId = " + fileId).list();
		session.close();
		
		return cloneList;
	}

	@Override
	public Repository getRepoById(int id) {
		Session session = sessionFactory.openSession();
		List<Repository> repoList = session.createQuery("from Repository where repositoryId = " + id).list();
		session.close();
		
		if (repoList != null && repoList.size() > 0)
			return repoList.get(0);
		return null;
	}

	@Override
	public Repository getRepositoryByProjectNameEn(String projectNameEn) {
		Session session = sessionFactory.openSession();
		List<Project> projectList = session.createQuery("from Project where projectNameEn = '" + projectNameEn+"'").list();
		if(projectList.size()<=0){
			session.close();
			
			return null;
		}else{
			int id = projectList.get(0).getRepositoryId();
			List<Repository> repoList = session.createQuery("from Repository where repositoryId = " + id).list();
			session.close();
			
			if (repoList != null && repoList.size() > 0)
				return repoList.get(0);
		}
		return null;
	}

	@Override
	public void updateCloneCcloc(Clone c, int repeat) {
		Session session = sessionFactory.openSession();
		int beginLine,endLine;
		int cloc=0,ecloc=0;
		int ccloc = 0,eccloc =0;
		int repeatType=0;
		double[]lengthScore={0,0.5,0.6,0.7,0.8,0.9};
		double[]repeatScore={0,0.1,0.3,0.5,0.8,1};
		beginLine =c.getBeginLine();
		endLine = c.getEndLine();
		cloc = endLine-beginLine+1;
		ecloc =c.getEcloc();
		repeatType = getRepeatType(repeat);	
		ccloc = (int) (cloc*(1-lengthScore[getLengthType(cloc)]*repeatScore[repeatType]));
		eccloc = (int) (ecloc*(1-lengthScore[getLengthType(ecloc)]*repeatScore[repeatType]));
		c.setCcloc(ccloc);		
		c.setEccloc(eccloc);	
		c.setRepeatTime(repeat);
		session.beginTransaction();
		session.update(c);
		session.getTransaction().commit();
		session.close();
		
		
	}
	
	public int getRepeatType(int repeatTime){
		int repeatType = 0;
		if(repeatTime < 4)
			repeatType = repeatTime;
		else 
			repeatType = 5;	
		return repeatType;
	}
	
	public int getLengthType(int length){
		int lengthType = 0;
		if(length < 6)
			lengthType = 0;
		else if(length < 10)
			lengthType = 1;
		else if(length < 20)
			lengthType = 2;
		else if(length < 50)
			lengthType = 3;
		else if(length <100)
			lengthType = 4;
		else
			lengthType = 5;
		return lengthType;
	}

	@Override
	public void deleteTeamById(int teamId) {
		Session session = sessionFactory.openSession();

		String hql1 = "delete Team where teamId = " + teamId;

		Query query = session.createQuery(hql1);
		query.executeUpdate();
		session.beginTransaction().commit();
		
		session.close();
		
	}

	@Override
	public void saveTeam(Team team) {
		synchronized (saveTeamClock) {
			Session session = sessionFactory.openSession();
			session.beginTransaction();
			session.save(team);
			session.getTransaction().commit();
			session.close();
			
		}
	}
	
	@Override
	public void saveCCStreamProperty(CCStreamProperty property) {
		synchronized (saveCCSTREAMClock) {
			Session session = sessionFactory.openSession();
			session.beginTransaction();
			session.save(property);
			session.getTransaction().commit();
			session.close();
			
		}
	}

	@Override
	public List<Team> getTeams() {
		Session session = sessionFactory.openSession();
		List<Team> teamList = session.createQuery("from Team").list();
		session.close();
		
		return teamList;
	}

	public File getFileOfCommit(Commit currentCommit) {
		int commitId = currentCommit.getCommitId();
		Session session = sessionFactory.openSession();
		List<File> fileList = session
				.createQuery("from File as f where f.commitId = " + commitId)
				.list();
		session.close();
		
		return fileList.get(0);
	}

	@Override
	public void saveCompareLanguage(CompareLanguage clan, Collection<String> suffixes) {
		// TODO Auto-generated method stub
		int compareId = clan.getCompareId();

		StringBuilder sb = new StringBuilder();
		sb.append(" and regexp(fileName,'");
		for (Iterator iterator = suffixes.iterator(); iterator.hasNext();) {
			String suffix = (String) iterator.next();
			sb.append("(\\."+suffix+"$)|");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("')=1");
		String selectSuffix = sb.toString();

		Session session = sessionFactory.openSession();
		Integer changeFileNum=0,cloneChangeFileNum=0;
		Integer loc=0,eloc=0,bloc=0,cmloc=0;
		Integer aloc=0,ealoc=0,dloc=0,edloc=0,chloc=0,echloc=0,bchloc=0,cmchloc=0;
		Integer cloc=0,ecloc=0,dcloc=0,edcloc=0,bcloc=0,cmcloc=0,bdcloc=0,cmdcloc=0;
		Integer ccloc = 0,eccloc = 0;
		
		List<Integer> locList = (List<Integer>) session
				.createQuery("select sum(loc) from ChangeFile where compareId =" + compareId + selectSuffix).list();
		List<Integer> elocList = (List<Integer>) session
				.createQuery("select sum(eloc) from ChangeFile where compareId =" + compareId + selectSuffix).list();
		List<Integer> blocList = (List<Integer>) session
				.createQuery("select sum(bloc) from ChangeFile where compareId =" + compareId + selectSuffix).list();
		List<Integer> cmlocList = (List<Integer>) session
				.createQuery("select sum(cmloc) from ChangeFile where compareId =" + compareId + selectSuffix).list();
		List<Integer> alocList = (List<Integer>) session
				.createQuery("select sum(aloc) from ChangeFile where compareId =" + compareId + selectSuffix).list();
		List<Integer> ealocList = (List<Integer>) session
				.createQuery("select sum(ealoc) from ChangeFile where compareId =" + compareId + selectSuffix).list();
		List<Integer> dlocList = (List<Integer>) session
				.createQuery("select sum(dloc) from ChangeFile where compareId =" + compareId + selectSuffix).list();
		List<Integer> edlocList = (List<Integer>) session
				.createQuery("select sum(edloc) from ChangeFile where compareId =" + compareId + selectSuffix).list();
		List<Integer> cmchlocList = (List<Integer>) session
				.createQuery("select sum(cmchloc)  from ChangeFile where compareId =" + compareId + selectSuffix).list();
		List<Integer> bchlocList = (List<Integer>) session
				.createQuery("select sum(bchloc)  from ChangeFile where compareId =" + compareId + selectSuffix).list();
		List<Integer> clocList = (List<Integer>) session
				.createQuery("select sum(cloc)  from ChangeFile where compareId =" + compareId + selectSuffix).list();
		List<Integer> eclocList = (List<Integer>) session
				.createQuery("select sum(ecloc)  from ChangeFile where compareId =" + compareId + selectSuffix).list();
		List<Integer> bclocList = (List<Integer>) session
				.createQuery("select sum(bcloc)  from ChangeFile where compareId =" + compareId + selectSuffix).list();
		List<Integer> cmclocList = (List<Integer>) session
				.createQuery("select sum(cmcloc)  from ChangeFile where compareId =" + compareId + selectSuffix).list();
		List<Integer> dclocList = (List<Integer>) session
				.createQuery("select sum(dcloc)  from ChangeFile where compareId =" + compareId + selectSuffix).list();
		List<Integer> edclocList = (List<Integer>) session
				.createQuery("select sum(edcloc)  from ChangeFile where compareId =" + compareId + selectSuffix).list();
		List<Integer> bdclocList = (List<Integer>) session
				.createQuery("select sum(bdcloc)  from ChangeFile where compareId =" + compareId + selectSuffix).list();
		List<Integer> cmdclocList = (List<Integer>) session
				.createQuery("select sum(cmdcloc)  from ChangeFile where compareId =" + compareId + selectSuffix).list();
		List<Integer> cclocList = (List<Integer>) session
				.createQuery("select sum(ccloc)  from ChangeFile where compareId =" + compareId + selectSuffix).list();
		List<Integer> ecclocList = (List<Integer>) session
				.createQuery("select sum(eccloc)  from ChangeFile where compareId =" + compareId + selectSuffix).list();
		List<ChangeFile> fileList = (List<ChangeFile>) session
				.createQuery("from ChangeFile where chloc > 0 and compareId = " + compareId+ selectSuffix).list();
		List<ChangeFile> cloneFileList = (List<ChangeFile>) session
				.createQuery("from ChangeFile where cloc > 0 and compareId = " + compareId+ selectSuffix).list();

		if (locList != null) {
			System.out.println("loc of compareLanguage is " + locList.get(0));
			Number num = (Number) locList.get(0);
			if (num != null)
				loc = num.intValue();
		}
		if (elocList != null) {
			System.out.println("eloc of compareLanguage is " + elocList.get(0));
			Number num = (Number) elocList.get(0);
			if (num != null)
				eloc = num.intValue();
		}
		if (blocList != null) {
			System.out.println("bloc of compareLanguage is " + blocList.get(0));
			Number num = (Number) blocList.get(0);
			if (num != null)
				bloc = num.intValue();
		}
		if (cmlocList != null) {
			System.out.println("cmloc of compareLanguage is " + cmlocList.get(0));
			Number num = (Number) cmlocList.get(0);
			if (num != null)
				cmloc = num.intValue();
		}
		if (alocList != null) {
			System.out.println("aloc of compareLanguage is " + alocList.get(0));
			Number num = (Number) alocList.get(0);
			if (num != null)
				aloc = num.intValue();
		}
		if (ealocList != null) {
			System.out.println("ealoc of compareLanguage is " + ealocList.get(0));
			Number num = (Number) ealocList.get(0);
			if (num != null)
				ealoc = num.intValue();
		}
		if (dlocList != null) {
			System.out.println("dloc of compareLanguage is " + dlocList.get(0));
			Number num = (Number) dlocList.get(0);
			if (num != null)
				dloc = num.intValue();
		}
		if (edlocList != null) {
			System.out.println("edloc of compareLanguage is " + edlocList.get(0));
			Number num = (Number) edlocList.get(0);
			if (num != null)
				edloc = num.intValue();
		}
		if (cmchlocList != null) {
			System.out.println("cmchloc of compareLanguage is " + edlocList.get(0));
			Number num = (Number) cmchlocList.get(0);
			if (num != null)
				cmchloc = num.intValue();
		}
		if (bchlocList != null) {
			System.out.println("bchloc of compareLanguage is " + edlocList.get(0));
			Number num = (Number) bchlocList.get(0);
			if (num != null)
				bchloc = num.intValue();
		}
	
		if (clocList != null) {
			System.out.println("cloc of compareLanguage is " + clocList.get(0));
			Number num = (Number) clocList.get(0);
			if (num != null)
				cloc = num.intValue();
		}
		if (eclocList != null) {
			System.out.println("celoc of compareLanguage is " + eclocList.get(0));
			Number num = (Number) eclocList.get(0);
			if (num != null)
				ecloc = num.intValue();
		}
		if (cmclocList != null) {
			System.out.println("cmcloc of compareLanguage is " + cmclocList.get(0));
			Number num = (Number) cmclocList.get(0);
			if (num != null)
				cmcloc = num.intValue();
		}
		if (bclocList != null) {
			System.out.println("bcloc of compareLanguage is " + bclocList.get(0));
			Number num = (Number) bclocList.get(0);
			if (num != null)
				bcloc = num.intValue();
		}
		if (dclocList != null) {
			System.out.println("dcloc of compare is " + dclocList.get(0));
			Number num = (Number) dclocList.get(0);
			if (num != null)
				dcloc = num.intValue();
		}

		if (edclocList != null) {
			System.out.println("celoc of compare is " + edclocList.get(0));
			Number num = (Number) edclocList.get(0);
			if (num != null)
				edcloc = num.intValue();
		}
		if (bdclocList != null) {
			System.out.println("bdcloc of compare is " + bdclocList.get(0));
			Number num = (Number) bdclocList.get(0);
			if (num != null)
				bdcloc = num.intValue();
		}
		if (cmdclocList != null) {
			System.out.println("cmdcloc of compare is " + cmdclocList.get(0));
			Number num = (Number) cmdclocList.get(0);
			if (num != null)
				cmdcloc = num.intValue();

		}
		if (cclocList != null) {
			System.out.println("ccloc of compare is " + cclocList.get(0));
			Number num = (Number) cclocList.get(0);
			if (num != null)
				ccloc = num.intValue();
		}
		if (ecclocList != null) {
			System.out.println("eccloc of compare is " + ecclocList.get(0));
			Number num = (Number) ecclocList.get(0);
			if (num != null)
				eccloc = num.intValue();
		}
		if (fileList != null && fileList.size() > 0){
			System.out.println("changeFileNum of compare is " + fileList.size());
			changeFileNum = fileList.size();			
		}
		if (cloneFileList != null && cloneFileList.size() > 0){
			System.out.println("cloneChangeFileNum of compare is " + cloneFileList.size());
			cloneChangeFileNum = cloneFileList.size();
		}

		chloc = aloc + dloc;
		echloc = ealoc + edloc;
		clan.setChangeFileNum(changeFileNum);
		clan.setCloneChangeFileNum(cloneChangeFileNum);
		clan.setLoc(loc);
		clan.setEloc(eloc);
		clan.setBloc(bloc);
		clan.setCmloc(cmloc);
		clan.setAloc(aloc);
		clan.setEaloc(ealoc);
		clan.setDloc(dloc);
		clan.setEdloc(edloc);
		clan.setChloc(chloc);
		clan.setEchloc(echloc);
		clan.setCloc(cloc);
		clan.setEcloc(ecloc);
		clan.setDcloc(dcloc);
		clan.setEdcloc(edcloc);
		clan.setBcloc(bcloc);
		clan.setCmcloc(cmcloc);
		clan.setBdcloc(bdcloc);
		clan.setCmdcloc(cmdcloc);
		clan.setBchloc(bchloc);
		clan.setCmchloc(cmchloc);
		clan.setCcloc(ccloc);
		clan.setEccloc(eccloc);		
		session.beginTransaction();
		session.save(clan);
		session.getTransaction().commit();
		session.close();
		
	}

	@Override
	public void deleteCommitById(int commitId) {
		Session session = sessionFactory.openSession();
		//comparelanguage,compare,cloneinchange,changefile

		String hql1 = "delete Clone where commitId = " + commitId;
		String hql2 = "delete CloneClass where commitId = " + commitId;
		String hql3 = "delete File where commitId = " + commitId;
		String hql4 = "delete Change where commitId = " + commitId;
		String hql5 = "delete Commit where commitId = " + commitId;
		String hql6 = "delete CommitLanguage where commitId = " + commitId;

		Query query = session.createQuery(hql1);
		query.executeUpdate();
		session.beginTransaction().commit();
		System.out.println("clone clear");
		query = session.createQuery(hql2);
		query.executeUpdate();
		session.beginTransaction().commit();
		System.out.println("clone class clear");
		query = session.createQuery(hql3);
		query.executeUpdate();
		session.beginTransaction().commit();
		System.out.println("file clear");
		query = session.createQuery(hql4);
		query.executeUpdate();
		session.beginTransaction().commit();
		System.out.println("diff clear");
		query = session.createQuery(hql5);
		query.executeUpdate();
		session.beginTransaction().commit();
		System.out.println("commit clear");
		query = session.createQuery(hql6);
		query.executeUpdate();
		session.beginTransaction().commit();
		System.out.println("commit language clear");
		List<Compare> compares = getComparesByCommitId(commitId);
		if(compares !=null){
			for(Compare compare:compares){
				int compareId = compare.getCompareId();
				String hql7 = "delete CompareLanguage where compareId = " + compareId;
				String hql8 = "delete Compare where commitId = " + compareId;
				String hql9 = "delete CloneInChange where commitId = " + compareId;
				String hql10 = "delete ChangeFile where commitId = " + compareId;
				System.out.println("delete Compare: "+compareId);
				query = session.createQuery(hql7);
				query.executeUpdate();
				session.beginTransaction().commit();
				System.out.println("...25%");
				query = session.createQuery(hql8);
				query.executeUpdate();
				session.beginTransaction().commit();
				System.out.println("...50%");
				query = session.createQuery(hql9);
				query.executeUpdate();
				session.beginTransaction().commit();
				System.out.println("...75%");
				query = session.createQuery(hql10);
				query.executeUpdate();
				session.beginTransaction().commit();
				System.out.println("...100%!");
				System.out.println("Compare: "+compareId+" done。。。");
			}
		}
		System.out.println("over!");
		session.close();
		
	}

	@Override
	public List<Compare> getComparesByCommitId(int commitId) {
		Session session = sessionFactory.openSession();
		List<Compare> compareList = session
				.createQuery("from Compare where preCommitId = " + commitId + " or commitId = " + commitId).list();
		session.close();
		
		if (compareList != null && compareList.size() > 0)
			return compareList;

		return null;
		
	}

	@Override
	public Clone getCloneByCloneId(int cloneId) {
		Session session = sessionFactory.openSession();
		List<Clone> cloneList = session
				.createQuery("from Clone where cloneId="+cloneId).list();
		session.close();
		
		if (cloneList != null && cloneList.size() > 0)
			return cloneList.get(0);
		return null;
	}

	@Override
	public List<Clone> getClonesByClassId(Integer cloneClassId) {
		Session session = sessionFactory.openSession();
		List<Clone> cloneList = session
				.createQuery("from Clone where cloneClassId="+cloneClassId).list();
		session.close();
		
		if (cloneList != null && cloneList.size() > 0)
			return cloneList;
		return null;
	}
	
	@Override
	public List<Clone> getClonesByCommitId(int commitId) {
		Session session = sessionFactory.openSession();
		List<Clone> cloneList = session
				.createQuery("from Clone where commitId="+commitId).list();
		session.close();
		
		if (cloneList != null && cloneList.size() > 0)
			return cloneList;
		return null;
	}
	
	@Override
	public ProgramLanguage getProgramLanguage(String language) {
		Session session = sessionFactory.openSession();
		List<ProgramLanguage> list = session
				.createQuery("from ProgramLanguage where language = '"+language+"'").list();
		session.close();
		
		if (list != null && list.size() == 1)
			return list.get(0);
		return null;
	}
	
	@Override
	public CCStreamProperty getCCStream(String stream) {
		Session session = sessionFactory.openSession();
		List<CCStreamProperty> list = session
				.createQuery("from CCStreamProperty where stream = '"+stream+"'").list();
		session.close();
		
		if (list != null && list.size() == 1)
			return list.get(0);
		return null;
	}

	@Override
	public List<ProgramLanguage> getProgramLanguages() {
		Session session = sessionFactory.openSession();
		List<ProgramLanguage> list = session
				.createQuery("from ProgramLanguage").list();
		session.close();
		
		if (list != null && list.size() > 0)
			return list;
		return null;
	}
	
	public List<File> getFilesOfCommitByIdTag(int commitId, int tag){
		Session session = sessionFactory.openSession();
		List<File> fileList = session
				.createQuery(String.format("from File as f where f.commitId = %d and f.renameTag = %d group by fileName", commitId, tag).toString())
				.list();
		session.close();
		
		return fileList;
	}

	@Override
	public int getAboveStageCommit(Integer projectId) {
		Session session = sessionFactory.openSession();
		List<Commit> list = session.createQuery(String.format("from Commit where projectId=%d and stage>=%d",projectId, Commit.FILE_A_FINISH)).list();
		session.close();
		
		if (list != null && list.size() > 0)
			return list.get(0).getCommitId();
		return -1;
	}

	@Override
	public void updateCCStreamProperty(CCStreamProperty property) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(property);
		session.getTransaction().commit();
		session.close();
	}
	
	@Override
	public void freshCCStreamProperty(CCStreamProperty property) {
		Session session = sessionFactory.openSession();
		List<File> list = session.createQuery("from CCStreamProperty where stream = '" + property.getStream()+"'").list();
		session.close();
		
		if (list != null && list.size() != 0){
			session.beginTransaction();
			session.update(property);
			session.getTransaction().commit();
		}
		else{
			session.beginTransaction();
			session.save(property);
			session.getTransaction().commit();
		}
		session.close();
		
	}

	@Override
	public List<CCStreamProperty> getCCStreams() {
		Session session = sessionFactory.openSession();
		List<CCStreamProperty> list = session.createQuery("from CCStreamProperty").list();
		session.close();
		
		return list;
	}

	@Override
	public void delCCStreamProperty(CCStreamProperty property) {
		Session session = sessionFactory.openSession();

		String hql1 = "delete CCStreamProperty where stream = '" + property.getStream()+"'";

		Query query = session.createQuery(hql1);
		query.executeUpdate();
		session.beginTransaction().commit();
		
		session.close();
		
	}
	
	private List<Commit> getCommitWithCondition(String condition){
		Session session = sessionFactory.openSession();
		List<Commit> commitList = session.createQuery("from Commit " + condition).list();
		session.close();
		return commitList;
	}
	
	public List<Commit> getUnfinishedCommit(int projectId){
		String condition = String.format("where projectId = %d and (stage >= %d or stage <= %d)", projectId, Commit.START, Commit.WAIT);
		return getCommitWithCondition(condition);
	}

	@Override
	public List<CompareLanguage> getCompareLanguageByCompare(int compareId) {
		Session session = sessionFactory.openSession();
		List<CompareLanguage> compareLanguage = session.createQuery("from CompareLanguage where compareId = " + compareId).list();
		session.close();
		return compareLanguage;
	}

	@Override
	public List<Clone> deleteClonesById(int id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Clone clone = this.getCloneByCloneId(id);
		int size = clone.getCloneSize();
		int cloneClassId = clone.getCloneClassId();
		List<Clone> clones = this.getClonesByClassId(cloneClassId);

		String hql1 = "delete Clone where cloneClassId = " + cloneClassId;
		String hql2 = "delete CloneClass where cloneClassId = " + cloneClassId;
		String hql3 = "delete CloneInChange where cloneClassId = " + cloneClassId;

		Query query = session.createQuery(hql1);
		query.executeUpdate();
		session.beginTransaction().commit();
		query = session.createQuery(hql2);
		query.executeUpdate();
		session.beginTransaction().commit();
		query = session.createQuery(hql3);
		query.executeUpdate();
		session.beginTransaction().commit();
		session.close();
		return clones;
	}

	@Override
	public ChangeFile getChangeFileByFileId(int fileId) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		List<ChangeFile> fileList = session.createQuery("from changeFile where fileId = " + fileId).list();
		session.close();
		
		if (fileList != null && fileList.size() > 0)
			return fileList.get(0);
		return null;
	}

	@Override
	public void updateCloneWithCloneClassId(int cloneClassId, int type) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String hql = String.format("update Clone c set c.cloneType = %d where c.cloneClassId= %d", type, cloneClassId);
		Query query = session.createQuery(hql);
		query.executeUpdate();
		tx.commit();
		session.close();
		
	}
}
