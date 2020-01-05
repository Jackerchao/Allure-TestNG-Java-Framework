package com.cc.test.helper;

public class EnumUtils {
	
	public enum TestEnvironment {
		UAT, SIT
	}
	
	public enum BrowserType {
		Chrome("Chrome"), IE("IE"), FireFox("FireFox");
		
		private String value;
		
		private BrowserType(String value) {
			this.value = value;
		}
		
		public String toString() {
			return String.valueOf(this.value);
		}
	}

}
