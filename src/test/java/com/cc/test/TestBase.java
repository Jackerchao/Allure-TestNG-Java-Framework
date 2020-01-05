package com.cc.test;

import org.openqa.selenium.WebDriver;

import com.cc.test.properties.EnvParams;

import com.cc.test.shared.report.Logging;
import com.cc.test.shared.web.util.SeleniumLib;

public class TestBase {
	
	private String caseID;
	private String caseName;
	private String caseDescription;
	private String manualCaseKey;
	private String afterMethodComments = "";
	private String browserVersion = "";
	private String testExecutor = "";
	private String runDescription = "";
	private int testScope; // 1 for regression test, 2 for smoke test
	private String testReleaseName = "";
	private String testCycleName = "";
	private String suiteName = "";
	private String suiteRunDescription = "";
	private int projectId;
	private String testEnv;
	private String elementConfigFile;
	public static WebDriver driver = null;
	protected SeleniumLib seleniumlib = null;
	
	// @BeforeClass
	public void beforeClass() throws Exception {
		System.setProperty("TestEnv", testEnv);
		Logging.setDefaultLogValue();
		Logging.logInfo("Automation testing start");
		Logging.setDefaultLogValue();		
		Logging.logInfo("runId=" + System.getProperty("runId"));
		Logging.logInfo("Current execution user account:" + System.getProperty("user.name"));
		Logging.logInfo("Project inin() -- End");
	}
	
	// @BeforeMethod
	public void beforeMethod() throws Exception {
		//System.setProperty("ui.elements.file", elementConfigFile);
		Logging.setDefaultLogValue();
	}
	
	// @AfterMethod
	public void afterMethod() throws Exception {
		Logging.addPreviousException();
	}
	
	// @AfterClass
	public void afterClass() throws Exception {
		Logging.setDefaultLogValue();
	}
	
	public void clearDriver() {
		if (driver != null) {
			try {
				seleniumlib.quitDriver();
			} catch (Exception e) {
				Logging.logWarn("Failed to quite driver, exception" + e.getMessage());
			}
		}
	}
	
	public void startDriver() {
		seleniumlib = new SeleniumLib(null);
		driver = seleniumlib.startDriver(EnvParams.getBrowserType(), EnvParams.getWebDriverPath());
	}
	
	public String getAfterMethodComments() {
		return afterMethodComments;
	}
	
	public void setAfterMethodComments(String afterMethodComments) {
		this.afterMethodComments = afterMethodComments;
	}

	public String getCaseID() {
		return caseID;
	}

	public void setCaseID(String caseID) {
		this.caseID = caseID;
	}

	public String getCaseName() {
		return caseName;
	}

	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}

	public String getCaseDescription() {
		return caseDescription;
	}

	public void setCaseDescription(String caseDescription) {
		this.caseDescription = caseDescription;
	}
	public String getManualCaseKey() {
		return manualCaseKey;
	}

	public void setManualCaseKey(String manualCaseKey) {
		this.manualCaseKey = manualCaseKey;
	}

	public String getBrowserVersion() {
		return browserVersion;
	}

	public void setBrowserVersion(String browserVersion) {
		this.browserVersion = browserVersion;
	}

	public String getTestExecutor() {
		return testExecutor;
	}

	public void setTestExecutor(String testExecutor) {
		this.testExecutor = testExecutor;
	}

	public String getRunDescription() {
		return runDescription;
	}

	public void setRunDescription(String runDescription) {
		this.runDescription = runDescription;
	}

	public int getTestScope() {
		return testScope;
	}

	public void setTestScope(int testScope) {
		this.testScope = testScope;
	}

	public String getTestReleaseName() {
		return testReleaseName;
	}

	public void setTestReleaseName(String testReleaseName) {
		this.testReleaseName = testReleaseName;
	}

	public String getTestCycleName() {
		return testCycleName;
	}

	public void setTestCycleName(String testCycleName) {
		this.testCycleName = testCycleName;
	}

	public String getSuiteName() {
		return suiteName;
	}

	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
	}

	public String getSuiteRunDescription() {
		return suiteRunDescription;
	}

	public void setSuiteRunDescription(String suiteRunDescription) {
		this.suiteRunDescription = suiteRunDescription;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getTestEnv() {
		return testEnv;
	}

	public void setTestEnv(String testEnv) {
		this.testEnv = testEnv;
	}

	public String getElementConfigFile() {
		return elementConfigFile;
	}

	public void setElementConfigFile(String elementConfigFile) {
		this.elementConfigFile = elementConfigFile;
	}
	

}
