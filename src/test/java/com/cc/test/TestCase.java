package com.cc.test;

import java.util.List;

import com.cc.test.listener.TestFailListener;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import org.testng.annotations.*;

import com.cc.test.flow.LoginFlow;
import com.cc.test.properties.EnvParams;
import com.cc.test.shared.datafactory.CaseDataRequest;
import com.cc.test.shared.datafactory.FetchTestData;
import com.cc.test.shared.datafactory.TestData;
import com.cc.test.shared.report.Logging;
@Listeners({TestFailListener.class})
public class TestCase extends TestBase {

	@BeforeClass
	public void beforeClass() throws Exception {
		this.setTestEnv(EnvParams.getTestEnvironment().name().toLowerCase());
		this.setProjectId(39);
		this.setBrowserVersion("66");
		this.setRunDescription("Automation");
		this.setTestScope(2);// 1 for regression test, 2 for smoke test
		this.setTestReleaseName("");
		this.setTestCycleName("");
		this.setSuiteName("Automation");
		this.setSuiteRunDescription("Test Case Batch Run");
		
		super.beforeClass();
	}
	
	public void beforeMethod(CaseDataRequest<TestData> caseData) throws Exception {
		//this.setElementConfigFile(EnvParams.getElementConfigPath());
		this.setCaseID(caseData.getCommonFields().getCaseID());
		this.setCaseName(caseData.getCommonFields().getCaseName());
		this.setCaseDescription(caseData.getCommonFields().getCaseDesc());
		this.setManualCaseKey(caseData.getCommonFields().getManualCaseKey());
		super.beforeMethod();
		startDriver();
	}
	
	@DataProvider(name = "TestData")
	public Object[][] getGroupedCaseData() throws Exception {
		FetchTestData fetchData = new FetchTestData(EnvParams.getDataFolder(), EnvParams.getDataFile(), EnvParams.getDataSheet());
		// return fetchData.getCaseArray
		Object[][] data = fetchData.getGroupedCaseArray();
		return data;
	}
	
	@Test (dataProvider = "TestData")
	public void fullTestSenario(List<CaseDataRequest<TestData>> caseData) {
		try {
			String operation = "";
			boolean init = true;
			
			for (int step = 0; step < caseData.size(); step++) {
				CaseDataRequest<TestData> stepData = caseData.get(step);
				
				if (stepData.getCommonFields().isRun()) {
					operation = stepData.getDataRequest().getOperation();
				}
				else {
					operation = "";
				}
				if (init == true && operation != "") {
					beforeMethod(stepData);
					init = false;
				}
				AllureLifecycle lifecycle = Allure.getLifecycle();
				//change the test name
				lifecycle.updateTestCase(testResult -> testResult.setName(getCaseName()));
				lifecycle.updateTestCase(testResult -> testResult.setDescription(getCaseDescription()));
				lifecycle.updateTestCase(testResult -> testResult.setTestCaseId(getManualCaseKey()));

				switch (operation.toLowerCase().trim()) {
				case "login":
				LoginFlow loginFlow = new LoginFlow(driver, stepData);
				loginFlow.executeTestCase("");
				break;
				
				}
				
				if (step == caseData.size() - 1 && init == false) {
					afterMethod();
				}
			}
		} catch (Exception e) {
			Logging.logError("Test case failed with exception -" + e.getMessage());
		}
	}
	
	//@AfterMethod
	public void afterMethod() throws Exception {
		clearDriver();
		
		this.setAfterMethodComments("");
		super.afterMethod();
	}
	
	@AfterClass
	public void afterClass() throws Exception {
		super.afterClass();
	}
	
	
	
}
