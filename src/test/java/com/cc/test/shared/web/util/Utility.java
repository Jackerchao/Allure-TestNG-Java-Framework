package com.cc.test.shared.web.util;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Utility {
	
	public static void killProcess() throws IOException {
		
	}
	
	public static void threadWait(int time) {
		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void threadWait(double time) {
		try {
			Thread.sleep( (long) time * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static String exceptionLog(Exception e) {
		String error = e.getMessage();
		int end = -1;
		try {
			end = error.indexOf("revision:");
		} catch (Exception ex) {
			end = -1;
		}
		
		if (end != -1) {
			error = error.substring(0, end);
		}
		
		return error;
	}
	
	public static double meg(double number) {
		int b = (int) Math.round(number * 1000);
		double c = (double) b / 1000;
		return c;
	}
	
	public static String getDateString (Date date, String dateFormat, String... timeZone) {
		SimpleDateFormat ft = setDateFormat(dateFormat, timeZone);
		String dateString = ft.format(date);
		return dateString;
	}
	
	public static Date getDateByIndex(String dateIndex) {
		Date valueDate = null;
		if (!StringUtils.isEmpty(dateIndex)) {
			int dateTime = Integer.parseInt(dateIndex);
			Calendar dateCalendar = getDateCalendarByIndex(dateTime);
			valueDate = dateCalendar.getTime();
		}
		return valueDate;
	}
	
	public static NodeList getNodesByXpath(String filePath, String expression) throws Exception {
		// filePath = "/" + filePath;
		Document doc = null;
		try {
			doc = getDocumnet(filePath);
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
		
		XPathFactory xpathfactory = XPathFactory.newInstance();
		XPath xpath = xpathfactory.newXPath();
		XPathExpression expr = xpath.compile(expression);
		NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		return nl;
	}
	
	public static ArrayList<Node> getChildElements(Element element) {
		NodeList list = element.getChildNodes();
		ArrayList<Node> elementList = new ArrayList<Node>();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				elementList.add(list.item(i));
			}
		}
		return elementList;
	}
	
	public static Document getDocumnet(String path) {
		try {
			Document doc = null;
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(new File(path));
			return doc;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void setClipboarData(String string) {
		StringSelection stringSelection = new StringSelection(string);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
	}
	
	public static void uploadFileByRobot(String filePath) throws AWTException {
		setClipboarData(filePath);
		//native key strokes for CTRL, V and ENTER keys
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		robot.delay(1000);
	}
	
	private static Calendar getDateCalendarByIndex(int dateIndex) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, dateIndex);
		return cal;
	}
	
	public static SimpleDateFormat setDateFormat(String format, String... timeZone) {
		SimpleDateFormat f = new SimpleDateFormat(format);
		if (timeZone.length > 0) {
			f.setTimeZone(TimeZone.getTimeZone(timeZone[0]));
		}
		return f;
	}
	
	public static File[] getFileListByName(String fileName) {
		String path = FileUtils.getUserDirectoryPath() + "\\Downloads";
		File fileDir = new File(path);
		FileFilter fileFilter = new WildcardFileFilter(fileName + "*.xls");
		File[] files = fileDir.listFiles(fileFilter);
		return files;
	}
	
	public static void clearFiles(File[] fileList) {
		for (int i = 0; i < fileList.length; i++) {
			fileList[i].delete();
		}
	}
}
