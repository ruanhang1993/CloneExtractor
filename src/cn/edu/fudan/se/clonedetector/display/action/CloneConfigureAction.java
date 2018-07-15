package cn.edu.fudan.se.clonedetector.display.action;

import java.util.List;

import cn.edu.fudan.se.clonedetector.display.service.CloneConfigureService;

public class CloneConfigureAction extends AbstractAction {
	private int mincloc;
	private int mintoken;
	private int minclass;
	private String encoding;
	private List<String> list;
	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	private boolean successful;

	public String cloneConfigure() {
		System.out.println(this.mincloc);
		System.out.println(this.mintoken);
		System.out.println(this.minclass);
		System.out.println(this.encoding);
		successful = ((CloneConfigureService) this.getService()).setClone(mintoken, mincloc, minclass, encoding);
		System.out.println("isSuccessful " + successful);
		return SUCCEED;
	}
	
	//add by Junyi
	public String getCloneConfigure() {
		//0 - minLine 
		//1 - minToken 
		//2 - minClass
		//3 - encoding
		list = ((CloneConfigureService) this.getService()).getClone();
		if(list.size()>0)
			successful = true;
		else
			successful = false;
		System.out.println("getCloneConfigure isSuccessful :" + successful);
		System.out.println(list.get(0));
		System.out.println(list.get(1));
		System.out.println(list.get(2));
		System.out.println(list.get(3));

		return SUCCEED;
	}

	public int getMincloc() {
		return mincloc;
	}

	public void setMincloc(int mincloc) {
		this.mincloc = mincloc;
	}

	public int getMintoken() {
		return mintoken;
	}

	public void setMintoken(int mintoken) {
		this.mintoken = mintoken;
	}

	public int getMinclass() {
		return minclass;
	}

	public void setMinclass(int minclass) {
		this.minclass = minclass;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean isSuccessful) {
		this.successful = isSuccessful;
	}

}
