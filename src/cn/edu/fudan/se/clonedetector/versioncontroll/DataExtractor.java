package cn.edu.fudan.se.clonedetector.versioncontroll;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.tmatesoft.svn.core.SVNException;

import cn.edu.fudan.se.clonedetector.bean.Author;
import cn.edu.fudan.se.clonedetector.bean.Clone;
import cn.edu.fudan.se.clonedetector.bean.CloneClass;
import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.bean.CommitLanguage;
import cn.edu.fudan.se.clonedetector.bean.File;
import cn.edu.fudan.se.clonedetector.bean.ProgramLanguage;
import cn.edu.fudan.se.clonedetector.bean.Project;
import cn.edu.fudan.se.clonedetector.bean.Repository;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.CloneDetectionResult;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.exception.FileWrongEncodingException;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl.CCFinderCloneDetector;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneClassImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.util.CCFinder;
import cn.edu.fudan.se.clonedetector.ccfinder.evolution.EvolutionAnalyse;
import cn.edu.fudan.se.clonedetector.dao.IDataAccessor;
import cn.edu.fudan.se.clonedetector.util.FileProcesser;

public abstract class DataExtractor {
	protected IDataAccessor dao;
	protected String repoLocation;
	protected String projectPrefix = "";
	protected List<String> extensions = new ArrayList<>(); //编程语言的名称，如c++
	protected String[] selectedDirs = {};// 需要分析的文件夹，如果为空，则所有文件夹都需要分析。
	private List<String> ignoredSuffixes = new ArrayList<>();// 忽略的文件后缀，形式如.h
	private List<String> suffixes = new ArrayList<>();// 需要分析的文件后缀，形式如.c
	private Map<String, String> suffix_fileList; // filelist是一个txt文件，记录了要分析的文件，因为这些文件的拓展名不是ccfinder支持的拓展名，所以采用这种方式
	protected boolean needCleanCcfxprep = false; // 决定是否需要遍历整个文件夹所有文件来清理ccfxprep，仅当采用fileList方式分析时需要清理
	protected String system;

	protected int minToken = 40;
	protected int minLine;
	protected int minClass;
	protected String encoding;
	
	public Type type = Type.ALL_TYPE;// 使用哪种模式进行分析

	public DataExtractor(IDataAccessor dao, String repoLocation) {
		this.dao = dao;
		this.repoLocation = repoLocation;
	}

//	public abstract void extract(String branch, int projectId) throws Exception;

	public void setProjectPrefix(String projectPrefix) {
		this.projectPrefix = projectPrefix;
	}

	public void setExtension(List<String> extension) {
		this.extensions = extension;
	}

	public List<String> getSuffixes() {
		return suffixes;
	}

	public void setSuffixes(List<String> suffixes) {
		this.suffixes = suffixes;
	}

	protected Collection<String> toInvolvedSuffixes(String extension){
		Collection<String> suffixes = new ArrayList<String>();
		ProgramLanguage pl = dao.getProgramLanguage(extension);
		String[]  set = pl.getSuffix().replaceAll("\\s", "").split(",");
		for (String str : set) {
			suffixes.add(str);
		}
		return suffixes;
	}
	
	protected boolean isValidFile(String fileName, Collection<String> involvedSuffixes) {
		if (!fileName.startsWith(projectPrefix)) {
			return false;
		}

		return hasExtension(fileName, involvedSuffixes);
	}

	/**
	 * determine whether this file need to be analyzed
	 * @param fileName
	 * @return
	 */
	 ;
	protected boolean hasExtension(String fileName, Collection<String> involvedSuffixes) {
		int index = fileName.lastIndexOf(".");
		if (index < 0) {
			return false;
		}
		String extension = fileName.substring(index, fileName.length());
		for (String ex : involvedSuffixes) {
			if (ex.equals(extension)){
				if (suffix_fileList.containsKey(ex)) {
					FileProcesser.writeFile(getFileList(suffix_fileList.get(ex)), fileName+"\n", true);
				}
				return true;
			}
		}
		return false;
	}	
	
	protected String getFileList(String language){
		return repoLocation+"\\"+language+".txt";
	}
	
	protected boolean isIgnored(String fileName) {		
		for (String suffix : ignoredSuffixes){
			if (fileName.endsWith(suffix)) {
				return true;
			}
		}

		return false;
	}
	
	public void setIgnoreSuffixes(List<String> ignoredSuffixes) {
		this.ignoredSuffixes = ignoredSuffixes;
	}

