package com.cc.test.ui.impl;

import org.openqa.selenium.WebDriver;

import com.cc.test.shared.web.util.SeleniumLib;

public class ModuleImplBase {
	
	protected SeleniumLib seleniumLib;
	protected WebDriver driver;
	
	public ModuleImplBase(WebDriver driver) {
		this.driver = driver;
		seleniumLib = new SeleniumLib(driver);
	}

}
