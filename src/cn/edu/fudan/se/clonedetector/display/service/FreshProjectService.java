package cn.edu.fudan.se.clonedetector.display.service;

import java.util.Date;

import cn.edu.fudan.se.clonedetector.bean.Project;
import cn.edu.fudan.se.clonedetector.bean.Repository;
import cn.edu.fudan.se.clonedetector.dao.IDataAccessor;

public class FreshProjectService extends AbstractService {

	public void freshProject(int projectId){
		Project proj = this.dao.getProjectById(projectId);
		System.out.format("projectName : %s%n", proj.getProjectNameCh());
		if (proj.getStream() != null && proj.getStream().length()>0) {
			freshCC(proj);
		}else{
			freshSVN(proj);
		}
	}

	private void freshSVN(Project proj) {
		Repository repo = this.dao.getRepoById(proj.getRepositoryId());
		String tag = "";
		if (repo.getAnalyzeType() != 0) {
			tag = ""+repo.getAnalyzeType();
		}
		new ProjectConfigureService(dao).extractURL(repo.getUrl(), repo.getUsername(), 
			repo.getPassword(), repo.getLanguage(), repo.getProcessDate().toString(), 
			proj.getProjectNameCh(),proj.getProjectNameEn(),proj.getDevelopCompany(), proj.getProjectTeam(), tag);
	}

	private void freshCC(Project proj) {

		Repository repo = this.dao.getRepoById(proj.getRepositoryId());
		String tag = "";
		if (repo.getAnalyzeType() != 0) {
			tag = ""+repo.getAnalyzeType();
		}
		String[] ccConcepts = new String[]{proj.getStream(), proj.getPvob(), proj.getComponent()};
		String[] proConcepts = new String[]{proj.getProjectNameCh(), proj.getProjectNameEn(), 
				proj.getDevelopCompany(), proj.getProjectTeam()};
		new ProjectConfigureService(dao).extractCC(ccConcepts, repo.getLanguage(), repo.getProcessDate().toString(), proConcepts, tag);		
		
	}
		
}
