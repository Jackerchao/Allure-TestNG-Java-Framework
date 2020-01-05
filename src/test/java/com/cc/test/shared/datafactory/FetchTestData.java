package com.cc.test.shared.datafactory;

public class FetchTestData extends DataFactoryBase<TestData> {
	public FetchTestData(String rootFolder, String dataFile, String testSheet) {
		super(rootFolder, dataFile, testSheet, TestData.class);
	}

}
