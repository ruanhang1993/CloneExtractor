package cn.edu.fudan.se.clonedetector.util.test;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;

public class TestLog4j {
	public static void main(String[] args) {
		Logger logger = cn.edu.fudan.se.clonedetector.util.Logger.getConfigLogger(TestLog4j.class);
		logger.info("test info");
		logger.warn("test warn");
	}

	private static void testSimpleLog() {
		 Logger logger = Logger.getLogger(TestLog4j.class);
	        //使用默认的配置信息，不需要写log4j.properties
	        BasicConfigurator.configure();
	        //设置日志输出级别为info，这将覆盖配置文件中设置的级别
	        logger.setLevel(Level.INFO);
	        //下面的消息将被输出
	        logger.info("this is an info");
	        logger.warn("this is a warn");
	        logger.error("this is an error");
	        logger.fatal("this is a fatal");	        
	}
	
	private static void testLogFile() {
		 Logger logger = Logger.getLogger(TestLog4j.class);
//		 SimpleLayout layout = new SimpleLayout();
	        HTMLLayout  layout = new HTMLLayout();
	        FileAppender appender = null;
	        try
	        {
	            //把输出端配置到out.txt
	            appender = new FileAppender(layout,"out.html",false);
	        }catch(Exception e)
	        {            
	        }
	        logger.addAppender(appender);//添加输出端
	        logger.setLevel((Level)Level.DEBUG);//覆盖配置文件中的级别
	        logger.debug("debug");
	        logger.info("info");
	        logger.warn("warn");
	        logger.error("error");
	        logger.fatal("fatal");     
	}
}
