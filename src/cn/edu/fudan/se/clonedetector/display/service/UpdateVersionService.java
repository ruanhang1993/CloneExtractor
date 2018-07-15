package cn.edu.fudan.se.clonedetector.display.service;

import cn.edu.fudan.se.clonedetector.bean.Commit;

public class UpdateVersionService extends AbstractService {
	public boolean updateVersion(String[] id, String[] val){
		for(int i = 0; i < id.length; i++){
			int index = Integer.parseInt(id[i]);
			Commit temp = this.dao.getCommitById(index);
			temp.setCommitCode(val[i]);
			this.dao.updateCommit(temp);
		}
		return true;
	}
}
