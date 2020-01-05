package com.cc.test.shared.common;

import java.awt.Desktop;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class AbstractReporter {
	
	private static final String ENCODING = "UTF-8";
	
	protected AbstractReporter() {
		
	}
	
	protected void replaceContent(String sourcePath, String targetPath, String[] search, String[] replace) throws Exception {
		try {
			FileReader reader = new FileReader (sourcePath);
			char[] dates = new char[1024];
			int count = 0;
			StringBuilder sb = new StringBuilder();
			while ((count = reader.read(dates)) > 0) {
				String str = String.valueOf(dates, 0, count);
				sb.append(str);
			}
			reader.close();
			
			String str = sb.toString();
			for (int i = 0; i < search.length; i++) {
				str = str.replace(search[i], replace[i]);
			}
			FileWriter writer = new FileWriter(targetPath);
			writer.write(str.toCharArray());
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void appendContent(String filename, String content) throws Exception {
		RandomAccessFile randomFile = null;
		try {
			randomFile = new RandomAccessFile(filename, "rw");
			
			long fileLength = randomFile.length();
			
			randomFile.seek(fileLength);
			randomFile.writeBytes(content);
			
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (randomFile != null) {
				try {
					randomFile.close();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected void openReport(String filePath) {
		try {
			Desktop.getDesktop().open(new File(filePath));
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected int getCountByColor(String filePath, String color) {
		try {
			FileReader reader = new FileReader(filePath);
			char[] dates = new char[1024];
			int count = 0;
			StringBuilder sb = new StringBuilder();
			while ((count = reader.read(dates)) > 0) {
				String str = String.valueOf(dates, 0, count);
				sb.append(str);
			}
			reader.close();
			
			String str = sb.toString();
			return countStr(str, color);
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	protected int countStr(String sourceString, String subString) {
		
		if (sourceString.indexOf(subString) < 0) {
			return 0;
		}else {
			return countStr(sourceString.substring(sourceString.indexOf(subString) + subString.length()), subString) +1;
		}
	}

}
