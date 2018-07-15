package cn.edu.fudan.se.clonedetector.display.action;

import java.util.List;

import cn.edu.fudan.se.clonedetector.bean.ProcessBar;
import cn.edu.fudan.se.clonedetector.display.service.ProcessBarService;

public class ProcessBarAction extends AbstractAction{
	private boolean successful;
	private List<ProcessBar> list;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String getProcessBar(){
		System.out.println("get process bar...");
		try {
			list = ((ProcessBarService) this.getService()).getProjectsBar();
			successful = true;
		} catch (Exception e) {
			e.printStackTrace();
			successful = false;
		}
		System.out.println("done");
		return SUCCEED;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<ProcessBar> getList() {
		return list;
	}

	public void setList(List<ProcessBar> list) {
		this.list = list;
	}

}
