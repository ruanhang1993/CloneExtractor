package cn.edu.fudan.se.clonedetector.display.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BackupDatabaseService extends AbstractService{

	public boolean backup(String mysqlBinPath, String username, String password, String path) {
		// TODO Auto-generated method stub
		if(path.equals(""))
			path = getClass().getResource("/").toString().substring(5)+"backup/";
		setPath(path);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		String fileName = "backup "+sdf.format(new Date())+".sql";
		try {
			File dir = new File(path);
			if(!dir.exists())
				dir.mkdirs();
			File file = new File(path+fileName);
			if(!file.exists())
				file.createNewFile();
			OutputStream output = new FileOutputStream(file);
			String dbname = "zhonghui_data";
			String command = "cmd /c " + mysqlBinPath + "mysqldump -u" + username  
	                + " -p"+password+" "+dbname; 
			System.out.println("start backup:");
			System.out.println(command);
	        PrintWriter p = null;  
	        BufferedReader reader = null;  
	        try {  
	            p = new PrintWriter(new OutputStreamWriter(output, "utf8")); 
	            Runtime rt = Runtime.getRuntime();
	            Process process = rt.exec(command); 
	            InputStreamReader inputStreamReader = new InputStreamReader(process  
	                    .getInputStream(), "utf8");  
	            reader = new BufferedReader(inputStreamReader);  
	            String line = null;  
	            while ((line = reader.readLine()) != null) {
//	            	System.out.println(line);
	                p.println(line);  
	            }  
	            p.flush();  
	        } catch (UnsupportedEncodingException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        } finally {  
	            try {  
	                if (reader != null) {  
	                    reader.close();  
	                }  
	                if (p != null) {  
	                    p.close();  
	                }  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            }  
	        }  
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("done");
		return true;
	}
	
	public boolean restore(String mysqlBinPath, String username, String password, String fileName){
		String path = getPath();
		try {  
            InputStream input = new FileInputStream(path+fileName);  
            //备份数据库的表名
            String dbname = "zhonghui_data";
            String command = "cmd /c " + mysqlBinPath + "mysql -u" + username  
                    + " -p" + password + " " + dbname;  
            System.out.println(command);
            try {  
            	System.out.println("restore start");
            	Runtime rt = Runtime.getRuntime();
                Process process = rt.exec(command);
                OutputStream out = process.getOutputStream();  
                String line = null;  
                String outStr = null;  
                StringBuffer sb = new StringBuffer("");  
                BufferedReader br = new BufferedReader(new InputStreamReader(input,  
                        "utf8"));  
                while ((line = br.readLine()) != null) { 
//                	System.out.println(line);
                    sb.append(line + "\r\n");  
                }  
                outStr = sb.toString(); 
                System.out.println(outStr);
                OutputStreamWriter writer = new OutputStreamWriter(out, "utf8");  
                writer.write(outStr);  
                writer.flush();  
                out.close();  
                br.close();  
                writer.close();  
            } catch (UnsupportedEncodingException e) {  
                e.printStackTrace();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
            
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } 
		System.out.println("done");
		return true;
	}
	
	public List<String> getBackupList(){
		String path = getPath();	
		List<String> backupList = new ArrayList<String>();
		File file=new File(path);
		if(file.exists()){
			File[] tempList = file.listFiles();
			System.out.println("backup num:"+tempList.length);
			for (int i = 0; i < tempList.length; i++) {
			   if (tempList[i].isFile()) {
				   backupList.add(tempList[i].getName());
				   System.out.println(tempList[i]);
			   }
			}
		}
		else{
			System.out.println("No Backup");
		}
		return backupList;
	}
	
	private void setPath(String path){
		String realPath = getClass().getResource("/").toString().substring(5);
		File f = new File(realPath+"path.backup");
		
		FileWriter fw;
		try {
			if(!f.exists())
				f.createNewFile();
			fw = new FileWriter(f);
			fw.write("path=" + path+"\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getPath(){
		String path = "";
		String realPath = getClass().getResource("/").toString().substring(5);
		File f = new File(realPath+"path.backup");
		FileReader fr;
		try {

			fr = new FileReader(f);
			if(fr.ready()){
				char[] cbuf = new char[1024];
				fr.read(cbuf);
				String content = new String(cbuf);
				
				String[] lines = content.split("\n");
				path = lines[0].substring(5);
			}
			fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return path;
	}
}
