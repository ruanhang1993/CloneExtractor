/**
 * 
 */
package cn.edu.fudan.se.clonedetector.display.service;

import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.bean.Compare;

public class OneCompareExtractService extends AbstractService {

	public Compare specifiedCompare(int compareId) {
		System.out.println("get in");

		Compare commit = this.dao.getCompreById(compareId);
		System.out.println("get successfully ");
		return commit;

	}
}
