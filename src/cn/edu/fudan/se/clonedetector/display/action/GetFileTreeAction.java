package cn.edu.fudan.se.clonedetector.display.action;

import java.util.List;

import org.tmatesoft.svn.core.SVNException;

import cn.edu.fudan.se.clonedetector.bean.MyFileTreeNode;
import cn.edu.fudan.se.clonedetector.display.service.GetFileTreeService;

public class GetFileTreeAction extends AbstractAction {
	private String url;
	private String port;
	private String username;
	private String password;
	private String tag;
	private boolean successful;
	private List<MyFileTreeNode> treeNode;

	public String getFileTree() {
		System.out.println("Access");
		System.out.println(username);
		System.out.println(url);
		System.out.println(port);
		System.out.println(password);
		System.out.format("tag : %s%n",tag);
		successful = false;
		try {
			treeNode = ((GetFileTreeService) this.getService()).getFileTree(url, port, username, password, tag);
		} catch (SVNException e) {
			e.printStackTrace();
		}
		
		if(treeNode != null)
			successful = true;
		return SUCCEED;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isSuccessful() {
		return successful;
	}
	
	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * @param isSuccessful
	 *            the isSuccessful to set
	 */
	public void setSuccessful(boolean isSuccessful) {
		this.successful = isSuccessful;
	}
	
	public List<MyFileTreeNode> getTreeNode() {
		return treeNode;
	}

}