	public void setSuffix_fileList(Map<String, String> language_file) {
		this.suffix_fileList = language_file;
	}

	protected File saveFile(Commit commit, String fileName, String validFileName, byte[] content, int loc, int eloc, int bloc, int cmloc) {
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
		File file = new File();
			file.setCommitId(commit.getCommitId());
			file.setFileName(fileName);
			file.setValidFileName(validFileName);
			file.setContent(content);
			file.setLoc(loc);
			file.setEloc(eloc);
			file.setBloc(bloc);
			file.setCmloc(cmloc);
			file.setFileType(fileType);
//			int id = dao.getLastFileId() + 1;
//			System.out.format("file name is : %s,file id is : %d%n",fileName,id);
//			file.setFileId(id);
			dao.saveFile(file);

			return file;
	}

	protected Commit saveCommit(String revisionID, String log, Date time, Author author, int projectId) {
		Commit commit = new Commit();
		commit.setRevisionId(revisionID);
		commit.setCommitLog(log);
		commit.setCommitDate(time);
		commit.setAuthorId(author.getAuthorId());
		commit.setScanDate(new Date());
		commit.setStage(Commit.WAIT);
//		int id = dao.getLastCommitId() + 1;
//		commit.setCommitId(id);
		commit.setProjectId(projectId);
		Project pj = dao.getProjectById(projectId);
		int repositoryId = pj.getRepositoryId();
		Repository repo = dao.getRepoById(repositoryId);
		commit.setSubmitDate(repo.getCommitDate());
		if (dao.getCommitByProjectAndRevisionId(projectId, revisionID) != null)
			return dao.getCommitByProjectAndRevisionId(projectId, revisionID);
		dao.saveCommit(commit);
		return commit;
	}
	
	protected Commit saveCommit(String revisionID, String log, Date time, Author author, int projectId, String version) {
		Commit commit = new Commit();
		commit.setRevisionId(revisionID);
		commit.setCommitLog(log);
		commit.setCommitDate(time);
		commit.setAuthorId(author.getAuthorId());
		commit.setScanDate(new Date());
		commit.setSelfVersionId(version);
		commit.setProjectId(projectId);
		Project pj = dao.getProjectById(projectId);
		int repositoryId = pj.getRepositoryId();
		Repository repo = dao.getRepoById(repositoryId);
		commit.setSubmitDate(repo.getCommitDate());
		if (dao.getCommitByProjectAndRevisionId(projectId, revisionID) != null)
			return dao.getCommitByProjectAndRevisionId(projectId, revisionID);
		dao.saveCommit(commit);
		return commit;
	}

	protected Author saveAuthor(String authorName) {
		Author author = new Author();
		author.setAuthorName(authorName);
//		author.setAuthorId(dao.getLastAuthorId() + 1);
		if (dao.getAuthorByName(authorName) != null)
			return dao.getAuthorByName(authorName);
		dao.saveAuthor(author);
		return author;
	}

	public Project saveProject(String projectPath, String projectNameCh, String projectNameEn, String developCompany, String projectLanguage) {
		Project project = new Project();
		project.setProjectPath(projectPath);
//		project.setProjectId(dao.getLastProjectId() + 1);
		project.setProjectNameCh(projectNameCh);
		project.setProjectNameEn(projectNameEn);
		project.setDevelopCompany(developCompany);
		project.setProjectLanguage(projectLanguage);
		dao.saveProject(project);
		return project;
	}

	public void saveProject(String projectPath, String projectNameCh, String projectNameEn, String developCompany, String projectLanguage, int repositoryId, String projectTeam) {
		Project project = new Project();
		project.setProjectPath(projectPath);
//		project.setProjectId(dao.getLastProjectId() + 1);
		project.setProjectNameCh(projectNameCh);
		project.setProjectNameEn(projectNameEn);
		project.setDevelopCompany(developCompany);
		project.setProjectLanguage(projectLanguage);
		project.setRepositoryId(repositoryId);
		project.setProjectTeam(projectTeam);
		dao.saveProject(project);
	}

	public Repository saveRepository(String url, String username, String password, String language, Date processDate, int analyzeType) {
		Repository repository = new Repository();
		repository.setUrl(url);
		repository.setUsername(username);
//		repository.setRepositoryId(dao.getLastRepositoryId() + 1);
		repository.setPassword(password);
		repository.setLanguage(language);
		repository.setCommitDate(new Date());
		repository.setProcessDate(processDate);
		repository.setAnalyzeType(analyzeType);
		dao.saveRepository(repository);
		return repository;
	}

