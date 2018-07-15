package cn.edu.fudan.se.clonedetector.ccfinder.clone.detector;

import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.FileGroup;

public interface ICloneDetector {

	public CloneDetectionResult detectClonesBetweenGroups(FileGroup changedFiles,
			FileGroup unchangedFiles) throws Exception;

	public CloneDetectionResult detectClonesInnerGroup(FileGroup changedFiles) throws Exception;

	public CloneDetectionResult detectClone(String path) throws Exception;
}
