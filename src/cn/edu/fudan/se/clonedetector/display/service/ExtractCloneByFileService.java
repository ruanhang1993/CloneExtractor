package cn.edu.fudan.se.clonedetector.display.service;

import java.util.Comparator;
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Clone;

public class ExtractCloneByFileService extends AbstractService {
	public List<Clone> specificedClones(int fileId) {
		List<Clone> clones = this.dao.getClonesByFileId(fileId);
		return clones;
	}
}
