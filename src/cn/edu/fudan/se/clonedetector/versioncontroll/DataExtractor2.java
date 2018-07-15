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
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl.CCFinderCloneDetector2;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneClassImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.util.CCFinder;
import cn.edu.fudan.se.clonedetector.ccfinder.evolution.EvolutionAnalyse;
import cn.edu.fudan.se.clonedetector.dao.IDataAccessor;
import cn.edu.fudan.se.clonedetector.util.FileProcesser;

/**
 * 允许分阶段save CloneImpl的结果而不是等到所有都完成
 * @author zhanghr
 * @date 2016年11月28日
 */
public abstract class DataExtractor2 extends DataExtractor{

	public DataExtractor2(IDataAccessor dao, String repoLocation) {
		super(dao, repoLocation);
	}
	
	@Override
	protected void retrieveClone(String location, String languageType, String fileList, int commitId, HashMap<String, Integer> nameId) {
		CCFinderCloneDetector2 detector = new CCFinderCloneDetector2();
		detector.setType(CCFinder.toCCLanguageType(languageType));
		detector.setMinFragLen(this.minToken);
		Queue<CloneDetectionResult> results = new LinkedBlockingQueue<CloneDetectionResult>(); 

	    Lock lock = new ReentrantLock();
	    Condition notEmpty = lock.newCondition();
	    
		String cloneFile = "";
		boolean detectClone = false;
		while(!detectClone){
			try {
				if (detector.getType().equals(CCFinder.TEXT)) {
					cloneFile = detector.findCloneFromFileList(location, fileList);					
				}else {
					cloneFile = detector.findCloneFromDir(location);
				}
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
		if (new java.io.File(cloneFile).exists()) {
			needCleanCcfxprep = true;
		}
		detector.detectClone(cloneFile, results, lock, notEmpty); //results是共享变量，lock、notEmpty用于唤醒处理result的线程
		int record =1;
		while(!detector.isIfFinished() ||  results.size() > 0){
			System.gc();
            lock.lock();
            CloneDetectionResult result = null;
            try {
                while(results.size() == 0){
                    try {
                        System.out.println("队列空，等待数据");
                        notEmpty.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
    			result = results.poll();				
			} finally {
				lock.unlock();
			}
            record = saveCloneDetectionResult(result, commitId, nameId, record);
		}
		backRenameFile(dao.getFilesOfCommitByIdTag(commitId, File.RENAME_RN_TAG));
	}
	

private int saveCloneDetectionResult(CloneDetectionResult result, int commitId, HashMap<String, Integer> nameId, int record) {

	if (result != null) {
		for (CloneClassImpl cci : result.getCloneClasses()) {
			// storage all cloneclasses
			System.out.format("retieve CloneClass %d --------------- %n", record++);
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
			}
			updateCloneClass(cci.getClones(), cc);
			
		}
	}
	return record;
		
	}

}
