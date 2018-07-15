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
	        //ʹ��Ĭ�ϵ�������Ϣ������Ҫдlog4j.properties
	        BasicConfigurator.configure();
	        //������־�������Ϊinfo���⽫���������ļ������õļ���
	        logger.setLevel(Level.INFO);
	        //�������Ϣ�������
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
	            //����������õ�out.txt
	            appender = new FileAppender(layout,"out.html",false);
	        }catch(Exception e)
	        {            
	        }
	        logger.addAppender(appender);//��������
	        logger.setLevel((Level)Level.DEBUG);//���������ļ��еļ���
	        logger.debug("debug");
	        logger.info("info");
	        logger.warn("warn");
	        logger.error("error");
	        logger.fatal("fatal");     
	}
}
