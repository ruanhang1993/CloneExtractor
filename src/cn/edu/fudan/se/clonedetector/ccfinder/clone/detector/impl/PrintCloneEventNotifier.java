package cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl;

import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.ICloneEventNotifier;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneClassImpl;

public class PrintCloneEventNotifier implements ICloneEventNotifier {

	@Override
	public void notifyCloneClassAdded(CloneClassImpl cloneClass) {
		System.out.println("Notifier: Clone class added");
	}

	@Override
	public void notifyCloneClassDeleted(CloneClassImpl cloneClass) {
		System.out.println("Notifier: Clone class deleted: " + cloneClass.getId());
	}

	@Override
	public void notifyCloneClassUpdated(CloneClassImpl cloneClass) {
		System.out.println("Notifier: Clone class updated: " + cloneClass.getId());
	}

	@Override
	public void notifyCloneInstanceAdded(CloneImpl cloneInstance) {
		System.out.println("Notifier: Clone instance added: "+cloneInstanceDetail(cloneInstance));
	}

	@Override
	public void notifyCloneInstanceDeleted(CloneImpl cloneInstance) {
		System.out.println("Notifier: Clone instance deleted: "+cloneInstanceDetail(cloneInstance));
	}

	@Override
	public void notifyCloneInstanceUpdated(CloneImpl cloneInstance) {
		System.out.println("Notifier: Clone instance updated: "+cloneInstanceDetail(cloneInstance));
	}

	private String cloneInstanceDetail(CloneImpl cloneInstance) {
		return cloneInstance.getFile()+":"+
				cloneInstance.getStartIndex()+","+
				cloneInstance.getEndIndex();
	}
}
