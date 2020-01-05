package com.cc.test.ui.bean;

import com.cc.test.shared.web.UIElement;

public class LoginBean  extends ModuleBeanBase {
	private String area = "LoginPage";
	private String section = "Login";
	
	public UIElement getUser() {
		return new UIElement(area, section, "User");
	}
	
	public UIElement getPassword() {
		return new UIElement(area, section, "Password");
	}
	
	public UIElement getLogin() {
		return new UIElement(area, section, "Login");
	}

}
