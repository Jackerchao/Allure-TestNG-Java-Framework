package com.cc.test.properties;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cc.test.helper.EnumUtils.BrowserType;
import com.cc.test.helper.EnumUtils.TestEnvironment;
import com.cc.test.shared.report.Logging;
import com.cc.test.shared.web.Browser;

public class EnvParams {
	
	public static String getReleaseName() {
		return PropUtils.getInstance().getPropOrSystemValue(PropertyConstants.RUN_RELASE_NAME, JenkinsParas.JENKINS_RELEASE_NAME);
		
	}
	
	public static String getCycleName() {
		return PropUtils.getInstance().getPropOrSystemValue(PropertyConstants.RUN_CYCLE_NAME, JenkinsParas.JENKINS_CYCLE_NAME);
		
	}
	
	public static String getDataFolder() {
		return PropUtils.getInstance().getPropOrSystemValue(PropertyConstants.RUN_DATA_FOLDER, JenkinsParas.JENKINS_DATA_FOLDER);
		
	}
	
	public static String getDataFile() {
		return PropUtils.getInstance().getPropOrSystemValue(PropertyConstants.RUN_DATA_FILE, JenkinsParas.JENKINS_DATA_FILE);
		
	}
	
	public static String getDataSheet() {
		return PropUtils.getInstance().getPropOrSystemValue(PropertyConstants.RUN_DATA_SHEET, JenkinsParas.JENKINS_DATA_SHEET);
		
	}
	
	public static BrowserType getBrowser() {
		String browser = PropUtils.getInstance().getPropOrSystemValue(PropertyConstants.RUN_DEFAULT_BROWSER, JenkinsParas.JENKINS_BROWSER);
		
		if ("IE".equals(browser.toUpperCase())) {
			return BrowserType.IE;
		} else if ("CHROME".equals(browser.toUpperCase())) {
			return BrowserType.Chrome;
		} else if ("FIREFOX".equals(browser.toUpperCase())) {
			return BrowserType.FireFox;
		} else {
			return BrowserType.IE;
		}
		
	}
	
	public static Browser getBrowserType() {
		
		String browser = PropUtils.getInstance().getPropOrSystemValue(PropertyConstants.RUN_DEFAULT_BROWSER, JenkinsParas.JENKINS_BROWSER);
		
		if ("IE".equals(browser.toUpperCase())) {
			return Browser.IE;
		} else if ("CHROME".equals(browser.toUpperCase())) {
			return Browser.CHROME;
		} else if ("FIREFOX".equals(browser.toUpperCase())) {
			return Browser.FIREFOX;
		} else {
			return Browser.IE;
		}
		
	}
	
	public static String getWebDriverPath() {
		if (getBrowserType().equals(Browser.FIREFOX)) {
			return PropUtils.getInstance().getPropValue(PropertyConstants.WEBDRIVER_FIREFOX_PATH); 
		} else if (getBrowserType().equals(Browser.IE)) {
			return PropUtils.getInstance().getPropValue(PropertyConstants.WEBDRIVER_IE_PATH); 
		} else {
			return PropUtils.getInstance().getPropValue(PropertyConstants.WEBDRIVER_CHROME_PATH); 
		}
	}
	
	public static List<Integer> getRerunTestCases() {
		String testCases = PropUtils.getInstance().getPropOrSystemValue(PropertyConstants.RUN_RERUN_CASEINDEXS, JenkinsParas.JENKINS_RERUN_TESTCASES);
		List<Integer> reruncaseList = new ArrayList<Integer>();
		
		if (StringUtils.isEmpty(testCases) || StringUtils.isEmpty(testCases.trim())) {
			return reruncaseList;
		}
		
		String[] caseList = testCases.split(",");
		
		for (int i = 0; i < caseList.length; i++) {
			if (caseList[i].contains("-")) {
				String[] caseFromTo = caseList[i].split("-");
				int caseFrom = Integer.parseInt(caseFromTo[0]);
				int caseTo = Integer.parseInt(caseFromTo[1]);
				for (int j = caseFrom; j < caseTo; j++ ) {
					reruncaseList.add(j);
				}
			} else {
				reruncaseList.add(Integer.parseInt(caseList[i]));
			}
		}
		
		return reruncaseList;
		
		
	}
	
	public static String getApplicationURL(String application) {
		String url = "";
		if (application.equalsIgnoreCase("link.sit")) {
			url = PropUtils.getInstance().getPropValue(PropertyConstants.ENV_LINK_TEST);
		} else if (application.equalsIgnoreCase("link.uat")) {
			url = PropUtils.getInstance().getPropValue(PropertyConstants.ENV_LINK_TEST);
		}
		
		return url;
	}
	
	public static TestEnvironment getTestEnvironment() {
		String environment = PropUtils.getInstance().getPropOrSystemValue(PropertyConstants.RUN_TEST_ENVIRONMENT, JenkinsParas.JENKINS_ENVIRONMNET);
		return getTestEnvironment(environment);
	}
	
	public static TestEnvironment getTestEnvironment(String environment) {
		if ("uat".equalsIgnoreCase(environment)) {
			return TestEnvironment.UAT;
		} else if ("sit".equalsIgnoreCase(environment)) {
			return TestEnvironment.SIT;
		} else {
			Logging.logError("TestEnvironment is set null");
			return null;
		}
	}
	
	public static String getElementConfigPath(String application) {
		String str = "";
		if (application.equalsIgnoreCase("element.sit")){
			str = PropUtils.getInstance().getPropValue(PropertyConstants.ENV_ELEMENTS_TEST);	
		} else if (application.equalsIgnoreCase("element.uat")) {
			str = PropUtils.getInstance().getPropValue(PropertyConstants.ENV_ELEMENTS_TEST);
		}
		
		return str;
	}

}
