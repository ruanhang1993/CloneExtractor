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

public class WithoutCloneBeforeTest {
	private IncrementalCloneDetector incrementalDetector;
	private ICloneData existed;
	private ICloneComparator comparator;

	@Before
	public void before() {
		CCFinderCloneDetector detector = new CCFinderCloneDetector();
		detector.setType("java");
		detector.setMinFragLen(20);

		ICloneEventNotifier notifier = new PrintCloneEventNotifier();

		comparator = new IdentityCloneComparator();

		existed = new ListCloneData();
		existed.addFile("X.java");
		existed.addFile("Y.java");

		incrementalDetector = new IncrementalCloneDetector(detector, notifier,
				comparator, existed);

		assertEquals(0, existed.getAllClones().size());
		assertEquals(2, existed.getAllFiles().size());
	}

	private CloneDetectionResult setup(String testName, String[] oldFiles,
			String[] newFiles) throws DataFileReadError, IOException {
		PrintStream log = createLog("./TestCases/WithoutCloneBefore/"
				+ testName + "/log");
		String cmd = "ccfx d java ";
		for (int i = 0; i < oldFiles.length; i++) {
			cmd += "./TestCases/WithoutCloneBefore/" + oldFiles[i] + " ";
		}
		for (int i = 0; i < newFiles.length; i++) {
			cmd += "./TestCases/WithoutCloneBefore/" + testName + "/"
					+ newFiles[i] + " ";
		}
		cmd += "-o result.ccfxd " + "-b 20";
		Shell.invoke(cmd, log);
		log.println();

		cmd = "ccfx p result.ccfxd";
		Shell.invoke(cmd, log);

		log.println();
		cmd = "cp result.ccfxd ./TestCases/WithoutCloneBefore/" + testName
				+ "/result.ccfxd";
		Shell.invoke(cmd, log);

		CCFinderDataDumper dumper = new CCFinderDataDumper("./result.ccfxd");
		return dumper.dump();
	}

	@Test
	public void testOriginalFiles() throws DataFileReadError, IOException {
		String[] oldFiles = { "X.java", "Y.java" };
		String[] newFiles = {};
		CloneDetectionResult result = setup("testOriginalFiles", oldFiles,
				newFiles);

		assertEquals(0, result.getClones().size());
	}

	@Test
	public void testNoCloneAfter() throws IOException {
		String[] oldFiles = { "Y.java" };
		String[] newFiles = { "X.java" };
		CloneDetectionResult result = setup("testNoCloneAfter", oldFiles,
				newFiles);

		assertEquals(0, result.getCloneClasses().size());
		assertEquals(0, result.getClones().size());

		List<String> files1 = new LinkedList<String>();
		files1.add("./TestCases/WithoutCloneBefore/Y.java");
		FileGroup unchanged = new FileGroup(files1);

		List<String> files2 = new LinkedList<String>();
		files2.add("./TestCases/WithoutCloneBefore/testNoCloneAfter/X.java");
		FileGroup changed = new FileGroup(files2);

		incrementalDetector.detect(changed, unchanged);

		assertEquals(0, existed.getAllClones().size());
		assertEquals(2, existed.getAllFiles().size());
	}

	@Test
	public void testAddingCloneEdittingOne() throws DataFileReadError,
			IOException {
		String[] oldFiles = { "Y.java" };
		String[] newFiles = { "X.java" };
		CloneDetectionResult result = setup("testAddingCloneEdittingOne",
				oldFiles, newFiles);

		List<String> files1 = new LinkedList<String>();
		files1.add("./TestCases/WithoutCloneBefore/Y.java");
		FileGroup unchanged = new FileGroup(files1);

		List<String> files2 = new LinkedList<String>();
		files2.add("./TestCases/WithoutCloneBefore/testAddingCloneEdittingOne/X.java");
		FileGroup changed = new FileGroup(files2);

		incrementalDetector.detect(changed, unchanged);

		assertEquals(result.getCloneClasses().size(), existed
				.getAllCloneClasses().size());
		assertEquals(result.getClones().size(), existed.getAllClones().size());

		for (CloneImpl c : result.getClones()) {
			assertTrue(existed.isExist(c, comparator));
		}
	}

	@Test
	public void testAddingCloneEdittingTwo() throws DataFileReadError,
			IOException {
		String[] oldFiles = {};
		String[] newFiles = { "X.java", "Y.java" };
		CloneDetectionResult result = setup("testAddingCloneEdittingTwo",
				oldFiles, newFiles);

		List<String> files1 = new LinkedList<String>();
		files1.add("./TestCases/WithoutCloneBefore/testAddingCloneEdittingTwo/X.java");
		files1.add("./TestCases/WithoutCloneBefore/testAddingCloneEdittingTwo/Y.java");
		FileGroup changed = new FileGroup(files1);

		incrementalDetector.detect(changed, null);

		assertEquals(result.getCloneClasses().size(), existed
				.getAllCloneClasses().size());
		assertEquals(result.getClones().size(), existed.getAllClones().size());

		for (CloneImpl c : result.getClones()) {
			assertTrue(existed.isExist(c, comparator));
		}
	}

	@Test
	public void testAddingCloneAddingOne() throws DataFileReadError,
			IOException {
		String[] oldFiles = { "X.java", "Y.java" };
		String[] newFiles = { "Z.java" };
		CloneDetectionResult result = setup("testAddingCloneAddingOne",
				oldFiles, newFiles);

		existed.addFile("Z.java");

		List<String> files1 = new LinkedList<String>();
		files1.add("./TestCases/WithoutCloneBefore/X.java");
		files1.add("./TestCases/WithoutCloneBefore/Y.java");
		FileGroup unchanged = new FileGroup(files1);

		List<String> files2 = new LinkedList<String>();
		files2.add("./TestCases/WithoutCloneBefore/testAddingCloneAddingOne/Z.java");
		FileGroup changed = new FileGroup(files2);

		incrementalDetector.detect(changed, unchanged);

		assertEquals(result.getCloneClasses().size(), existed
				.getAllCloneClasses().size());
		assertEquals(result.getClones().size(), existed.getAllClones().size());

		for (CloneImpl c : result.getClones()) {
			assertTrue(existed.isExist(c, comparator));
		}
	}

	@After
	public void cleanup() {
		java.io.File dir = new java.io.File("./");
		if (dir.isDirectory()) {
			java.io.File[] files = dir.listFiles();
			for (java.io.File f : files) {
				String name = f.getName();
				if (name.endsWith(".ccfxd") || name.endsWith(".tmp")
						|| name.endsWith(".ccfxprep"))
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
