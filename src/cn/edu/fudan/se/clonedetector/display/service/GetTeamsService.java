package cn.edu.fudan.se.clonedetector.display.service;

import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Team;

public class GetTeamsService extends AbstractService {
	public List<Team> getTeams() {
		List<Team> teams = this.dao.getTeams();
		return teams;		
	}
}
