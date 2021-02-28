package com.ftpclient.util.log;

import java.io.File;
import java.io.FileWriter;

/**
 * Ispisvac logova
 * 
 * @author Djordje Velickovic
 *
 */
class LogWriter {
	//private static LogWriter instance = new LogWriter();
//	public static LogWriter getInstance(){
//		return instance;
//	}
	
	private String fileName = "error_log.txt";

	public void setFileName(String fileName) {
	    this.fileName = fileName;
	}
   
	public String getFileName() {
		return fileName;
	}
	
	public LogWriter(){}
	
	
	/**
	 * Ispisuje poruku na kraj fajla
	 * @param warning
	 */
	public void write(String warning){
		try (FileWriter fileWriter = new FileWriter(new File(fileName),true)) {
			fileWriter.append(warning);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
