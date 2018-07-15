package cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import com.sun.org.apache.bcel.internal.generic.NEW;

import utility.PrepReader;
import utility.PrepReaderError;
import utility.PrepToken;

import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.CloneDetectionResult;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneClassImpl;

import model.CcfxDetectionOptions;
import model.ClonePair;
import model.CloneSet;
import model.DataFileReadError;
import model.Model;
import model.SourceFile;

public class CCFinderDataDumperThread implements Runnable{
	private Model model = new Model();
	private Queue<CloneDetectionResult> results;
	private int threshold = 500;
	private CCFinderCloneDetector2 detector;
	private Lock lock;
	private Condition notEmpty;
	private String dataFile;
	
	public CCFinderDataDumperThread(String data, Queue<CloneDetectionResult> results, CCFinderCloneDetector2 ccFinderCloneDetector, Lock lock, Condition notEmpty) throws DataFileReadError, IOException {
		dataFile = data;
		model.readCloneDataFile(dataFile);
		this.results = results;
		this.detector = ccFinderCloneDetector;
		this.lock = lock;
		this.notEmpty = notEmpty;
	}

	public Queue<CloneDetectionResult> getResults() {
		return results;
	}

	public CCFinderDataDumperThread setThreshold(int threshold) {
		this.threshold = threshold;
		return this;
	}

	public void dump() {
		System.out.println();
		System.out.println("Dumping...");

		CloneDetectionResult result = new CloneDetectionResult();
		long num = model.getCloneSetCount();
		System.out.println("num : " + num);
		CloneSet[] cloneSets = model.getCloneSets((int) num);
		int i =0;
		int number = 0;
		
		for (CloneSet cs : cloneSets) {
			CloneClassImpl cloneClass = new CloneClassImpl();
			cloneClass.setId(-1);
			ClonePair[] cps = model.getClonePairsOfCloneSets(new long[] { cs.id });
			System.out.format("NO.%d (id : %d) clone set : %n", (++i), cs.id);
			int j =0;
//			int singleThreshold = 0;//todo: to run fast, break advance
			for (ClonePair cp : cps) {
				number++;
//				singleThreshold++;
//				if (singleThreshold > threshold) {
//					break;
//				}
				System.out.println("	NO."+(++j)+" clone pair!");
				CloneImpl clone = new CloneImpl();
				clone.setId(-1);
				clone.setStartIndex(getLine(cp.leftFile, getStartIndex(cp.leftFile, cp.leftBegin)));
				clone.setEndIndex(getLine(cp.leftFile, getEndIndex(cp.leftFile, cp.leftEnd)));
				clone.setFragment(getFragment(cp.leftFile, getStartIndex(cp.leftFile, cp.leftBegin),
						getEndIndex(cp.leftFile, cp.leftEnd)));
				clone.setFile(model.getFile(cp.leftFile).path);
				cloneClass.addClone(clone);
			}
			// set the cloneSize for each clone
			int size = cloneClass.getClones().size();
			for (CloneImpl clone : cloneClass.getClones()) {
				clone.setCloneSize(size);
			}
			result.addCloneClass(cloneClass);
			if(number >= threshold){ // 当存储的clonepair个数超过threshold个数，唤醒处理clonepair的线程
				results.add(result);
				result= new CloneDetectionResult();
				number = 0;
				wakeup();
			}
		}
		results.add(result);
		detector.setIfFinished(true);
		wakeup();
		new File(dataFile).delete();
		System.out.println("Dump finished.");
		System.out.println("---------------------------------");
		System.out.println();
	}

	private void wakeup() {
		lock.lock();
		notEmpty.signal();
		lock.unlock();
	}

	private int getStartIndex(int localFileId, int begin) {
		SourceFile file = model.getFile(localFileId);
		PrepToken[] tokens = getTokens(file.path);

		return tokens[begin].beginIndex;
	}

	private int getEndIndex(int localFileId, int end) {
		SourceFile file = model.getFile(localFileId);
		PrepToken[] tokens = getTokens(file.path);

		return tokens[end].endIndex;
	}

	private PrepToken[] getTokens(String path) {
		PrepToken[] tokens;
		CcfxDetectionOptions options = model.getDetectionOption();
		String postfix = options.getPostfix();
		String[] prepDirs = options.get("n");
		if (postfix == null)
			postfix = "." + model.getPreprocessScript() + ".ccfxprep";

		String prepFilePath = toPrepFilePath(path, prepDirs);

		try {
			tokens = (new PrepReader()).read(prepFilePath, postfix);
		} catch (PrepReaderError e) {
			tokens = null;
		} catch (IOException e) {
			tokens = null;
		}

		return tokens;
	}

	private String toPrepFilePath(String path, String[] prepDirs) {
		for (int i = 0; i < prepDirs.length; i++) {
			String prepDir = prepDirs[i];
			if (path.startsWith(prepDir)) {
				if (path.length() > prepDir.length() && path.charAt(prepDir.length()) == File.separatorChar) {
					return prepDir + File.separator + ".ccfxprepdir" + path.substring(prepDir.length());
				}
			}
		}

		return path;
	}

	protected String getFragment(int localFileId, int startIndex, int endIndex) {
		SourceFile file = model.getFile(localFileId);

		int len = endIndex - startIndex;
		char[] buffer = new char[len];

		try {
			FileReader fr = new FileReader(file.path);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			// do {
			// br.mark(999999);
			// line = br.readLine();
			// } while (!line .startsWith("public class"));
			// br.reset();
			br.skip(startIndex);
			br.read(buffer, 0, len);
			fr.close();
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String code = new String(buffer);
		//System.out.println("The code is :\n" +code +"\nend");
		return code;
	}

	public int getLine(int localFileId, int index) {
		SourceFile file = model.getFile(localFileId);
		char[] buffer = new char[index];
		try {
			FileReader fr = new FileReader(file.path);
			BufferedReader br = new BufferedReader(fr);
			br.mark(999999);
			br.read(buffer, 0, index);
			fr.close();
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] lineStart = new String(buffer).split("\n");
		int line = lineStart.length;
		if (buffer.length > 0 && buffer[buffer.length - 1] == '\n') {
			line++;
		}
		//System.out.println(line);
		return line;
	}

	@Override
	public void run() {
		dump();
		
	}
}
