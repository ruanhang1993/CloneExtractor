package cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl;

import java.util.LinkedList;
import java.util.List;

import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.CloneDetectionResult;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.ICloneComparator;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.ICloneData;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneClassImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.FileGroup;

public class ListCloneData implements ICloneData {
	private List<CloneImpl> clones = new LinkedList<CloneImpl>();
	private List<String> files = new LinkedList<String>();
	private int id = 0;
	private int classId = 0;
	
	@Override
	public CloneDetectionResult getClonesByFiles(FileGroup fileGroup) {
		CloneDetectionResult result = new CloneDetectionResult();
		
		List<String> files = fileGroup.getFiles();
		for (String file: files) {
			String real = getRealFile(file);
			for (CloneImpl c: clones) {
				if (c.getFile().equals(real))
					result.addClone(c);
			}
		}
		
		return result;
	}

	@Override
	public boolean isExist(CloneClassImpl cloneClass, ICloneComparator comparator) {
		return getExistedCloneFromClass(cloneClass, comparator)==null ? false : true;
	}
	
	@Override
	public boolean isExist(CloneImpl cloneInstance, ICloneComparator comparator) {
		return getExistedClone(cloneInstance, comparator)==null ? false : true;
	}

	private CloneImpl getExistedClone(CloneImpl cloneInstance, ICloneComparator comparator) {
		for (CloneImpl c: clones) {
			cloneInstance.setFile(getRealFile(cloneInstance.getFile()));
			if (comparator.sameInstance(c, cloneInstance)) {
				cloneInstance.setClassId(c.getClassId());
				cloneInstance.setId(c.getId());
				return c;
			}
		}
		
		return null;
	}
	
	private CloneImpl getExistedCloneFromClass(CloneClassImpl cloneClass, ICloneComparator comparator) {
		List<CloneImpl> cs = cloneClass.getClones();
		for (CloneImpl c: cs) {
			CloneImpl existed = getExistedClone(c, comparator);
			if (existed != null) {
				cloneClass.setId(existed.getClassId());
				return existed;
			}
		}
		
		return null;
	}
	
	@Override
	public void syncronize(CloneDetectionResult result, ICloneComparator comparator) {
		List<CloneClassImpl> classes = result.getCloneClasses();
		for (CloneClassImpl cc : classes) {
			syncronizeCloneClass(cc, comparator);
		}
	}
	
	private void syncronizeCloneClass(CloneClassImpl cc, ICloneComparator comparator) {
		CloneImpl existed = getExistedCloneFromClass(cc, comparator);
		int cid = -1;
		if (existed == null) 
			cid = classId++;
		else
			cid = existed.getClassId();

		cc.setId(cid);
		List<CloneImpl> cs = cc.getClones();
		for (CloneImpl c: cs) {
			c.setClassId(cid);
			syncronizeCloneInstance(c, comparator);
		}
	}
	
	private void syncronizeCloneInstance(CloneImpl cloneInstance, ICloneComparator comparator) {
		cloneInstance.setFile(getRealFile(cloneInstance.getFile()));
		CloneImpl existed = getExistedClone(cloneInstance, comparator);
		if (existed == null) {
			cloneInstance.setId(id++);
			clones.add(cloneInstance);
		} else {
			cloneInstance.setId(existed.getId());
		}
	}
	
	
	private String getRealFile(String file) {
		for (String f: files) {
			if (file.endsWith(f))
				return f;
		}
		
		return null;
	}

	@Override
	public boolean isExist(String file) {
		for (String f: files) {
			if (file.endsWith(f))
				return true;
		}
		
		return false;
	}

	@Override
	public List<CloneImpl> getAllClones() {
		return clones;
	}

	@Override
	public void addFile(String file) {
		files.add(file);
	}

	@Override
	public List<String> getAllFiles() {
		return files;
	}

	@Override
	public List<CloneClassImpl> getAllCloneClasses() {
		List<CloneClassImpl> classes = new LinkedList<CloneClassImpl>();
		
		for (CloneImpl c: clones) {
			int classid = c.getClassId();
			CloneClassImpl cc = getCloneClassById(classes, classid);
			cc.addClone(c);
		}
		
		return classes;
	}

	private CloneClassImpl getCloneClassById(List<CloneClassImpl> classes, int id) {
		for (CloneClassImpl cc: classes) {
			if (cc.getId() == id)
				return cc;
		}
		
		CloneClassImpl cc = new CloneClassImpl();
		cc.setId(id);
		classes.add(cc);
		return cc;
	}

	@Override
	public CloneClassImpl getCloneClassById(int id) {
		CloneClassImpl cc = new CloneClassImpl();
		cc.setId(id);
		
		List<CloneImpl> cs = new LinkedList<CloneImpl>();
		for (CloneImpl c: clones) {
			if (c.getClassId() == id)
				cs.add(c);
		}
		
		cc.setClones(cs);
		return cc;
	}

	@Override
	public void removeCloneInstance(CloneImpl cloneInstance) {
		clones.remove(cloneInstance);
	}

	@Override
	public void removeCloneClass(CloneClassImpl cloneClass) {
		List<CloneImpl> list = new LinkedList<CloneImpl>(clones);
		for (CloneImpl c: list) {
			if (c.getClassId() == cloneClass.getId())
				removeCloneInstance(c);
		}
	}
}
