package cn.edu.fudan.se.clonedetector.bean;

public class CommitLanguage implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer commitId;
	private String language;
	private Integer fileNum;
	private Integer cloneFileNum;
	private Integer loc;
	private Integer eloc;
	private Integer cloc;
	private Integer ecloc;
	private Integer bloc;
	private Integer cmloc;
	private Integer bcloc;
	private Integer cmcloc;
	//’€À„¥˙¬Î
	private Integer ccloc;
	private Integer eccloc;
	
	public CommitLanguage() {
	}


	public Integer getCommitId() {
		return this.commitId;
	}

	public void setCommitId(Integer commitId) {
		this.commitId = commitId;
	}
	public Integer getFileNum() {
		return fileNum;
	}

	public void setFileNum(Integer fileNum) {
		this.fileNum = fileNum;
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

	public Integer getCloneFileNum() {
		return cloneFileNum;
	}

	public void setCloneFileNum(Integer cloneFileNum) {
		this.cloneFileNum = cloneFileNum;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	/**
	 * @return the bloc
	 */
	public Integer getBloc() {
		return bloc;
	}


	/**
	 * @param bloc the bloc to set
	 */
	public void setBloc(Integer bloc) {
		this.bloc = bloc;
	}


	/**
	 * @return the cmloc
	 */
	public Integer getCmloc() {
		return cmloc;
	}


	/**
	 * @param cmloc the cmloc to set
	 */
	public void setCmloc(Integer cmloc) {
		this.cmloc = cmloc;
	}


	/**
	 * @return the bcloc
	 */
	public Integer getBcloc() {
		return bcloc;
	}


	/**
	 * @param bcloc the bcloc to set
	 */
	public void setBcloc(Integer bcloc) {
		this.bcloc = bcloc;
	}


	/**
	 * @return the cmcloc
	 */
	public Integer getCmcloc() {
		return cmcloc;
	}


	/**
	 * @param cmcloc the cmcloc to set
	 */
	public void setCmcloc(Integer cmcloc) {
		this.cmcloc = cmcloc;
	}


	/**
	 * @return the ccloc
	 */
	public Integer getCcloc() {
		return ccloc;
	}


	/**
	 * @param ccloc the ccloc to set
	 */
	public void setCcloc(Integer ccloc) {
		this.ccloc = ccloc;
	}


	/**
	 * @return the eccloc
	 */
	public Integer getEccloc() {
		return eccloc;
	}


	/**
	 * @param eccloc the eccloc to set
	 */
	public void setEccloc(Integer eccloc) {
		this.eccloc = eccloc;
	}
	
}
