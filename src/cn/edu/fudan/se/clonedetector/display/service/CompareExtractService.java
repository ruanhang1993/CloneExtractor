/**
 * 
 */
package cn.edu.fudan.se.clonedetector.display.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.Author;
import cn.edu.fudan.se.clonedetector.bean.Change;
import cn.edu.fudan.se.clonedetector.bean.Compare;

public class CompareExtractService extends AbstractService {

	public List<Compare> specificedCompares(int projectId) {
		System.out.println("get in CompareExtractService");

		List<Compare> compares = this.dao.getComparesByProjectId(projectId);

		System.out.println("get compares from database successfully ");
		return compares;

	}

	public String getProjectName(int projectId) {
		String projectName = this.dao.getProjectById(projectId).getProjectNameCh();
		return projectName;
	}

}
