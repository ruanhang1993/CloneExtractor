package cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl;

import java.util.List;

import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.ICloneComparator;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneClassImpl;

public class IdentityCloneComparator implements ICloneComparator{

	@Override
	public boolean sameInstance(CloneImpl c, CloneImpl cloneInstance) {
		return c.equals(cloneInstance);
	}

	@Override
	public boolean sameClass(CloneClassImpl cc1, CloneClassImpl cc2) {
		List<CloneImpl> clones1 = cc1.getClones();
		List<CloneImpl> clones2 = cc2.getClones();
		
		if (clones1.size() != clones2.size())
			return false;
		
		for (CloneImpl c: clones1) {
			if (!clones2.contains(c))
				return false;
		}
		
		return true;
	}

}
