package com.cc.test.shared.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.cc.test.shared.report.Logging;

public class PropUtilBase {
	private ArrayList<String> resourceFileList;
	private Properties props;
	
	public void loadProperties() {
		if (resourceFileList == null) {
			Logging.logError("No properties file exists");
			return;
		} else {
			try {
				//When there is no file related to env, directly props
				//files
				String fileList = resourceFileList.toString();
				if (!fileList.contains("%s")) {
					if (props != null) {
						return;
					}
				}
				
				props = new Properties();
				for (int i = 0; i < resourceFileList.size(); i++) {
					String fileName = resourceFileList.get(i);
					if (fileName.contains("%s")) {
						String testEnv = System.getProperty("TestEnv");
						if (testEnv !=null) {
							fileName = fileName.replace("%s", testEnv.toLowerCase());
							InputStream runProps = PropUtilBase.class.getResourceAsStream("/" + fileName);
							props.load(runProps);
							runProps.close();
						} else {
							Logging.logInfo("TestEnv is not set");
						}
					} else {
						InputStream runProps = PropUtilBase.class.getResourceAsStream("/" + fileName);
						props.load(runProps);
						runProps.close();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getPropOrSystemValue(String propertyPara, String sysPara) {
		String value = System.getenv(sysPara);
		
		if (StringUtils.isEmpty(value)) {
			value = getPropValue(propertyPara);
		}
		
		return value;
	}
	
	public String getPropValue(String propertyPara) {
		loadProperties();
		return props.getProperty(propertyPara);
	}


	public void setResourceFileList(ArrayList<String> resourceFileList) {
		this.resourceFileList = resourceFileList;
	}
	

}
