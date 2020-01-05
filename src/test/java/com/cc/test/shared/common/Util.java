package com.cc.test.shared.common;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;

public class Util {
	
	public static String getProperty(String key, Class<?> clazz) {
		return getProperty(key, clazz, null);
	}
	
	public static String getProperty(String key, Class<?> clazz, String defValue){
		Properties props = new Properties();
		String value = null;
		try{
			props.load(clazz.getClassLoader().getResourceAsStream("default.properties"));
			value = (props.getProperty(key) == null) ? defValue : props.getProperty(key);
			
		}catch(Exception e) {
			
			e.printStackTrace();
		}
		return value;
	}
	
	public static void loadProperties(Class<?> clazz) {
		Properties props = new Properties();
		try{
			props.load(clazz.getClassLoader().getResourceAsStream("default.properties"));
			Enumeration<?> e = props.propertyNames();
			
			while(e.hasMoreElements()){
				String key = (String) e.nextElement();
				System.setProperty(key, props.getProperty(key));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String generateRandomName(int length){
		return "test" + RandomStringUtils.randomAlphanumeric(length);
		
		}
	
	public static Object execMethod(Class<?> clazz, String methodName, Class<?> claz, Object... args){
		Method m = null;
		try{
			m = clazz.getDeclaredMethod(methodName, claz);
		}catch(NoSuchMethodException e){
			//TODO Auto-generated catch block
			e.printStackTrace();
		}catch(SecurityException e){
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try{
			return m.invoke(null, args);
		}catch (IllegalAccessException e){
			//TODO Auto-generated catch block
			e.printStackTrace();
		}catch(IllegalArgumentException e){
			//TODO Auto-generated catch block
			e.printStackTrace();
		}catch(InvocationTargetException e){
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Delete the file or directory at the supplied path. This method works on a 
	 * directory that is not empty, unlike the {@link File#delete()} method.
	 * 
	 * @param path
	 * 			the path to the file or directory that is to be deleted
	 * @return true if the file or directory at the supplied path existed and 
	 * was successfully deleted, or false otherwise 
	 */
	public static boolean delete(String path){
		if (path == null || path.trim().length() == 0){
			return false;
		}
		return delete(new File(path));
	}
	/**
	 * Delete the file or directory given by the supplied reference. This method works on a 
	 * directory that is not empty, unlike the {@link File#delete()} method.
	 * 
	 * @param fileOrDirectory
	 * 			the reference to the Java file object that is to be deleted
	 * @return true if the file or directory  existed and 
	 * was successfully deleted, or false otherwise 
	 */
	
	public static boolean delete(File fileOrDirectory){
		if (fileOrDirectory == null){
			return false;
		}
		if (!fileOrDirectory.exists()){
			return false;
		}
		
		//The file/directory exists, so if a directory delete all ot the 
		//contents...
		if(fileOrDirectory.isDirectory()){
			for(File childFile : fileOrDirectory.listFiles()){
				delete(childFile);//recursive call(good enough for now until we need
								  //something better)
			}
			//Now an empty diretory
		}
		//whether this is a file or empty directory, just delete it...
		return fileOrDirectory.delete();
	}
	
	

}
