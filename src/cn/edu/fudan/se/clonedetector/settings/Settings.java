package cn.edu.fudan.se.clonedetector.settings;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class Settings {
	public static String[] projectIndex;
	
	public static String repoLocation;
	
	public static String targetBranchName;

	public static String projectPrefix;

	public static String[] ignoredNames;
	
	public static String systemName;
	
	public static void readSettings() throws ConfigurationException {
		Configuration config = new PropertiesConfiguration("clonedector.properties");
		
		String repoLocation = config.getString("repoLocation");
		if (!repoLocation.endsWith("//")) {
			repoLocation += "//";
		}
		Settings.repoLocation = repoLocation;
		
		String[] projectIndex = config.getStringArray("projectIndex");
		Settings.projectIndex = new String[projectIndex.length];
		for (int i = 0; i < projectIndex.length; i++) {
			String index = projectIndex[i];
			if (index.equals(",")) {
				index = "";
			}
			if (index.endsWith("/")) {
				index = index.substring(0, index.length()-1); 
			}
			Settings.projectIndex[i] = index;
		}
		
		Settings.targetBranchName = config.getString("targetBranchName");
		
		Settings.projectPrefix = config.getString("projectPrefix");
		
		Settings.ignoredNames = config.getStringArray("ignoredNames");
		
		String name = config.getString("systemName");
		if (name.equals("") || name == null) {
			if (!projectPrefix.equals("")) {
				String temp = projectPrefix;
				if (temp.endsWith("/")) {
					temp = temp.substring(0, temp.length()-1);
				}
				
				name = temp.substring(temp.lastIndexOf('/')+1, temp.length());
			}
		}
		Settings.systemName = name;
	}
}

