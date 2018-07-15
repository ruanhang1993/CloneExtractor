package cn.edu.fudan.se.clonedetector.bean;

/**
 * @author echo
 *
 */
public class CloneInChange {
	private Integer cloneInChangeId;
	private Integer changeId;
	private Integer cloneId;
	private Integer cloneClassId;
	private Integer cloneType;
	private Integer changeType;
	private Integer beginLine;
	private Integer endLine;
	private Integer compareId;
	private Integer fileId;
	private Integer ecloc;
	private Integer bcloc;
	private Integer cmcloc;
	private Integer ccloc;
	private Integer eccloc;
	public CloneInChange() {
	}

	public CloneInChange(Integer cloneInChangeId, Integer changeId,
			Integer cloneId, Integer cloneClassId, Integer cloneType,
			Integer changeType, Integer beginLine, Integer endLine) {
		this.cloneInChangeId = cloneInChangeId;
		this.changeId = changeId;
		this.cloneId = cloneId;
		this.cloneClassId = cloneClassId;
		this.cloneType = cloneType;
		this.changeType = changeType;
		this.beginLine = beginLine;
		this.endLine = endLine;
	}

	public Integer getCloneInChangeId() {
		return cloneInChangeId;
	}

	public void setCloneInChangeId(Integer cloneInChangeId) {
		this.cloneInChangeId = cloneInChangeId;
	}

	public Integer getChangeId() {
		return changeId;
	}

	public void setChangeId(Integer changeId) {
		this.changeId = changeId;
	}

	public Integer getCloneId() {
		return cloneId;
	}

	public void setCloneId(Integer cloneId) {
		this.cloneId = cloneId;
	}

	public Integer getCloneClassId() {
		return cloneClassId;
	}

	public void setCloneClassId(Integer cloneClassId) {
		this.cloneClassId = cloneClassId;
	}

	public Integer getCloneType() {
		return cloneType;
	}

	public void setCloneType(Integer cloneType) {
		this.cloneType = cloneType;
	}

	public Integer getChangeType() {
		return changeType;
	}

	public void setChangeType(Integer changeType) {
		this.changeType = changeType;
	}

	public Integer getBeginLine() {
		return beginLine;
	}

	public void setBeginLine(Integer beginLine) {
		this.beginLine = beginLine;
	}

	public Integer getEndLine() {
		return endLine;
	}

	public Integer getCompareId() {
		return compareId;
	}

	public void setCompareId(Integer compareId) {
		this.compareId = compareId;
	}

	public void setEndLine(Integer endLine) {
		this.endLine = endLine;
	}

	public Integer getFileId() {
		return fileId;
	}

	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}

	public Integer getEcloc() {
		return ecloc;
	}

	public void setEcloc(Integer ecloc) {
		this.ecloc = ecloc;
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
