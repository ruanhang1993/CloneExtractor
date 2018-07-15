package cn.edu.fudan.se.clonedetector.display.service;

public class DeleteTeamService extends AbstractService {
	public void deleteTeam(int teamId){
		this.dao.deleteTeamById(teamId);
	}
}
