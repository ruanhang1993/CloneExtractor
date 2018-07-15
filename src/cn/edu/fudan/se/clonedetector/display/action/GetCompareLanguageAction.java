package cn.edu.fudan.se.clonedetector.display.action;

import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.CompareLanguage;
import cn.edu.fudan.se.clonedetector.display.service.GetCompareLanguageService;

public class GetCompareLanguageAction extends AbstractAction {
	private boolean successful;
	private List<CompareLanguage> list;
	private int compareId;

	public String getCompareLanguage() {
		list = ((GetCompareLanguageService) this.getService()).getList(compareId);
		successful = true;
		System.out.println("getCompareLanguage " + compareId);
		return SUCCEED;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public List<CompareLanguage> getList() {
		return list;
	}

	public void setList(List<CompareLanguage> list) {
		this.list = list;
	}

	public int getCompareId() {
		return compareId;
	}

	public void setCompareId(int compareId) {
		this.compareId = compareId;
	}
}
