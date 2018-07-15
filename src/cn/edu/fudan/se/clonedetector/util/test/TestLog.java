package cn.edu.fudan.se.clonedetector.util.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;

import cn.edu.fudan.se.clonedetector.util.Logger;

public class TestLog {
	public static void main(String[] args) throws FileNotFoundException {
	for (int i = 0; i < 2; i++) {
		try {
			int[] array = new int[]{1,2};
			int t = array[i+2];
		} catch (Exception e) {
			Logger.log(e.getMessage(),"D:/log.txt");
		}
	}
	}
}
