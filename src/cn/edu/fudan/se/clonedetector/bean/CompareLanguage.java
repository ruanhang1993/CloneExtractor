package cn.edu.fudan.se.clonedetector.bean;

import java.util.Date;

public class CompareLanguage implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer compareId;
	private String language;
	private String revisionId;
	private String preRevisionId;
	private Integer commitId;
	private Integer preCommitId;
	private Integer changeFileNum;
	private Integer cloneChangeFileNum;
	private Integer loc;
	private Integer eloc;
	private Integer cloc;
	private Integer ecloc;
	private Integer chloc;
	private Integer echloc;
	private Integer aloc;
	private Integer ealoc;
	private Integer dloc;
	private Integer edloc;

	private Integer dcloc;
	private Integer edcloc;
	
	private Integer bloc;
	private Integer cmloc;
	private Integer bcloc;
	private Integer cmcloc;
	private Integer bdcloc;
	private Integer cmdcloc;
	private Integer bchloc;
	private Integer cmchloc;
	
	private Integer ccloc;
	private Integer eccloc;

	public CompareLanguage() {
		// TODO Auto-generated constructor stub
	}
	public CompareLanguage(Integer compareId, String revisionId, String preRevisionId, 
			Integer CommitId, Integer preCommitId, String language) {
		this.setCompareId(compareId);
		this.setRevisionId(revisionId);
		this.setPreRevisionId(preRevisionId);
		this.setCommitId(CommitId);
		this.setPreCommitId(preCommitId);
		this.setLanguage(language);
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getCompareId() {
		return compareId;
	}
	public void setCompareId(Integer compareId) {
		this.compareId = compareId;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getRevisionId() {
		return revisionId;
	}
	public void setRevisionId(String revisionId) {
		this.revisionId = revisionId;
	}
	public String getPreRevisionId() {
		return preRevisionId;
	}
	public void setPreRevisionId(String preRevisionId) {
		this.preRevisionId = preRevisionId;
	}
	public Integer getCommitId() {
		return commitId;
	}
	public void setCommitId(Integer commitId) {
		this.commitId = commitId;
	}
	public Integer getPreCommitId() {
		return preCommitId;
	}
	public void setPreCommitId(Integer preCommitId) {
		this.preCommitId = preCommitId;
	}
	public Integer getChangeFileNum() {
		return changeFileNum;
	}
	public void setChangeFileNum(Integer changeFileNum) {
		this.changeFileNum = changeFileNum;
	}
	public Integer getCloneChangeFileNum() {
		return cloneChangeFileNum;
	}
	public void setCloneChangeFileNum(Integer cloneChangeFileNum) {
		this.cloneChangeFileNum = cloneChangeFileNum;
	}
	public Integer getLoc() {
		return loc;
	}
	public void setLoc(Integer loc) {
		this.loc = loc;
	}
	public Integer getEloc() {
		return eloc;
	}
	public void setEloc(Integer eloc) {
		this.eloc = eloc;
	}
	public Integer getCloc() {
		return cloc;
	}
	public void setCloc(Integer cloc) {
		this.cloc = cloc;
	}
	public Integer getEcloc() {
		return ecloc;
	}
	public void setEcloc(Integer ecloc) {
		this.ecloc = ecloc;
	}
	public Integer getChloc() {
		return chloc;
	}
	public void setChloc(Integer chloc) {
		this.chloc = chloc;
	}
	public Integer getEchloc() {
		return echloc;
	}
	public void setEchloc(Integer echloc) {
		this.echloc = echloc;
	}
	public Integer getAloc() {
		return aloc;
	}
	public void setAloc(Integer aloc) {
		this.aloc = aloc;
	}
	public Integer getEaloc() {
		return ealoc;
	}
	public void setEaloc(Integer ealoc) {
		this.ealoc = ealoc;
	}
	public Integer getDloc() {
		return dloc;
	}
	public void setDloc(Integer dloc) {
		this.dloc = dloc;
	}
	public Integer getEdloc() {
		return edloc;
	}
	public void setEdloc(Integer edloc) {
		this.edloc = edloc;
	}
	public Integer getDcloc() {
		return dcloc;
	}
	public void setDcloc(Integer dcloc) {
		this.dcloc = dcloc;
	}
	public Integer getEdcloc() {
		return edcloc;
	}
	public void setEdcloc(Integer edcloc) {
		this.edcloc = edcloc;
	}
	public Integer getBloc() {
		return bloc;
	}
	public void setBloc(Integer bloc) {
		this.bloc = bloc;
	}
	public Integer getCmloc() {
		return cmloc;
	}
	public void setCmloc(Integer cmloc) {
		this.cmloc = cmloc;
	}
	public Integer getBcloc() {
		return bcloc;
	}
	public void setBcloc(Integer bcloc) {
		this.bcloc = bcloc;
	}
	public Integer getCmcloc() {
		return cmcloc;
	}
	public void setCmcloc(Integer cmcloc) {
		this.cmcloc = cmcloc;
	}
	public Integer getBdcloc() {
		return bdcloc;
	}
	public void setBdcloc(Integer bdcloc) {
		this.bdcloc = bdcloc;
	}
	public Integer getCmdcloc() {
		return cmdcloc;
	}
	public void setCmdcloc(Integer cmdcloc) {
		this.cmdcloc = cmdcloc;
	}
	public Integer getBchloc() {
		return bchloc;
	}
	public void setBchloc(Integer bchloc) {
		this.bchloc = bchloc;
	}
	public Integer getCmchloc() {
		return cmchloc;
	}
	public void setCmchloc(Integer cmchloc) {
		this.cmchloc = cmchloc;
	}
	public Integer getCcloc() {
		return ccloc;
	}
	public void setCcloc(Integer ccloc) {
		this.ccloc = ccloc;
	}
	public Integer getEccloc() {
		return eccloc;
	}
	public void setEccloc(Integer eccloc) {
		this.eccloc = eccloc;
	}

}
