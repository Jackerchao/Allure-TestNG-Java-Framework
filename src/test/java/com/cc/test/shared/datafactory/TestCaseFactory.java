package com.cc.test.shared.datafactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;

import com.cc.test.shared.report.Logging;
import com.cc.test.shared.utils.CommonUtils;
import com.cc.test.shared.utils.ExcelUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.gson.Gson;

import edu.emory.mathcs.backport.java.util.Arrays;

public class TestCaseFactory {
	
	private static final String XSSFCell = null;

	public static String  initialData(String dataPath, String... masks) {
		String rootFolder = dataPath.substring(0, dataPath.lastIndexOf("\\") + 1);
		String dataFile = dataPath.substring(dataPath.lastIndexOf("\\") + 1, dataPath.length());
		String outPutPath = "";
		
		String mask = "";
		if (masks.length > 0) {
			mask = masks[0] + "_";
		}
		
		outPutPath = rootFolder + "Output Log\\" + dataFile.substring(0, dataPath.lastIndexOf(".")) + "_" + mask + CommonUtils.getTimeStamp() + ".xlsx";
		dataPath = rootFolder + dataFile;
		
		Logging.logInfo("Prepare test data:" + outPutPath);
		ExcelUtil.copyFile(dataPath, outPutPath);
		return outPutPath;
	}
	
	public static <T> List<T> loadTestCase(String filePath, String sheetName, Class<T[]> classOfT) throws Exception {
		String json = convertXlsFileToJson(filePath, sheetName);
		Gson gson = new Gson();
		T[] arr = gson.fromJson(json, classOfT);
		@SuppressWarnings("unchecked")
		List<T> tclist = Arrays.asList(arr);
		return tclist;
	}
	
