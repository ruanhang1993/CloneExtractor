package cn.edu.fudan.se.clonedetector.ccfinder.clone.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import model.DataFileReadError;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.CloneDetectionResult;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.ICloneComparator;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.ICloneData;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.ICloneEventNotifier;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.IncrementalCloneDetector;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl.CCFinderCloneDetector;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl.CCFinderDataDumper;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl.IdentityCloneComparator;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl.ListCloneData;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl.PrintCloneEventNotifier;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.FileGroup;
import cn.edu.fudan.se.clonedetector.ccfinder.util.Shell;

public class WithCloneBeforeTest {
	private IncrementalCloneDetector incrementalDetector;
	private ICloneData existed;
	private ICloneComparator comparator;
	
	@Before
	public void before() throws DataFileReadError, IOException {
		CCFinderCloneDetector detector = new CCFinderCloneDetector();
		detector.setType("java");
		detector.setMinFragLen(20);
		
		ICloneEventNotifier notifier = new PrintCloneEventNotifier();
		
		comparator = new IdentityCloneComparator();
		
		existed = new ListCloneData();
		existed.addFile("X.java");
		existed.addFile("Y.java");		
		
		String cmd = "ccfx d java ./TestCases/WithCloneBefore/X.java ./TestCases/WithCloneBefore/Y.java " +
				"-o result.ccfxd -b 20";
		Shell.invoke(cmd, System.out);
		CCFinderDataDumper dumper = new CCFinderDataDumper("result.ccfxd");
		CloneDetectionResult result = dumper.dump();
		existed.syncronize(result, comparator);
		
		incrementalDetector = new IncrementalCloneDetector(detector, notifier, comparator, existed);
	}
	
	private CloneDetectionResult setup(String testName, String[] oldFiles, String[] newFiles) throws DataFileReadError, IOException {
		PrintStream log = createLog("./TestCases/WithCloneBefore/"+testName+"/log");
		String cmd = "ccfx d java ";
		for (int i = 0; i < oldFiles.length; i++) {
			cmd += "./TestCases/WithCloneBefore/"+oldFiles[i]+" ";
		}
		for (int i = 0; i < newFiles.length; i++) {
			cmd += "./TestCases/WithCloneBefore/"+testName+"/"+newFiles[i]+" ";
		}
		cmd += "-o result.ccfxd " +
				"-b 20";
		Shell.invoke(cmd, log);
		log.println();
		
		cmd = "ccfx p result.ccfxd";
		Shell.invoke(cmd, log);
		
		log.println();
		cmd = "cp result.ccfxd ./TestCases/WithCloneBefore/"+testName+"/result.ccfxd";
		Shell.invoke(cmd, log);
		
		CCFinderDataDumper dumper = new CCFinderDataDumper("./result.ccfxd");
		return dumper.dump();
	}
	
	@Test
	public void testNoCloneChangeEdittingOne() throws DataFileReadError, IOException {
		String[] oldFiles = {"Y.java"};
		String[] newFiles = {"X.java"};
		CloneDetectionResult result = setup("testNoCloneChangeEdittingOne", oldFiles, newFiles);
		
		
		List<String> files1 = new LinkedList<String>();
		files1.add("./TestCases/WithCloneBefore/Y.java");
		FileGroup unchanged = new FileGroup(files1);
		
		List<String> files2 = new LinkedList<String>();
		files2.add("./TestCases/WithCloneBefore/testNoCloneChangeEdittingOne/X.java");
		FileGroup changed = new FileGroup(files2);
		
		incrementalDetector.detect(changed, unchanged);
		
		//TODO version problem
		assertEquals(result.getCloneClasses().size(), existed.getAllCloneClasses().size());
		assertEquals(result.getClones().size(), existed.getAllClones().size());
		
		for (CloneImpl c: result.getClones()) {
			assertTrue(existed.isExist(c, comparator));
		}
	}
	
