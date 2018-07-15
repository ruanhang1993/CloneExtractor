package cn.edu.fudan.se.clonedetector.versioncontroll.svn;

import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc2.SvnCleanup;
import org.tmatesoft.svn.core.wc2.SvnGetInfo;
import org.tmatesoft.svn.core.wc2.SvnInfo;
import org.tmatesoft.svn.core.wc2.SvnList;
import org.tmatesoft.svn.core.wc2.SvnLog;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnRevisionRange;
import org.tmatesoft.svn.core.wc2.SvnTarget;
import org.tmatesoft.svn.core.wc2.SvnUpdate;

import cn.edu.fudan.se.clonedetector.bean.Author;
import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.bean.CommitLanguage;
import cn.edu.fudan.se.clonedetector.bean.MyFileTreeNode;
import cn.edu.fudan.se.clonedetector.bean.Project;
import cn.edu.fudan.se.clonedetector.bean.Repository;
import cn.edu.fudan.se.clonedetector.dao.IDataAccessor;
import cn.edu.fudan.se.clonedetector.differ.lineDiffImp.Diff2Version;
import cn.edu.fudan.se.clonedetector.display.action.AutoCompareAction;
import cn.edu.fudan.se.clonedetector.util.FileProcesser;
import cn.edu.fudan.se.clonedetector.util.Logger;
import cn.edu.fudan.se.clonedetector.versioncontroll.DataExtractor2;
import cn.edu.fudan.se.clonedetector.versioncontroll.Type;

public class SVNExtractor extends DataExtractor2 {

	private SvnOperationFactory svnOperationFactory = null;
	private String relativeBasePath;
	private String branch;
	ISVNAuthenticationManager authManager = null;
	// added by rh
	private String name = "lemon2008";
	private String password = "wsklxsx";
	
	private SVNURL svnurl; // checkout svnurl

	public SVNExtractor(IDataAccessor dao, String repoLocation) {
		super(dao, repoLocation);
		// Set up connection protocols support:
		// http:// and https://
		DAVRepositoryFactory.setup();
		// svn://, svn+xxx:// (svn+ssh:// in particular)
		SVNRepositoryFactoryImpl.setup();
		// file:///
		FSRepositoryFactory.setup();

		svnOperationFactory = new SvnOperationFactory();
		authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
		svnOperationFactory.setAuthenticationManager(authManager);
		//getClass().getResource("/") -> file:\C:\...\wtpwebapps\Zhonghui\WEB-INF\classes\
		//changed by junyi
		String path = getClass().getResource("/").toString().substring(5);
		readCloneProperty(path+"clone.properties");
	}

	public boolean checkURL(String url, String name, String password) throws SVNException {
		boolean test = false;
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
		FSRepositoryFactory.setup();
		svnOperationFactory = new SvnOperationFactory();
		this.setName(name);
		this.setPassword(password);
		authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
		svnOperationFactory.setAuthenticationManager(authManager);
		SVNURL svnurl = SVNURL.parseURIDecoded(url);
		SVNRepository repository = SVNRepositoryFactory.create(svnurl);
		repository.setAuthenticationManager(authManager);
		try{
			test = true;
			repository.testConnection();
		}catch(SVNException e){
			test = false;
		}
		repository.closeSession();
		return test;
	}

	@Override
	@Deprecated
	public void extract(String branch, int num, int projectId) throws Exception {
		this.branch = branch;
		System.out.println("extracting 1/" + num + "...");
		Commit curr = getFirstRevision(projectId);
		dao.updateCommitLoc(curr);
		Commit first = curr;
		Commit post = null;
		try {
			for (int i = 1; i < num; i++) {
				System.out.println("\nextracting " + (i + 1) + "/" + num + "...");
				post = curr;
				try {
					curr = retrieveCommit(post.getRevisionId(), projectId);
					if (curr.getPreRevisionId() != null && curr.getPreRevisionId().equals("finished")) {
						System.out.println("no more revision to retrive.");
					} else {
						System.out.println("deleted unfinished file");
						dao.deleteFilesByCommitId(curr.getCommitId());
						curr = retrieveCommit(post.getRevisionId(), projectId);
					}
					post.setPreRevisionId(curr.getRevisionId());
					dao.updateCommit(post);
				} catch (NoRevisionMoreException ex) {
					System.out.println("no more revision to retrive.");
					return;
				} catch (SVNException | ConnectionRefusedException ex) {
					Commit c = dao.getEarliestCommit();
					Logger.log("failed on " + c.getRevisionId());
					if (!c.getRevisionId().equals(post.getRevisionId())) {
						dao.deleteAllAbout(c);
					}
					curr = post;
					i--;
				}
			}
		} finally {
			updateTo(first, repoLocation);
		}
		System.out.format("extratc %s finished!%n", branch);
	}

