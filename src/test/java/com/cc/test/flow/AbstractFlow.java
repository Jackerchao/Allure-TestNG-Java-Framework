package com.cc.test.flow;

import org.openqa.selenium.WebDriver;

import com.cc.test.shared.datafactory.CaseDataRequest;
import com.cc.test.shared.datafactory.TestData;
import com.cc.test.shared.web.util.SeleniumLib;

public abstract class AbstractFlow {
	protected WebDriver driver = null;
	protected SeleniumLib seleniumLib = null;
	protected TestData testData;
	
	public AbstractFlow(WebDriver driver, CaseDataRequest<TestData> testData) {
		this.driver = driver;
		this.seleniumLib = new SeleniumLib(driver);
		this.testData = testData.getDataRequest();
	}

}
