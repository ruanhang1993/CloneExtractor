package cn.edu.fudan.se.clonedetector.bean;

public class Team implements java.io.Serializable {
	private Integer teamId;
	private String teamName;
	
	public Team() {
	}

	public Team(String teamName) {
		this.teamName = teamName;
	}

	public Integer getTeamId() {
		return teamId;
	}

	public void setTeamId(Integer teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
}
