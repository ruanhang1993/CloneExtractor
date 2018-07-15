package cn.edu.fudan.se.clonedetector.ccfinder.clone.model;

import java.util.List;

public class FileGroup {
	private List<String> files;
	
	public FileGroup(List<String> files) {
		this.files = files;
	}
	
	public List<String> getFiles() {
		return files;
	}
}