	@Test
	public void testNoCloneChangeEdittingTwo() throws DataFileReadError, IOException {
		String[] oldFiles = {};
		String[] newFiles = {"X.java", "Y.java"};
		CloneDetectionResult result = setup("testNoCloneChangeEdittingTwo", oldFiles, newFiles);
		
		List<String> files = new LinkedList<String>();
		files.add("./TestCases/WithCloneBefore/testNoCloneChangeEdittingTwo/X.java");
		files.add("./TestCases/WithCloneBefore/testNoCloneChangeEdittingTwo/Y.java");
		FileGroup changed  = new FileGroup(files);
		
		incrementalDetector.detect(changed, null);
		
		assertEquals(result.getCloneClasses().size(), existed.getAllCloneClasses().size());
		assertEquals(result.getClones().size(), existed.getAllClones().size());
		
		for (CloneImpl c: result.getClones()) {
			assertTrue(existed.isExist(c, comparator));
		}
	}
	
	@Test
	public void testAddingCloneEdittingOne() throws DataFileReadError, IOException {
		String[] oldFiles = {"Y.java"};
		String[] newFiles = {"X.java"};
		CloneDetectionResult result = setup("testAddingCloneEdittingOne", oldFiles, newFiles);
		
		List<String> files1 = new LinkedList<String>();
		files1.add("./TestCases/WithCloneBefore/Y.java");
		FileGroup unchanged = new FileGroup(files1);
		
		List<String> files2 = new LinkedList<String>();
		files2.add("./TestCases/WithCloneBefore/testAddingCloneEdittingOne/X.java");
		FileGroup changed = new FileGroup(files2);
		
		incrementalDetector.detect(changed, unchanged);
		
		assertEquals(result.getCloneClasses().size(), existed.getAllCloneClasses().size());
		assertEquals(result.getClones().size(), existed.getAllClones().size());
		
		for (CloneImpl c: result.getClones()) {
			assertTrue(existed.isExist(c, comparator));
		}
	}
	
	@Test
	public void testAddingCloneEdittingTwo() throws DataFileReadError, IOException {
		String[] oldFiles = {};
		String[] newFiles = {"X.java", "Y.java"};
		CloneDetectionResult result = setup("testAddingCloneEdittingTwo", oldFiles, newFiles);
		
		List<String> files = new LinkedList<String>();
		files.add("./TestCases/WithCloneBefore/testAddingCloneEdittingTwo/X.java");
		files.add("./TestCases/WithCloneBefore/testAddingCloneEdittingTwo/Y.java");
		FileGroup changed  = new FileGroup(files);
		
		incrementalDetector.detect(changed, null);
		
		assertEquals(result.getCloneClasses().size(), existed.getAllCloneClasses().size());
		assertEquals(result.getClones().size(), existed.getAllClones().size());
		
		for (CloneImpl c: result.getClones()) {
			assertTrue(existed.isExist(c, comparator));
		}
	}
	
	@Test
	public void testRemovingCloneEdittingOne() throws DataFileReadError, IOException {
		String[] oldFiles = {"Y.java"};
		String[] newFiles = {"X.java"};
		CloneDetectionResult result = setup("testRemovingCloneEdittingOne", oldFiles, newFiles);
		
		List<String> files1 = new LinkedList<String>();
		files1.add("./TestCases/WithCloneBefore/Y.java");
		FileGroup unchanged = new FileGroup(files1);
		
		List<String> files2 = new LinkedList<String>();
		files2.add("./TestCases/WithCloneBefore/testRemovingCloneEdittingOne/X.java");
		FileGroup changed = new FileGroup(files2);
		
		incrementalDetector.detect(changed, unchanged);
		
		assertEquals(result.getCloneClasses().size(), existed.getAllCloneClasses().size());
		assertEquals(result.getClones().size(), existed.getAllClones().size());
		
		for (CloneImpl c: result.getClones()) {
			assertTrue(existed.isExist(c, comparator));
		}
	}
	
