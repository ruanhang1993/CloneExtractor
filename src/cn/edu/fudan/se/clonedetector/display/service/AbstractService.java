package cn.edu.fudan.se.clonedetector.display.service;

import cn.edu.fudan.se.clonedetector.dao.IDataAccessor;

public abstract class AbstractService {
	IDataAccessor dao = null;
	
	public IDataAccessor getDao() {
		return dao;
	}
	public void setDao(IDataAccessor dao) {
		this.dao = dao;
	}
	
}
