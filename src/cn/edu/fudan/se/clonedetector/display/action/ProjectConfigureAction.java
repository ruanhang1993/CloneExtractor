package cn.edu.fudan.se.clonedetector.display.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.bean.Project;
import cn.edu.fudan.se.clonedetector.bean.Repository;
import cn.edu.fudan.se.clonedetector.display.service.DiffExtractService;
import cn.edu.fudan.se.clonedetector.display.service.ExtractRepoService;
import cn.edu.fudan.se.clonedetector.display.service.ProjectConfigureService;
import cn.edu.fudan.se.clonedetector.display.service.ProjectsExtractService;
import cn.edu.fudan.se.clonedetector.display.service.VersionExtractService;

public class ProjectConfigureAction extends AbstractAction {
	private String url;
	private String port;
	private String username;
	private String password;
	private String language;
	private String commitDate;
	private String processDate;
	private String projectNameCh;
	private String projectNameEn;
	private String projectTeam;
	private String developCompany;
	private String tag;
	private String empty;
	private String tocheckout;
	private boolean successful;
	private List<Commit> commits;
	private String versionControl;
	
	private String steam;
	private String pvod;
	private String component;

	public String projectConfigure() {
		System.out.println("Access");
		System.out.format("versionControl: %s%n", versionControl);
		System.out.println(username);
		System.out.println(url);
		System.out.println(port);
		System.out.println(password);
		System.out.println(commitDate);
		System.out.println(processDate);
		System.out.format("tag : %s%n",tag);
		System.out.format("projectTeam : %s%n",projectTeam);
		System.out.format("projectNameCh : %s%n",projectNameCh);
		System.out.format("projectNameEn : %s%n",projectNameEn);
		System.out.format("developCompany : %s%n",developCompany);
		System.out.format("tocheckout: %s%n", tocheckout);
		System.out.format("empty: %s%n", empty);
		
		System.out.format("steam : %s%n",steam);
		System.out.format("component : %s%n",component);
		System.out.format("pvod: %s%n", pvod);
		
		if(versionControl.equals("cc")){
			String[] ccConcepts = new String[]{steam, pvod, component};
			String[] proConcepts = new String[]{projectNameCh, projectNameEn, developCompany, projectTeam};
			if(empty!=null){
				String[] emptys = empty.replaceAll("\\s","").split(",");
				String[] tocheckouts = tocheckout.replaceAll("\\s","").split(",");
				
				successful = ((ProjectConfigureService) this.getService()).extractCC(emptys, tocheckouts, ccConcepts, 
						language, processDate, proConcepts, tag);				
			}else{
				successful = ((ProjectConfigureService) this.getService()).extractCC(ccConcepts, language, processDate, proConcepts, tag);				
			}
		}else{
			if(empty!=null){
				String[] emptys = empty.replaceAll("\\s","").split(",");
				String[] tocheckouts = tocheckout.replaceAll("\\s","").split(",");
				
				successful = ((ProjectConfigureService) this.getService()).extractURL(emptys, tocheckouts, url, username, password, language,
						processDate, projectNameCh,projectNameEn,developCompany, projectTeam, tag);
			}else{
				successful = ((ProjectConfigureService) this.getService()).extractURL(url, username, password, language,
						processDate, projectNameCh,projectNameEn,developCompany, projectTeam, tag);
			}
		}
		return SUCCEED;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCommitDate() {
		return commitDate;
	}

	public void setCommitDate(String commitDate) {
		this.commitDate = commitDate;
	}

	public String getProcessDate() {
		return processDate;
	}

	public void setProcessDate(String processDate) {
		this.processDate = processDate;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public String getProjectTeam() {
		return projectTeam;
	}

	public void setProjectTeam(String projectTeam) {
		this.projectTeam = projectTeam;
	}
	
	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getProjectNameCh() {
		return projectNameCh;
	}

	public void setProjectNameCh(String projectNameCh) {
		this.projectNameCh = projectNameCh;
	}

	public String getProjectNameEn() {
		return projectNameEn;
	}

	public void setProjectNameEn(String projectNameEn) {
		this.projectNameEn = projectNameEn;
	}

	public String getDevelopCompany() {
		return developCompany;
	}

	public void setDevelopCompany(String developCompany) {
		this.developCompany = developCompany;
	}

	public String getEmpty() {
		return empty;
	}

	public void setEmpty(String empty) {
		this.empty = empty;
	}

	public String getTocheckout() {
		return tocheckout;
	}

	public void setTocheckout(String tocheckout) {
		this.tocheckout = tocheckout;
	}

	public String getSteam() {
		return steam;
	}

	public void setSteam(String steam) {
		this.steam = steam;
	}

	public String getPvod() {
		return pvod;
	}

	public void setPvod(String pvod) {
		this.pvod = pvod;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getVersionControl() {
		return versionControl;
	}

	public void setVersionControl(String versionControl) {
		this.versionControl = versionControl;
	}

	/**
	 * @param isSuccessful
	 *            the isSuccessful to set
	 */
	public void setSuccessful(boolean isSuccessful) {
		this.successful = isSuccessful;
	}

}