	@Test
	public void testRemovingCloneEdittingTwo() throws DataFileReadError, IOException {
		String[] oldFiles = {};
		String[] newFiles = {"X.java", "Y.java"};
		CloneDetectionResult result = setup("testRemovingCloneEdittingTwo", oldFiles, newFiles);
		
		List<String> files = new LinkedList<String>();
		files.add("./TestCases/WithCloneBefore/testRemovingCloneEdittingTwo/X.java");
		files.add("./TestCases/WithCloneBefore/testRemovingCloneEdittingTwo/Y.java");
		FileGroup changed  = new FileGroup(files);
		
		incrementalDetector.detect(changed, null);
		
		assertEquals(result.getCloneClasses().size(), existed.getAllCloneClasses().size());
		assertEquals(result.getClones().size(), existed.getAllClones().size());
		
		for (CloneImpl c: result.getClones()) {
			assertTrue(existed.isExist(c, comparator));
		}
	}
	
	@Test
	public void testAddingCloneNoClassAddingOne() throws DataFileReadError, IOException {
		String[] oldFiles = {"X.java", "Y.java"};
		String[] newFiles = {"Z.java"};
		CloneDetectionResult result = setup("testAddingCloneNoClassAddingOne", oldFiles, newFiles);
		
		existed.addFile("Z.java");
		
		List<String> files1 = new LinkedList<String>();
		files1.add("./TestCases/WithCloneBefore/X.java");
		files1.add("./TestCases/WithCloneBefore/Y.java");
		FileGroup unchanged = new FileGroup(files1);
		
		List<String> files2 = new LinkedList<String>();
		files2.add("./TestCases/WithCloneBefore/testAddingCloneNoClassAddingOne/Z.java");
		FileGroup changed = new FileGroup(files2);
		
		incrementalDetector.detect(changed, unchanged);
		
		
		assertEquals(result.getCloneClasses().size(), existed.getAllCloneClasses().size());
		assertEquals(result.getClones().size(), existed.getAllClones().size());
		
		for (CloneImpl c: result.getClones()) {
			assertTrue(existed.isExist(c, comparator));
		}
	}
	
	@Test
	public void testAddingClassAddingOne() throws DataFileReadError, IOException {
		String[] oldFiles = {"X.java", "Y.java"};
		String[] newFiles = {"Z.java"};
		CloneDetectionResult result = setup("testAddingClassAddingOne", oldFiles, newFiles);
		
		existed.addFile("Z.java");
		
		List<String> files1 = new LinkedList<String>();
		files1.add("./TestCases/WithCloneBefore/X.java");
		files1.add("./TestCases/WithCloneBefore/Y.java");
		FileGroup unchanged = new FileGroup(files1);
		
		List<String> files2 = new LinkedList<String>();
		files2.add("./TestCases/WithCloneBefore/testAddingClassAddingOne/Z.java");
		FileGroup changed = new FileGroup(files2);
		
		incrementalDetector.detect(changed, unchanged);
		
		
		assertEquals(result.getCloneClasses().size(), existed.getAllCloneClasses().size());
		assertEquals(result.getClones().size(), existed.getAllClones().size());
		
		for (CloneImpl c: result.getClones()) {
			assertTrue(existed.isExist(c, comparator));
		}
	}
	
