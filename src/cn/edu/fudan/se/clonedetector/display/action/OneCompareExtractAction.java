package cn.edu.fudan.se.clonedetector.display.action;

import cn.edu.fudan.se.clonedetector.bean.Compare;
import cn.edu.fudan.se.clonedetector.display.service.OneCompareExtractService;

public class OneCompareExtractAction extends AbstractAction {
	// private Map<String, List<FileDisplayInfo>> CompareMap;
	private Compare compare;

	private boolean successful;
	private static final long serialCompareUID = 1L;
	private int compareId;

	public String extractOneCompare() {
		System.out.println("extracting compare..." + compareId);
		compare = (Compare) ((OneCompareExtractService) this.getService()).specifiedCompare(compareId);

		this.setSuccessful(true);
		System.out.println("done");
		return SUCCEED;
	}

	/**
	 * @return the isSuccessful
	 */
	public boolean isSuccessful() {
		return successful;
	}

	/**
	 * @param isSuccessful
	 *            the isSuccessful to set
	 */
	public void setSuccessful(boolean isSuccessful) {
		this.successful = isSuccessful;
	}

	public Compare getCompare() {
		return compare;
	}

	public void setCompare(Compare compare) {
		this.compare = compare;
	}

	public int getCompareId() {
		return compareId;
	}

	public void setCompareId(int compareId) {
		this.compareId = compareId;
	}

}