	protected Clone saveClone(int cloneClassId, int cloneType, int fileId, int commitId, int beginLine, int endLine,
			int cloneSize, int ecloc, int bcloc, int cmcloc) {
		Clone clone = new Clone();
		clone.setBeginLine(beginLine);
		clone.setEndLine(endLine);
		clone.setCloneClassId(cloneClassId);
//		clone.setCloneId(dao.getLastCloneId() + 1);
		clone.setCloneType(cloneType);
		clone.setCommitId(commitId);
		clone.setFileId(fileId);
		clone.setCloneSize(cloneSize);
//		clone.setEcloc(ecloc);
		clone.setEcloc(ecloc+1);//modify by junyi,1 为修正值，观察发现所得有效克隆代码行比实际值小1
		clone.setBcloc(bcloc);
		clone.setCmcloc(cmcloc);
		dao.saveClone(clone);
		return clone;
	}

	protected CloneClass saveCloneClass(int commitId) {
		CloneClass cloneClass = new CloneClass();
//		cloneClass.setCloneClassId(dao.getLastCloneClassId() + 1);
		cloneClass.setCommitId(commitId);
		dao.saveCloneClass(cloneClass);
		return cloneClass;
	}

	public void checkout(String svnurl, String path, boolean isRecursive, Type type) throws SVNException {

	}

	public void checkout(String svnurl, String path, boolean isRecursive, Date date,  Type type, String projectNameCh, String projectNameEn, String developCompany, String projectTeam) throws SVNException {

	}
	
	public void checkout(String[] empty, String[] tocheckout, String svnurl, String path, boolean isRecursive, Date date,  Type type, String projectNameCh, String projectNameEn, String developCompany, String projectTeam) throws SVNException {

	}
	
	public void checkout(String svnurl, String path, boolean isRecursive, Date date,  Type type, String projectNameCh, String projectNameEn, String developCompany, String projectTeam, String version) throws SVNException {

	}

	public boolean checkURL(String url, String name, String password) throws SVNException {
		return false;

	}

	public void extract(String branch, int num, int projectId) throws Exception {
		// TODO Auto-generated method stub

	}


	/**
	 * 
	 * @param ccConcepts	String stream, String pvob, String component, String view, String view_local_path
	 * @param b
	 * @param pD
	 * @param type
	 * @param proConcepts	String projectNameCh, String projectNameEn, String developCompany, String projectTeam
	 */
	public void checkout(String[] ccConcepts, boolean b, Date pD, Type type, String[] proConcepts) {
		// TODO Auto-generated method stub
		
	}

	public void saveProject(String projectPath, String projectNameCh, String projectNameEn, String developCompany, String projectLanguage, int repositoryId, String projectTeam,
			String stream, String pvob, String component, String view, String viewLocalPath) {
		Project project = new Project();
		project.setProjectPath(projectPath);
//		project.setProjectId(dao.getLastProjectId() + 1);
		project.setProjectNameCh(projectNameCh);
		project.setProjectNameEn(projectNameEn);
		project.setDevelopCompany(developCompany);
		project.setProjectLanguage(projectLanguage);
		project.setRepositoryId(repositoryId);
		project.setProjectTeam(projectTeam);
		project.setStream(stream);
		project.setComponent(component);
		project.setPvob(pvob);
		project.setViewLocalPath(viewLocalPath);
		project.setView(view);
		dao.saveProject(project);
	}
	
	protected Commit saveCommit(String revisionID, Date time, Author author, int projectId, String selfVersionId) {
		Commit commit = new Commit();
		commit.setRevisionId(revisionID);
		commit.setCommitDate(time);
		commit.setAuthorId(author.getAuthorId());
		commit.setScanDate(new Date());
		commit.setSelfVersionId(selfVersionId);
		commit.setProjectId(projectId);
		commit.setStage(Commit.WAIT);
		Project pj = dao.getProjectById(projectId);
		int repositoryId = pj.getRepositoryId();
		Repository repo = dao.getRepoById(repositoryId);
		commit.setSubmitDate(repo.getCommitDate());
		if (dao.getCommitByProjectAndRevisionId(projectId, revisionID) != null)
			return dao.getCommitByProjectAndRevisionId(projectId, revisionID);
		dao.saveCommit(commit);
		return commit;
	}

