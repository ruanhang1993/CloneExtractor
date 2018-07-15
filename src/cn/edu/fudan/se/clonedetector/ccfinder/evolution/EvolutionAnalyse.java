package cn.edu.fudan.se.clonedetector.ccfinder.evolution;

import java.io.*;
import java.util.*;

import cn.edu.fudan.se.clonedetector.ccfinder.util.Shell;
import cn.edu.fudan.se.clonedetector.util.FileProcesser;

public class EvolutionAnalyse {
	HashMap<String, String> lan2lan;

	public static void main(String[] args) throws Exception {
		EvolutionAnalyse ea = new EvolutionAnalyse("D:\\TestSVN\\kaoti\\11\\src\\com\\Exam\\student\\ExamPage.java", ".");
		ea.countIncompleteLineByString(1, 2, "hello\nworld\n//sdlflfj\n23124213\n/*fdslfsdjf\nsfdlj*/sfdfs\nfdsjlj\n");
		// System.out.println("ea.size = " + ea.countIncompleteLine(2, 7));
	}

	private String language = "java";
	private String path;
	private String baseDir;
	private final String LOG_FILE = "log.txt";
	List<String> commits = new ArrayList<String>();

	public EvolutionAnalyse(String path, String baseDir) {
		lan2lan = new HashMap<String, String>();
		lan2lan.put("java", "Java");
		lan2lan.put("cobol", "COBOL");
		lan2lan.put("csharp", "C#");
		if((!"".equals(path))&&(path!=null))
			this.language = path2language(path);
		else
			this.language = "Java";
		this.path = path;
		if (baseDir == null || baseDir.length()==0) {
			this.baseDir = ".";
		}else
			this.baseDir = baseDir;
	}
	
	public String path2language(String p){
		String temp = p;
		String[] array = temp.split("\\.");
		return array[array.length-1];
	}

	@Deprecated
	public void analyse() throws Exception {
		getLog();
		int numOfRevision = getRevisionCount();
		// System.out.println(numOfRevision);
		for (int i = 0; i < numOfRevision; i++) {
			countLine(i);
		}
	}

