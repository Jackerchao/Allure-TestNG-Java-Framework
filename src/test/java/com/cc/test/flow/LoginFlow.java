package com.cc.test.flow;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;

import com.cc.test.properties.EnvParams;
import com.cc.test.shared.datafactory.CaseDataRequest;
import com.cc.test.shared.datafactory.TestData;
import com.cc.test.shared.report.Logging;
import com.cc.test.ui.impl.LoginImpl;

public class LoginFlow extends AbstractFlow{
	
	public LoginFlow(WebDriver driver, CaseDataRequest<TestData> caseData) {
		super(driver, caseData);
		System.setProperty("ui.elements.file", EnvParams.getElementConfigPath("element.sit"));
	}
	@Step("Login Flow")
	public void executeTestCase(String flag) throws Exception {
		navigateToSite();
		if (flag.equalsIgnoreCase("")) {
			login(testData);
		} else if (flag.equalsIgnoreCase("au")) {
			loinAu(testData);
		}
	}
    @Step("Login AU")
	public void loinAu(TestData loginData) throws Exception {
		Logging.addNewStepRun("Login website");
		LoginImpl loginImpl = new LoginImpl(driver);
		Logging.logInfo("Input username and password.");
		loginImpl.login(loginData.getUsername(), loginData.getPassword());
		Logging.updateStepRun();
		
	}
    @Step("Login")
	public void login(TestData loginData) throws Exception {
		Logging.addNewStepRun("Login website");
		LoginImpl loginImpl = new LoginImpl(driver);
		Logging.logInfo("Input username and password.");
		loginImpl.login(loginData.getUsername(), loginData.getPassword());
		Logging.updateStepRun();
		
	}
    @Step("Launch Application")
	public void navigateToSite() {
		Logging.addNewStepRun("Navigate to site");
		String testLink = getTestLink();
		Logging.logInfo(testLink);
		driver.navigate().to(testLink);
		Logging.updateStepRun();
	}

	private String getTestLink() {
		
		return EnvParams.getApplicationURL("Link.SIT");
	}
}
