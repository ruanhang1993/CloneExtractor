package cn.edu.fudan.se.clonedetector.bean;

public class MyFileTreeNode {
	String id;
	String pId;
	String name;
	boolean nocheck;
	boolean checked;
	String relativePath;
	
	public MyFileTreeNode() {
	}
	public MyFileTreeNode(String id, String pId, String name, String relativePath, boolean nocheck, boolean checked) {
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.relativePath = relativePath;
		this.nocheck = nocheck;
		this.checked = checked;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getpId() {
		return pId;
	}
	public void setpId(String pId) {
		this.pId = pId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRelativePath() {
		return relativePath;
	}
	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}
	public boolean isNocheck() {
		return nocheck;
	}
	public void setNocheck(boolean nocheck) {
		this.nocheck = nocheck;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
}
