package cn.edu.fudan.se.clonedetector.versioncontroll.cc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Multiset.Entry;
import com.ibm.icu.text.StringTransform;

import cn.edu.fudan.se.clonedetector.bean.CloneClass;
import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.CloneDetectionResult;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.impl.CCFinderCloneDetector;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneClassImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.CloneImpl;
import cn.edu.fudan.se.clonedetector.ccfinder.evolution.EvolutionAnalyse;

public class Test {

	public static void main(String[] args) {
		Date startDate = null;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		try {
			startDate = sdf.parse("2016-06-00 00:00:00");			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		testCompare("NGCNYTS_Front","NGCNYTS_Front_ST","NGCNYTS_PVOB",".", System.out, startDate);
	}
	
	private static void testCompare(String component, String stream, String pvob, String wd, PrintStream out,Date startDate) {		
		String fmtSeperator = " ";
		HashMap<String, String> hashMap = new HashMap<>();// (vision, timeLong)
		List<Commit> commits = new ArrayList<Commit>();
		String fmt = "\"%n"+fmtSeperator+"%d"+fmtSeperator+"%u"+"\\n\"";
		String cmd = String.format("%s lsbl -fmt %s -component %s -stream %s", 
				"cleartool", fmt, component+"@\\"+pvob, stream+"@\\"+pvob);//会按时间顺序由旧到新排序
		try {
			out.println("PROC> "+cmd);
			Process process = Runtime.getRuntime().exec(cmd, null, new File(wd));
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String s = "";
			String blName = "",  blTime = "";
			while((s = br.readLine()) != null){
				String[] blInfos = s.split(fmtSeperator);
				blName = blInfos[0];
				blTime = blInfos[1];
				Date time = parseTime(blTime);
				if (startDate!=null && time.after(startDate)) {
					String vision = pickVisionFromBL(blName);
					if (vision != null) {
						hashMap.put(vision, s);
					}
					out.format("vision : %s, name : %s, time : %s %n", vision, blName, blTime);
				}
			}
			
			Iterator it = hashMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String vision = (String) entry.getKey();
				String blInfo = (String) entry.getValue();
				Commit commit = new Commit();
				Date time = parseTime( blInfo.split(fmtSeperator)[1]);
				commit.setCommitDate(time);
				commit.setCommitCode(vision);
				commits.add(commit);
			}
			commits.sort(new Comparator<Commit>(){
				public int compare(Commit c1, Commit c2){
					return c1.getCommitDate().compareTo(c2.getCommitDate());
				}
			});
			for (Commit c : commits) {
		     	System.out.format("add commits time : %s , version : %s %n", c.getCommitDate(), c.getSelfVersionId());
			}
			process.waitFor();
			process.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	private static String pickVisionFromBL(String blName) {
		String pattern = "((\\d+)\\.)+(\\d+)*";
	    Pattern p1 = Pattern.compile(pattern);
	    Matcher m = p1.matcher(blName);
	    if (m.find()){
	    	String vision = m.group();
	    	vision = vision.endsWith(".") ? vision.substring(0, vision.length()-1) : vision;
	    	return vision;
	    }
	    return null;		
	}
	
	private void testRegex() {		
		regex("((\\d+)\\.)+(\\w)+", "ST-2016-09-29_2.6.0.1.UAT2.2");
		regex("((\\d+)\\.)+(\\w)+", "ST-2016-09-29_2.6.0.1");
		System.out.println("===================");
		regex("((\\d+)\\.)+(\\d+)*", "ST-2016-09-29_2.6.0.1.UAT2.2");
		regex("((\\d+)\\.)+(\\d+)*", "ST-2016-09-29_2.6.0.1");
	}
	
	private void testTime() {		
		String string = "2011-11-17T10:49:09+08:00";
		Date date1 = parseTime(string);
		Date date2 = parseTime("2016-06-00 00:00:00+");	
		Date date3 = parseTime("2016-07-00 00:00:00+");	
		if (date1.before(date2)) {
			System.out.println(date1.getTime());
		}
		if (date2.before(date3)) {
			System.out.println(date3.getTime());
		}
	}
	
	private static Date parseTime(String blTime) {
		blTime = blTime.replace("T", " ");
		blTime = blTime.substring(0, blTime.indexOf('+'));
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = null;

		
		try {
			date = sdf.parse(blTime);
//			System.out.println(date.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}
	 
	 public static void regex(String pattern, String input) {
       Pattern p1 = Pattern.compile(pattern);
       // get a matcher object
       Matcher m = p1.matcher(input);
       if (m.find()){
           System.out.println(m.group());
           String vision = m.group();
           vision = vision.endsWith(".") ? vision.substring(0, vision.length()-1) : vision;
           System.out.format("vision : %s%n", vision);
       }
       System.out.format("%s ------------%n%n",input);
	}
	    
	 private static void testGetBL() {
//			CCExtractor ccExtractor = new CCExtractor();
//			ccExtractor.getBL("Test", "Test_UAT", "PV_Test",".", System.out);
		}
		
		private static void testRebase(){
//			CCExtractor ccExtractor = new CCExtractor(null, null);
//			ccExtractor.rebase("Test_Pro_2011-11-17-2", "codescanner_Test_DEV1_1","Test_DEV1", "PV_Test",".", System.out);
			rebase("codescanner_Test_Pro_2016_11_1", "codescanner_Test_DEV1_1","Test_DEV1", "PV_Test",".", System.out);
		}
		
		public static void rebase(String bl,String view, String stream, String pvob, String wd, PrintStream out) {
			String cmd = String.format("%s rebase -baseline %s -view %s -stream %s -complete", 
					"cleartool", bl+"@\\"+pvob, view, stream+"@\\"+pvob);
			try {
				out.println("PROC> "+cmd);
				Process process = Runtime.getRuntime().exec(cmd, null, new File(wd));
				InputStream is = process.getInputStream();
				InputStreamReader isr = new InputStreamReader(is, "utf-8");
				BufferedReader br = new BufferedReader(isr);
				String s = "";
				while((s = br.readLine()) != null){
					out.println(s);
				}
				process.waitFor();
				process.destroy();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private static void testUpdate(){
//			CCExtractor ccExtractor = new CCExtractor();
//			ccExtractor.update("D:\\TestCC\\codescanner_TestDEV1_2", System.out);
		}
		
		private static void test2FS(){
			HashMap<String, String> map1 = new HashMap<String, String>();
			HashMap<String, String> map2 = new HashMap<String, String>();
			String path = "M:\\codescanner_Test_DEV1_init";//"M:\\xionglu_CMDS_Report_Document_int\\DPS\\DPS_SRC\\Java\\dps-ui\\src\\com\\cfets\\framework";
			testFSFile(path, "D:\\TestCC\\files3.txt", map1); 
			System.out.println("---------------------------------------------");
			path = "M:\\codescanner_Test_DEV1";
			testFSFile(path, "D:\\TestCC\\files4.txt", map2);
			
			for (String key : map2.keySet()) {
				if (!map1.containsKey(key)) {
					System.err.format("map1 contains no file : %s %s %n",key, map2.get(key));
				}else if(!map1.get(key).equals(map2.get(key))){
					System.err.format("map1 map2 conflict : %s %s %s %n",key, map1.get(key), map2.get(key));
				}
			}
			for (String key : map1.keySet()) {
				if (!map2.containsKey(key)) {
					System.err.format("map2 contains no file : %s %s %n",key, map2.get(key));
				}else if(!map1.get(key).equals(map2.get(key))){
					System.err.format("map1 map2 conflict : %s %s %s %n",key, map1.get(key), map2.get(key));
				}
			}
		}
		
		private static void testFSFile(String path, String log, HashMap<String,String> map){
			File file = new File(log);
			FileOutputStream ps = null;
			try {
				ps = new FileOutputStream(file);
				showFSFiles(new File(path), ps, map, path.length());
				ps.flush();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				if (ps != null) {
					try {
						ps.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		private static void showFSFiles(File destPath) {
			if (destPath.isFile()) {
				System.out.println(new Date(destPath.lastModified()).toString()+" "+destPath.getPath());
			}else {
				File[] children = destPath.listFiles();
				for (File file : children) {
					showFSFiles(file);
				}
			}
			
		}
		static int i =0;
		private static void showFSFiles(File destPath, OutputStream out, HashMap<String,String> map, int prefix) {
			String s= new Date(destPath.lastModified()).toString()+" "+destPath.getPath()+"\r\n";
			System.out.print((i++)+s);
			String key = destPath.getPath().substring(prefix);
			if (map.containsKey(key)) {
				System.err.println(key);
			}
			map.put(key, new Date(destPath.lastModified()).toString());
			if (destPath.isFile()) {
				try {
					
					out.write(s.getBytes());
					out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				File[] children = destPath.listFiles();
				for (File file : children) {
					showFSFiles(file, out, map, prefix);
				}
			}
			
		}
		
}
