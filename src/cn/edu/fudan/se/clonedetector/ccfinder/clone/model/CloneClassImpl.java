package cn.edu.fudan.se.clonedetector.ccfinder.clone.model;

import java.util.LinkedList;
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Commit;

public class CloneClassImpl {
	private int id;
	private List<CloneImpl> clones = new LinkedList<CloneImpl>();

	private Commit commit;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<CloneImpl> getClones() {
		return clones;
	}

	public void setClones(List<CloneImpl> clones) {
		this.clones = clones;
	}

	public Commit getCommit() {
		return commit;
	}

	public void setCommit(Commit commit) {
		this.commit = commit;
	}

	public void addClone(CloneImpl clone) {
		if (!exist(clone)) {
			clone.setClassId(id);
			clones.add(clone);
		}
	}

	private boolean exist(CloneImpl clone) {
		for (CloneImpl c : clones) {
			if (c.equals(clone))
				return true;
		}

		return false;
	}

}
