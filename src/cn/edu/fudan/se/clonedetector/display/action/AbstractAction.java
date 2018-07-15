package cn.edu.fudan.se.clonedetector.display.action;


import com.opensymphony.xwork2.ActionSupport;

import cn.edu.fudan.se.clonedetector.display.service.AbstractService;

public abstract class AbstractAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7228884868019141289L;
	
	protected final static String SUCCEED = "SUCCEED";
	protected final static String FAIL = "FAIL";
	protected AbstractService service ;
	/**
	 * @return the service
	 */
	public AbstractService getService() {
		return service;
	}
	/**
	 * @param service the service to set
	 */
	public void setService(AbstractService service) {
		this.service = service;
	}
}
