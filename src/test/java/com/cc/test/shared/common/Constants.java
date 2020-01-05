package com.cc.test.shared.common;

public class Constants {

	public static final String DEFAULT_USER = "xw42420";
	public static final String DEFAULT_PASSWORD = "Welcome13";
	public static final String LOGIN_USER_ELEMENT = "USER";
	public static final String LOGIN_PASSWORD_ELEMENT = "PASSWORD";
	public static final String LOGIN_SUBMIT_ELEMENT = "signonBtn";
	public static final String LOGIN_URL = "";
	public static final String LOGIN_URL_PORD = "";
	public static final String LOGOUT_URL = "";
	public static final String BROWSER = "browser";
	public static final String RESOURCES_PATH = "src/test/resources/";
	
	public enum OrderingParams {
		Name, DataType, ConverterFomat, ASCorDESC
	}
	
	public enum DataType {
		STRING("string"), DOUBLE("double"), INT("int"), DATE("date"), DATETIME("datetime"), TIME("time");
		
		private final String value;
		
		private DataType(String v) {
			this.value = v;
		}
		
		public String value() {
			return this.value;
		}
	}
}
