package cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import com.sun.org.apache.bcel.internal.generic.NEW;

import model.DataFileReadError;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.CloneDetectionResult;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.ICloneDetector;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.exception.FileWrongEncodingException;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.FileGroup;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.util.CCFinder;

public class CCFinderCloneDetector2 implements ICloneDetector {
	private String type;
	private int len;
	private static AtomicLong value = new AtomicLong(0);
	private volatile boolean ifFinished = false;

	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

	public void setMinFragLen(int len) {
		this.len = len;
	}

	@Override
	public CloneDetectionResult detectClonesBetweenGroups(FileGroup changedFiles, FileGroup unchangedFiles) throws Exception {
		String dataFile = "./between.ccfxd";
		CCFinder ccfx = new CCFinder();
		ccfx.setType(type).setGroups(changedFiles, unchangedFiles).setRangeOptions(false, false, true)
				.setDataFile(dataFile).setMinimumCloneLength(len);

		ccfx.run();

		CloneDetectionResult result = null;
		try {
			CCFinderDataDumper dumper = new CCFinderDataDumper(dataFile);
			result = dumper.dump();
		} catch (DataFileReadError | IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public CloneDetectionResult detectClonesInnerGroup(FileGroup changedFiles) throws Exception {
		String dataFile = "./inner.ccfxd";
		CCFinder ccfx = new CCFinder();
		ccfx.setType(type).setFiles(changedFiles).setDataFile(dataFile).setMinimumCloneLength(len);

		ccfx.run();

		CloneDetectionResult result = null;
		try {
			CCFinderDataDumper dumper = new CCFinderDataDumper(dataFile);
			result = dumper.dump();
		} catch (DataFileReadError | IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public String findCloneFromDir(String path) throws Exception{
		String dataFile = getUniqueCcOutPath(path);

		CCFinder ccfx = new CCFinder();
		ccfx.setType(type).setDataFile(dataFile).setInputDir(path, true).setMinimumCloneLength(len);

		ccfx.run();
		return dataFile;
	}
	
	public String findCloneFromFileList(String path, String fileList) throws Exception{
		String dataFile = getUniqueCcOutPath(path);

		CCFinder ccfx = new CCFinder();
		ccfx.setType(type).setDataFile(dataFile).setFileList(fileList).setMinimumCloneLength(len);

		ccfx.run();
		return dataFile;
	}
	
	@Deprecated
	public CloneDetectionResult detectClone(String dataFile){
		CloneDetectionResult result = null;
		try {
			FileReader fr = new FileReader(dataFile);
			try {
				fr.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				CCFinderDataDumper dumper = new CCFinderDataDumper(dataFile);
				result = dumper.dump();
			} catch (DataFileReadError | IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			// e1.printStackTrace();
			System.err.println("cannot find out.ccfxd");
		}

		return result;
	}
	

	public void detectClone(String dataFile, Queue<CloneDetectionResult> results, Lock lock, Condition notEmpty){
		detectClone(dataFile, results, lock, notEmpty,500);
	}
	
	public void detectClone(String dataFile, Queue<CloneDetectionResult> results, Lock lock, Condition notEmpty, int threshold){
		if (!new File(dataFile).exists()) {
			System.err.println("cannot find out.ccfxd");
			setIfFinished(true);
			lock.lock();
			notEmpty.signal();
			lock.unlock();
			return ;
		}
			
		try {
			new Thread(new CCFinderDataDumperThread(dataFile, results, this, lock, notEmpty).setThreshold(threshold)).start();
		} catch (DataFileReadError | IOException e) {
			e.printStackTrace();
		}

	}

	@Deprecated
	/**
	 * 这个out.ccfxd会出现在eclipse根目录下，由于ccfinder当没有生成物时不会删除已有的生成物，
	 * 所以重启eclipse可能造成新项目读了旧项目的ccfxd
	 * @return
	 */
	private String getUniqueCcOutPath() {
		long v; 
		do { 
			v = value.get(); 
		}while (!value.compareAndSet(v, v + 1));
		
		return "./out"+v+".ccfxd";
	}
	
	/**
	 * 一个path任何时刻最多只可以出现一个ccfxd，注意不要对一个项目同时进行多个扫描
	 * @param path
	 * @return
	 */
	private String getUniqueCcOutPath(String path) {
		String out = path+"/out.ccfxd";
		new File(out).delete();		
		java.io.File dir = new java.io.File(path);
		if (dir.isDirectory()) {
			java.io.File[] files = dir.listFiles();
			for (java.io.File f : files) {
				String name = f.getName();
				if (name.endsWith(".ccfxd") || name.endsWith(".tmp") || name.endsWith(".ccfxprep")) {
					f.delete();
				}

			}
		}
		return out;
	}
	

	public boolean isIfFinished() {
		return ifFinished;
	}
	
	public void setIfFinished(boolean bl) {
		ifFinished = bl;
	}


}
