package com.cc.test.shared.report;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.qameta.allure.Step;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;

public class Logging {
	
	public static String logMessage = "";
	public static String errorMessage = "";
	private static Log log = LogFactory.getLog(Logging.class);
	private static String caseError = "";
	private static String caseWarn = "";
	@Step("{0}")
	public static void logInfo(String str) {
		String date = getDateString();
		logMessage = logMessage + "<div>" + date + str + "</div>";
		log.info(str);
	}
	
	public static void logHighlight(String str) {
		String date = getDateString();
		logMessage = logMessage + "<div style = \"color: green\">" + date + str + "</div>";
		log.info(str);
	}
	@Step("{0}")
	public static void logError (String str) {
		Assert.assertEquals("Successfully", "Failed");
		String date = getDateString();
		logMessage = logMessage + "<div style = \"color: red\">" + date + str + "</div>";
		if ((errorMessage == null) || (errorMessage.equals(""))) {
			errorMessage = str;
		}
		caseError = caseError + str + "<br>";
		log.error(str);
	}
	
	public static void logWarn (String str) {
		log.warn(str);
		String date = getDateString();
		logMessage = logMessage + "<div style = \"color: #FF7F00\">" + date + str + "</div>";
		
		caseWarn = caseWarn + str + "<br>";
	}
	
	public static void logWarn (String str, boolean isPend) {
		
		logWarn(str);
	}
	
	public static void setDefaultLogValue() {
		logMessage = "";
		errorMessage = "";
		caseError = "";
		caseWarn = "";
	}
	
	public static void info(String info) {
		log.info(info);
	}
	
	public static void errorMsg(String info) {
		log.error(info);
	}
	
	private static String getDateString() {
		String format = "yyyy-MM-dd hh:mm:ss";
		SimpleDateFormat dataFormat = new SimpleDateFormat(format);
		Date date = new Date();
		String dataString = dataFormat.format(date) + "  ";
		return dataString;
	}
	
	public static void addNewStepRun (String stepName) {
		log.info("runId: " + System.getProperty("runId"));
		log.info("suiteRunId:" + System.getProperty("suiteRunId"));
		log.info("caseRunId: " + System.getProperty("caseRunId"));
		addPreviousException();
		setDefaultLogValue();
	}
	
	public static void updateStepRun() {
		caseError = caseError.replace("'", "''");
		caseWarn = caseWarn.replace("'", "''");
	}
	
	public static void addPreviousException() {
		if ((logMessage != null) && (!"".equals(logMessage))) {
			try {
				updateStepRun();
			} catch (Exception e) {
				updateStepRun();
			}
		}
	}
	
	

}
