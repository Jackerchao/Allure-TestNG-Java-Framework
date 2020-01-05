package com.cc.test.shared.datafactory;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CaseData extends CaseCommonFields{
	private Map<String, Object> caseData = new HashMap<String, Object>();
	
	public Map<String, Object> getCaseData() {
		return caseData;
	}
	
	public void setCaseData(Map<String, Object> caseData) {
		this.caseData = caseData;
	}
	
	public <T> T getTestData(Class<T> typeParameterClass) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.convertValue(caseData, typeParameterClass);
	}
	
	public CaseCommonFields getCommonFields() {
		return getTestData(CaseCommonFields.class);
	}
	
	public CaseCommonFields getCaseFields() {
		return getTestData(CaseCommonFields.class);
	}

}
