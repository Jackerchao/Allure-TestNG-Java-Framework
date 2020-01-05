package com.cc.test.shared.datafactory;

public class CaseDataRequest<T> {

	private CaseCommonFields commonFields;
	private T dataRequest;
	
	public CaseCommonFields getCommonFields() {
		return commonFields;
	}
	public void setCommonFields(CaseCommonFields commonFields) {
		this.commonFields = commonFields;
	}
	public T getDataRequest() {
		return dataRequest;
	}
	public void setDataRequest(T dataRequest) {
		this.dataRequest = dataRequest;
	}
	
	
	

}
