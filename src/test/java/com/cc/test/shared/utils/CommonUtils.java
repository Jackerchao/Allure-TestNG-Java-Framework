package com.cc.test.shared.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;


import com.cc.test.shared.datafactory.CaseDataRequest;
import com.cc.test.shared.report.Logging;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonUtils {
	
	public static List<Map<String, Object>> resultSetToListMap(ResultSet rs) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (rs == null) {
			Logging.logError("The resultset it null");
			return null;
		}
		
		ResultSetMetaData meta = rs.getMetaData();
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i =1; i < meta.getColumnCount();i++) {
				String key = meta.getColumnName(i);
				String value = rs.getString(key);
				map.put(key, value);
			}
			list.add(map);
		}
		return list;
	}
	
	public static String getTimeStamp(){
		return new SimpleDateFormat("yyyyMMddHHmmsss").format(new Date());
	}
	
	public static BigDecimal parseStringToDecimal (String value) throws Exception{
		if(StringUtils.isEmpty(value)) {
			return null;
		}
		
		DecimalFormat decimalformate = new DecimalFormat("###,###,###");
		try {
			return (BigDecimal)decimalformate.parse(value);
		}catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static <T> List<Map<String, Object>> converDataToMap(List<T> data) {
		List<Map<String, Object>> mlist = new ArrayList<Map<String, Object>>();
		for(T activity:data){
			mlist.add(getMap(activity));
			
		}
		return mlist;
	}
	
	public static <T,K> List<Map<String, Object>> converDataToMap(List<T> data, List<K> childProperty) {
		List<Map<String, Object>> mlist = new ArrayList<Map<String, Object>>();
		
		for(int i = 0; i < data.size(); i++) {
			Map<String, Object> mapList = new HashMap<String, Object>();
			mapList.putAll(getMap(data.get(i)));
			if (childProperty != null && childProperty.size() > i) {
				mapList.putAll(getMap(childProperty.get(i)));
			}
			mlist.add(mapList);
		}
		return mlist;
	}
	
	public static Map<String, Object> getMap (Object o) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
		mapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> mappedObject = mapper.convertValue(o, Map.class);
		
		return mappedObject;
	}
	
	public static <T> T getObject(Map<String, Object> map, Class<T> classOfT) {
		ObjectMapper mapper = new ObjectMapper();
		T object = mapper.convertValue(map, classOfT);
		return object;
	} 
	
	public static <T> List<T> convertListMapToListObject(List<Map<String, Object>> dataList, Class<T> classOfT) {
		
		List<T> listObject = new ArrayList<T>();
		for (int i = 0; i < dataList.size(); i ++) {
			listObject.add(getObject(dataList.get(i), classOfT));
			
		}
		return listObject;
	}
	
	public static String getPathByFolderFileName(String folder, String fileName) {
		String pathType = getPathType(folder);
		String path = "";
		if (!folder.endsWith(pathType)) {
			path = folder + pathType;
			
		}else {
			path = folder;
		}
		path = path + fileName;
		return path;
	}
	
	public static String getPathType(String path) {
		String pathType = null;
		
		if(path.contains("/")) {
			pathType = "/";
		}else {
			pathType = "\\";
		}
		
		return pathType;
	}
	
	public static String optimizeFolderPath(String folderPath) {
		String pathType = CommonUtils.getPathType(folderPath);
		if(!folderPath.endsWith(pathType)) {
			folderPath = folderPath + pathType;
		}
		return folderPath;
	}
	
	public static <T> List<T> converResultsetToListObject(ResultSet rs, Class classOfT) {
		
		List<Map<String, Object>> dataList;
		try{
			dataList = resultSetToListMap(rs);
			
		}catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return convertListMapToListObject(dataList,classOfT);
	}
	
	public static <T> Object[][] convertListArray(List<T> caseDataList) throws Exception {
		
		Object[][] caseData = null;
		 
		caseData = new Object[caseDataList.size()][1];
		int count = 0;
		for (T node : caseDataList) {
			caseData[count++][0] = node;
		}
		return caseData;
	}
	
	public static <T> Object[][] convertMapToArray(Map<String, List<CaseDataRequest<T>>> caseDataList) throws Exception {
			
			Object[][] caseData = null;
			 
			caseData = new Object[caseDataList.size()][1];
			int count = 0;
			for (String caseId : caseDataList.keySet()) {
				caseData[count++][0] = caseDataList.get(caseId);
			}
			return caseData;
		}
	
	public static List<Map<String, Object>> covertCSVToListMap(String filePath) {
		List<Map<String, Object>> listObject = new ArrayList<Map<String, Object>>();
		try{
			BufferedReader sourceReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			@SuppressWarnings("resource")
			List<CSVRecord> sourceRecords = new CSVParser(sourceReader, CSVFormat.DEFAULT).getRecords();
			for (CSVRecord record : sourceRecords) {
				listObject.add(convertMapStringToObject(record.toMap()));
			}
		}catch (Exception e) {
			Logging.logError("CSV Convert to List map exception-" + e.getMessage());
		}
		return listObject;
	}
	
	public static Map<String, Object> convertMapStringToObject(Map<String, String> map) {
		Map<String, Object> convertMap = new HashMap<String, Object>();
		
		for (String key : map.keySet()) {
			convertMap.put(key, map.get(key));
		}
		return convertMap;
	}
	
	public static String objectToString(Object object) {
		
		if (object == null) {
			return null;
		}else {
			return String.valueOf(object);
		}
	}
	

}
