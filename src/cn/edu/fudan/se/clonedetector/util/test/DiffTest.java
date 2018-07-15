package cn.edu.fudan.se.clonedetector.util.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cn.edu.fudan.se.clonedetector.bean.File;
import cn.edu.fudan.se.clonedetector.dao.IDataAccessor;
import cn.edu.fudan.se.clonedetector.dao.hibernate.HibernateDao;
import cn.edu.fudan.se.clonedetector.differ.ChangeSaveNotify;
import cn.edu.fudan.se.clonedetector.differ.IDifferDetector;
import cn.edu.fudan.se.clonedetector.differ.IDifferOutcomeNotify;
import cn.edu.fudan.se.clonedetector.differ.lineDiffImp.LineDiffDetectorImp;

public class DiffTest {

	private File left;
	private File right;

	private IDifferDetector detector;

	@Before
	public void prepareFile() {
		String leftContent = readFile("HadoopStoreBuilderReducerLeft.java");
		String rightContent = readFile("HadoopStoreBuilderReducerRight.java");

		left = new File();
		left.setFileName("haha");
		left.setFileId(10);
		left.setContent(leftContent.getBytes());
		left.setCommitId(1);

		right = new File();
		right.setFileName("haha");
		right.setFileId(11);
		right.setContent(rightContent.getBytes());
		right.setCommitId(2);

		detector = new LineDiffDetectorImp();
		IDifferOutcomeNotify notify = new ChangeSaveNotify();
		detector.addOutcomeNotify(notify);

	}

	@Test
	public void testDiff() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		IDataAccessor dao = (HibernateDao)Class.forName("cn.edu.fudan.se.clonedetector.dao.hibernate.HibernateDao").newInstance();
		System.out.println("test diff");
		LinkedList<File> leftFileList = new LinkedList<File>();
		leftFileList.add(left);
		LinkedList<File> rightFileList = new LinkedList<File>();
		rightFileList.add(right);
		detector.detectDiffer(leftFileList, rightFileList, 2, 1, dao);
	}

	@Test
	public void testMatch() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		IDataAccessor dao = (HibernateDao)Class.forName("cn.edu.fudan.se.clonedetector.dao.hibernate.HibernateDao").newInstance();
		System.out.println("test match");
		LinkedList<File> leftFileList = new LinkedList<File>();
		leftFileList.add(left);
		LinkedList<File> rightFileList = new LinkedList<File>();
		rightFileList.add(right);

		File leftDismatchFile = new File();
		String leftContent = this.readFile("HadoopStoreBuilderReducerLeft.java");
		leftDismatchFile.setCommitId(2);
		leftDismatchFile.setFileId(2);
		leftDismatchFile.setContent(leftContent.getBytes());
		leftDismatchFile.setFileName("leftFile");
		File rightDismatchFile = new File();
		String rightContent = this.readFile("HadoopStoreBuilderReducerRight.java");
		rightDismatchFile.setCommitId(2);
		rightDismatchFile.setFileId(2);
		rightDismatchFile.setContent(rightContent.getBytes());
		rightDismatchFile.setFileName("rightFile");

		leftFileList.add(leftDismatchFile);
		rightFileList.add(rightDismatchFile);

		detector.detectDiffer(leftFileList, rightFileList, 2, 1, dao);
	}

	private String readFile(String filePath) {
		String ret = "";
		try {
			FileInputStream fis = new FileInputStream(filePath);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String s = "";
			while ((s = br.readLine()) != null) {
				ret += s + "\n";
			}

			br.close();

		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;

	}
}
