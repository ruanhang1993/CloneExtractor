package cn.edu.fudan.se.clonedetector.display.service;

import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.CCStreamProperty;
import cn.edu.fudan.se.clonedetector.dao.hibernate.HibernateDao;

public class GetCCConfigService extends AbstractService {
	public List<CCStreamProperty> getCC() {
		List<CCStreamProperty> properties = dao.getCCStreams();
		return properties;
	}

}
