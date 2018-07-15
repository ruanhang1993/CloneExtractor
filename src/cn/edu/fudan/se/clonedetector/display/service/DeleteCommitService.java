package cn.edu.fudan.se.clonedetector.display.service;

public class DeleteCommitService extends AbstractService{
	public void deleteCommit(int commitId){
		this.dao.deleteCommitById(commitId);
	}
}