	private long parseID(String revisionID) {
		return Long.parseLong(revisionID.substring(1));
	}

	private void retrieveFiles(Commit curr) throws SVNException {
		backRenameFile(curr.getProjectId());
		if (type != Type.RELEASE_VERSION_TYPE) { // 发布版本模式下，每个commit是与文件夹对应，所以不能update
			updateTo(curr, repoLocation);
		}
		updateCommitStage(curr, Commit.CHECKOUT_FINISH);
		retrieveFilesFromFileSystem(curr);
//		retrieveFilesFromFileSystem_SVN(curr);
	}

	@Deprecated
	private void retrieveFilesFromFileSystem_SVN(Commit curr) throws SVNException {
		System.out.println("commit id is : " + curr.getCommitId() + " ... ");
		System.out.println("retrieving files...");
		HashMap<String, Integer> nameId = new HashMap<String, Integer>();
		long currId = parseID(curr.getRevisionId());
		int relativeFSPathStrIdx = svnurl.toString().length();// repoLocation与svnurl在文件结构中在同一位置，
															  // 所以entry的url切掉这部分恰好是相对于repoLocation的地址
		Collection<SVNDirEntry> entries = getAnaylyzeEntries_SVN(currId, selectedDirs);
		Collection<String> involvedSuffixes = getSuffixes();
		if (entries != null){
			entries.stream().filter((entry) -> entry.getKind().equals(SVNNodeKind.FILE)).filter((entry) -> {
				if (isIgnored(entry.getRelativePath())){
					new File(repoLocation + entry.getURL().toString().substring(relativeFSPathStrIdx)).delete();
					return false;
				}
				return isValidFile(entry.getRelativePath(), involvedSuffixes);
			}).forEach((entry) -> {
				
				File fileInFileSystem = new File(repoLocation + entry.getURL().toString().substring(relativeFSPathStrIdx));
				
				System.out.println("repoLocation + relative " + repoLocation +" + "+ entry.getRelativePath());
				
				String validFileName = (type == Type.RELEASE_VERSION_TYPE) ? 
						entry.getRelativePath()	: // getRelativePath()相对于target path的地址，注意这里entry的target不一定是repoLocation
						fileInFileSystem.getAbsolutePath();
						
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
			FileProcesser.delFSFiles(new File(cloneDetectSrc+"/.ccfxprepdir"));
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



//private void analyzeFile(File fileInFileSystem, Commit curr, HashMap<String, Integer> nameId, String validFileName) {
//
//	try {
//		InputStream is = new FileInputStream(fileInFileSystem);
//		byte[] byteArray = IOUtils.toByteArray(is);
//		int loc = getTotalLines(fileInFileSystem);
//		EvolutionAnalyse ea = new EvolutionAnalyse(fileInFileSystem.getAbsolutePath());
//		int res[] = ea.countLine();
//		int bloc = res[0];
//		int cmloc = res[1];
//		int eloc = res[2];
//		cn.edu.fudan.se.clonedetector.bean.File sourceFile = saveFile(curr, fileInFileSystem.getAbsolutePath(), validFileName,
//				byteArray, loc, eloc,bloc,cmloc);
//		nameId.put(fileInFileSystem.getAbsolutePath(), sourceFile.getFileId());
//		sourceFile = null;
//		System.gc();
//		is.close();
//	} catch (Exception e) {
//		e.printStackTrace();
//	}
//		
//	}

//	private String generateFullPath(String relativePath) {
//		String path = this.relativeBasePath + "/" + relativePath;
//		return cutBranch(path);
//	}

//	private String cutBranch(String path) {
//		return path.substring(path.indexOf(branch) + branch.length() + 1);
//	}

	/**
	 * 只考虑属于selectedDirs的文件夹版本
	 * 
	 * @param currId 
	 * @param selectedDirs 相对于repoLocation的路径
	 * @return
	 * @throws SVNException
	 */
	@Deprecated
	private Collection<SVNDirEntry> getAnaylyzeEntries_SVN(long currId, String[] selectedDirs) throws SVNException {
		if (type == Type.RELEASE_VERSION_TYPE && selectedDirs.length == 0) { //没有发布版本文件，忽略
			return null;
		}else if (selectedDirs.length == 0) { 
			return getSvnList(repoLocation, currId);
		}else if (selectedDirs.length > 0) {
			Collection<SVNDirEntry> entries = getSvnList(repoLocation+"\\"+selectedDirs[0], currId);
			for (int i =1;i<selectedDirs.length;i++) {
				entries.addAll(getSvnList(repoLocation+"\\"+selectedDirs[i], currId));
			}
			return entries;
		}
		return null;
	}
	
	@Deprecated
	private Collection<SVNDirEntry> getSvnList(String repoLocation, long currId) throws SVNException  {
		SvnList list = svnOperationFactory.createList();
		list.setSingleTarget(SvnTarget.fromFile(new File(repoLocation), SVNRevision.HEAD));
		list.setRevision(SVNRevision.create(currId));
		list.setDepth(SVNDepth.INFINITY);
		Collection<SVNDirEntry> entries = list.run(null);
		return entries;
}

	private void updateTo(Commit curr, String path) throws SVNException {
		boolean flag = true;
		while (flag) {
			try {
				System.out.println("updating...");
				long currId = parseID(curr.getRevisionId());
				SvnUpdate update = svnOperationFactory.createUpdate();
				update.setSingleTarget(SvnTarget.fromFile(new File(path)));
				update.setRevision(SVNRevision.create(currId));
				update.run();
				System.out.println("over update");
				flag = false;
			} catch (SVNException ex) {
				cleanup();
				flag = true;
			}
		}

	}

	private void cleanup() throws SVNException {
		SvnCleanup cleanup = svnOperationFactory.createCleanup();
		cleanup.setDepth(SVNDepth.INFINITY);
		cleanup.setSingleTarget(SvnTarget.fromFile(new File(repoLocation)));
		cleanup.run();
	}

	private Commit getFirstRevision(int projectId)
			throws SVNException, NoRevisionMoreException, ConnectionRefusedException {
		long lastestId = getLatestId();
		System.out.println("lastedId is : " + lastestId);
		lastestId++;
		return retrieveCommit("r" + lastestId, projectId);
	}

	private long getLatestId() throws SVNException {
		SvnGetInfo getInfo = svnOperationFactory.createGetInfo();
		getInfo.setSingleTarget(SvnTarget.fromFile(new File(repoLocation)));
		SvnInfo info = getInfo.run();
		String path = info.getUrl().getPath();
		String repoPath = info.getRepositoryRootUrl().getPath();
		relativeBasePath = path.substring(path.indexOf(repoPath) + repoPath.length());
		return info.getRevision();
	}

	@Deprecated
	private Commit retrieveCommit(String postId, int projectId)
			throws SVNException, NoRevisionMoreException, ConnectionRefusedException {
		try {
			System.out.println("retrieving commit...");
			long id = parseID(postId);
			SVNLogEntry entry = null;
			while (entry == null) {
				id--;
				if (id <= 0) {
					throw new NoRevisionMoreException();
				}
				SvnLog log = svnOperationFactory.createLog();
				log.setSingleTarget(SvnTarget.fromFile(new File(repoLocation)));
				log.setUseMergeHistory(true);
				log.addRange(SvnRevisionRange.create(SVNRevision.create(id), SVNRevision.create(id)));
				log.setDiscoverChangedPaths(true);
				entry = log.run();
			}

			// todo select special directory
			if (type == Type.RELEASE_VERSION_TYPE) {
				selectAnalyzedDir(entry.getRevision());
			}
			
			String revisionID = "r" + entry.getRevision();
			String log = entry.getMessage();
			Date time = entry.getDate();
			Author author = retrieveAuthor(entry);
			Commit commit = saveCommit(revisionID, log, time, author, projectId);
			if (commit.getPreRevisionId() == null || commit.getPreRevisionId().equals("start")){
				dao.deleteFilesByCommitId(commit.getCommitId());
				retrieveFiles(commit);
				dao.updateCommitLoc(commit);
			}
			
			return commit;
		} catch (SVNException ex) {
			if (ex.getMessage().contains("connection refused by the server")) {
				throw new ConnectionRefusedException();
			} else if (ex.getMessage().contains("svn: E195012: Unable to find repository location for")) {
				throw new NoRevisionMoreException();
			} else {
				throw ex;
			}
		}
	}

	/**
	 * 分析该次提交中是否增加了一个版本发布文件夹，一个版本文件夹是svnurl的子文件夹，并且以v+数字开头
	 * 		如果存在添加该文件夹到selectedDirs
	 * 		如果没有这样的文件夹，selectedDirs为空
	 * @param revision
	 * @throws SVNException 
	 */
	private void selectAnalyzedDir(long revision) throws SVNException {
		selectedDirs = new String[]{}; 
		SVNRepository repository = SVNRepositoryFactory.create(svnurl);
		repository.setAuthenticationManager(authManager);
		Collection logEntries = repository.log( new String[] { "" } , null , revision , revision , true , true );
		
        SVNLogEntry logEntry = ( SVNLogEntry ) logEntries.iterator( ).next( );
        System.out.println( "select analyzed dir:-----------------------" );
        if ( logEntry.getChangedPaths( ).size( ) > 0 ) {
           System.out.println( "changed paths:" );
           String relative = svnurl.toString().substring(repository.getRepositoryRoot().toString().length());// 根目录之后直到到tag标签这段路径
           Iterator changedPaths = logEntry.getChangedPaths().keySet( ).iterator( );

           while (changedPaths.hasNext( )) {
               SVNLogEntryPath entryPath = ( SVNLogEntryPath ) logEntry.getChangedPaths( ).get( changedPaths.next( ) );
               if (entryPath.getKind().equals(SVNNodeKind.DIR) && entryPath.getType() == 'A' ) {
            	   display(entryPath);
            	   if(entryPath.getPath().length() <= relative.length())
            		   continue;
            	   String[] dirNames = entryPath.getPath().substring(relative.length()).split("/");// 取tag标签之后的路径，以/开头
            	   if (dirNames.length == 2 && isValidVersionDir(dirNames[1])) { // index = 1是因为第一个是""
            		   selectedDirs = new String[] {dirNames[1]};
            		   break;
            	   }
               }
           }
       }
        if (selectedDirs.length > 0) {
            System.out.format( "select analyzed dir[0]: %s%n", selectedDirs[0] );			
		}else{
			System.err.println("select analyzed dir is null");
		}
		
	}


	/**
	 * 判断是否一个版本文件夹——文件夹名字以v、V开头，紧接一个数字
	 * @param dirName
	 * @return
	 */
	private boolean isValidVersionDir(String dirName) {
		String header = "(v|V)";
        Pattern p = Pattern.compile(header+"\\d+(\\.\\d+)*");
        Matcher m = p.matcher(dirName); // get a matcher object
        if(m.find()){
            String group = m.group();
            if (group.length() == dirName.length())
                return true;
        }
        return false;
	}

	private void display(SVNLogEntryPath entryPath) {
        System.out.println( " "
                + entryPath.getType( )
                + " "
                + entryPath.getPath( )
                + ( ( entryPath.getCopyPath( ) != null ) ? " (from "
                        + entryPath.getCopyPath( ) + " revision "
                        + entryPath.getCopyRevision( ) + ")" : "" ) );
        
	}

	private Author retrieveAuthor(SVNLogEntry entry) {
		Author author = saveAuthor(entry.getAuthor());
		return author;
	}
	
	/**
	 *@param path 路径中不包含//，即使用/example/tag，不能使用/example//tag
	 */
	@Override
	public void checkout(String url, String path, boolean isRecursive, Date processDate, Type type,String projectNameCh, String projectNameEn, String developCompany,String projectTeam) throws SVNException {
		
		this.type = type;
		svnurl = SVNURL.parseURIDecoded(url);
		SVNRepository repository = SVNRepositoryFactory.create(svnurl);
		repository.setAuthenticationManager(authManager);
		if(projectNameEn == null || projectNameEn.length()==0){
			String[] projectNames = url.split("/");
			projectNameEn = projectNames[projectNames.length - 2];
		}
		path += projectNameEn + "\\";
		this.repoLocation = path;
		File destPath = new File(path);

		SVNClientManager ourClientManager = SVNClientManager.newInstance();
		ourClientManager.setAuthenticationManager(authManager);
		SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
		updateClient.setIgnoreExternals(true);
		long latestRevision = repository.getLatestRevision();
		Repository existedRepo = this.dao.getRepositoryByProjectNameEn(projectNameEn);
		System.out.println("Repo: "+ (existedRepo==null));
		if (existedRepo != null) {
			cleanup();
		}

		if (existedRepo == null) {
			int analyzeType = 0;
			if (this.type == Type.RELEASE_VERSION_TYPE) {
				analyzeType = 1;
			}
			String language = "";
			if(extensions.size() == 1){
				language = extensions.get(0);
			}else{
				language = "";
			}
			existedRepo = super.saveRepository(url, name, password, language, processDate, analyzeType);
			super.saveProject(this.repoLocation, projectNameCh,projectNameEn,developCompany, language, existedRepo.getRepositoryId(), projectTeam);
		} else {
			existedRepo.setProcessDate(new Date());
			this.dao.updateRepository(existedRepo);
		}

		while(true){
			try{
				if (updateClient.doCheckout(svnurl, destPath, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY,
						isRecursive) == latestRevision) {
					ourClientManager.dispose();
					break;
					}
			}catch (SVNException e) {
				if (e.toString().contains("is already a working copy for a different URL; perform update")) {
					FileProcesser.delFSFiles(destPath);
					System.out.println("retry checkout");
				}else {
					System.out.println("handle exception and retry checkout");
					throw e;
				}
			}
		}
		
		extractProjects(existedRepo.getRepositoryId());
		existedRepo = null;
		System.gc();
		return;
	}
	
		/**
		 *@param path 路径中不包含//，即使用/example/tag，不能使用/example//tag
		 */
		@Override
	public void checkout(String[] empty, String[] tocheckout, String url, String path, boolean isRecursive, Date processDate, Type type,String projectNameCh, String projectNameEn, String developCompany,String projectTeam) throws SVNException {
			this.type = type;
			svnurl = SVNURL.parseURIDecoded(url);
			SVNRepository repository = SVNRepositoryFactory.create(svnurl);
			repository.setAuthenticationManager(authManager);
			if(projectNameEn == null || projectNameEn.length()==0){
				String[] projectNames = url.split("/");
				projectNameEn = projectNames[projectNames.length - 2];
			}
			path += projectNameEn + "\\";
			this.repoLocation = path;
			File destPath = new File(path);
			
			SVNClientManager ourClientManager = SVNClientManager.newInstance();
			ourClientManager.setAuthenticationManager(authManager);
			SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
			updateClient.setIgnoreExternals(true);
			long latestRevision = repository.getLatestRevision();
			Repository existedRepo = this.dao.getRepositoryByProjectNameEn(projectNameEn);
			System.out.println("Repo: "+ (existedRepo==null));
			if (existedRepo != null) {
				cleanup();
			}
			
			if (existedRepo == null) {
				int analyzeType = 0;
				if (this.type == Type.RELEASE_VERSION_TYPE) {
					analyzeType = 1;
				}
				String language = "";
				if(extensions.size() == 1){
					language = extensions.get(0);
				}else{
					language = "";
				}

				existedRepo = super.saveRepository(url, name, password, language, processDate, analyzeType);
				super.saveProject(this.repoLocation, projectNameCh,projectNameEn,developCompany, language, existedRepo.getRepositoryId(), projectTeam);
			} else {
				existedRepo.setProcessDate(new Date());
				this.dao.updateRepository(existedRepo);
			}

			while(true){
				try{
					long temp = updateClient.doCheckout(svnurl, destPath, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.EMPTY,
							isRecursive);
					for(String s : empty){
						System.out.println("empty: "+url+s+"||path: "+path+s);
						svnurl = SVNURL.parseURIDecoded(url+s);
						String tempPath = path+s;
						File tempDestPath = new File(tempPath);
						temp = updateClient.doCheckout(svnurl, tempDestPath, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.EMPTY,
								isRecursive);
					}
					for(String s : tocheckout){
						System.out.println("tocheckout: "+url+s+"||path: "+path+s);
						svnurl = SVNURL.parseURIDecoded(url+s);
						String tempPath = path+s;
						File tempDestPath = new File(tempPath);
						temp = updateClient.doCheckout(svnurl, tempDestPath, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY,
								isRecursive);
					}
					/**此处的for已经可以拉下来文件结构，但是目录并未处于管理下*/
//					for(String s : tocheckout){
//						System.out.println("tocheckout: "+url+s+"||path: "+path+s);
//						svnurl = SVNURL.parseURIDecoded(url+s);
//						String tempPath = path+s;
//						File tempDestPath = new File(tempPath);
//						temp = updateClient.doCheckout(svnurl, tempDestPath, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY,
//								isRecursive);
//					}
					if (temp == latestRevision) {
						ourClientManager.dispose();
						System.out.println("checkout Done!");
						break;
					}
				}catch (SVNException e) {
					if (e.toString().contains("is already a working copy for a different URL; perform update")) {
						e.printStackTrace();
						FileProcesser.delFSFiles(destPath);
						System.out.println("retry checkout");
					}else {
						System.out.println("handle exception and retry checkout");
						throw e;
					}
				}
			}
			
			extractProjects(existedRepo.getRepositoryId());
			existedRepo = null;
			System.gc();
			return;
		}
		
	public void checkout(String url, String path, boolean isRecursive, Date processDate, Type type,String projectNameCh, String projectNameEn, String developCompany,String projectTeam, String version) throws SVNException {
			this.type = type;
			svnurl = SVNURL.parseURIDecoded(url);
			SVNRepository repository = SVNRepositoryFactory.create(svnurl);
			repository.setAuthenticationManager(authManager);
			if(projectNameEn == null || projectNameEn.length()==0){
				String[] projectNames = url.split("/");
				projectNameEn = projectNames[projectNames.length - 2];
			}
			path += projectNameEn + "\\";
			this.repoLocation = path;
			File destPath = new File(path);

			SVNClientManager ourClientManager = SVNClientManager.newInstance();
			ourClientManager.setAuthenticationManager(authManager);
			SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
			updateClient.setIgnoreExternals(true);
			long latestRevision = repository.getLatestRevision();
			Repository existedRepo = this.dao.getRepositoryByProjectNameEn(projectNameEn);
			if (existedRepo != null) {
				cleanup();
			}
			if (existedRepo == null) {
				int analyzeType = 0;
				if (this.type == Type.RELEASE_VERSION_TYPE) {
					analyzeType = 1;
				}
				String language = "";
				if(extensions.size() == 1){
					language = extensions.get(0);
				}else{
					language = "";
				}

				existedRepo = super.saveRepository(url, name, password, language, processDate, analyzeType);
				super.saveProject(this.repoLocation, projectNameCh,projectNameEn,developCompany, language, existedRepo.getRepositoryId(), projectTeam);
			} else {
				existedRepo.setProcessDate(new Date());
				this.dao.updateRepository(existedRepo);
			}

			while(true){
				try{
					if (updateClient.doCheckout(svnurl, destPath, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY,
							isRecursive) == latestRevision) {
						ourClientManager.dispose();
						break;
						}
				}catch (SVNException e) {
					if (e.toString().contains("is already a working copy for a different URL; perform update")) {
						FileProcesser.delFSFiles(destPath);
						System.out.println("retry checkout");
					}else {
						System.out.println("handle exception and retry checkout");
						throw e;
					}
				}
			}

			extractProjects(existedRepo.getRepositoryId(), version);
			existedRepo = null;
			System.gc();
			return;
		}

	private void extractProjects(int repositoryId) {
		extractProjects(repositoryId, null);
	}
	private void extractProjects(int repositoryId, String version) {
		List<Project> projects = this.dao.getProjectByRepositoryId(repositoryId);
		for (Project pro : projects) {
			try {
//				this.extract(pro.getProjectName(), 10000, pro.getProjectId());
				this.extract(pro.getProjectNameCh(), pro.getProjectId(), version);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		projects = null;
		System.gc();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void extract(String branch, int projectId, String version) throws Exception {
		this.branch = branch;
		
        System.out.println( "resolve commits history from SVN :-------------------" );
        List<Commit> commits;
		if (type == Type.RELEASE_VERSION_TYPE) {
            commits = getCommitsByFS(projectId);
		}else if (version == null || version.length() == 0) {
            commits = getCommitsBySVN(projectId);
		}else {
			commits = getLastCommitBySVN(projectId, version);
		}
        System.out.println( "resolve commits history from SVN finished!-----------" );
        //与extract(String, int, int)不同，这里是降序遍历，因为获取commit的方式不同，所以顺序不同
        int num = commits.size();
		Commit curr;
		try {
			int topIndex = num-1;
//			int buttomIndex = (topIndex-4+1)>=0 ? (topIndex-4+1) : 0;  
			int buttomIndex = 0;
			for (int i = topIndex; i >= buttomIndex; i--) {
				System.out.println("\nextracting " + (num-i) + "/" + num + "...");
				try {
					curr = commits.get(i);
					if (curr.getStage() != Commit.ALL_FINISH){
						if (curr.getStage() == Commit.WAIT || curr.getStage() == Commit.FAILED) {
							updateCommitStage(curr, Commit.START);							
						}
						// todo select special directory
						if (type == Type.RELEASE_VERSION_TYPE) {
							String tagVersion =curr.getSelfVersionId();
							selectedDirs = new String[]{ tagVersion};
						}
						
						dao.deleteFilesByCommitId(curr.getCommitId());
						retrieveFiles(curr);
						dao.updateCommitLoc2(curr);
					}
					
					if (curr.getStage() == Commit.ALL_FINISH) {
						System.out.format("revision %s retriving finished.%n", curr.getRevisionId());
						//auto compare
						if(i<topIndex&&i>=buttomIndex){
							int preCommitId,commitId;
							preCommitId = curr.getCommitId();
							commitId = commits.get(i+1).getCommitId();
							Date pD = new Date();
							Runnable runnable = new Runnable() {
								public void run() {
									Diff2Version diff = new Diff2Version();
									diff.changeOf2Version(preCommitId, commitId, dao);
								}
							};
							long timeSpan = pD.getTime() - new Date().getTime();
							ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
							service.schedule(runnable, timeSpan, TimeUnit.MILLISECONDS);
						}
					} else {
						System.out.println("deleted unfinished file");
						dao.deleteFilesByCommitId(curr.getCommitId());
						throw new CommitRetieveUnFinishedException();
					}
				} catch (CommitRetieveUnFinishedException e) {
					i++;
				}catch (Exception e) {
					e.printStackTrace(new PrintStream(new File(repoLocation+"\\log.txt")));
					updateCommitStage(commits.get(i), Commit.FAILED);
					return;
				}
			}
			System.out.format("%n%nextratc %s finished!%n%n", branch);
		} finally {
//			updateTo(first, repoLocation);
		}
	}


	private List<Commit> getCommitsByFS(int projectId) {
		List<Commit> commits = new ArrayList<Commit>();
		selectedDirs = new String[]{}; 
		File[] logEntries = new File(repoLocation).listFiles();
        System.out.println( "select analyzed dir:-----------------------" );
        for(File entryPath : logEntries){
            String[] dirNames = entryPath.getPath().substring(repoLocation.length()).split("/");// 取tag标签之后的路径，以/开头
            String tagNumber = dirNames[0];
            if (entryPath.isDirectory() && isValidVersionDir(tagNumber)) {
            	String revisionID = "r"+tagNumber.substring(1);// 删除v|V
            	selectedDirs = new String[] {tagNumber};
            	Commit commit = new Commit();
            	commit.setRevisionId(revisionID);
            	commit.setProjectId(projectId);
            	commit.setSelfVersionId(tagNumber);
        		commit.setScanDate(new Date());
        		commit.setStage(Commit.WAIT);
            	Commit tmp = dao.getCommitByProjectAndRevisionId(projectId, revisionID);
        		if (tmp == null){
            		dao.saveCommit(commit);					
				}else {
					commit = tmp;
				}
            	System.out.format("form commits : %s %n", revisionID);
            	commits.add(commit);
            }
        }
		commits.sort(new Comparator<Commit>(){
			public int compare(Commit c1, Commit c2){
				String[] revisionId1 = c1.getRevisionId().substring(1).split("\\."); //与isValidVersionDir方法密切相关
				String[] revisionId2 = c2.getRevisionId().substring(1).split("\\.");
				for (int i = 0; i < revisionId1.length && i < revisionId2.length; i++) {
					int n1 = Integer.parseInt(revisionId1[i]);
					int n2 = Integer.parseInt(revisionId2[i]);
					if (n1 != n2) {
						return n1-n2;
					}					
				}
				if (revisionId1.length > revisionId2.length) {
					return -1;
				}else {
					return 1;
				}
			}
		});
		Commit curr;
		Commit pre = commits.get(0);
		pre.setPreRevisionId("-1");
		for (int i = 1; i < commits.size(); i++) {
			curr = commits.get(i);
			if (!pre.getRevisionId().equals(curr.getPreRevisionId())) {
				curr.setPreRevisionId(pre.getRevisionId());
				dao.updateCommit(curr);
			}
			pre = curr;
	     	System.out.format("add commits : %s , version : %s ,preRevision : %s%n", curr.getCommitId(), curr.getSelfVersionId(), curr.getPreRevisionId());
		}
        return commits;
	}

	private List<Commit> getCommitsBySVN(int projectId) throws SVNException{
		List<Commit> commits = new ArrayList<Commit>();
		SVNRepository repository = SVNRepositoryFactory.create(svnurl);
		repository.setAuthenticationManager(authManager);
		Collection logEntries = repository.log( new String[] { "" } , null , 0 , -1 , true , true );
        Iterator it = logEntries.iterator( );
        Commit pre = new Commit();
        pre.setPreRevisionId("-1");
        while (it.hasNext( )) {
           SVNLogEntry logEntry = ( SVNLogEntry )it.next();
     	   String revisionID = "r" + logEntry.getRevision();
     	   String log = logEntry.getMessage();
     	   Date time = logEntry.getDate();
     	   Author author = retrieveAuthor(logEntry);
     	   Commit commit = saveCommit(revisionID, log, time, author, projectId);
     	   System.out.format("add commits : %s %n", revisionID);
     	   commit.setPreRevisionId(pre.getRevisionId());
     	   commits.add(commit);
     	   pre = commit;
        }
        return commits;
	}
	
	private List<Commit> getLastCommitBySVN(int projectId, String version) throws SVNException{
		List<Commit> commits = new ArrayList<Commit>();
		SVNRepository repository = SVNRepositoryFactory.create(svnurl);
		repository.setAuthenticationManager(authManager);
		Collection logEntries = repository.log( new String[] { "" } , null , 0 , -1 , true , true );
        Iterator it = logEntries.iterator( );
        
        SVNLogEntry logEntry = null;
        SVNLogEntry preLogEntry = null;
        while (it.hasNext( )) {
           preLogEntry = logEntry;
           logEntry = ( SVNLogEntry )it.next();
        }
        if(logEntry!=null){
        	String revisionID = "r" + logEntry.getRevision();
      	    String log = logEntry.getMessage();
      	    Date time = logEntry.getDate();
      	    Author author = retrieveAuthor(logEntry);
            Commit pre = dao.getLastCommit(projectId);
      	    Commit commit = saveCommit(revisionID, log, time, author, projectId, version);
      	    if (pre == null) {
				commit.setPreRevisionId("-1");
			}else {
				commit.setPreRevisionId(pre.getRevisionId());
			}
      	    commits.add(commit);
        }
        return commits;
	}
	public List<MyFileTreeNode> getFileTree(String url) throws SVNException{
		SVNURL svnurl = SVNURL.parseURIDecoded(url);
		SVNRepository repository = SVNRepositoryFactory.create(svnurl);
		repository.setAuthenticationManager(authManager);
		
		List<MyFileTreeNode> result = new ArrayList<MyFileTreeNode>();
		SVNNodeKind nodeKind = repository.checkPath("", SVNRevision.HEAD.getNumber());
		if (nodeKind == SVNNodeKind.NONE) {
			return null;
		} else if (nodeKind == SVNNodeKind.FILE) {
			return null;
		} else if (nodeKind == SVNNodeKind.DIR) {
			SVNProperties fileProperties = new SVNProperties();
			ArrayList array = new ArrayList();
			repository.getDir("", SVNRevision.HEAD.getNumber(), fileProperties, (Collection)array);
			for(int i = 0; i < array.size(); i++){
				SVNDirEntry entry = (SVNDirEntry)array.get(i);
				storeThisNode("-1", i, entry.getRelativePath(), entry.getName(), repository, result);
			}
		}
		return result;
	}
	public void storeThisNode(String parentId, int index, String relativePath, String name, SVNRepository repo, List<MyFileTreeNode> list) throws SVNException{
		SVNNodeKind nodeKind = repo.checkPath(relativePath, SVNRevision.HEAD.getNumber());
		if (nodeKind == SVNNodeKind.NONE) {
			System.out.println(relativePath+" do not exist.");
			return;
		} else if (nodeKind == SVNNodeKind.FILE) {
			list.add(new MyFileTreeNode(parentId+"i"+index, parentId, name, relativePath, true, false));
			System.out.println("add: "+parentId+"i"+index+" | parent: "+parentId+" | path: "+relativePath);
			return;
		} else if (nodeKind == SVNNodeKind.DIR) {
			list.add(new MyFileTreeNode(parentId+"i"+index, parentId, name, relativePath, false, true));
			ArrayList array = new ArrayList();
			repo.getDir(relativePath, SVNRevision.HEAD.getNumber(), null, (Collection)array);
			System.out.println(array.size()+"add: "+parentId+"i"+index+" | parent: "+parentId+" | path: "+relativePath);
			for(int i = 0; i < array.size(); i++){
				SVNDirEntry entry = (SVNDirEntry)array.get(i);
				storeThisNode(parentId+"i"+index, i, relativePath+"/"+entry.getRelativePath(), entry.getName(), repo, list);
			}
		}
	}
}
