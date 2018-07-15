package cn.edu.fudan.se.clonedetector.display.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.*;

public class DeleteCloneService extends AbstractService{
	public void deleteClone(int cloneId){
		List<Clone> clones = dao.deleteClonesById(cloneId);
		int commitId = clones.get(0).getCommitId();
		int compareId = 0;
		Commit commit = dao.getCommitById(commitId);
		List<CommitLanguage> cls = dao.getCommitLanguageById(commitId);
		for(Clone c:clones){
			int fileId = c.getFileId();
			ChangeFile cf = dao.getChangeFileByFileId(fileId);
			compareId = cf.getCompareId();
			dao.updateFileCLoc(fileId);
			dao.updateChangeFileLoc(cf);
		}
		List<CompareLanguage> comls = dao.getCompareLanguageByCompare(compareId);
		Compare compare = dao.getCompreById(compareId);
		for(CompareLanguage cl:comls){
			dao.saveCompareLanguage(cl, toInvolvedSuffixes(cl.getLanguage()));
		}
		for(CommitLanguage cl:cls){
			dao.updateCommitLanguageCloc(cl,toInvolvedSuffixes(cl.getLanguage()));
		}
		dao.updateCommitLoc2(commit);
		dao.updateCompareLoc(compare);
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

}
