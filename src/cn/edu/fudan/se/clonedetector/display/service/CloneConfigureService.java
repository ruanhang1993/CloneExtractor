package cn.edu.fudan.se.clonedetector.display.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class CloneConfigureService extends AbstractService {
	public static void main(String args[]) {
		CloneConfigureService ccs = new CloneConfigureService();
		ccs.setClone(1, 2, 3, "utf-8");
	}

	public boolean setClone(int minToken, int minLine, int minClass, String encoding) {
		//getClass().getResource("/") -> file:\C:\...\wtpwebapps\Zhonghui\WEB-INF\classes\
		//changed by junyi
		String path = getClass().getResource("/").toString().substring(5);
		File f = new File(path+"clone.properties");
		FileWriter fw;
		boolean result = false;
		try {
			fw = new FileWriter(f);
			fw.write("minLine=" + minLine + "\n");
			
			fw.write("minToken=" + minToken + "\n");

			fw.write("minClass=" + minClass + "\n");

			fw.write("encoding=" + encoding);
			fw.close();
			result = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//add by Junyi
	public List<String> getClone() {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		String path = getClass().getResource("/").toString().substring(5);
		File f = new File(path+"clone.properties");
		FileReader fr;
		try {

			fr = new FileReader(f);
			if(fr.ready()){
				char[] cbuf = new char[1024];
				fr.read(cbuf);
				String content = new String(cbuf);
				
				String[] lines = content.split("\n");
				String minLine = lines[0].substring(8);
				String minToken = lines[1].substring(9);				
				String minClass = lines[2].substring(9);
				String encoding = lines[3].substring(9);
								
				list.add(minLine);
				list.add(minToken);
				list.add(minClass);
				list.add(encoding);
			}
			fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return list;
	}
}
