package cn.edu.fudan.se.clonedetector.ccfinder.clone.test;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.CloneDetectionResult;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.ICloneComparator;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.ICloneData;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.ICloneEventNotifier;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.IncrementalCloneDetector;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl.CCFinderCloneDetector;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl.IdentityCloneComparator;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl.ListCloneData;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl.PrintCloneEventNotifier;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneClassImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.FileGroup;

public class BasicTest {
	
	@Test
	public void test() {
		CCFinderCloneDetector detector = new CCFinderCloneDetector();
		detector.setType("java");
		detector.setMinFragLen(10);
		
		ICloneEventNotifier notifier = new PrintCloneEventNotifier();
		
		ICloneComparator comparator = new IdentityCloneComparator();
		
		ICloneData existed = new ListCloneData();
		existed.addFile("Test1.java");
		existed.addFile("Test2.java");
		
		IncrementalCloneDetector incrementalDetector = new IncrementalCloneDetector(detector, notifier, comparator, existed);
		
		List<String> files = new LinkedList<String>();
		files.add("Test1.java");
		files.add("Test2.java");
		FileGroup changedFiles = new FileGroup(files);
		incrementalDetector.detect(changedFiles, null);
		
		System.out.println(existed.getAllClones());
	}
	
	@Test
	public void IncreseTest() {
		CCFinderCloneDetector detector = new CCFinderCloneDetector();
		detector.setType("java");
		detector.setMinFragLen(10);
		
		ICloneEventNotifier notifier = new PrintCloneEventNotifier();
		
		ICloneComparator comparator = new IdentityCloneComparator();
		
		ICloneData existed = new ListCloneData();
		existed.addFile("Test1.java");
		existed.addFile("Test2.java");
		existed.addFile("Test3.java");
		CloneDetectionResult old = new CloneDetectionResult();
		CloneClassImpl cc = new CloneClassImpl();
		CloneImpl c1 = new CloneImpl();
		c1.setFile("Test1.java");
		c1.setStartIndex(7);
		c1.setEndIndex(143);
		cc.addClone(c1);
		CloneImpl c2 = new CloneImpl();
		c2.setFile("Test2.java");
		c2.setStartIndex(7);
		c2.setEndIndex(143);
		cc.addClone(c2);
		old.addCloneClass(cc);
		existed.syncronize(old, comparator);
		
		IncrementalCloneDetector incrementalDetector = new IncrementalCloneDetector(detector, notifier, comparator, existed);
		
		List<String> files1 = new LinkedList<String>();
		files1.add("Test1.java");
		files1.add("Test2.java");
		FileGroup unchangedFiles = new FileGroup(files1);
		
		List<String> files2 = new LinkedList<String>();
		files2.add("Test3.java");
		FileGroup changed = new FileGroup(files2);
		incrementalDetector.detect(changed, unchangedFiles);
		
		System.out.println(existed.getAllClones());
	}
	
	@After
	public void cleanup() {
		java.io.File dir = new java.io.File("./");
		if (dir.isDirectory()) {
			java.io.File[] files = dir.listFiles();
			for (java.io.File f: files) {
				String name = f.getName();
				if (name.endsWith(".ccfxd")
						|| name.endsWith(".tmp")
						|| name.endsWith(".ccfxprep"))
					f.delete();
			}
		}
	}
}
