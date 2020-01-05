package com.cc.test.shared.datafactory;

public class CaseCommonFields {
	
	//@Expose
	//@SerializedName(value = "CaseId", alternate = {"_CaseID", "CaseID"})
	//@JasonProperty("_CaseID")
	
	private String caseID;
	
	private int caseIndex;
	
	private String caseName;
	
	private String caseDesc;
	private String manualCaseKey;


	//@Expose
	//@SerializedName(value = "CaseType", alternate = {"_CaseType"})
	//@JasonProperty("_CaseType")
	//private String caseType;
	//
	//@Expose
	//@SerializedName(value = "Result", alternate = {"_Result"})
	//@JasonProperty("_Result")
	//private String result;
	
	private String isRun;
	
	public String getCaseID() {
		return caseID;
	}

	public void setCaseID(String caseID) {
		this.caseID = caseID;
	}

	public int getCaseIndex() {
		return caseIndex;
	}

	public void setCaseIndex(int caseIndex) {
		this.caseIndex = caseIndex;
	}

	public String getCaseName() {
		return caseName;
	}

	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}

	public String getCaseDesc() {
		return caseDesc;
	}

	public void setCaseDesc(String caseDesc) {
		this.caseDesc = caseDesc;
	}
	public String getManualCaseKey() {
		return manualCaseKey;
	}

	public void setManualCaseKey(String manualCaseKey) {
		this.manualCaseKey = manualCaseKey;
	}

	public String getIsRun() {
		return isRun;
	}

	public void setIsRun(String isRun) {
		this.isRun = isRun;
	}
	
	public boolean isRun() {
		if ("no".equalsIgnoreCase(this.getIsRun())) {
			return false;
		} else {
			return true;
		}
	}
	

}
