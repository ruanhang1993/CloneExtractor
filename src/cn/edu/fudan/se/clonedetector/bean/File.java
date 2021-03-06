package cn.edu.fudan.se.clonedetector.bean;


// Generated 2016-5-20 10:49:33 by Hibernate Tools 3.4.0.CR1

/**
 * File generated by hbm2java
 */
public class File implements java.io.Serializable {

	private Integer fileId;
	private String fileName;// local fs file name
	private String validFileName;// valid file name, 如果是处于RELEASE_VERSION_TYPE模式，则是相对于发布版本根目录的相对路径；否则，等于fileName

	private Integer commitId;
	private byte[] content;
	private Integer loc;
	private Integer eloc;
	private Integer cloc;
	private Integer ecloc;
	private Integer bloc;
	private Integer cmloc;
	private Integer bcloc;
	private Integer cmcloc;
	//折算代码
	private Integer ccloc;
	private Integer eccloc;
	
	private Integer cloneNum;
	private Integer cloneClassNum;
	
	private String fileType;
	private int renameTag;
	public static final int NO_RENAME_TAG = 0;
	public static final int RENAME_RN_TAG = 1;

	public File() {
	}

	public File(String fileName, Integer commitId, byte[] content) {
		this.fileName = fileName;
		this.commitId = commitId;
		this.content = content;
	}

	public Integer getFileId() {
		return this.fileId;
	}

	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Integer getCommitId() {
		return this.commitId;
	}

	public void setCommitId(Integer commitId) {
		this.commitId = commitId;
	}

	public byte[] getContent() {
		return this.content;
	}

	public void setContent(byte[] content) {
		this.content = content;
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
	

	public String getValidFileName() {
		return validFileName;
	}

	public void setValidFileName(String validFileName) {
		this.validFileName = validFileName;
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

	public Integer getCloneNum() {
		return cloneNum;
	}

	public void setCloneNum(Integer cloneNum) {
		this.cloneNum = cloneNum;
	}

	/**
	 * @return the cloneClassNum
	 */
	public Integer getCloneClassNum() {
		return cloneClassNum;
	}

	/**
	 * @param cloneClassNum the cloneClassNum to set
	 */
	public void setCloneClassNum(Integer cloneClassNum) {
		this.cloneClassNum = cloneClassNum;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public int getRenameTag() {
		return renameTag;
	}

	public void setRenameTag(int renameTag) {
		this.renameTag = renameTag;
	}

}
