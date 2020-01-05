package com.cc.test.properties;

import java.util.ArrayList;
import java.util.Arrays;

import com.cc.test.shared.utils.PropUtilBase;



public class PropUtils extends PropUtilBase {
	private static final PropUtils propUtils = new PropUtils();
	
	private PropUtils() {
		setResourceFileList(new ArrayList<String>(Arrays.asList("run.properties", "config.common.properties",
				"test.env.properties")));
		
	}
	
	public static PropUtils getInstance() {
		return propUtils;
	}

}
