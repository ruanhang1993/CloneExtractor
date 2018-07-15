package cn.edu.fudan.se.clonedetector.display.service;

import java.io.File;
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.*;

public class DeleteProjectService extends AbstractService {
	public void deleteProject(int projectId){
		Project pj = this.dao.getProjectById(projectId);
		List<Commit>commits = this.dao.getCommitsByProjectId(projectId);
		List<Compare>compares = this.dao.getComparesByProjectId(projectId);
		String path = pj.getProjectPath();		
		int repositoryId = pj.getRepositoryId();
		int compareId;
		int commitId;
		System.out.println("commits size: "+commits.size());
		System.out.println("compares size: "+compares.size());
		String matches = "[A-Za-z]:\\\\[^:?\"><*]*";
		//modify by junyi
		if(path.matches(matches)){
			if (pj.getStream() == null || pj.getStream().length()==0) {
				DeleteFolder(path);
			}
			for(int i = 0;i<compares.size();i++){
				compareId = compares.get(i).getCompareId();
				System.out.println("delete compare: "+compareId);
				this.dao.deleteClonesByCompareId(compareId);
				System.out.println("done: "+i);
			}
			System.out.println("next");
			for(int i = 0;i<commits.size();i++){
				commitId = commits.get(i).getCommitId();
				System.out.println("delete commit: "+commitId);
				this.dao.deleteThingsByCommitId(commitId);
				System.out.println("done: "+i);
			}
			this.dao.deleteCommitByProjectId(projectId);
			this.dao.deleteProjectById(projectId);
			this.dao.deleteRepositoryById(repositoryId);
			System.out.println("end");
		}
	}
	
	//add by Junyi
    private boolean DeleteFolder(String path) {
    	boolean result = false;
        File file = new File(path);
        
        if (!file.exists()) {  // 
            return result;
        } else {
            
            if (file.isFile()) {  
                return deleteFile(path);
            } else {  
                return deleteDirectory(path);
            }
        }
    }	
	
    //add by Junyi
    private boolean deleteDirectory(String path) {
    	boolean result = false;
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        File dir = new File(path);
        
        if (!dir.exists() || !dir.isDirectory()) {
            return result;
        }
        result = true;
 
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            
            if (files[i].isFile()) {
                result = deleteFile(files[i].getAbsolutePath());
                if (!result) break;
            } 
            else {
                result = deleteDirectory(files[i].getAbsolutePath());
                if (!result) break;
            }
        }
        if (!result) return false;
        
        if (dir.delete()) {
            return true;
        } else {
            return false;
        }
    }
    
    //add by Junyi
    private boolean deleteFile(String path) {  
        boolean result = false;  
        File file = new File(path);   
        if (file.isFile() && file.exists()) {  
            file.delete();  
            result = true;  
        }  
        return result;  
    }
}
