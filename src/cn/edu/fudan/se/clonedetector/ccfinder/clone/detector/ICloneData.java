package cn.edu.fudan.se.clonedetector.ccfinder.clone.detector;

import java.util.List;

import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneClassImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.FileGroup;


public interface ICloneData {

	public CloneDetectionResult getClonesByFiles(FileGroup fileGroup);

	public boolean isExist(CloneClassImpl cloneClass, ICloneComparator comparator);

	public boolean isExist(CloneImpl cloneInstance, ICloneComparator comparator);
	
	public boolean isExist(String file);

	public void syncronize(CloneDetectionResult result, ICloneComparator comparator);

	public List<CloneImpl> getAllClones();
	
	public List<CloneClassImpl> getAllCloneClasses();
	
	public void addFile(String file);
	
	public List<String> getAllFiles();
	
	public CloneClassImpl getCloneClassById(int id);

	public void removeCloneInstance(CloneImpl cloneInstance);

	public void removeCloneClass(CloneClassImpl cloneClass);
}
