package cn.edu.fudan.se.clonedetector.display.service;

import cn.edu.fudan.se.clonedetector.bean.Project;
import cn.edu.fudan.se.clonedetector.bean.Repository;

public class UpdateProjectService extends AbstractService{
	public boolean updateProject(int projectId, String developCompany, String projectTeam, String projectNameEn, String projectNameCh){
		Project project = this.dao.getProjectById(projectId);
		project.setDevelopCompany(developCompany);
		project.setProjectNameCh(projectNameCh);
		project.setProjectNameEn(projectNameEn);
		project.setProjectTeam(projectTeam);
		this.dao.updateProject(project);
		return true;
	}
	public boolean updateSvnProject(int projectId, String developCompany, String projectTeam, String projectNameEn, String projectNameCh,String address,String username,String password){
		Project project = this.dao.getProjectById(projectId);
		project.setDevelopCompany(developCompany);
		project.setProjectNameCh(projectNameCh);
		project.setProjectNameEn(projectNameEn);
		project.setProjectTeam(projectTeam);
		this.dao.updateProject(project);
		Repository repository  = this.dao.getRepoById(project.getRepositoryId());
		repository.setUrl(address);
		repository.setPassword(password);
		repository.setUsername(username);
		this.dao.updateRepository(repository);
		return true;
	}
	public boolean updateCcProject(int projectId, String developCompany, String projectTeam, String projectNameEn, String projectNameCh,String stream,String pvob,String component){
		Project project = this.dao.getProjectById(projectId);
		project.setDevelopCompany(developCompany);
		project.setProjectNameCh(projectNameCh);
		project.setProjectNameEn(projectNameEn);
		project.setProjectTeam(projectTeam);
		project.setStream(stream);
		project.setPvob(pvob);
		project.setComponent(component);
		this.dao.updateProject(project);
		return true;
	}
}
