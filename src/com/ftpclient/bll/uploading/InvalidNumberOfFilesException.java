package com.ftpclient.bll.uploading;

public class InvalidNumberOfFilesException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidNumberOfFilesException(int expectedMaxNumberOfFiles, int numberOfFiles) {
		super("Number of files must be larger than zero and smaller than "+(expectedMaxNumberOfFiles+1)+". You have specified "+numberOfFiles+"!");
	}
	
}
