package cn.edu.fudan.se.clonedetector.display.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import main.TestDao;

import org.tmatesoft.svn.core.SVNException;

import com.itextpdf.text.xml.xmp.PdfA1Schema;

import cn.edu.fudan.se.clonedetector.dao.hibernate.HibernateDao;
import cn.edu.fudan.se.clonedetector.differ.lineDiffImp.Diff2Version;
import cn.edu.fudan.se.clonedetector.versioncontroll.DataExtractor;
import cn.edu.fudan.se.clonedetector.versioncontroll.svn.SVNExtractor;

public class DiffExtractService extends AbstractService {
	public void extractDiff(int preCommitId, int commitId, String compareDate) {
		System.out.println("get in the diff extract function");
		Date pD = String2Date(compareDate);
		Runnable runnable = new Runnable() {
			public void run() {
				Diff2Version diff = new Diff2Version();
				diff.changeOf2Version(preCommitId, commitId, dao);
			}
		};
		long timeSpan = pD.getTime() - new Date().getTime();
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.schedule(runnable, timeSpan, TimeUnit.MILLISECONDS);

	}

	public Date String2Date(String processDate) {
		java.text.SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		String s = processDate;
		Date date = null;
		try {
			date = formatter.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(date);
		return date;
	}

}
