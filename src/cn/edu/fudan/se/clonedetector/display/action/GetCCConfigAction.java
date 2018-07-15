package cn.edu.fudan.se.clonedetector.display.action;

import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.CCStreamProperty;
import cn.edu.fudan.se.clonedetector.display.service.GetCCConfigService;

public class GetCCConfigAction extends AbstractAction {
	private List<CCStreamProperty> list;

	private boolean successful;
	
	public String getCCConfigure() {
		list = ((GetCCConfigService) this.getService()).getCC();
		System.out.println("CCConfigure size is :" + list.size());
		successful = true;
		return SUCCEED;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean isSuccessful) {
		this.successful = isSuccessful;
	}

	public List<CCStreamProperty> getList() {
		return list;
	}

	public void setList(List<CCStreamProperty> list) {
		this.list = list;
	}

}
