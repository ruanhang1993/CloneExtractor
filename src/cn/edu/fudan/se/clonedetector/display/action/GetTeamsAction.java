package cn.edu.fudan.se.clonedetector.display.action;

import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Team;
import cn.edu.fudan.se.clonedetector.display.service.GetTeamsService;

public class GetTeamsAction extends AbstractAction {
	private List<Team> teams;
	private boolean successful;

	public String getAllTeams() {
		System.out.println("extracting teams...");
		teams = (List<Team>) ((GetTeamsService) this.getService()).getTeams();
		this.setSuccessful(true);
		return SUCCEED;
	}

	public boolean isSuccessful() {
		return successful;
	}
	public void setSuccessful(boolean isSuccessful) {
		this.successful = isSuccessful;
	}

	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}
	public List<Team> getTeams() {
		return this.teams;
	}
	
}
