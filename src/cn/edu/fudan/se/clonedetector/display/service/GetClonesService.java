package cn.edu.fudan.se.clonedetector.display.service;

import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Clone;
import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.bean.File;

public class GetClonesService extends AbstractService{
	public List<Clone> getCloneObjectsById(int cloneId){
		Clone c = this.dao.getCloneByCloneId(cloneId);
		List<Clone> clones = this.dao.getClonesByClassId(c.getCloneClassId());
		return clones;
	}
	
	public String getVersion(int commitId){
		Commit commit =this.dao.getCommitById(commitId);
		return commit.getRevisionId();
	}
	
	public String getFilename(int fileId){
		File file = this.dao.getFileById(fileId);
		return file.getFileName();
	}
}
