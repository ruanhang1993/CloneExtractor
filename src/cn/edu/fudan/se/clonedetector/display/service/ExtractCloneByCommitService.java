package cn.edu.fudan.se.clonedetector.display.service;

import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Clone;
import cn.edu.fudan.se.clonedetector.bean.File;

public class ExtractCloneByCommitService extends AbstractService {
	public List<Clone> specificedClones(int commitId) {
		List<Clone> clones = this.dao.getClonesByCommitId(commitId);
		return clones;
	}
	
	public String getFilename(int fileId){
		File file = this.dao.getFileById(fileId);
		return file.getFileName();
	}
}
