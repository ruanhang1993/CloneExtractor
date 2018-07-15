package cn.edu.fudan.se.clonedetector.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Author;
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
import cn.edu.fudan.se.clonedetector.bean.CCStreamProperty;

public interface IDataAccessor {
	public <T> void save(List<T> list);

	public <T> void save(T obj);

	public List<Commit> getCommits();

	public Commit getLastCommit(int projectId);

	public List<Project> getProjects();

//	public int getLastCommitId();
//
//	public int getLastChangeFileId();
//
//	public int getLastCompareId();
//
//	public int getLastCloneInChangeId();
//
//	public int getLastFileId();
//
//	public int getLastChangeId();
//
//	public int getLastRepositoryId();
//
//	public int getLastAuthorId();
//
//	public int getLastCloneClassId();
//
//	public int getLastCloneId();
//
//	public int getLastProjectId();

	public List<String> getRevisionIDs();

	public Commit getCommitById(int id);
	
	public Commit getCommitByIdAndLanguage(int id, String language);

	public String getFileNameById(int fileId);

	public List<File> getFilesOfCommitByIdCountFrom(int id, int index, int count);

	public List<ChangeFile> getChangeFilesOfCompareByIdCountFrom(int compareId, int index, int count);

	public List<Change> getChangesOfCommit(Commit commit);

	public List<Change> getChangesOfCommitIdAndType(int preCommitId, int commitId, int types);

	public List<Change> getChangesOfFileName(String fileName);

	public int getCommitCount();

	public List<Commit> getCommitsFromTo(int start, int end);

	public File getFileOfCommitByName(Commit currentCommit, String fileName);

	public File getFileById(int fileId);

	public File getFileOfCommitByIdByName(int id, String fileName);

	public List<Author> getAuthors();

	public Author getAuthorById(int id);

	public Author getAuthorByName(String name);

	public List<File> getFiles();

	public List<Commit> getCommitByAuthorAndDate(Author author, Date t1, Date t2);

	public void deleteAllAbout(Commit c);

	public Commit getEarliestCommit();

	public Commit getCommitByProjectAndRevisionId(int projectId, String revisionId);

	public List<Change> getChangesOfOldFileName(String fileName);

	public List<Clone> getClones();

	public List<Clone> getClonesOfCommit(Commit commit);

	public List<Clone> getClonesOfCommitId(int commit);

	public List<Clone> getClonesOfCommitIdAndFileId(int commitId, int fileId);

	public List<CloneClass> getCloneClassOfCommit(Commit commit);

	public void saveAuthor(Author author);

	public void saveChange(Change change);

	public void saveClone(Clone clone);

	public void saveCloneClass(CloneClass cloneClass);

	public void saveCommit(Commit commit);

	public void saveCompare(Compare compare);

	public void saveFile(File file);

	public void saveChangeFile(ChangeFile cf);

	public void saveProject(Project project);

	public void saveCloneInChange(CloneInChange cloneInClass);

	public void saveRepository(Repository repo);

	public void updateAuthor(Author author);

	public void updateChange(Change change);

	public void updateClone(Clone clone);

	public void updateChangeFile(ChangeFile cf);

	public void updateCloneClass(CloneClass cloneClass);

	public void updateCommit(Commit commit);

	public void updateCompare(Compare commit);

	public void updateFile(File file);

	public void updateProject(Project project);

	public void updateCloneInChange(CloneInChange cloneInClass);

	public void updateRepository(Repository repo);

	public List<Commit> getCommitsByProjectId(int projectId);

	public void updateCommitCLoc(Commit commit);

	public void updateCommitLoc(Commit commit);

	public void updateChangeFileLoc(ChangeFile cf);

	public List<Integer> getCloneFileIdByCommitId(int commit);

	public void updateFileCLoc(int fileId);

	public void updateChangeFileLoc(int fileId);

	public void updateCompareLoc(Compare compare);

	public ChangeFile getChangeFileById(int changeFileId);

	public List<Integer> getDistinctFileFromChange(int compareId);

	public List<Compare> getComparesByProjectId(int projectId);

	public Compare getCompareBetween2Version(int preCommitId, int commitId);

	public Compare getCompreById(int compareId);

	public List<Project> getProjectByRepositoryId(int repository);

	public Project getProjectById(int id);

	public Repository getRepositoryByAddress(String address);
	
	public Repository getRepositoryByProjectNameEn(String projectNameEn);

	/*	comment by junyi 
	 * deleteFilesByCommitId():
	 * delete Clone where commitId
	 * delete CloneClass where commitId
	 * delete File where commitId
	 * delete Commit where commitId */
	public void deleteFilesByCommitId(int commitId);
	/*	add by junyi 
	 * deleteCommitById():
	 * delete Clone where commitId
	 * delete CloneClass where commitId
	 * delete File where commitId
	 * delete Diff where commitId*/
	public void deleteThingsByCommitId(int commitId);
	/* comment by junyi
	 * deleteClonesByCompareId()
	 * delete Change where compareId
	 * delete CloneInChange where compareId
	 * delete ChangeFile where compareId
	 * delete Compare where compareId*/
	public void deleteClonesByCompareId(int compareId);
	public void deleteCommitByProjectId(int projectId);
	public void deleteProjectById(int projectId);
	public void deleteRepositoryById(int RepositoryId);
	
	public List<Clone> getClonesByFileId(int fileId);
	
	public Repository getRepoById(int id);
	
	/**
	 * 如果发现该种语言没有相关文件，则不保存
	 * @param commitId
	 * @param language
	 */
	public CommitLanguage saveCommitLanguage(CommitLanguage cl, Collection<String> suffixes);

	void updateCommitLoc2(Commit commit);
	
	public List<CommitLanguage> getCommitLanguageById(int id);

	void updateCommitLanguageCloc(CommitLanguage cl, Collection<String> suffixes);

	public void updateCloneCcloc(Clone c, int repeat);

	public int getLengthType(int length);

	public int getRepeatType(int repeatTime);

	public void deleteTeamById(int teamId);
	
	public void saveTeam(Team team);
	
	public List<Team> getTeams();
	
	public File getFileOfCommit(Commit currentCommit);

	public void saveCompareLanguage(CompareLanguage clan, Collection<String> suffixes);

	public void deleteCommitById(int commitId);

	public List<Compare> getComparesByCommitId(int commitId);

	public Clone getCloneByCloneId(int cloneId);

	public List<Clone> getClonesByClassId(Integer cloneClassId);

	public List<Clone> getClonesByCommitId(int commitId);

	public ProgramLanguage getProgramLanguage(String language);
	
	public List<ProgramLanguage> getProgramLanguages();

	public CCStreamProperty getCCStream(String stream);

	public List<File> getFilesOfCommitByIdTag(int commitId, int renameRnTag);

	public int getAboveStageCommit(Integer projectId);

	public boolean hasCommitLanguage(int commitId, String language);

	void saveCCStreamProperty(CCStreamProperty property);
	
	void updateCCStreamProperty(CCStreamProperty property);

	void freshCCStreamProperty(CCStreamProperty property);

	public List<CCStreamProperty> getCCStreams();

	public void delCCStreamProperty(CCStreamProperty property);

	public List<Commit> getUnfinishedCommit(int projectId);
	
	public List<CompareLanguage> getCompareLanguageByCompare(int compareId);

	public List<Clone> deleteClonesById(int cloneId);

	public ChangeFile getChangeFileByFileId(int fileId);
	
	public void updateCloneWithCloneClassId(int cloneClassId, int type);

}
