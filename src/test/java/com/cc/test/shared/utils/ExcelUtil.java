package com.cc.test.shared.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cc.test.shared.report.Logging;

public class ExcelUtil {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	private static DecimalFormat df = new DecimalFormat("#");
	
	public static XSSFWorkbook getWorkBook(String filePath) throws Exception {
		
		InputStream fileSteam;
		
		if (!filePath.contains("https://")) {
			fileSteam = new FileInputStream(new File(filePath));
			XSSFWorkbook workbook = new XSSFWorkbook(fileSteam);
			fileSteam.close();
			return workbook;
		} else {
			String urlStr = filePath;
			URL url = new URL(urlStr);
			URLConnection uc = url.openConnection();
			if(url.getUserInfo() != null) {
				String basicAuth = "Basic " + new String(new Base64().encode(url.getUserInfo().getBytes()));
				uc.setRequestProperty("Authorization", basicAuth);
			}
			
			fileSteam = uc.getInputStream();
			XSSFWorkbook workbook = new XSSFWorkbook(fileSteam);
			fileSteam.close();
			return workbook;
		}
	}
	
	/**
	 * Get HSSFWorkbook instance with a string
	 * read excel that version is before 2003
	 * @param filePath
	 */
	
	public static HSSFWorkbook getWorkbook(String filePath) {
		HSSFWorkbook workbook = null;
		InputStream in = null;
		
		try {
			in = new FileInputStream(filePath);
			workbook = new HSSFWorkbook(in);
		} catch (FileNotFoundException e1){
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return workbook;
		
	}
	
	/**
	 * Get a sheet with index
	 * @param filePath
	 * @param index
	 */
	public static Sheet getSheet(String filePath, int index) {
		Sheet sheet = getWorkbook(filePath).getSheetAt(index);
		return sheet;
		
	}
	
	
	
	public static Sheet getXSSFSheet (String filePath, int index) {
		XSSFSheet sheet = null;
		try {
			sheet = getWorkBook(filePath).getSheetAt(index);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sheet;
	}
	
	public static Sheet getXSSFSheet (String filePath, String sheetName) {
		XSSFSheet sheet = null;
		try {
			sheet = getWorkBook(filePath).getSheet(sheetName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sheet;
	}
	
	public void downloadFile(String fileURL, String targetPath) {
		try {
			doTrustToCertificates();
			URL u =new URL(fileURL);
			File file = new File(targetPath);
			if (!file.exists()) {
				FileUtils.copyURLToFile(u, file);
				System.out.println(fileURL+ "download");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static XSSFWorkbook getHugeWorkBook(String file) throws Exception {
		InputStream fileSteam;
		fileSteam = new FileInputStream(new File(file));
		XSSFWorkbook workbook = new XSSFWorkbook(fileSteam);
		fileSteam.close();
		return workbook;
	}
	
	public static void copyFile(String fromFile, String toFile) {
		
		File from = new File(fromFile);
		File to = new File(toFile);
		try {
			FileUtils.copyFile(from, to);
			Logging.logInfo(toFile + " is copied");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<Map<String, Object>> loadExcelDataToListMap(XSSFSheet xssfSheet) throws Exception {
		
		XSSFRow headerRow = xssfSheet.getRow(0);
		int columns = headerRow.getLastCellNum();
		List<Map<String, Object>> mlist = new ArrayList<Map<String, Object>>();
		for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
			XSSFRow xssfRow = xssfSheet.getRow(rowNum);
			if (xssfRow != null) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 0; i < columns; i++ ) {
					XSSFCell key_cell = headerRow.getCell(i);
					XSSFCell cell = xssfRow.getCell(i);
					map.put(getStringValue(key_cell), getValue(cell));
				}
			}
		}
		return mlist;
		
	}
	
	public static List<Map<String, Object>> loadExcelDataToListMap(String filePath, String sheetName) throws Exception {
		XSSFWorkbook xssfWorkbook = ExcelUtil.getWorkBook(filePath);
		XSSFSheet xssfSheet = xssfWorkbook.getSheet(sheetName);
		if (xssfSheet == null) {
			return null;
		}
		
		return loadExcelDataToListMap(xssfSheet);
	}
	
	public static List<Map<String, Object>> loadExcelDataToListMap(String filePath) throws Exception {
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		XSSFWorkbook xssfWorkbook = ExcelUtil.getWorkBook(filePath);
		for (int i = 0; i < xssfWorkbook.getNumberOfSheets(); i++) {
			mapList.addAll(loadExcelDataToListMap(xssfWorkbook.getSheetAt(i)));
		}
		return mapList;
				
	}
	
	
	public static void writeValueToExcel(XSSFWorkbook workBook, String filePath) throws Exception {
		FileOutputStream outFile = new FileOutputStream(new File(filePath));
		workBook.write(outFile);
		outFile.close();
	}
	
	public static FileOutputStream writeValueToExcelNotClose(XSSFWorkbook workBook, String filePath) throws Exception {
		FileOutputStream outFile = new FileOutputStream(new File(filePath));
		workBook.write(outFile);
		return outFile;
	}
	
	public static void writeValueToExcel(String filePath) throws Exception {
		//doesn't work
		XSSFWorkbook workBook = getWorkBook(filePath);
		FileOutputStream outFile = new FileOutputStream(new File(filePath));
		workBook.write(outFile);
		outFile.close();
	}
	
	public static String getValueByColunmName(Row headerRow, Row dataRow, String colunmName) {
		return getValueByInRowIndex(dataRow, getExcelColumnIndex(headerRow, colunmName));
	}
	
	public static String getValueByInRowIndex(Row row, int colunmIndex) {
		if (colunmIndex < 0) {
			return null;
		}
		
		String text = "";
		try {
			Cell cell = row.getCell(colunmIndex);
			if (cell == null) {
				return null;
			}
			
			switch (cell.getCellType()){
			case Cell.CELL_TYPE_BOOLEAN:
				text = cell.getBooleanCellValue() + "";
				break;
			case Cell.CELL_TYPE_NUMERIC:
				text = (int)Math.round(cell.getNumericCellValue()) + "";
				break;
			case Cell.CELL_TYPE_STRING:
				text = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_BLANK:
				text = "";
				break;
			default:
				text = cell.getStringCellValue();
					
			}
			
		} catch (NullPointerException ex) {
			text = "";
		}
		
		return text;
	}
	
	public static int getExcelColumnIndex(Row headRow, String columnName) {
		Map<String, Integer> headers = getExcelHeadersNameAndIndex(headRow);
		if(headers.containsKey(columnName)) {
			return headers.get(columnName);
		} else {
			return -1;
		}
	}
	
	public static Map<String, Integer> getExcelHeadersNameAndIndex(Row headRow) {
		Map<String, Integer> headers = new HashMap<String, Integer>();
		Iterator<Cell> cellIterator = headRow.cellIterator();
		
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			String cellValue = cell.getStringCellValue();
			int cellIndex = cell.getColumnIndex();
			headers.put(cellValue, cellIndex);
		}
		
		return headers;
	}
	
	public static int getColunmIndex(Row headRow, String colunmName) {
		Map<String, Integer> headers = getExcelHeadersNameAndIndex(headRow);
		try {
			return headers.get(colunmName);
		} catch (NullPointerException ex) {
			return -1;
		}
		
	}
	
	public static void adddCellWithValue(Row row, String cellValue) {
		if (getColunmIndex(row,cellValue) == -1) {
			row.createCell(row.getPhysicalNumberOfCells()).setCellValue(cellValue);
		}
	}
	
	public static void writeResultToExcel(String fileObsolutePath, String sheetName, int rowNumber, String colunmName, String colunmValue) {
		XSSFWorkbook workBook;
		try {
			workBook = getWorkBook(fileObsolutePath);
			XSSFSheet sheet = workBook.getSheet(sheetName);
			Row dataRow = sheet.getRow(rowNumber);
			Row headerRow = sheet.getRow(0);
			
			int cellIndex = getColunmIndex(headerRow, colunmName);
			Cell cell = dataRow.createCell(cellIndex);
			cell.setCellValue(colunmValue);
			writeValueToExcel(workBook, fileObsolutePath);
			System.out.println(" written successfully");
		} catch (Exception e) {
			System.out.println("Write resut exception for" + "SheetName-" + sheetName + "Rownumber" + rowNumber);
			e.printStackTrace();
		}
	}
	
	public static void writeResultToExcel(String fileObsolutePath, String sheetName, int rowNumber, String colunmValue) {
		XSSFWorkbook workBook;
		try {
			writeResultToExcel(fileObsolutePath, sheetName, rowNumber, "_Result", colunmValue );
		} catch (Exception e) {
			try {
				writeResultToExcel(fileObsolutePath, sheetName, rowNumber, "_Result", colunmValue );
			} catch (Exception ex) {
				writeResultToExcel(fileObsolutePath, sheetName, rowNumber, "_Result", colunmValue );
			}
		}
	}
	
	public static XSSFWorkbook createGetExcelWithSheet (String filePath, String sheetName) {
		try {
			XSSFWorkbook workbook;
			createFolder(getFileFolder(filePath));
			File file = new File(filePath);
			if(!file.exists()) {
				workbook = new XSSFWorkbook();
			} else {
				workbook = getWorkBook(filePath);
			}
			
			if (workbook.getSheet(sheetName) == null) {
				workbook.createSheet(sheetName);
			}
			return workbook;
		} catch (Exception e) {
			System.out.print("Create file failed" + filePath);
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getFileFolder(String filePath) {
		String pathType = null;
		
		if (filePath.contains("/")) {
			pathType = "/";
		} else {
			pathType = "\\";
		}
		
		String folder = filePath.substring(0,filePath.lastIndexOf(pathType));
		return folder;
	}
	
	
	public static boolean removeSheet(String filePath, String sheetName) {
		try {
			XSSFWorkbook workbook;
			File file = new File(filePath);
			if(!file.exists()) {
				return true;
			} else {
				workbook = getWorkBook(filePath);
				if (workbook.getSheet(sheetName) != null) {
					int index = workbook.getSheetIndex(sheetName);
					workbook.removeSheetAt(index);
					writeValueToExcel(workbook, filePath);
					return true;
				} else {
					return true;
				}
			}
		} catch (Exception e) {
			System.out.print("delete sheet failed" + sheetName);
			e.printStackTrace();
			return false;
		}
	}
	
	
	public static void setCellValue(Cell cell, Object value) {
		if (value != null) {
			if (value.getClass() == Integer.class) {
				cell.setCellValue((Integer)value);
			} else if (value.getClass() == BigInteger.class) {
				cell.setCellValue(((BigInteger)value).intValue());
			} else if (value.getClass() == Boolean.class) {
				cell.setCellValue((Boolean)value);
			} else if (value.getClass() == Double.class) {
				cell.setCellValue((Double)value);
			} else if (value.getClass() == BigDecimal.class) {
				cell.setCellValue(((BigDecimal)value).doubleValue());
			} else if (value.getClass() == Timestamp.class) {
				Date date = new Date(((Timestamp)value).getTime());
				Format foematter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String s = foematter.format(date);
				cell.setCellValue(s);
			} else {
				cell.setCellValue(value.toString());
			}
		} else {
			cell.setCellValue((String) value);
		}
	}
	
	public static <T> void writeListObejectToExcel(List<T> dataList, String filePath, String sheetName) {
		writeListMapToExcel(CommonUtils.converDataToMap(dataList),filePath,sheetName);
	}
	
	public static void writeListMapToExcel(List<Map<String, Object>> mlist, String filePath, String sheetName) {
		XSSFWorkbook xssfWorkbook = createGetExcelWithSheet(filePath, sheetName);
		writeListMapWorkbook(mlist,xssfWorkbook,sheetName);
		try {
			writeValueToExcel(xssfWorkbook,filePath);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.print("Write file to Excel failed");
		}
	}
	
	public static XSSFWorkbook writeListMapWorkbook(List<Map<String, Object>> mlist, XSSFWorkbook xssfWorkbook, String sheetName) {
		if (xssfWorkbook == null) {
			xssfWorkbook = new XSSFWorkbook();
		}
		
		if (xssfWorkbook.getSheet(sheetName) == null) {
			xssfWorkbook.createSheet(sheetName);
		}
		
		//Read the Sheet
		XSSFSheet xssfSheet = xssfWorkbook.getSheet(sheetName);
		XSSFRow headerRow = xssfSheet.createRow(0);
		for (int i = 0; i < mlist.size(); i++ ) {
			Row dataRow = xssfSheet.createRow(i + 1);
			Map<String, Object> map = mlist.get(i);
			int columnIndex = 0;
			for (String key:map.keySet()) {
				Cell header = headerRow.createCell(columnIndex);
				header.setCellValue(key);
				Cell value = dataRow.createCell(columnIndex);
				setCellValue(value, map.get(key));
				columnIndex++;
			}
		}
		
		return xssfWorkbook;
	}
	
	public static void writeObjectToExcel(Object obj, String filePath, String sheetName) {
		writeMapToExcel(CommonUtils.getMap(obj), filePath, sheetName);
	}
	
	public static void writeMapToExcel(Map<String, Object> map, String filePath, String sheetName) {
		XSSFWorkbook xssfWorkbook = createGetExcelWithSheet(filePath, sheetName);
		//Read the Sheet
		XSSFSheet xssfSheet = xssfWorkbook.getSheet(sheetName);
		boolean existHeader = false;
		XSSFRow headerRow = null;
		int lastColumnNumber = xssfSheet.getLastRowNum();
		if (lastColumnNumber == 0) {
			headerRow = xssfSheet.createRow(0);
			existHeader = false;
		} else {
			existHeader = true;
		}
		
		Row dataRow = xssfSheet.createRow(lastColumnNumber + 1);
		int columnIndex = 0;
		for (String key:map.keySet()){
			if (!existHeader) {
				Cell header = headerRow.createCell(columnIndex);
				header.setCellValue(key);
			}
			Cell value = dataRow.createCell(columnIndex);
			if (map.get(key) == null) {
				setCellValue(value, "");
			} else {
				setCellValue(value, map.get(key));
			}
			columnIndex++;
		}
		
		try {
			writeValueToExcel(xssfWorkbook, filePath);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.print("Write map file To Excel failed");
		}
		
	}
	
	public static <T> void writeListObjectToCSV(List<T> dataList, String filePath) {
		WriteListMapToCSV(CommonUtils.converDataToMap(dataList), filePath);
	}
	
	public static void WriteListMapToCSV(List<Map<String, Object>> mlist, String filePath) {
		try {
			if (mlist.size() > 0) {
				StringBuilder csvData = new StringBuilder("");
				StringBuilder headerRow = new StringBuilder("");
				StringBuilder dataList = new StringBuilder("");
				
				for (int i = 0; i < mlist.size(); i++) {
					StringBuilder dataRow = new StringBuilder("");
					Map<String, Object> map = mlist.get(i);
					
					for (String key : map.keySet()) {
						if(i == 0) {
							headerRow.append(key.toLowerCase() + ",");
						}
						dataRow.append(setCSVValue(map.get(key)) + ",");
					}
					dataList.append(removeLastSeprater(dataRow));
				}
				
				csvData.append(removeLastSeprater(headerRow));
				csvData.append(dataList);
				FileUtils.writeStringToFile(new File(filePath), csvData.toString());
				
			} else {
				FileUtils.writeStringToFile(new File(filePath), "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.print("Write file to Excel failed");
		}
	}
	
	
	public static void deleteFile(String fileName) {
		File file = new File(fileName);
		deleteFile(file);
	}
	
	public static void deleteFile(File file) {
		if (file.exists()) {
			file.delete();
		}
	}
	
	public static void createFolder(String folderName) {
		File dirFile = new File(folderName);
		 if (!dirFile.exists() && !dirFile.isDirectory()) {
			 dirFile.mkdir();
		 }
	}
	
	private static StringBuilder removeLastSeprater(StringBuilder data) {
		StringBuilder value = new StringBuilder(data.substring(0, data.length() - 1));
		value.append("\n");
		return value;
	}
	
	private static String setCSVValue(Object columnValue) {
		if (columnValue == null) {
			return "NULL";
		} else if (columnValue.toString().isEmpty()) {
			return "";
		} else if (columnValue.toString().contains(",")) {
			return StringEscapeUtils.escapeCsv(columnValue.toString());
		} else {
			return columnValue.toString();
		}
	}
	
	private static void doTrustToCertificates() throws Exception {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
				
			}
			
			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
				
			}
			
		}};
		
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	
	public static Object getValue(Cell cell) {
		Object value = null;
		if (cell == null) {
			return null;
		}
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:
			value = cell.getBooleanCellValue() + "";
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if ("#,##0".equals(cell.getCellStyle().getDataFormatString())) {
				value = df.format(cell.getNumericCellValue());
			} else if ("General".equals(cell.getCellStyle().getDataFormatString())) {
				value = String.valueOf(cell.getNumericCellValue());
			} else if (cell.getCellStyle().getDataFormatString().contains("yy")) {
				value = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
			} else {
				value = String.valueOf(cell.getNumericCellValue());
			}
			break;
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue() + "";
			if ("null".equalsIgnoreCase(value.toString())) {
				value = null;
			}
			break;
		case Cell.CELL_TYPE_BLANK:
			value = null;
			break;
		case Cell.CELL_TYPE_FORMULA:
			switch (cell.getCachedFormulaResultType()) {
			case Cell.CELL_TYPE_STRING:
				value = cell.getStringCellValue() + "";
				break;
			case Cell.CELL_TYPE_NUMERIC:
				value = cell.getNumericCellValue();
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		return value;
	}
	
	public static String getStringValue(Cell cell) {
		String value = null;
		if (cell == null) {
			return null;
		}
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:
			value = String.valueOf(cell.getBooleanCellValue()) + "";
			break;
		case Cell.CELL_TYPE_NUMERIC:
			value = String.valueOf(cell.getNumericCellValue()) + "";
			if (value.endsWith(".0")) {
				value = value.substring(0, value.length() - 2) + "";
			}
			break;
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue() + "";
			if ("null".equalsIgnoreCase(value)) {
				value = null;
			}
			break;
		case Cell.CELL_TYPE_BLANK:
			value = null;
			break;
		case Cell.CELL_TYPE_FORMULA:
			switch (cell.getCachedFormulaResultType()) {
			case Cell.CELL_TYPE_STRING:
				value = cell.getStringCellValue() + "";
				break;
			case Cell.CELL_TYPE_NUMERIC:
				value = String.valueOf(cell.getNumericCellValue()) + "";
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		return value;
	}
	
	static {
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
		new javax.net.ssl.HostnameVerifier(){
			public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
				if (hostname.equals("gpfcsl-stg.nam.nsroot.net") || hostname.equals("gpfcsl.nam.nsroot.net")) {
					return true;
				}
				return false;
			}
		}
				);
	}
	
}
