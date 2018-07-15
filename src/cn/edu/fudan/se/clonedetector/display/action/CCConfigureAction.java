package cn.edu.fudan.se.clonedetector.display.action;

import cn.edu.fudan.se.clonedetector.display.service.CCConfigureService;

public class CCConfigureAction extends AbstractAction {
	private String streamList;
	private boolean successful;

	
	public String delCCConfigure() {
		System.out.println(streamList);
		successful = ((CCConfigureService) this.getService()).ccConfigure(streamList, CCConfigureService.DEL);
		System.out.println("isSuccessful : " + successful);
		return SUCCEED;
	}
	
	public String addCCConfigure() {
		System.out.println(streamList);
		successful = ((CCConfigureService) this.getService()).ccConfigure(streamList, CCConfigureService.ADD);
		System.out.println("isSuccessful : " + successful);
		return SUCCEED;
	}

	public String updateCCConfigure() {
		System.out.println(streamList);
		successful = ((CCConfigureService) this.getService()).ccConfigure(streamList, CCConfigureService.UPDATE);
		System.out.println("isSuccessful : " + successful);
		return SUCCEED;
	}
	

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean isSuccessful) {
		this.successful = isSuccessful;
	}

	public String getStreamList() {
		return streamList;
	}

	public void setStreamList(String streamList) {
		this.streamList = streamList;
	}
	
}
