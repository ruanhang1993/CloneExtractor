/**
 * 
 */
package cn.edu.fudan.se.clonedetector.display.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Author;
import cn.edu.fudan.se.clonedetector.bean.Change;
import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.bean.CommitLanguage;


public class OneVersionExtractService extends AbstractService {
	private Comparator<Commit> comCommit = new Comparator<Commit>() {
		public int compare(Commit s1, Commit s2) {
			return s1.getCommitDate().compareTo(s2.getCommitDate());
		}
	};

	public Commit specifiedCommit(int commitId) {
		System.out.println("get specifiedCommit : "+commitId);
		
		Commit commit = this.dao.getCommitById(commitId);
		
		System.out.println("get successfully ");
		return commit;
		
	}
	
	public List<CommitLanguage> involvedCommitLs(int commitId) {
		System.out.println("get involvedCommitLs ");
		
		List<CommitLanguage> list = this.dao.getCommitLanguageById(commitId);
		
		System.out.println("get successfully ");
		return list;
		
	}
}
