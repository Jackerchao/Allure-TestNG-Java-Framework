package com.cc.test.ui.impl;

import org.openqa.selenium.WebDriver;

import com.cc.test.shared.report.Logging;
import com.cc.test.shared.web.util.Utility;
import com.cc.test.ui.bean.LoginBean;

public class LoginImpl extends ModuleImplBase{
	
	LoginBean loginBean = new LoginBean();
	
	public LoginImpl(WebDriver driver) {
		super(driver);
	}
	
	public boolean login(String user, String password) throws Exception {
		
		try {
			seleniumLib.inputValue(user, loginBean.getUser());
			seleniumLib.inputValue(password, loginBean.getPassword());
			seleniumLib.sendEnter(loginBean.getPassword());
			seleniumLib.click(loginBean.getLogin());
		} catch (Exception e) {
			Logging.logError("Login failed: " + e.getMessage());
			return false;
		}
		if (seleniumLib.isExistsElement(loginBean.getLogin())) {
			Logging.logError("Login failed");
			return false;
		}
	
	Logging.logInfo("Login with user: " + user);
	Utility.threadWait(2);
	seleniumLib.waitPageContentLoad();
	
	return true;
	}

}
