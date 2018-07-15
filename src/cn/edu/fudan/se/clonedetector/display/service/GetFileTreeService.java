package cn.edu.fudan.se.clonedetector.display.service;

import java.util.List;

import org.tmatesoft.svn.core.SVNException;

import cn.edu.fudan.se.clonedetector.bean.MyFileTreeNode;
import cn.edu.fudan.se.clonedetector.dao.hibernate.HibernateDao;
import cn.edu.fudan.se.clonedetector.versioncontroll.svn.SVNExtractor;

public class GetFileTreeService extends AbstractService {
	public List<MyFileTreeNode> getFileTree(String url, String port, String username, String password,String tag) throws SVNException {
		SVNExtractor extractor = new SVNExtractor(dao, "");
		boolean testConnection = false;
		try {
			testConnection = extractor.checkURL(url, username, password);
		} catch (SVNException e) {
			e.printStackTrace();
		}
		if(!testConnection){
			return null;
		}else{
			return extractor.getFileTree(url);
		}
	}
}
