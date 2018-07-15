package cn.edu.fudan.se.clonedetector.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;

import jdk.jfr.events.FileWriteEvent;

public class FileProcesser {
	public static void delFSFiles(File destPath) {
		if (destPath.isFile()) {
			destPath.delete();
		}else {
			File[] children = destPath.listFiles();
			if (children!= null && children.length > 0) {
				for (File file : children) {
					delFSFiles(file);
				}
			}
			destPath.delete();
		}
		
	}

	public static void delFSFiles(File destPath, String suffix) {
		if (destPath.isFile() && destPath.getName().endsWith(suffix)) {
			destPath.delete();
		}else {
			File[] children = destPath.listFiles();
			if (children!= null && children.length > 0) {
				for (File file : children) {
					delFSFiles(file, suffix);
				}
			}
		}		
	}
	
	/**
     * 转换文件编码
     * @param srcFileName
     * @param destFileName
     * @param srcEncoding
     * @param destEncoding
     * @throws IOException
     */
    public static void transformFileEncoding(String srcFileName, String destFileName, String srcEncoding, String destEncoding) throws IOException {
        String line_separator = System.getProperty("line.separator");
        FileInputStream fis = new FileInputStream(srcFileName);
        StringBuffer content = new StringBuffer();
        DataInputStream in = new DataInputStream(fis);
        BufferedReader d = new BufferedReader(new InputStreamReader(in, srcEncoding));
        String line = null;
        while ((line = d.readLine()) != null)
            content.append(line + line_separator);
        d.close();
        in.close();
        fis.close();

        Writer ow = new OutputStreamWriter(new FileOutputStream(destFileName), destEncoding);
        ow.write(content.toString());
        ow.close();
    }
    

public static void travelFSFiles(File destPath, Collection<String> entries) {
	if (destPath.isFile()) {
		entries.add(destPath.getAbsolutePath());
	}else if (destPath.isDirectory()) {
		File[] children = destPath.listFiles();
		for (File file : children) {
			travelFSFiles(file, entries);
		}
	}		
}

public static  void travelFSDirs(File destPath, Collection<String> entries) {
	if (destPath.isDirectory()) {
		entries.add(destPath.getAbsolutePath());
		File[] children = destPath.listFiles();
		for (File file : children) {
			travelFSFiles(file, entries);
		}
	}		
}

public static void createFileParent(File file) {
	if (!file.getParentFile().exists()) {
		file.getParentFile().mkdirs();
	}
}

/**
 * FileWriter.write方法会创建不存在的文件，但不会创建不存在的文件夹
 * @param fileName
 * @param content
 * @param b 是否以追加形式写文件
 */
public static void writeFile(String fileName, String content, boolean b) {
    try {
        FileWriter writer = new FileWriter(fileName, b);
        writer.write(content);
        writer.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public static void main(String[] args) {
	FileProcesser.writeFile("E:\\1.txt", "hhh", true);
}

}
