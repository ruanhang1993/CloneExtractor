package cn.edu.fudan.se.clonedetector.ccfinder.clone.detector;

import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneImpl;

import java.io.File;
import java.io.IOException;

import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.exception.FileWrongEncodingException;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneClassImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.FileGroup;
import cn.edu.fudan.se.clonedetector.util.FileProcesser;

public class IncrementalCloneDetector {
	private ICloneDetector detector;
	private ICloneEventNotifier notifier;
	private ICloneComparator comparator;
	private ICloneData existed;
	
	public IncrementalCloneDetector(ICloneDetector detector, 
			ICloneEventNotifier notifier, ICloneComparator comparator,
			ICloneData existed) {
		this.detector = detector;
		this.notifier = notifier;
		this.comparator = comparator;
		this.existed = existed;
	}
	
	public void detect(FileGroup changedFiles, FileGroup unchangedFiles) {
		CloneDetectionResult invalidatedClones = existed.getClonesByFiles(changedFiles);
		
		CloneDetectionResult groupResult = detectClonesBetweenGroups(changedFiles, unchangedFiles);
		//existed.syncronize(groupResult, comparator);
		updateClones(groupResult, invalidatedClones, changedFiles);
				
		CloneDetectionResult updatedResult = detectClonesInnerGroup(changedFiles);
		//existed.syncronize(updatedResult, comparator);
		updateClones(updatedResult, invalidatedClones, changedFiles);
		
		notifyRemovedCloneEvents(invalidatedClones);
	}

	private CloneDetectionResult detectClonesInnerGroup(FileGroup changedFiles) {
		CloneDetectionResult result = null;
		boolean detectClone = false;
		while(!detectClone){
			try {
				result = detector.detectClonesInnerGroup(changedFiles);
				detectClone = true;
			} catch (Exception e) {
				if (e instanceof FileWrongEncodingException) {
					String wrongFile = "";
					if (wrongFile.equals(e.getMessage())) {
						String pathname = wrongFile+".del";
						new File(wrongFile).renameTo(new File(pathname));
					}else {
						wrongFile = e.getMessage();
						String pathname = wrongFile+".rm";
						new File(wrongFile).renameTo(new File(pathname));
						try {
							FileProcesser.transformFileEncoding(pathname, wrongFile, "utf-16", "utf-8");
						} catch (IOException e1) {
							e1.printStackTrace();
						}				
					}
				}
			}
		}
		return result;
	}

	private CloneDetectionResult detectClonesBetweenGroups(FileGroup changedFiles, FileGroup unchangedFiles) {
		CloneDetectionResult result = null;
		boolean detectClone = false;
		while(!detectClone){
			try {
				result = detector.detectClonesBetweenGroups(changedFiles, unchangedFiles);
				detectClone = true;
			} catch (Exception e) {
				if (e instanceof FileWrongEncodingException) {
					String wrongFile = "";
					if (wrongFile.equals(e.getMessage())) {
						String pathname = wrongFile+".del";
						new File(wrongFile).renameTo(new File(pathname));
					}else {
						wrongFile = e.getMessage();
						String pathname = wrongFile+".rm";
						new File(wrongFile).renameTo(new File(pathname));
						try {
							FileProcesser.transformFileEncoding(pathname, wrongFile, "utf-16", "utf-8");
						} catch (IOException e1) {
							e1.printStackTrace();
						}				
					}
				}
			}
		}
		return result;
	}

	private void updateClones(CloneDetectionResult updated,
			CloneDetectionResult invalidatedClones, FileGroup changedFiles) {
		updateCloneClass(updated, invalidatedClones);
		updateCloneInstance(updated, invalidatedClones, changedFiles);	
		existed.syncronize(updated, comparator);
	}

	private void updateCloneInstance(CloneDetectionResult updated,
			CloneDetectionResult invalidatedClones,
			FileGroup changedFiles) {
		for (CloneImpl cloneInstance:updated.getClones()){
			if (!existed.isExist(cloneInstance, comparator)){
				notifier.notifyCloneInstanceAdded(cloneInstance);
			}
			else{
				//if (changedFiles.getFiles().contains(cloneInstance.getFile()))
				//	notifier.notifyCloneInstanceUpdated(cloneInstance);
				invalidatedClones.removeCloneInstance(cloneInstance);
			}
		}
	}

	private void updateCloneClass(CloneDetectionResult updated,
			CloneDetectionResult invalidatedClones) {
		for (CloneClassImpl cloneClass:updated.getCloneClasses()){
			if (!existed.isExist(cloneClass, comparator)){
				notifier.notifyCloneClassAdded(cloneClass);
			} else {
				boolean classUpdated = false;
				for(CloneImpl c: cloneClass.getClones()) {
					if (!existed.isExist(c, comparator)) {
						classUpdated = true;
						break;
					}
				}
				if (classUpdated)
					notifier.notifyCloneClassUpdated(cloneClass);
				invalidatedClones.removeCloneClass(cloneClass);
			}
		}
		
	}

	private void notifyRemovedCloneEvents(CloneDetectionResult invalidatedClones) {
		for (CloneClassImpl cloneClass:invalidatedClones.getCloneClasses()){
			notifier.notifyCloneClassDeleted(cloneClass);
			existed.removeCloneClass(cloneClass);
		}
		for (CloneImpl cloneInstance:invalidatedClones.getClones()){
			notifier.notifyCloneInstanceDeleted(cloneInstance);
			existed.removeCloneInstance(cloneInstance);
		}		
	}
}
