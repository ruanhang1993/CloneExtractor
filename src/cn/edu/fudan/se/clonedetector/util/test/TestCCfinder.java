package cn.edu.fudan.se.clonedetector.util.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.CloneDetectionResult;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.exception.FileWrongEncodingException;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl.CCFinderCloneDetector;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl.CCFinderCloneDetector2;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneClassImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.evolution.EvolutionAnalyse;
import cn.edu.fudan.se.clonedetector.util.FileProcesser;

public class TestCCfinder {
	
	public static void main(String[] args) {
		new TestCCfinder().testCloneAsDataExtractor2_WN("E:\\TestSVN\\MySampleCode\\v3.10", "cpp");
		new TestCCfinder().testCloneAsDataExtractor2_WN("E:\\TestSVN\\MySampleCode\\v3.10", "java");
		new TestCCfinder().testCloneAsDataExtractor("E:\\TestSVN\\MySampleCode\\v3.10", "cpp");
		new TestCCfinder().testCloneAsDataExtractor("E:\\TestSVN\\MySampleCode\\v3.10", "java");
	}

//	private static void testCloneAsDataExtractor2(String location, String language) {
//		CCFinderCloneDetector2 detector = new CCFinderCloneDetector2();
//		detector.setType(language);
//		detector.setMinFragLen(50);
//		BlockingQueue<CloneDetectionResult> results = new LinkedBlockingQueue<CloneDetectionResult>();
//
//
//		String cloneFile = "";
//		boolean detectClone = false;
//		while(!detectClone){
//			try {
//				cloneFile = detector.findClone(location);
//				detectClone = true;
//			} catch (Exception e) {
//				if (e instanceof FileWrongEncodingException) {
//					String wrongFile = "";
//					if (wrongFile.equals(e.getMessage())) {
//						String pathname = wrongFile+".del";
//						new java.io.File(wrongFile).renameTo(new java.io.File(pathname));
//					}else {
//						wrongFile = e.getMessage();
//						String pathname = wrongFile+".rn";
//						new java.io.File(wrongFile).renameTo(new java.io.File(pathname));
//						try {
//							FileProcesser.transformFileEncoding(pathname, wrongFile, "utf-16", "utf-8");
//						} catch (IOException e1) {
//							e1.printStackTrace();
//						}				
//					}
//				}
//			}
//		}
//		detector.detectClone(cloneFile, results,5);
//		int i =1;
//		while(!detector.isIfFinished() || results.size() > 0){
//			CloneDetectionResult result = results.poll();
//			if (result != null) {
//
//				for (CloneClassImpl cci : result.getCloneClasses()) {
//					// storage all cloneclasses
//					System.err.format("retieve CloneClass %d --------------- %n", i++);
//					for (CloneImpl ci : cci.getClones()) {
//						System.out.println((ci.getFile()));
//						int beginLine = ci.getStartIndex();
//						int endLine = ci.getEndIndex();
//						EvolutionAnalyse ea = new EvolutionAnalyse(ci.getFile(),location);
//						int[] ecloc = ea.countIncompleteLine(beginLine, endLine);
//						System.out.println(ecloc);
//						ea = null;
//						System.gc();
//						// System.out.println("clone " + clone.getCloneId() + " inserted
//						// succeffully");
//					}
//				}
//			}
//		}		
//		
//	}
	

