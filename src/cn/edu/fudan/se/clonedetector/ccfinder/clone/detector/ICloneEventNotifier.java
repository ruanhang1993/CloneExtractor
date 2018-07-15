package cn.edu.fudan.se.clonedetector.ccfinder.clone.detector;

import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneClassImpl;

public interface ICloneEventNotifier {

	void notifyCloneClassAdded(CloneClassImpl cloneClass);

	void notifyCloneClassDeleted(CloneClassImpl cloneClass);

	void notifyCloneClassUpdated(CloneClassImpl cloneClass);

	void notifyCloneInstanceAdded(CloneImpl cloneInstance);

	void notifyCloneInstanceDeleted(CloneImpl cloneInstance);

	void notifyCloneInstanceUpdated(CloneImpl cloneInstance);

}
