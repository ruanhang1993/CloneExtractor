package cn.edu.fudan.se.clonedetector.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.xml.DOMConfigurator;

public class Logger {
	public static void log(String errMsg) {
		try {
			File file = new File("err.log");
			if (!file.exists())
				file.createNewFile();
			FileWriter fw = new FileWriter(file);
			fw.append(errMsg);
			fw.close();
		} catch (IOException ex) {
			// nothing
		}
	}
	

	public static void log(String errMsg, String log) {
		try {
			File file = new File(log);
			if (!file.exists())
				file.createNewFile();
			FileWriter fw = new FileWriter(file);
			fw.append(errMsg);
			fw.close();
		} catch (IOException ex) {
			// nothing
		}
	}
	
	public static org.apache.log4j.Logger getHtmlLogger(Class class1){
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(class1);
		BasicConfigurator.configure();
		PropertyConfigurator.configure("config/log4j.properties");
		DOMConfigurator.configure("");
	    HTMLLayout  layout = new HTMLLayout();
	    FileAppender appender = null;
	    try{
	    	appender = new FileAppender(layout,"logs/"+class1.getName()+".html",false);
	    }catch(Exception e){            
	    }
	    logger.addAppender(appender);//添加输出端
	    logger.setLevel((Level)Level.DEBUG);//覆盖配置文件中的级别
	    return logger;   
	}
	
	public static org.apache.log4j.Logger getSimpleLogger(Class class1){
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(class1);
		SimpleLayout layout = new SimpleLayout();
	    FileAppender appender = null;
	    try{
	    	appender = new FileAppender(layout,"log/"+class1.getName()+".txt",false);
	    }catch(Exception e){            
	    }
	    logger.addAppender(appender);//添加输出端
	    logger.setLevel((Level)Level.DEBUG);//覆盖配置文件中的级别
	    return logger;   
	}
	
	public static org.apache.log4j.Logger getConfigLogger(Class class1){
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(class1);
		BasicConfigurator.configure();
		PropertyConfigurator.configure("config/log4j.propertie");
	    return logger;   
	}
}
