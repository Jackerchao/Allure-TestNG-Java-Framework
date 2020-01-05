package com.cc.test.ui.bean;

public class ModuleBeanBase {
	public ModuleBeanBase() {
		
	}
	
	public String getElement(String area, String section, String elementName) {
		String element_pathformat = "//Area[@Name='%s']/Section[@Name='%s']/Element[@Name='%s']";
		return String.format(element_pathformat, area, section, elementName);
	}

}