	private void testCloneAsDataExtractor2_WN(String location, String language) {
		CCFinderCloneDetector2 detector = new CCFinderCloneDetector2();
		detector.setType(language);
		detector.setMinFragLen(50);
		BlockingQueue<CloneDetectionResult> results = new LinkedBlockingQueue<CloneDetectionResult>();

	    Lock lock = new ReentrantLock();
	    Condition notEmpty = lock.newCondition();

		String cloneFile = "";
		boolean detectClone = false;
		while(!detectClone){
			try {
				cloneFile = detector.findCloneFromDir(location);
				detectClone = true;
			} catch (Exception e) {
				if (e instanceof FileWrongEncodingException) {
					String wrongFile = "";
					if (wrongFile.equals(e.getMessage())) {
						String pathname = wrongFile+".del";
						new java.io.File(wrongFile).renameTo(new java.io.File(pathname));
					}else {
						wrongFile = e.getMessage();
						String pathname = wrongFile+".rn";
						new java.io.File(wrongFile).renameTo(new java.io.File(pathname));
						try {
							FileProcesser.transformFileEncoding(pathname, wrongFile, "utf-16", "utf-8");
						} catch (IOException e1) {
							e1.printStackTrace();
						}				
					}
				}
			}
		}
		detector.detectClone(cloneFile, results, lock, notEmpty, 100);
		int record =1;
		while(!detector.isIfFinished() ||  results.size() > 0){
            lock.lock();
            CloneDetectionResult result = null;
            try {
                while(results.size() == 0){
                    try {
                        System.out.println("队列空，等待数据");
                        notEmpty.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
    			result = results.poll();				
			} finally {
				lock.unlock();
			}
            record = saveCloneDetectionResult(result, location, record);
		}		
	}
	
	private void testCloneAsDataExtractor(String location, String language) {
		
		CCFinderCloneDetector detector = new CCFinderCloneDetector();
		detector.setType(language);
		detector.setMinFragLen(50);
		boolean detectClone = false;

        CloneDetectionResult result = null;
		while(!detectClone){
			try {
				result = detector.detectClone(location);
				detectClone = true;
			} catch (Exception e) {
				if (e instanceof FileWrongEncodingException) {
					String wrongFile = "";
					if (wrongFile.equals(e.getMessage())) {
						String pathname = wrongFile+".del";
						new java.io.File(wrongFile).renameTo(new java.io.File(pathname));
					}else {
						wrongFile = e.getMessage();
						String pathname = wrongFile+".rn";
						new java.io.File(wrongFile).renameTo(new java.io.File(pathname));
						try {
							FileProcesser.transformFileEncoding(pathname, wrongFile, "utf-16", "utf-8");
						} catch (IOException e1) {
							e1.printStackTrace();
						}				
					}
				}
			}
		}
		int record = 0;
		saveCloneDetectionResult(result, location, record);
	}
	

private  int saveCloneDetectionResult(CloneDetectionResult result, String location, int record) {

	if (result != null) {
		int cloneImplN = 0;
		for (CloneClassImpl cci : result.getCloneClasses()) {
			// storage all cloneclasses
			System.err.format("retieve CloneClass %d --------------- %n", record++);
			for (CloneImpl ci : cci.getClones()) {
				cloneImplN++;
				System.out.println((ci.getFile()));
				int beginLine = ci.getStartIndex();
				int endLine = ci.getEndIndex();
				EvolutionAnalyse ea = new EvolutionAnalyse(ci.getFile(),location);
				int[] ecloc = ea.countIncompleteLine(beginLine, endLine);
				System.out.println(ecloc);
				ea = null;
				System.gc();
			}
		}
		System.out.println(cloneImplN+"===========================================================");
	}
	return record;
		
	}
	
    private static String generateOutputName(String fileName1) {
    	int index1 = fileName1.lastIndexOf("\\");
    	String output1 = fileName1.substring(0, index1+1)+fileName1.substring(index1)+".cs";
    	return output1;
	}

	public static void readFileByBytes(String fileName1, int mode) {
    	String output1 = generateOutputName(fileName1);
        File file1 = new File(fileName1);
        InputStream in1 = null;
        FileOutputStream out1 = null;
        int sum1 = 0, count1 = 0;
        try {
			out1 = new FileOutputStream(new File(output1));
            in1 = new FileInputStream(file1);
            int tempbyte1;
            while ((tempbyte1 = in1.read()) != -1) {
//                System.out.format("%d %s%n", tempbyte1, " ");
                sum1++;
                if (tempbyte1 != 0) {
                	count1++;
                	if (mode == 0) {
                        out1.write((tempbyte1+"\n").getBytes());
    				}else {
                        out1.write(tempbyte1);
    				}
				}
            }
            System.out.format("%ncount : %d sum : %d %n", count1, sum1);
            in1.close();
	        out1.close();   
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
