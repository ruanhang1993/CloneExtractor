package cn.edu.fudan.se.clonedetector.ccfinder.clone.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;

import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.exception.FileWrongEncodingException;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.model.FileGroup;
import cn.edu.fudan.se.clonedetector.util.FileProcesser;

public class CCFinder {
	private String cmd = "ccfx";
	public static final String JAVA = "java";
	public static final String CPP = "cpp";
	public static final String VB = "visualbasic";
	public static final String TEXT = "plaintext";
	public static final String CSHARP = "csharp";

	public CCFinder() {
	}

	public CCFinder setType(String type) {
		cmd += " d " + type;
		return this;
	}

	public CCFinder setFiles(FileGroup files) {
		cmd += " " + groupToString(files);
		return this;
	}

	public CCFinder setGroups(FileGroup group1, FileGroup group2) {
		cmd += " " + groupToString(group1) + " -is " + groupToString(group2);
		return this;
	}

	public CCFinder setRangeOptions(boolean withinFile, boolean betweenFiles, boolean betweenGroups) {
		cmd += " -w ";
		if (withinFile)
			cmd += "w+";
		else
			cmd += "w-";

		if (betweenFiles)
			cmd += "f+";
		else
			cmd += "f-";

		if (betweenGroups)
			cmd += "g+";
		else
			cmd += "g-";

		return this;
	}

	public CCFinder setDataFile(String dataFile) {
		cmd += " -o " + dataFile;
		return this;
	}

	public CCFinder setInputDir(String inputDir, boolean preprocess) {

		cmd += (preprocess ? " -dn " : " -d ") + inputDir;
		return this;
	}

	public CCFinder setPreprocessLocation(String location) {
		cmd += " -n " + location;
		return this;
	}

	public CCFinder setMinimumCloneLength(int minLength) {
		cmd += " -b " + minLength;
		cmd += " -pp- ";
		return this;
	}
	

	public CCFinder setFileList(String fileList) {
		cmd += " -i "+fileList;
		return this;
	}


	public void run() throws FileWrongEncodingException{
		invoke(cmd, System.out);
	}

	private String groupToString(FileGroup group) {
		if (group == null)
			return "";

		List<String> files = group.getFiles();
		String result = "";
		for (String element : files) {
			result += element.toString() + " ";
		}
		return result.trim();
	}

	public void setPP() {
		cmd += " -pp- ";
	}

	public static void invoke(String cmd, String wd, PrintStream out) throws FileWrongEncodingException{
		try {
			System.out.println();
			 System.out.println("Start invoking...");

			out.println("PROC> " + cmd);
			Process process = Runtime.getRuntime().exec(cmd, null, new File(wd));
			
			InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            InputStream iserr = process.getErrorStream();
            InputStreamReader isrerr = new InputStreamReader(iserr, "utf-8");
            BufferedReader brerr = new BufferedReader(isrerr);
            String s = "";
			String wrongEncodingFileName = null;
			final String wrongEncodingFileError = "error: invalid string (wrong character encoding?)";
			while ((s = br.readLine()) != null) {
				out.println(s);
			}
			while ((s = brerr.readLine()) != null) {
				out.println(s);
				if (s.startsWith(wrongEncodingFileError)) {
					s = s.substring(wrongEncodingFileError.length(), s.length()-1);
					wrongEncodingFileName = s.substring(s.indexOf('\'')+1);
				}
			}
            process.waitFor();
            process.destroy();System.out.println("Invoke finished.");
			System.out.println("---------------------------------");
			System.out.println();
			if (wrongEncodingFileName != null) {
				throw new FileWrongEncodingException(wrongEncodingFileName);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void invoke(String cmd, PrintStream out) throws FileWrongEncodingException{
		invoke(cmd, ".", out);
	}
	
	/**
	 * change to ccfinder's language type
	 * 
	 * Name of Preprocess Script	Programming Language	Extensions of Source Files
	 * 				cobol				Cobol					.cbl, .cob, .cobol
	 * 				cpp					C/C++					.h, .hh, .hpp, .hxx, .c, .cc, .cpp, .cxx
	 * 				csharp				C#						.cs
	 * 				java				Java					.java
	 * 				visualbasic			Visual Basic			.vb, .bas, .frm
	 * 				plaintext			Text file				.txt
	 * @param language
	 * @return
	 */
	public static String toCCLanguageType(String language){
		String type = language;
		if(language.equalsIgnoreCase("c") || language.equalsIgnoreCase("c++"))
			type = CPP;
		else if(language.equalsIgnoreCase("c#"))
			type = CSHARP;
		else if (language.equalsIgnoreCase("vb") || language.equalsIgnoreCase("visualbasic")) {
			type = VB;
		}else if (language.equals("java")) {
			type = JAVA;
		}else {
			type = TEXT;
		}
		return type;
	}
}
