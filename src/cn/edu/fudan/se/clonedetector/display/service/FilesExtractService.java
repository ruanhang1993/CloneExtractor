/**
 * 
 */
package cn.edu.fudan.se.clonedetector.display.service;

import java.util.Comparator;
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.bean.File;
import cn.edu.fudan.se.clonedetector.bean.Project;

public class FilesExtractService extends AbstractService {
	private Comparator<Commit> comCommit = new Comparator<Commit>() {
		public int compare(Commit s1, Commit s2) {
			return s1.getCommitDate().compareTo(s2.getCommitDate());
		}
	};

	public List<File> specificedFiles(int commitId) {
		System.out.println("get in");

		List<File> files = this.dao.getFilesOfCommitByIdCountFrom(commitId, -1, -1);

		System.out.println("get successfully ");
		return files;

	}
	
	public String getRelativePath(int commitId) {
		Commit commit = this.dao.getCommitById(commitId);
		Project project = this.dao.getProjectById(commit.getProjectId());
		return project.getProjectPath();

	}
}
