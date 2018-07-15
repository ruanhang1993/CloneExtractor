package cn.edu.fudan.se.clonedetector.ccfinder.clone.detector;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneClassImpl;

public class CloneDetectionResult {
	private List<CloneClassImpl> classes = new LinkedList<CloneClassImpl>();

	public List<CloneClassImpl> getCloneClasses() {
		return classes;
	}

	public void removeCloneClass(CloneClassImpl cloneClass) {
		classes.remove(cloneClass);
	}

	public List<CloneImpl> getClones() {
		List<CloneImpl> clones = new ArrayList<CloneImpl>();
		for (CloneClassImpl cloneClass: classes) {
			clones.addAll(cloneClass.getClones());
		}
		
		return clones;
	}

	public void removeCloneInstance(CloneImpl cloneInstance) {
		for (CloneClassImpl cloneClass: classes) {
			cloneClass.getClones().remove(cloneInstance);
			if (cloneClass.getClones().isEmpty())
				removeCloneClass(cloneClass);
		}
	}

	public void addCloneClass(CloneClassImpl cloneClass) {
		classes.add(cloneClass);
	}
	
	public void addClone(CloneImpl clone) {
		for (CloneClassImpl c: classes) {
			if (c.getId() == clone.getClassId()) {
				c.addClone(clone);
				return;
			}
		}
		
		CloneClassImpl cc = new CloneClassImpl();
		cc.setId(clone.getClassId());
		cc.addClone(clone);
		classes.add(cc);
	}

}
