package cn.edu.fudan.se.clonedetector.ccfinder.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Shell {

	public static void main(String args[]) {
		// invoke("cmd.exe copy result.ccfxd
		// .\\TestCases\\NoCloneBefore\\testAddingCloneEdittingOne\\result.ccfxd
		// /Y", System.out);
	}

	public static void invoke(String cmd, String wd, PrintStream out) {
		try {
			//System.out.println();
			// System.out.println("Start invoking...");

			out.println("PROC> " + cmd);
			Process process = Runtime.getRuntime().exec(cmd, null, new File(wd));

			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String s = "";
			while ((s = br.readLine()) != null) {
				out.println(s);
			}
			process.waitFor();

			//System.out.println("Invoke finished.");
			//System.out.println("---------------------------------");
			//System.out.println();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void invoke(String cmd, PrintStream out) {
		invoke(cmd, ".", out);
	}
}
