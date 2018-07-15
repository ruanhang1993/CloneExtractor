package cn.edu.fudan.se.clonedetector.ccfinder.clone.model;

import cn.edu.fudan.se.clonedetector.bean.Commit;

public class CloneImpl {
	private int id;
	private int classId;
	private String file;
	private int startIndex, endIndex;
	private String fragment;
	private int cloneSize;
	private int type = 0;
	public static int TYPE1 = 1; // identical
	public static int TYPE2 = 2;
	private Commit commit;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	public String getFragment() {
		return fragment;
	}

	public void setFragment(String fragment) {
		this.fragment = fragment;
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public int getCloneSize() {
		return cloneSize;
	}

	public void setCloneSize(int cloneSize) {
		this.cloneSize = cloneSize;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Commit getCommit() {
		return commit;
	}

	public void setCommit(Commit commit) {
		this.commit = commit;
	}

	@Override
	public boolean equals(Object clone) {
		if (!(clone instanceof CloneImpl))
			return false;

		CloneImpl c = (CloneImpl) clone;
		if (file.equals(c.getFile()) && startIndex == c.getStartIndex() && endIndex == c.getEndIndex())
			return true;

		return false;

	}

	public String toString() {
		return file + ":" + startIndex + "," + endIndex + ":" + classId;
	}
}
