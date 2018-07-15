package cn.edu.fudan.se.clonedetector.display.action;

import java.util.List;

import cn.edu.fudan.se.clonedetector.display.service.DeleteTeamService;

public class DeleteTeamAction extends AbstractAction {
		private int teamId;
		private boolean successful;

		public String deleteTeam() {
			System.out.println("delete team " + teamId);
			((DeleteTeamService) this.getService()).deleteTeam(teamId);
			this.setSuccessful(true);
			return SUCCEED;
		}

		public boolean isSuccessful() {
			return successful;
		}

		public void setSuccessful(boolean isSuccessful) {
			this.successful = isSuccessful;
		}

		public int getTeamId() {
			return teamId;
		}

		public void setTeamId(int teamId) {
			this.teamId = teamId;
		}
		
}