	private void getLog() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(LOG_FILE));
		while (true) {
			String line = br.readLine();
			if (line == null)
				break;
			if (line.startsWith("commit")) {
				commits.add(line.substring(7));
			}
		}

		br.close();
	}

	private int getRevisionCount() {
		return commits.size();
	}

	@Deprecated
	private void changeToRevision(int i) {
		String id = commits.get(i);
		Shell.invoke("git checkout " + id, path, System.out);
	}

	@Deprecated
	private void countLine(int i) throws Exception {
		changeToRevision(i);
		PrintStream out = new PrintStream(new File("cloc"));
		Shell.invoke("cloc " + path, ".", out);
		out.close();
		BufferedReader br = new BufferedReader(new FileReader("cloc"));
		boolean startCount = false;
		while (true) {
			String line = br.readLine();
			if (line == null)
				break;
			if (line.startsWith("Language")){
				line = br.readLine();
				startCount = true;
				continue;
			}
			if (startCount) {
				// System.out.println(line);
				StringTokenizer st = new StringTokenizer(line);
				st.nextToken();
				int numOfFile = Integer.parseInt(st.nextToken());
				st.nextToken();
				st.nextToken();
				int numOfCode = Integer.parseInt(st.nextToken());
				writeData(i, numOfFile, numOfCode);
				break;
			}
		}
		br.close();
	}

	private void writeData(int revision, int numOfFile, int numOfLine) throws Exception {
		System.out.println("writing data...");
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("data", true)));
		out.append(revision + "\t" + numOfFile + "\t" + numOfLine + "\n");
		out.close();
		System.out.println("data writed");
	}

	public int[] countLine() {
		String clocFile = baseDir+"/cloc";
		FileProcesser.createFileParent(new File(clocFile));
		return countLine(clocFile);
	}
	
	private int[] countLine(String clocFile) {
		int res[]= {0,0,0};
		PrintStream out;
		try {
			out = new PrintStream(new File(clocFile));
			Shell.invoke("cloc " + path, ".", out);
			out.close();
			BufferedReader br = new BufferedReader(new FileReader(clocFile));
			boolean startCount = false;
			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				if (line.startsWith("Language")){
					line = br.readLine();
					startCount = true;
					continue;
				}
				if (startCount) {
//					System.out.println(line);
					StringTokenizer st = new StringTokenizer(line);
					st.nextToken();//name of Language
					st.nextToken();//num of files
					res[0]= Integer.parseInt(st.nextToken());//lines of blank
					res[1]= Integer.parseInt(st.nextToken());//lines of comment
					res[2]= Integer.parseInt(st.nextToken());//code
					break;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public int[] countIncompleteLine(int beginLine, int endLine) {
		String fileCopy = baseDir+"newFile." + language;
		String clocFile = baseDir+"/cloc";
		FileProcesser.createFileParent(new File(fileCopy));
		FileProcesser.createFileParent(new File(clocFile));
		return countIncompleteLine(beginLine, endLine, fileCopy, clocFile);
	}
	
	private int[] countIncompleteLine(int beginLine, int endLine, String fileCopy, String clocFile) {
		int res[]= {0,0,0};
		FileReader reader;
		String copyContent = "";
		try {
			reader = new FileReader(path);
			BufferedReader br = new BufferedReader(reader);
			String str = null;
			int i = 0;
			while ((str = br.readLine()) != null) {
				i++;
				if (i < beginLine)
					continue;
				if (i == endLine)
					break;
				copyContent += str + "\n";

			}
			br.close();
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File f = new File(fileCopy);
		FileWriter fw;
		try {

			fw = new FileWriter(f);
			fw.write(copyContent);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintStream out;
		try {
			out = new PrintStream(new File(clocFile));
			Shell.invoke("cloc " + fileCopy, ".", out);
			out.close();
			BufferedReader br = new BufferedReader(new FileReader(clocFile));
			boolean startCount = false;
			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				if (line.startsWith("Language")){
					line = br.readLine();
					startCount = true;
					continue;
				}
				if (startCount) {
					// System.out.println(line);
					StringTokenizer st = new StringTokenizer(line);
					st.nextToken();//name of Language
					st.nextToken();//num of files
					res[0]= Integer.parseInt(st.nextToken());//lines of blank
					res[1]= Integer.parseInt(st.nextToken());//lines of comment
					res[2]= Integer.parseInt(st.nextToken());//code
					break;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;

	}

	public int[] countIncompleteLineByString(int beginLine, int endLine, String fileContent) {
		String fileCopy = baseDir+"/incomplete." + language;
		String clocFile = baseDir+"/cloc";
		FileProcesser.createFileParent(new File(fileCopy));
		FileProcesser.createFileParent(new File(clocFile));
		return countIncompleteLineByString(beginLine, endLine, fileContent, fileCopy, clocFile);
	}
	
	private int[] countIncompleteLineByString(int beginLine, int endLine, String fileContent, String fileCopy, String clocFile) {
		int res[]={0,0,0};
		String copyContent = "";
		String[] fileContentGroup = fileContent.split("\n");
		//System.out.println("fileContentGroup size is : " + fileContentGroup.length);
		for (int i = beginLine - 1; i < endLine; i++) {
			copyContent += fileContentGroup[i] + "\n";
		}

		File f = new File(fileCopy);
		FileWriter fw;
		try {

			fw = new FileWriter(f);
			fw.write(copyContent);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintStream out;
		try {
			out = new PrintStream(new File(clocFile));
			Shell.invoke("cloc " + fileCopy, ".", out);
			out.close();
			BufferedReader br = new BufferedReader(new FileReader(clocFile));
			boolean startCount = false;
			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				if (line.startsWith("Language")){
					line = br.readLine();
					startCount = true;
					continue;
				}
				if (startCount) {
					// System.out.println(line);
					StringTokenizer st = new StringTokenizer(line);
					st.nextToken();//name of Language
					st.nextToken();//num of files
					res[0]= Integer.parseInt(st.nextToken());//lines of blank
					res[1]= Integer.parseInt(st.nextToken());//lines of comment
					res[2]= Integer.parseInt(st.nextToken());//code
					break;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("result is : " + result);
		return res;

	}
}
