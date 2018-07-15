package main;

import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.CloneDetectionResult;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl.CCFinderCloneDetector;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneImpl;
import cn.edu.fudan.se.clonedetector.dao.IDataAccessor;
import cn.edu.fudan.se.clonedetector.dao.hibernate.HibernateDao;
import cn.edu.fudan.se.clonedetector.versioncontroll.DataExtractor;
import cn.edu.fudan.se.clonedetector.versioncontroll.Type;
import cn.edu.fudan.se.clonedetector.versioncontroll.svn.SVNExtractor;

public class TestGit {
	public static void main(String[] args) throws Exception {
//		 CCFinderCloneDetector detector = new CCFinderCloneDetector();
//		 detector.setType("java");
//		 detector.setMinFragLen(10);
//		 CloneDetectionResult result =
//		 detector.detectClone("D:\\Tool\\CCFX\\code\\bean");
//		 for(CloneImpl clone : result.getClones()){
//		 System.out.println(clone.getFile() + " " + clone.getCloneSize());
//		 }
//		 //delete the unused file
//		 java.io.File dir = new java.io.File("./");
//		 if (dir.isDirectory()) {
//		 java.io.File[] files = dir.listFiles();
//		 for (java.io.File f: files) {
//		 String name = f.getName();
//		 if (name.endsWith(".ccfxd")
//		 || name.endsWith(".tmp")
//		 || name.endsWith(".ccfxprep"))
//		 f.delete();
//		 }
//		 }
//		String test = "D:\\hello\\hello\\world";
//		System.out.println(test);
//		String r = test.replaceAll("\\\\", "\\\\\\\\");
////		System.out.println(r);
		String svnpath= "svn://svn.code.sf.net/p/jhotdraw/svn/trunk";
		IDataAccessor dao = (HibernateDao)Class.forName("cn.edu.fudan.se.clonedetector.dao.hibernate.HibernateDao").newInstance();
		SVNExtractor svnE = new SVNExtractor(dao, "E:\\TestForSVN");
		svnE.checkout(svnpath, "E:\\TestForSVN", false, Type.ALL_TYPE);
	}

}
