package com.cc.test.shared.datafactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cc.test.shared.report.Logging;
import com.cc.test.shared.utils.CommonUtils;

public class DataFactoryBase<T> {

		private String testLogFile;
		private String testSheet;
		protected Class<T> classOfT;
		
		public DataFactoryBase(String rootFolder, String dataFile, String testSheet, Class<T> classOfT) {
			this.classOfT = classOfT;
			this.testSheet = testSheet;
			testLogFile = TestCaseFactory.initialData(CommonUtils.optimizeFolderPath(rootFolder) + dataFile);
		}
		
		public List<CaseDataRequest<T>> getCaseList() {
			String[] sheets = testSheet.split(",");
			List<CaseDataRequest<T>> requestList = new ArrayList<CaseDataRequest<T>>();
			
			for (int i = 0; i < sheets.length; i++) {
				List<CaseData> caseList;
				try {
					caseList = TestCaseFactory.loadCaseData(testLogFile, sheets[i]);
				} catch (Exception e) {
					caseList = new ArrayList<CaseData>();
					Logging.logError(e.getMessage());
					e.printStackTrace();
				}
				
				for (CaseData caseData : caseList) {
					CaseDataRequest<T> requestData = new CaseDataRequest<T>();
					
					requestData.setCommonFields(caseData.getCaseFields());
					requestData.setDataRequest(setDataRequest(caseData));
					requestList.add(requestData);
				}
			}
			return requestList;
		}
		
		public Map<String, List<CaseDataRequest<T>>> groupByCaseID() {
			Map<String, List<CaseDataRequest<T>>> dataMap = new HashMap<String, List<CaseDataRequest<T>>>();
			
			List<CaseDataRequest<T>> dataList = getCaseList();
			
			for (CaseDataRequest<T> dataRequest : dataList) {
				String id = dataRequest.getCommonFields().getCaseID();
				if (dataMap.containsKey(id)) {
					List<CaseDataRequest<T>> caseStepList = dataMap.get(id);
					caseStepList.add(dataRequest);
					dataMap.put(id, caseStepList);
				} else {
					List<CaseDataRequest<T>> caseStepList = new ArrayList<CaseDataRequest<T>>();
					caseStepList.add(dataRequest);
					dataMap.put(id, caseStepList);
				}
			}
			
			return dataMap;
		}
		
		public Object[][] getCaseArray() {
			try {
				return CommonUtils.convertListArray(getCaseList());
			} catch (Exception e) {
				Logging.logError("Failed to fetch test data-" + e.getMessage());
				e.printStackTrace();
				return null;
			}
		}
		
		public Object[][] getGroupedCaseArray() {
			try {
				return CommonUtils.convertMapToArray(groupByCaseID());
			} catch (Exception e) {
				Logging.logError("Failed to fetch test data-" + e.getMessage());
				e.printStackTrace();
				return null;
			}
		}
		
		public T setDataRequest (CaseData caseData) {
			return caseData.getTestData(classOfT);
		}
		
}