	@Test
	public void testAddingCloneAddingClassAddingOne() throws DataFileReadError, IOException {
		String[] oldFiles = {"X.java", "Y.java"};
		String[] newFiles = {"Z.java"};
		CloneDetectionResult result = setup("testAddingCloneAddingClassAddingOne", oldFiles, newFiles);
		
		existed.addFile("Z.java");
		
		List<String> files1 = new LinkedList<String>();
		files1.add("./TestCases/WithCloneBefore/X.java");
		files1.add("./TestCases/WithCloneBefore/Y.java");
		FileGroup unchanged = new FileGroup(files1);
		
		List<String> files2 = new LinkedList<String>();
		files2.add("./TestCases/WithCloneBefore/testAddingCloneAddingClassAddingOne/Z.java");
		FileGroup changed = new FileGroup(files2);
		
		incrementalDetector.detect(changed, unchanged);
		
		
		assertEquals(result.getCloneClasses().size(), existed.getAllCloneClasses().size());
		assertEquals(result.getClones().size(), existed.getAllClones().size());
		
		for (CloneImpl c: result.getClones()) {
			assertTrue(existed.isExist(c, comparator));
		}
	}
	
	@Test
	public void testAddingCloneX_MAndY_NAddingTwo() throws DataFileReadError, IOException {
		String[] oldFiles = {"X.java", "Y.java"};
		String[] newFiles = {"M.java", "N.java"};
		CloneDetectionResult result = setup("testAddingCloneX_MAndY_NAddingTwo", oldFiles, newFiles);
		
		existed.addFile("M.java");
		existed.addFile("N.java");
		
		List<String> files1 = new LinkedList<String>();
		files1.add("./TestCases/WithCloneBefore/X.java");
		files1.add("./TestCases/WithCloneBefore/Y.java");
		FileGroup unchanged = new FileGroup(files1);
		
		List<String> files2 = new LinkedList<String>();
		files2.add("./TestCases/WithCloneBefore/testAddingCloneX_MAndY_NAddingTwo/M.java");
		files2.add("./TestCases/WithCloneBefore/testAddingCloneX_MAndY_NAddingTwo/N.java");
		FileGroup changed = new FileGroup(files2);
		
		incrementalDetector.detect(changed, unchanged);
		
		
		assertEquals(result.getCloneClasses().size(), existed.getAllCloneClasses().size());
		assertEquals(result.getClones().size(), existed.getAllClones().size());
		
		for (CloneImpl c: result.getClones()) {
			assertTrue(existed.isExist(c, comparator));
		}
	}
	
	@Test
	public void testAddingCloneM_NAddingTwo() throws DataFileReadError, IOException {
		String[] oldFiles = {"X.java", "Y.java"};
		String[] newFiles = {"M.java", "N.java"};
		CloneDetectionResult result = setup("testAddingCloneM_NAddingTwo", oldFiles, newFiles);
		
		existed.addFile("M.java");
		existed.addFile("N.java");
		
		List<String> files1 = new LinkedList<String>();
		files1.add("./TestCases/WithCloneBefore/X.java");
		files1.add("./TestCases/WithCloneBefore/Y.java");
		FileGroup unchanged = new FileGroup(files1);
		
		List<String> files2 = new LinkedList<String>();
		files2.add("./TestCases/WithCloneBefore/testAddingCloneM_NAddingTwo/M.java");
		files2.add("./TestCases/WithCloneBefore/testAddingCloneM_NAddingTwo/N.java");
		FileGroup changed = new FileGroup(files2);
		
		incrementalDetector.detect(changed, unchanged);
		
		
		assertEquals(result.getCloneClasses().size(), existed.getAllCloneClasses().size());
		assertEquals(result.getClones().size(), existed.getAllClones().size());
		
		for (CloneImpl c: result.getClones()) {
			assertTrue(existed.isExist(c, comparator));
		}
	}
	
	@After
	public void cleanup() {
		java.io.File dir = new java.io.File("./");
		if (dir.isDirectory()) {
			java.io.File[] files = dir.listFiles();
			for (java.io.File f: files) {
				String name = f.getName();
				if (name.endsWith(".ccfxd")
						|| name.endsWith(".tmp"))
					f.delete();
			}
		}
	}
	
	private PrintStream createLog(String filename) {
		try {
			java.io.File log = new java.io.File(filename);
			if (!log.exists())
				log.createNewFile();
			return new PrintStream(log);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
}
