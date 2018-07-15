package cn.edu.fudan.se.clonedetector.display.service;

import cn.edu.fudan.se.clonedetector.bean.Team;

public class AddTeamService extends AbstractService {
	public void addTeam(String name){
		this.dao.saveTeam(new Team(name));
	}
}
