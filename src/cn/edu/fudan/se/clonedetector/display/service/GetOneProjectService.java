package cn.edu.fudan.se.clonedetector.display.service;

import cn.edu.fudan.se.clonedetector.bean.Project;

public class GetOneProjectService extends AbstractService {
	public Project getOneProject(int id){
		return this.dao.getProjectById(id);
	}
}
