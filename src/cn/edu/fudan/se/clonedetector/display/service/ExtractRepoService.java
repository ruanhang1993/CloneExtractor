package cn.edu.fudan.se.clonedetector.display.service;

import cn.edu.fudan.se.clonedetector.bean.Repository;
import cn.edu.fudan.se.clonedetector.bean.Project;

public class ExtractRepoService extends AbstractService {
	public Repository repo(int projectId) {
		Project project = this.dao.getProjectById(projectId);
		Repository repo = this.dao.getRepoById(project.getRepositoryId());
		return repo;		
	}
}
