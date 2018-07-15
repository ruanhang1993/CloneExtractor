/**
 * 
 */
package cn.edu.fudan.se.clonedetector.display.service;

import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Commit;


public class CommitExtractService extends AbstractService {
	

	public Commit lastCommit(int projectId) {
		System.out.println("get in");
		
		System.out.println("this is ");
		Commit commit = this.dao.getLastCommit(projectId);
		return commit;		
	}

}
