package com.cc.test.shared.web;

public enum Browser {
	CHROME, IE, FF, FIREFOX, HTMLUNIT, PHANTOMJS;
	
	@Override
	public String toString() {
		return this.name();
	}

}
