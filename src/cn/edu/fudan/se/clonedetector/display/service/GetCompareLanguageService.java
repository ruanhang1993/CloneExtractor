package cn.edu.fudan.se.clonedetector.display.service;

import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.CompareLanguage;

public class GetCompareLanguageService extends AbstractService {
	public List<CompareLanguage> getList(int compareId) {
		return this.dao.getCompareLanguageByCompare(compareId);		
	}
}