	public final void readCloneProperty(String cloneFile) {
		FileReader reader;
		try {
			reader = new FileReader(cloneFile);
			BufferedReader br = new BufferedReader(reader);

			String str = null;
			String[] property = new String[4];
			for (int i = 0; i < property.length; i++) {
				property[i] = "";
			}
			int i = 0;
			while ((str = br.readLine()) != null) {
				String[] propertyArr = str.split("=");
				if (propertyArr.length >= 2) {
					property[i] = propertyArr[1];
					// System.out.println(property[i]);
				}
				i++;
			}
			if (!property[1].equals(""))
				this.minToken = Integer.parseInt(property[1]);

			if (!property[0].equals(""))
				this.minLine = Integer.parseInt(property[0]);

			if (!property[2].equals(""))
				this.minClass = Integer.parseInt(property[2]);

			if (!property[3].equals(""))
				this.encoding = property[3];
			br.close();
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public List<ProgramLanguage> getAnalyseLanguages(){
		return dao.getProgramLanguages();
	}
	
	public void updateWrongEncodingFile(HashMap<String, Integer> nameId, String wrongFile) {
		try {
			java.io.File fileInFileSystem = new java.io.File(wrongFile);
			InputStream is = new FileInputStream(fileInFileSystem);
			byte[] byteArray = IOUtils.toByteArray(is);
			int loc = getTotalLines(fileInFileSystem);
			EvolutionAnalyse ea = new EvolutionAnalyse(fileInFileSystem.getAbsolutePath(), repoLocation);
			int res[] = ea.countLine();
			int bloc = res[0];
			int cmloc = res[1];
			int eloc = res[2];
			cn.edu.fudan.se.clonedetector.bean.File sourceFile = dao.getFileById(nameId.get(wrongFile));
			sourceFile.setRenameTag(File.RENAME_RN_TAG);
			sourceFile.setContent(byteArray);
			sourceFile.setLoc(loc);
			sourceFile.setEloc(eloc);
			sourceFile.setBloc(bloc);
			sourceFile.setCmloc(cmloc);
			dao.updateFile(sourceFile);
			System.gc();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public int getTotalLines(java.io.File fileInFileSystem) {
		int countLine = 0;
		FileReader fr;
		try {
			fr = new FileReader(fileInFileSystem);
			BufferedReader br = new BufferedReader(fr);
			try {
				while (br.readLine() != null) {
					countLine++;
				}
				br.close();
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return countLine;
	}
	
	@Deprecated
	protected void retrieveClone(String location, String languageType, String fileList, int commitId, HashMap<String, Integer> nameId) {
		CCFinderCloneDetector detector = new CCFinderCloneDetector();
		detector.setType(CCFinder.toCCLanguageType(languageType));
		detector.setMinFragLen(this.minToken);
		CloneDetectionResult result = null;

		boolean detectClone = false;
		while(!detectClone){
			try {
				result = detector.detectClone(location);
				detectClone = true;
			} catch (Exception e) {
				if (e instanceof FileWrongEncodingException) {
					String wrongFile = "";
					if (wrongFile.equals(e.getMessage())) {
						String pathname = wrongFile+".del";
						new java.io.File(wrongFile).renameTo(new java.io.File(pathname));
					}else {
						wrongFile = e.getMessage();
						String pathname = wrongFile+".rn";
						new java.io.File(wrongFile).renameTo(new java.io.File(pathname));
						try {
							FileProcesser.transformFileEncoding(pathname, wrongFile, "utf-16", "utf-8");
						} catch (IOException e1) {
							e1.printStackTrace();
						}				
					}
					updateWrongEncodingFile(nameId, wrongFile);
				}
			}
		}
		if (result == null)
			return;
		int i =1;
		for (CloneClassImpl cci : result.getCloneClasses()) {
			// storage all cloneclasses
			System.out.format("retieve CloneClass %d --------------- %n", i++);
			CloneClass cc = saveCloneClass(commitId);
			int repeat=0;
			for (CloneImpl ci : cci.getClones()) {
				//todo test
				System.out.print((nameId==null)+"/nameId, ");
				System.out.print((ci==null)+"/ci, ");
				System.out.print(ci.getFile()+", ");
				System.out.println(nameId.get(ci.getFile()));
				int fileId = nameId.get(ci.getFile());
				int beginLine = ci.getStartIndex();
				int endLine = ci.getEndIndex();
				EvolutionAnalyse ea = new EvolutionAnalyse(ci.getFile(), repoLocation);
				int res[] = ea.countIncompleteLine(beginLine, endLine);
				int bcloc = res[0];
				int cmcloc = res[1];
				int ecloc = res[2];
				Clone c = saveClone(cc.getCloneClassId(), ci.getType(), fileId, commitId, beginLine, endLine, ci.getCloneSize(),
						ecloc,bcloc,cmcloc);
				dao.updateCloneCcloc(c,repeat);
				repeat++;
				ea = null;
				System.gc();
				// System.out.println("clone " + clone.getCloneId() + " inserted
				// succeffully");
			}
			updateCloneClass(cci.getClones(), cc);
		}
		backRenameFile(dao.getFilesOfCommitByIdTag(commitId, File.RENAME_RN_TAG));
	}

protected void updateCloneClass(List<CloneImpl> list, CloneClass cc) {
	int size = list.size();
	int[] randNum = pickRandomNum(size);
	int type = CloneImpl.TYPE1;
	CloneImpl ci1 = list.get(randNum[0]), ci2 = list.get(randNum[1]);
	if (ci1.getFragment() != null && ci2.getFragment() != null) {
		if (!ci1.getFragment().equals(ci2.getFragment())) {
			type = CloneImpl.TYPE2;
		}
	}else {
		if (!( (ci1.getEndIndex()-ci1.getStartIndex())==(ci2.getEndIndex()-ci2.getStartIndex() ))) {
			type = CloneImpl.TYPE2;			
		}
	}
	cc.setType(type);
	dao.updateCloneClass(cc);
	dao.updateCloneWithCloneClassId(cc.getCloneClassId(), type);
}

private int[] pickRandomNum(int size) {
	int number1 = (int)(Math.random()*size);
	int number2 = 0;
	int times = 3;
	for (int i = 0; i < times; i++) {
		if ( (number2 = (int)(Math.random()*size)) == number1) {
			return new int[]{number1, number2};
		}
	}
	if (number1 == 0) {
		number2 = size-1;
	}else {
		number2 = number1-1;
	}
	return new int[]{number1, number2};
}


protected void backRenameFile(List<File> renameFiles) {
	if (renameFiles == null) {
		return;
	}
	for (File file : renameFiles) {
		java.io.File origin = new java.io.File(file.getFileName()+".rn");
		if (origin.exists()) {
			java.io.File rename = new java.io.File(file.getFileName());
			rename.delete();
			origin.renameTo(rename);
			origin.delete();
		}
	}
}

protected void backRenameFile(int projectId) {
	int commitId = getNeedBackRFCommit(projectId);
	if (commitId>0) {
		backRenameFile(dao.getFilesOfCommitByIdTag(commitId, File.RENAME_RN_TAG));
	}
}

private int getNeedBackRFCommit(Integer projectId) {
	return dao.getAboveStageCommit(projectId);
}

//--------------------------------------remove from CCExtractor
protected void analyzeFileAndStore(java.io.File fileInFileSystem, Commit curr, HashMap<String, Integer> nameId, String validFileName) {

	try {
		InputStream is = new java.io.FileInputStream(fileInFileSystem);
		byte[] byteArray = IOUtils.toByteArray(is);
		int loc = getTotalLines(fileInFileSystem);
		EvolutionAnalyse ea = new EvolutionAnalyse(fileInFileSystem.getAbsolutePath(), repoLocation);
		int res[] = ea.countLine();
		int bloc = res[0];
		int cmloc = res[1];
		int eloc = res[2];
		cn.edu.fudan.se.clonedetector.bean.File sourceFile = saveFile(curr, fileInFileSystem.getAbsolutePath(), validFileName,
				byteArray, loc, eloc,bloc,cmloc);
		nameId.put(fileInFileSystem.getAbsolutePath(), sourceFile.getFileId());
		sourceFile = null;
		System.gc();
		is.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
		
	}

public void updateCommitStage(Commit curr, int stage) {
	curr.setStage(stage);
	dao.updateCommit(curr);
}

/**
 * 只考虑属于selectedDirs的文件夹版本
 * 
 * @param currId 
 * @param selectedDirs 相对于repoLocation的路径
 * @return
 * @throws SVNException
 */
private Collection<String> getAnaylyzeEntries(String[] selectedDirs){
	if (type == Type.RELEASE_VERSION_TYPE && selectedDirs.length == 0) { //没有发布版本文件，忽略
		return null;
	}else if (selectedDirs.length == 0) { 
		return getCoverFileList(repoLocation);
	}else if (selectedDirs.length > 0) {
		Collection<String> entries = getCoverFileList(repoLocation+"\\"+selectedDirs[0]);
		for (int i =1;i<selectedDirs.length;i++) {
			entries.addAll(getCoverFileList(repoLocation+"\\"+selectedDirs[i]));
		}
		return entries;
	}
	return null;
}

/**
 * 包含的所有文件
 * @param repoLocation
 * @return
 */
private Collection<String> getCoverFileList(String repoLocation){
	Collection<String> entries = new ArrayList<>();
	FileProcesser.travelFSFiles(new java.io.File(repoLocation), entries);
	return entries;
}


protected void retrieveFilesFromFileSystem(Commit curr) {
	System.out.format("commit revision is : %s  ... %n" , curr.getRevisionId());
	System.out.println("retrieving files...");
	cleanFileList();
	HashMap<String, Integer> nameId = new HashMap<String, Integer>();
	Collection<String> entries = getAnaylyzeEntries(selectedDirs);
	Collection<String> involvedSuffixes = getSuffixes();
	int relativeFSPathStrIdx = (selectedDirs != null && selectedDirs.length>0) ?  
			new java.io.File(repoLocation+selectedDirs[0]).getPath().length() : repoLocation.length();// 截取有效文件名（用于忽略版本文件夹前缀）
	if (entries != null){
		entries.stream().filter((entry) -> {
			if (isIgnored(entry)){
				new java.io.File(entry).delete();
				return false;
			}
			return isValidFile(entry, involvedSuffixes);
		}).forEach((entry) -> {
							
			System.out.println("file : "+ entry);
			
			String validFileName = entry;
			if (type == Type.RELEASE_VERSION_TYPE) 
				validFileName = entry.substring(relativeFSPathStrIdx);
			
			java.io.File fileInFileSystem = new java.io.File(entry);
			analyzeFileAndStore(fileInFileSystem, curr, nameId, validFileName);
			fileInFileSystem = null;
			System.gc();
		});
	}
	updateCommitStage(curr, Commit.FILE_A_FINISH);
	System.out.println("---------------------------------------------\n");
	List<CommitLanguage> cls = new ArrayList<>(2);
	if(nameId != null && nameId.size() > 0){
		String cloneDetectSrc = repoLocation;
		if (selectedDirs.length > 0) {
			 // todo 因为目前的Ccfinder类只支持输入一个文件夹
			cloneDetectSrc = repoLocation+selectedDirs[0];
		}
		for (String extension : extensions) {
			System.out.format("%ndetect clone for %s%n", extension);
			retrieveClone(cloneDetectSrc, extension, getFileList(extension), curr.getCommitId(), nameId);
			CommitLanguage cl = new CommitLanguage();
			cl.setCommitId(curr.getCommitId());
			cl.setLanguage(extension);
			System.out.format("save CommitLanguage %s %n", extension);
			if(dao.saveCommitLanguage(cl, toInvolvedSuffixes(extension)) != null)
					cls.add(cl);
		}
		FileProcesser.delFSFiles(new java.io.File(cloneDetectSrc+"/.ccfxprepdir"));
		if (needCleanCcfxprep) {
			FileProcesser.delFSFiles(new java.io.File(cloneDetectSrc), ".ccfxprep");
		}
	}else{
		System.err.println("no involved files!");
	}
	updateCommitStage(curr, Commit.CCFINDER_FINISH);
	System.out.println("---------------------------------------------\n");
	for (Integer fi : nameId.values()) {
		System.out.format("update fileId %d cloc%n", fi);
		dao.updateFileCLoc(fi);
	}
	updateCommitStage(curr, Commit.UPDATE_FILE_CLOC_FINISH);
	for (CommitLanguage cl : cls) {
		System.out.format("update CommitLanguage %s cloc%n", cl.getLanguage());
		dao.updateCommitLanguageCloc(cl,toInvolvedSuffixes(cl.getLanguage()));
	}
	System.out.println("Finish retrieving files ... ");
	System.out.println("---------------------------------------------\n");
}

/**
 * 清理ccfinder中不支持的语言的fileList
 */
private void cleanFileList() {
	for (String language : extensions) {
		new java.io.File(getFileList(language)).delete();
	}
	
}

//--------------------------------------remove from CCExtractor

public void extract(String branch, int projectId, String version) throws Exception{} ;
}
