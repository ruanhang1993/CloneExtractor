package cn.edu.fudan.se.clonedetector.display.action;

import cn.edu.fudan.se.clonedetector.display.service.AddTeamService;

public class AddTeamAction extends AbstractAction {
	private String teamName;
	private boolean successful;

	public String addTeam() {
		System.out.println("add team "+teamName);
		((AddTeamService) this.getService()).addTeam(teamName);
		this.setSuccessful(true);
		return SUCCEED;
	}

	public boolean isSuccessful() {
		return successful;
	}
	
	public void setSuccessful(boolean isSuccessful) {
		this.successful = isSuccessful;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
}
