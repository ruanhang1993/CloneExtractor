/**
 * 
 */
package cn.edu.fudan.se.clonedetector.display.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Author;
import cn.edu.fudan.se.clonedetector.bean.Change;
import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.bean.Project;

public class VersionExtractService extends AbstractService {
	private Comparator<Commit> comCommit = new Comparator<Commit>() {
		public int compare(Commit s1, Commit s2) {
			return s1.getCommitDate().compareTo(s2.getCommitDate());
		}
	};

	public List<Commit> specificedCommits(int projectId) {
		System.out.println("get in");

		List<Commit> commits = this.dao.getCommitsByProjectId(projectId);

		System.out.println("get successfully ");
		return commits;

	}

	public String getProjectName(int projectId) {
		String projectName = this.dao.getProjectById(projectId).getProjectNameCh();
		return projectName;
	}
	
	public Project getProject(int projectId) {
		return this.dao.getProjectById(projectId);
	}
}