	public static List<CaseCommonFields> loadCommonFieldsData(String filePath, String sheetName) {
		try {
			return loadTestCase(filePath, sheetName, CaseCommonFields[].class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String convertXlsFileToJson(String filePath, String sheetName) throws Exception {
		return new Gson().toJson(loadTestCase(filePath, sheetName));
	}
	
	public static List<CaseData> loadCaseData(String filePath, String sheetName) throws Exception {
		List<CaseData> caseDataList = new ArrayList<CaseData>();
		List<Map<String, Object>> dataList = loadTestCase(filePath, sheetName);
		
		for (int i = 0; i < dataList.size(); i++) {
			CaseData caseData = new CaseData();
			caseData.setCaseData(dataList.get(i));
			caseDataList.add(caseData);
		}
		
		return caseDataList;
	}
	
	public static List<Map<String, Object>> loadTestCase(String filePath, String sheetName) throws Exception {
		XSSFWorkbook xssfWorkbook = ExcelUtil.getWorkBook(filePath);
		XSSFSheet xssfSheet = xssfWorkbook.getSheet(sheetName);
		if (xssfSheet == null) {
			return null;
		}
		
		XSSFRow headerRow = xssfSheet.getRow(0);
		int columns = headerRow.getLastCellNum();
		
		List<Map<String, Object>> mlist = new ArrayList<Map<String, Object>>();
		for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
			XSSFRow xssfRow = xssfSheet.getRow(rowNum);
			if (xssfRow != null) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 0; i < columns; i++) {
					XSSFCell key_cell = headerRow.getCell(i);
					XSSFCell cell = xssfRow.getCell(i);
					try {
						map.put(getStringValue(key_cell), getValue(cell));
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
				if (map.get("IsRun") == null || !"N".equalsIgnoreCase(map.get("IsRun").toString())) {
					map.put("CaseIndex", rowNum + "");
					map.put("Result", "");
					mlist.add(map);
				}
			}
		}
		return mlist;
	}
	
	public static List<CaseData> loadCaseData(String jenkindDataFile, String defaultDataPath, String sheetList, String... params) throws Exception {
		List<CaseData> caseDataList = new ArrayList<CaseData>();
		String dataFile = getDataFile(jenkindDataFile, defaultDataPath, params);
		String[] sheets = sheetList.split(",");
		for (int i = 0; i < sheets.length; i++) {
			caseDataList.addAll(loadCaseData(dataFile, sheets[i]));
		}
		
		return caseDataList;
	}
	
	public static Object[][] loadCaseData(List<String> rerunList, List<CaseData> caseDataList) {
		Object[][] caseData = null;
		try {
			List<CaseData> validPartialRunCases = new ArrayList<CaseData>();
			if (rerunList.size() > 0) {
				int caseIndex = 0;
				
				for (CaseData node : caseDataList) {
					if (rerunList.contains(node.getCaseID())) {
						validPartialRunCases.add(node);
					}
				}
				
				caseData = new Object[validPartialRunCases.size()][1];
				for (CaseCommonFields node : validPartialRunCases) {
					caseData[caseIndex++][0] = node;
				}
				
				Logging.logInfo(caseData.length + "Test cases to be re-running");
				
			} else {
				caseData = new Object[caseDataList.size()][1];
				int count = 0;
				for (CaseCommonFields node : caseDataList) {
					caseData[count++][0] = node;
				}
			}
		} catch (Exception e) {
			Logging.logInfo("Get test data failed with exception-" + e.getMessage());
		}
		return caseData;
	}
	
	public static Object[][] loadCaseDataInt(List<Integer> rerunList, List<CaseData> caseDataList) {
		Object[][] caseData = null;
		try {
			List<CaseData> validPartialRunCases = new ArrayList<CaseData>();
			if (rerunList.size() > 0) {
				int count = 0;
				int caseIndex = 0;
				
				for (CaseData node : caseDataList) {
					if (rerunList.contains(count++)) {
						node.setCaseIndex(count);
						validPartialRunCases.add(node);
					}
				}
				
				caseData = new Object[validPartialRunCases.size()][1];
				for (CaseCommonFields node : validPartialRunCases) {
					caseData[caseIndex++][0] = node;
				}
				
				Logging.logInfo(caseData.length + "Test cases to be re-running");
			} else {
				caseData = new Object[caseDataList.size()][1];
				int count = 0;
				for (CaseCommonFields node : caseDataList) {
					caseData[count++][0] = node;
				}
			}
		} catch (Exception e) {
			Logging.logInfo("Get test data failed with exception-" + e.getMessage());
		}
		return caseData;
	}
	
	@SuppressWarnings("deprecation")
	public static List<Map<String, String>> loadCSVData(String dataFile) throws JsonProcessingException, IOException {
		List<Map<String, String>> response = new LinkedList<Map<String, String>>();
		CsvMapper mapper = new CsvMapper();
		CsvSchema schema = CsvSchema.emptySchema().withHeader();
		MappingIterator<Map<String, String>> iterator = mapper.reader(Map.class).with(schema).readValues(new File(dataFile));
		while (iterator.hasNext()) {
			response.add(iterator.next());
		}
		return response;
		
	}
	
	public static Object getValue(Cell cell) {
		Object value = null;
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_BOOLEAN:
				value = cell.getBooleanCellValue() + "";
				break;
			case Cell.CELL_TYPE_NUMERIC:
				value = cell.getNumericCellValue();
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
		}
		return value;
	}
	
	public static String getStringValue(Cell cell) {
		String value = null;
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
					value = String.valueOf(cell.getNumericCellValue());
					break;
				default:
					break;
				}
		}
		return value;
	}
	
	public static String getDataFile(String jenkindDataFile, String defaultDataPath, String... params) {
		String filePath = "";
		String custompath = System.getenv(jenkindDataFile);
		
		if (StringUtils.isEmpty(custompath)) {
			File folder = new File(defaultDataPath);
			File[] listOfFiles = folder.listFiles();
			
			for (File file : listOfFiles) {
				if (file.isFile()) {
					String fileName = file.getName();
					if (fileName.toUpperCase().endsWith(".XLSX") && !fileName.startsWith("~$")) {
						boolean isMatch = true;
						if (params.length > 0) {
							for (int i = 0; i < params.length; i++) {
								if (fileName.contains(params[i])) {
									isMatch = true;
								} else {
									isMatch = false;
									break;
								}
							}
						}
						
						if (isMatch) {
							filePath = folder + "\\" + fileName;
							break;
						}
					  
						}
				  	  }
			       }
				} else {
					filePath = custompath + "\\TestData.xlsx";
				}
				
				Logging.logInfo("File Path:" + filePath);
				return filePath;
			}
		
	
}
