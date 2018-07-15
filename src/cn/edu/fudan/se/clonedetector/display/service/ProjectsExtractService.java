/**
 * 
 */
package cn.edu.fudan.se.clonedetector.display.service;

import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Project;


public class ProjectsExtractService extends AbstractService {
	

	public List<Project> specificedProjects() {
		System.out.println("get in");
		
		System.out.println("this is ");
		List<Project> Projects = this.dao.getProjects();
		return Projects;		
	}
	//add by junyi
	public Project getLastProject() {
		System.out.println("get in ");
		
		System.out.println("this is getLastProject()");
		List<Project> Projects = this.dao.getProjects();
		int size = Projects.size();
		return Projects.get(size-1);		
	}

	public List<Project> getVersions() {
		return this.dao.getProjects();
	}
}
