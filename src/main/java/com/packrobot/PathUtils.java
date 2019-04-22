package com.packrobot;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathUtils {
	private final Logger _logger = LoggerFactory.getLogger(getClass());
	private static PathUtils instance = null;
	private String classPath;
	private String appPath;
	public static String WEB_INFO = "/WEB-INF/";
	
	public static synchronized PathUtils getInstance() {
		if (instance == null) {
			instance = new PathUtils();
			instance._logger.debug("getInstance()" +" new PathUtils instance");
		}
		return instance;
	}

	public PathUtils() {
		try {
			classPath = java.net.URLDecoder.decode(PathUtils.class.getResource("PathUtils.properties").getFile(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String fileProtocol=PathUtils.class.getResource("PathUtils.properties").getProtocol();
		_logger.info("Properties Protocol:"+PathUtils.class.getResource("PathUtils.properties").getProtocol());
		
		if(fileProtocol.equalsIgnoreCase("file")&&classPath.indexOf("file:")==0){
			classPath=classPath.substring(5, classPath.length());
		}else if(fileProtocol.equalsIgnoreCase("jar")&&classPath.indexOf("file:")==0){
			//   file:/Server/webapps/app
			classPath=classPath.substring(5, classPath.length());
		}else if(fileProtocol.equalsIgnoreCase("wsjar")&&classPath.indexOf("file:")==0){
				classPath=classPath.substring(5, classPath.length());			
		}else if(classPath.equalsIgnoreCase("file:")){
			classPath=classPath.substring(5, classPath.length());
		}
		
		classPath=classPath.substring(0,classPath.indexOf(("/"+PathUtils.class.getName().replaceAll("\\.", "/")+".properties")));
		if(classPath.indexOf(WEB_INFO)==-1) {
			appPath=classPath.substring(0,classPath.lastIndexOf("/"));
		}else {
			appPath=classPath.substring(0,classPath.lastIndexOf(WEB_INFO));
		}
		
		System.setProperty("APP_PATH", appPath);
		System.setProperty("CLASSES_PATH", classPath);
		
		_logger.info("PathUtils  App   Path  : " + appPath);
		_logger.info("PathUtils  Class Path  : " + classPath);
	}
	
	public  String getAppPath(){
		return appPath + "/";
	}
	
	public  String getClassPath(){
		return classPath + "/";
	}
	
	public  String getWebInf(){
		return (classPath.lastIndexOf(WEB_INFO)>-1)? (appPath + WEB_INFO) : "";
	}
}
