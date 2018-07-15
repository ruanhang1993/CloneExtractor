package cn.edu.fudan.se.clonedetector.util.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;

import cn.edu.fudan.se.clonedetector.ccfinder.clone.detector.exception.FileWrongEncodingException;
import cn.edu.fudan.se.clonedetector.ccfinder.clone.util.CCFinder;
import cn.edu.fudan.se.clonedetector.ccfinder.util.Shell;

public class TestCmdOutput {
	public static void main(String[] args) {
	}
	public static void pre(){
		String cmd = "cmd.exe /C cleartool rebase  -cancel -view codescanner_NGCNYTS_Frontend_Clone1 -stream NGCNYTS_Frontend_Clone@\\NGCNYTS_PVOB";
		try {
			System.out.println("PROC> "+cmd);
			Process process = Runtime.getRuntime().exec(cmd, null, new File("."));
			InputStream is = process.getInputStream();
			InputStream iserr = process.getErrorStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			InputStreamReader isrerr = new InputStreamReader(iserr, "utf-8");
			BufferedReader brerr = new BufferedReader(isrerr);
			OutputStream os = process.getOutputStream();
			String s = "";
			
			while((s = br.readLine()) != null){
				System.out.println(s);
				if(s.startsWith("Started by \"codescanner\"")){
					os.write("y".getBytes());
					os.flush();
					os.close();
				}
			}
			while((s = brerr.readLine()) != null){
				System.out.println(s);
			}
			process.waitFor();
			process.destroyForcibly();
	} catch (IOException e) {
		e.printStackTrace();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	}
	
}
