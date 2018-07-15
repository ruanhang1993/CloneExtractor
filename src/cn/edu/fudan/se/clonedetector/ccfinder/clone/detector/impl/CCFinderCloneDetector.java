package cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.sun.org.apache.bcel.internal.generic.NEW;

import model.DataFileReadError;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.CloneDetectionResult;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.ICloneDetector;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.exception.FileWrongEncodingException;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.FileGroup;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.util.CCFinder;

public class CCFinderCloneDetector implements ICloneDetector {
	private String type;
	private int len;
	private static AtomicLong value = new AtomicLong(0);

	public void setType(String type) {
		this.type = type;
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

	@Override
	public CloneDetectionResult detectClone(String path) throws Exception{
		String dataFile = getUniqueCcOutPath(path);

		CCFinder ccfx = new CCFinder();
		ccfx.setType(type).setDataFile(dataFile).setInputDir(path, true).setMinimumCloneLength(len);

		ccfx.run();

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

	@Deprecated
	/**
	 * ���out.ccfxd�������eclipse��Ŀ¼�£�����ccfinder��û��������ʱ����ɾ�����е������
	 * ��������eclipse�����������Ŀ���˾���Ŀ��ccfxd
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
	 * һ��path�κ�ʱ�����ֻ���Գ���һ��ccfxd��ע�ⲻҪ��һ����Ŀͬʱ���ж��ɨ��
	 * @param path
	 * @return
	 */
	private String getUniqueCcOutPath(String path) {
		String out = path+"/out.ccfxd";
		new File(out).delete();		
		java.io.File dir = new java.io.File("./");
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


	public String findClone(String path) throws Exception{
		String dataFile = getUniqueCcOutPath(path);

		CCFinder ccfx = new CCFinder();
		ccfx.setType(type).setDataFile(dataFile).setInputDir(path, true).setMinimumCloneLength(len);

		ccfx.run();
		return dataFile;
	}
	
}
