package cn.edu.fudan.se.clonedetector.versioncontroll.cc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tmatesoft.svn.core.SVNException;

import cn.edu.fudan.se.clonedetector.bean.Author;
import cn.edu.fudan.se.clonedetector.bean.CCStreamProperty;
import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.bean.CommitLanguage;
import cn.edu.fudan.se.clonedetector.bean.Project;
import cn.edu.fudan.se.clonedetector.bean.Repository;
import cn.edu.fudan.se.clonedetector.dao.IDataAccessor;
import cn.edu.fudan.se.clonedetector.util.FileProcesser;
import cn.edu.fudan.se.clonedetector.versioncontroll.DataExtractor2;
import cn.edu.fudan.se.clonedetector.versioncontroll.Type;

public class CCExtractor extends DataExtractor2 {

	private String component;
	private String pvob;
	private String stream;
	private String blstream;
	private String view;
	private String view_local_path;
	private String activity = "NGCTS00804042";	

	public CCExtractor(IDataAccessor dao, String repoLocation) {
		super(dao, repoLocation);
		String path = getClass().getResource("/").toString().substring(5);
		readCloneProperty(path+"clone.properties");
	}
	
	private String fmtSeperator = " ";
	private String CLEARTOOL = "cleartool";
	
	/**
	 * cleartool lsbl -fmt "%n %d %u\n" -component Test@\PV_Test -stream Test_UAT@\PV_Test
	 * 输出格式: Test_Pro_2011-11-17 2011-11-17T10:49:09+08:00 ccadmin
	 * @param component
	 * @param stream
	 * @param vob
	 * @param wd
	 * @param out
	 */
	@SuppressWarnings("rawtypes")
	public List<Commit> getCommitsByBL(String component, String stream, String pvob, String wd, PrintStream out, int projectId, Date startDate) {
		HashMap<String, String> hashMap = new HashMap<>();// (vision, timeLong)
		List<Commit> commits = new ArrayList<Commit>();
		String fmt = "\"%n"+fmtSeperator+"%d"+fmtSeperator+"%u"+"\\n\"";
		String cmd = String.format("%s lsbl -fmt %s -component %s -stream %s", 
				CLEARTOOL, fmt, component+"@\\"+pvob, stream+"@\\"+pvob);//会按时间顺序由旧到新排序
		try {
			out.println("PROC> "+cmd);
			Process process = Runtime.getRuntime().exec(cmd, null, new File(wd));
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String s = "";
			String blName = "",  blTime = "";
			while((s = br.readLine()) != null){
				String[] blInfos = s.split(fmtSeperator);
				blName = blInfos[0];
				blTime = blInfos[1];
				Date time = parseTime(blTime);
				if (startDate!=null && time.after(startDate)) {
					String selfVersionId = pickVisionFromBL(blName);
					if (hashMap.size()<=5 && selfVersionId != null) {
						hashMap.put(selfVersionId, s);
					}
					out.format("vision : %s, name : %s, time : %s %n", selfVersionId, blName, blTime);
				}
			}
			process.waitFor();
			process.destroy();
			int i =1;
			Iterator it = hashMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String selfVersionId = (String) entry.getKey();
				String blInfo = (String) entry.getValue();
				Commit commit = formCommit(blInfo, selfVersionId, projectId);
				commits.add(commit);
				System.out.println("formCommit "+i);
				i++;
			}
			commits.sort(new Comparator<Commit>(){
				public int compare(Commit c1, Commit c2){
					return c1.getCommitDate().compareTo(c2.getCommitDate());
				}
			});
			for (Commit c : commits) {
		     	System.out.format("add commits : %s , version : %s %n", c.getCommitId(), c.getSelfVersionId());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return commits;
	}
	
	/**
	 * 选取版本号
	 * @param blName
	 * @return
	 */
	private String pickVisionFromBL(String blName) {
		String pattern = "((\\d+)\\.)+(\\d+)*";
	    Pattern p1 = Pattern.compile(pattern);
	    Matcher m = p1.matcher(blName);
	    if (m.find()){
	    	String vision = m.group();
	    	vision = vision.endsWith(".") ? vision.substring(0, vision.length()-1) : vision;
	    	return vision;
	    }
	    return null;		
	}

	private Commit formCommit(String blInfo, String selfVersionId, int projectId) {
		String[] bl = blInfo.split(fmtSeperator);
		String blName = bl[0];
		String blTime = bl[1];
		String authorN = bl[2];
		String revisionID = "r" + blName;
		Date time = parseTime(blTime);
		Author author = saveAuthor(authorN);
		return saveCommit(revisionID, time, author, projectId, selfVersionId);
	}

	private Date parseTime(String blTime) {
		blTime = blTime.replace("T", " ");
		blTime = blTime.substring(0, blTime.indexOf('+'));
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		try {
			return sdf.parse(blTime);			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * cleartool rebase -baseline Test_Pro_2011-11-17-2@\PV_Test -view codescanner_Test_DEV1_1 -stream Test_DEV1@\PV_Test -complete
	 * 这里虽然没有出现本地路径，但是要求view对应的本地路径已经加载有内容（因为默认mkview创建的静态视图没有加载规则所以没有内容）
	 * @param bl
	 * @param view
	 * @param stream
	 * @param pvob
	 * @param wd
	 * @param out
	 */
	public void rebase(String bl,String view, String stream, String pvob, String wd, PrintStream out) {
		String cmd = String.format("%s rebase -baseline %s -view %s -stream %s -complete", 
				CLEARTOOL, bl+"@\\"+pvob, view, stream);
		boolean retry = false;
		try {
			out.println("PROC> "+cmd);
			Process process = Runtime.getRuntime().exec(cmd, null, new File(wd));
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			InputStream iserr = process.getErrorStream();
			InputStreamReader isrerr = new InputStreamReader(iserr, "utf-8");
			BufferedReader brerr = new BufferedReader(isrerr);
			String s = "";
			while((s = br.readLine()) != null){
				out.println(s);
			}
			while((s = brerr.readLine()) != null){
				System.out.println(s);
				if (s.contains("prevents usage of options")) {
					retry = true;
				}
			}
			process.waitFor();
			process.destroy();
			if (retry) {
				rebaseCancle(view, stream, pvob, wd, out);
				rebase(bl, view, stream, pvob, wd, out);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void rebaseCancle(String view, String stream, String pvob, String wd, PrintStream out) {
		String cmd = String.format("%s rebase  -cancel -view %s -stream %s", 
				CLEARTOOL, view, stream);
		try {
			System.out.println("PROC> "+cmd);
			Process process = Runtime.getRuntime().exec(cmd, null, new File("."));
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			InputStream iserr = process.getErrorStream();
			InputStreamReader isrerr = new InputStreamReader(iserr, "utf-8");
			BufferedReader brerr = new BufferedReader(isrerr);
			OutputStream os = process.getOutputStream();
			String s = "";
			
			while((s = br.readLine()) != null){
				System.out.println(s);
				if(s.startsWith("Started by \"codescanner\"")){
					os.write("y".getBytes());
					os.flush();
					os.close();
				}
			}
			while((s = brerr.readLine()) != null){
				System.out.println(s);
			}
			process.waitFor();
			process.destroy();
			System.out.println("rebase cancel!!");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * D:\TestCC\codescanner_Test_DEV1_1 > cleartool update 
	 * update("D:\TestCC\codescanner_Test_DEV1_1", System.out)
	 * @param wd view所在根目录
	 * @param out
	 */
	public void update(String wd, PrintStream out) {
		String cmd = String.format("%s update", CLEARTOOL);
		try {
			out.format("PROC> (%s) %s %n", wd, cmd);
			Process process = Runtime.getRuntime().exec(cmd, null, new File(wd));
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String s = "";
			while((s = br.readLine()) != null){
				out.println(s);
			}
			process.waitFor();
			process.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * D:\TestCC\codescanner_Test_DEV1_1 > cleartool update 
	 * update("D:\TestCC\codescanner_Test_DEV1_1", System.out)
	 * @param wd view所在根目录
	 * @param out
	 */
	public void setActivity(String activity, String view_tag, String wd, PrintStream out) {
		String cmd = String.format("%s setactivity -view %s %s", CLEARTOOL, view_tag, activity);
		try {
			out.format("PROC> (%s) %s %n", wd, cmd);
			Process process = Runtime.getRuntime().exec(cmd, null, new File(wd));
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String s = "";
			while((s = br.readLine()) != null){
				out.println(s);
			}
			process.waitFor();
			process.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	private void retrieveFiles(Commit curr) throws ReBaseException {
		backRenameFile(curr.getProjectId());
		updateTo(curr);
		updateCommitStage(curr, Commit.CHECKOUT_FINISH);
		retrieveFilesFromFileSystem(curr);
	}

//	private void retrieveFilesFromFileSystem(Commit curr) {
//		System.out.format("commit revision is : %s  ... " , curr.getRevisionId());
//		System.out.println("retrieving files...");
//		HashMap<String, Integer> nameId = new HashMap<String, Integer>();
//		Collection<String> entries = getAnaylyzeEntries(selectedDirs);
//		Collection<String> involvedSuffixes = getSuffixes();
//		int relativeFSPathStrIdx = (selectedDirs != null && selectedDirs.length>0) ?  
//				new File(repoLocation+selectedDirs[0]).getPath().length() : repoLocation.length();// 截取有效文件名（用于忽略版本文件夹前缀）
//		if (entries != null){
//			entries.stream().filter((entry) -> {
//				if (isIgnored(entry)){
//					new File(entry).delete();
//					return false;
//				}
//				return isValidFile(entry, involvedSuffixes);
//			}).forEach((entry) -> {
//								
//				System.out.println("file : "+ entry);
//				
//				String validFileName = entry;
//				if (type == Type.RELEASE_VERSION_TYPE) 
//					validFileName = entry.substring(relativeFSPathStrIdx);
//				
//				File fileInFileSystem = new File(entry);
//				analyzeFileAndStore(fileInFileSystem, curr, nameId, validFileName);
//				fileInFileSystem = null;
//				System.gc();
//			});
//		}
//		updateCommitStage(curr, Commit.FILE_A_FINISH);
//		System.out.println("---------------------------------------------\n");
//		List<CommitLanguage> cls = new ArrayList<>(2);
//		if(nameId != null && nameId.size() > 0){
//			String cloneDetectSrc = repoLocation;
//			if (selectedDirs.length > 0) {
//				 // todo 因为目前的Ccfinder类只支持输入一个文件夹
//				cloneDetectSrc = repoLocation+selectedDirs[0];
//			}
//			for (String extension : extensions) {
//				System.out.format("%ndetect clone for %s%n", extension);
//				retrieveClone(cloneDetectSrc, extension, curr.getCommitId(), nameId);
//				CommitLanguage cl = new CommitLanguage();
//				cl.setCommitId(curr.getCommitId());
//				cl.setLanguage(extension);
//				System.out.format("save CommitLanguage %s %n", extension);
//				if(dao.saveCommitLanguage(cl, toInvolvedSuffixes(extension)) != null)
//						cls.add(cl);
//			}
//			FileProcesser.delFSFiles(new File(cloneDetectSrc+"/.ccfxprepdir"));
//		}else{
//			System.err.println("no involved files!");
//		}
//		updateCommitStage(curr, Commit.CCFINDER_FINISH);
//		System.out.println("---------------------------------------------\n");
//		for (Integer fi : nameId.values()) {
//			System.out.format("update fileId %d cloc%n", fi);
//			dao.updateFileCLoc(fi);
//		}
//		updateCommitStage(curr, Commit.UPDATE_FILE_CLOC_FINISH);
//		for (CommitLanguage cl : cls) {
//			System.out.format("update CommitLanguage %s cloc%n", cl.getLanguage());
//			dao.updateCommitLanguageCloc(cl,toInvolvedSuffixes(cl.getLanguage()));
//		}
//		System.out.println("Finish retrieving files ... ");
//		System.out.println("---------------------------------------------\n");
//	}
	
//	protected void analyzeFileAndStore(java.io.File fileInFileSystem, Commit curr, HashMap<String, Integer> nameId, String validFileName) {
//
//		try {
//			InputStream is = new java.io.FileInputStream(fileInFileSystem);
//			byte[] byteArray = IOUtils.toByteArray(is);
//			int loc = getTotalLines(fileInFileSystem);
//			EvolutionAnalyse ea = new EvolutionAnalyse(fileInFileSystem.getAbsolutePath());
//			int res[] = ea.countLine();
//			int bloc = res[0];
//			int cmloc = res[1];
//			int eloc = res[2];
//			cn.edu.fudan.se.clonedetector.bean.File sourceFile = saveFile(curr, fileInFileSystem.getAbsolutePath(), validFileName,
//					byteArray, loc, eloc,bloc,cmloc);
//			nameId.put(fileInFileSystem.getAbsolutePath(), sourceFile.getFileId());
//			sourceFile = null;
//			System.gc();
//			is.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//			
//	}

///**
// * 只考虑属于selectedDirs的文件夹版本
// * 
// * @param currId 
// * @param selectedDirs 相对于repoLocation的路径
// * @return
// * @throws SVNException
// */
//public Collection<String> getAnaylyzeEntries(String[] selectedDirs){
//	if (type == Type.RELEASE_VERSION_TYPE && selectedDirs.length == 0) { //没有发布版本文件，忽略
//		return null;
//	}else if (selectedDirs.length == 0) { 
//		return getCoverFileList(repoLocation);
//	}else if (selectedDirs.length > 0) {
//		Collection<String> entries = getCoverFileList(repoLocation+"\\"+selectedDirs[0]);
//		for (int i =1;i<selectedDirs.length;i++) {
//			entries.addAll(getCoverFileList(repoLocation+"\\"+selectedDirs[i]));
//		}
//		return entries;
//	}
//	return null;
//}
//
///**
// * 包含的所有文件
// * @param repoLocation
// * @return
// */
//private Collection<String> getCoverFileList(String repoLocation){
//	Collection<String> entries = new ArrayList<>();
//	FileProcesser.travelFSFiles(new java.io.File(repoLocation), entries);
//	return entries;
//}

	private void updateTo(Commit curr) throws ReBaseException {
		System.out.println("updating...");
		String baseline = parseID(curr.getRevisionId());
		rebase(baseline, view, blstream, pvob, ".", System.out);
//		update(view_local_path, System.out);
		System.out.println("over update");
	}
	
	private String parseID(String revisionID) {
		return revisionID.substring(1);
	}
	
	/**
	 * 分析该次提交中是否增加了一个版本发布文件夹，一个版本文件夹是svnurl的子文件夹，并且以v+数字开头
	 * 		如果存在添加该文件夹到selectedDirs
	 * 		如果没有这样的文件夹，selectedDirs为空
	 * @param revision
	 * @throws SVNException 
	 */
	private void selectAnalyzedDir() throws SVNException {
		selectedDirs = new String[]{}; 
		String relative = repoLocation;
		Collection<String> entries = new ArrayList<>();
		FileProcesser.travelFSFiles(new File(repoLocation), entries);
		Iterator<String> iterator = entries.iterator();
           while (iterator.hasNext( )) {
               String entryPath = iterator.next( );
               if (new File(entryPath).isDirectory()) {
            	   System.out.println(entryPath);
            	   if(entryPath.length() <= relative.length())
            		   continue;
            	   String[] dirNames = entryPath.substring(relative.length()).split("/");// 取tag标签之后的路径，以/开头
            	   if (dirNames.length == 2 && isValidVersionDir(dirNames[1])) { // index = 1是因为第一个是""
            		   selectedDirs = new String[] {dirNames[1]};
            		   break;
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
		String[] labels = dirName.split("\\.");
		if (labels.length>=2 && (labels[0].startsWith("V") || labels[0].startsWith("v"))) {
			try{
				if (Integer.parseInt(labels[0].substring(1))>=0) {
					return true;
				}
			}catch (java.lang.NumberFormatException ex){
				return false;
			}
		}else if(dirName.charAt(0)>='0' && dirName.charAt(0)<= '9'){
			return true;
		}
		return false;
	}

//	private void retrieveClone(String location, String languageType, int commitId, HashMap<String, Integer> nameId) {
//		CCFinderCloneDetector detector = new CCFinderCloneDetector();
//		detector.setType(toCCLanguageType(languageType));
//		detector.setMinFragLen(this.minToken);
//		CloneDetectionResult result = null;
//		boolean detectClone = false;
//		while(!detectClone){
//			try {
//				result = detector.detectClone(location);
//				detectClone = true;
//			} catch (Exception e) {
//				if (e instanceof FileWrongEncodingException) {
//					String wrongFile = "";
//					if (wrongFile.equals(e.getMessage())) {
//						String pathname = wrongFile+".del";
//						new File(wrongFile).renameTo(new File(pathname));
//					}else {
//						wrongFile = e.getMessage();
//						String pathname = wrongFile+".rm";
//						new File(wrongFile).renameTo(new File(pathname));
//						try {
//							FileProcesser.transformFileEncoding(pathname, wrongFile, "utf-16", "utf-8");
//						} catch (IOException e1) {
//							e1.printStackTrace();
//						}				
//					}
//					updateWrongEncodingFile(nameId, wrongFile);
//				}
//			}
//		}
//		if (result == null)
//			return;
//		int i =1;
//		for (CloneClassImpl cci : result.getCloneClasses()) {
//			// storage all cloneclasses
//			System.out.format("retieve CloneClass %d --------------- %n", i++);
//			CloneClass cc = saveCloneClass(commitId);
//			int repeat=0;
//			for (CloneImpl ci : cci.getClones()) {
//				//todo test
//				System.out.print((nameId==null)+"/nameId, ");
//				System.out.print((ci==null)+"/ci, ");
//				System.out.print(ci.getFile()+", ");
//				System.out.println(nameId.get(ci.getFile()));
//				int fileId = nameId.get(ci.getFile());
//				int beginLine = ci.getStartIndex();
//				int endLine = ci.getEndIndex();
//				EvolutionAnalyse ea = new EvolutionAnalyse(ci.getFile());
//				int res[] = ea.countIncompleteLine(beginLine, endLine);
//				int bcloc = res[0];
//				int cmcloc = res[1];
//				int ecloc = res[2];
//				Clone c = saveClone(cc.getCloneClassId(), ci.getType(), fileId, commitId, beginLine, endLine, ci.getCloneSize(),
//						ecloc,bcloc,cmcloc);
//				dao.updateCloneCcloc(c,repeat);
//				repeat++;
//				ea = null;
//				System.gc();
//			}
//		}
//
//	}
	
	

	@Override
	/**
	 * 
	 * @param ccConcepts	String stream, String pvob, String component
	 * @param b
	 * @param processDate
	 * @param allType
	 * @param proConcepts	String projectNameCh, String projectNameEn, String developCompany, String projectTeam
	 */
	public void checkout(String[] ccConcepts, boolean b, Date processDate, Type type, String[] proConcepts) {
		this.type = type;
		setCCVaribales(ccConcepts);
		
		String projectNameCh = proConcepts[0];
		String projectNameEn = proConcepts[1];
		String developCompany = proConcepts[2];
		String projectTeam = proConcepts[3];
		
		// todo fill repoLocation
		this.repoLocation = view_local_path;

		Repository existedRepo = this.dao.getRepositoryByProjectNameEn(projectNameEn);
		System.out.println("Repo: "+ (existedRepo==null));

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

			Repository myRepository = super.saveRepository(this.stream, "codescanner", "codescanner", language, processDate, analyzeType);
			super.saveProject(this.repoLocation, projectNameCh,projectNameEn,developCompany, language, myRepository.getRepositoryId(), projectTeam,
					stream, pvob, component, view, view_local_path);
			extractProjects(myRepository.getRepositoryId());
			myRepository = null;
			System.gc();
		} else {
			existedRepo.setProcessDate(new Date());
			this.dao.updateRepository(existedRepo);
			extractProjects(existedRepo.getRepositoryId());
		}
		existedRepo = null;
		System.gc();
		return;
	}

	private void setCCVaribales(String[] ccConcepts) {
		this.stream = ccConcepts[0];
		this.pvob = ccConcepts[1];
		this.component = ccConcepts[2];
		CCStreamProperty ccStream = dao.getCCStream(stream+"@\\\\"+pvob);
		if (ccStream != null) {
			this.blstream = ccStream.getBlstream();
			this.view = ccStream.getView();
			this.view_local_path = ccStream.getViewLocalPath();
		}
		
	}

	private void extractProjects(int repositoryId) {
		List<Project> projects = this.dao.getProjectByRepositoryId(repositoryId);
		for (Project pro : projects) {
			try {
				this.extract(pro.getProjectId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		projects = null;
		System.gc();
	}

	public void extract(int projectId) throws Exception {
		Date startDate = null;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		try {
			startDate = sdf.parse("2016-09-00 00:00:00");			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Commit> commits = getCommitsByBL(component, stream, pvob, ".", System.out, projectId, startDate);
		setActivity(activity, view, ".", System.out);
        int num = commits.size();
		Commit curr;
		try {
			int buttomIndex = 0;  
			int processNum = num;
			int topIndex = (buttomIndex+processNum) <= num ? (buttomIndex+processNum) :num;
			
			rebaseCancle(view, blstream, pvob, ".", System.out);
			for (int i = buttomIndex; i < topIndex; i++) {
				System.out.println("\nextracting " + (i+1) + "/" + num + "...");
				try {
					curr = commits.get(i);
					if (i > 0) {
						curr.setPreRevisionId(commits.get(i-1).getRevisionId());
					}else {
						curr.setPreRevisionId("-1");
					}
					if (curr.getStage() != Commit.ALL_FINISH){
						if (curr.getStage() == Commit.WAIT || curr.getStage() == Commit.FAILED) {
							updateCommitStage(curr, Commit.START);							
						}
						if (type == Type.RELEASE_VERSION_TYPE) {
							selectAnalyzedDir();
						}
						
						dao.deleteFilesByCommitId(curr.getCommitId());
						retrieveFiles(curr);
						dao.updateCommitLoc2(curr);
					}
					
					if (curr.getStage() == Commit.ALL_FINISH) {
						System.out.format("revision %s retriving finished.%n", curr.getRevisionId());
					} else {
						System.out.println("deleted unfinished file");
						dao.deleteFilesByCommitId(curr.getCommitId());
						throw new CommitRetieveUnFinishedException();
					}
				} catch (CommitRetieveUnFinishedException e) {
					i--;
				}catch (Exception e) {
					e.printStackTrace(new PrintStream(new File(view_local_path+"\\log.txt")));
					updateCommitStage(commits.get(i), Commit.FAILED);
					return;
				}
				rebaseCancle(view, blstream, pvob, ".", System.out);
			}
			System.out.format("%n%nextratc view : %s , stream : %s finished!%n%n", view, stream);
		} finally {
			
		}
	}
	
	
}
