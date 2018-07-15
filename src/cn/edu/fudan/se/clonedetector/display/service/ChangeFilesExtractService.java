/**
 * 
 */
package cn.edu.fudan.se.clonedetector.display.service;

import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.ChangeFile;

public class ChangeFilesExtractService extends AbstractService {

	public List<ChangeFile> specificedChangesFiles(int compareId) {
		System.out.println("get in");

		List<ChangeFile> files = this.dao.getChangeFilesOfCompareByIdCountFrom(compareId, -1, -1);

		System.out.println("get successfully ");
		return files;

	}
}
