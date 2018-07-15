package cn.edu.fudan.se.clonedetector.ccfinder.clone.detector;

import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneClassImpl;

public interface ICloneComparator {

	public boolean sameInstance(CloneImpl c, CloneImpl cloneInstance);
	
	public boolean sameClass(CloneClassImpl cc1, CloneClassImpl cc2);

}
