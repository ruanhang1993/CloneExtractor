package cn.edu.fudan.se.clonedetector.display.action;

import java.util.ArrayList;
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Repository;
import cn.edu.fudan.se.clonedetector.display.service.ExtractRepoService;

public class ExtractRepoAction extends AbstractAction {
		private boolean successful;
		private String projects;
		private List<Repository> repos = new ArrayList<>();
		
		public String getProjects() {
			return projects;
		}

		public void setProjects(String projects) {
			this.projects = projects;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public String extractRepo() {
			System.out.println("extracting the repo of the project ...");
			int projectId = -1;
			System.out.println(projects);
			String[] projectIds = projects.replaceAll("\\s","").split(",");
			for(int i=0;i<projectIds.length;i++){
				try{
					projectId = Integer.parseInt(projectIds[i]);
				}catch(Exception e){
					e.printStackTrace();
				}
				System.out.format("projectId : %d%n", projectId);
				
				Repository repo = ((ExtractRepoService) this.getService()).repo(projectId);
				System.out.format("repo id : %d%n",repo.getRepositoryId());
				repos.add(repo);
			}
			this.setSuccessful(true);
			System.out.println("done");
			return SUCCEED;
		}

		/**
		 * @return the isSuccessful
		 */
		public boolean isSuccessful() {
			return successful;
		}

		/**
		 * @param isSuccessful
		 *            the isSuccessful to set
		 */
		public void setSuccessful(boolean isSuccessful) {
			this.successful = isSuccessful;
		}

		public List<Repository> getRepos() {
			return repos;
		}

}
