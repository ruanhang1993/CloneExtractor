package cn.edu.fudan.se.clonedetector.display.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.edu.fudan.se.clonedetector.bean.CCStreamProperty;
import cn.edu.fudan.se.clonedetector.dao.IDataAccessor;
import cn.edu.fudan.se.clonedetector.dao.hibernate.HibernateDao;

public class CCConfigureService extends AbstractService {

	public static final int ADD = 1;
	public static final int UPDATE = 2;
	public static final int DEL = 3;
	private Collection<CCStreamProperty> list;
	
	private boolean configCC(IDataAccessor dao, CCStreamProperty property, int type) {
		switch (type) {
		case ADD:
			return addCC(dao, property);
		case UPDATE:
			return updateCC(dao, property);
		case DEL:
			return delCC(dao, property);
		default:
			return false;
		}
	}
	
	private boolean addCC(IDataAccessor dao, CCStreamProperty property) {
		try {
			dao.saveCCStreamProperty(property);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private boolean delCC(IDataAccessor dao, CCStreamProperty property) {
		try {
			dao.delCCStreamProperty(property);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private boolean updateCC(IDataAccessor dao, CCStreamProperty property) {
		try {
			dao.updateCCStreamProperty(property);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public boolean ccConfigure(String streamList, int type) {
		boolean result = true;
//		IDataAccessor dao = HibernateDao.getInstance();
		if (type ==DEL ) {
			result = parseCCStreamStr(streamList);
		}else {
			result = parseCCStreamJson(streamList);
		}		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			CCStreamProperty property = (CCStreamProperty) iterator.next();
			if (!configCC(dao, property, type)) {
				result = false;
			}
		}
		return result;
	}
	
	private boolean parseCCStreamStr(String streamList){
		list = new ArrayList<>();
		try {
			String[] array = streamList.replaceAll("\\s","").split(",");
			for (int i = 0; i < array.length; i++) {
				CCStreamProperty property = new CCStreamProperty();
				property.setStream(array[i]);
				list.add(property);				
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean parseCCStreamJson(String streamList){
		list = new ArrayList<>();
		try {
			JSONArray array = new JSONArray(streamList);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				CCStreamProperty property = new CCStreamProperty();
				property.setBlstream(obj.getString("blstream"));
				property.setStream(obj.getString("stream"));
				property.setView(obj.getString("view"));
				property.setViewLocalPath(obj.getString("viewLocalPath"));
				list.add(property);				
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
