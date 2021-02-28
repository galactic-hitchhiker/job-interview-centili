package com.ftpclient.ftp.exceptions;

/**
 * 
 * Exception za FTP protokol
 * @author Djordje Velickovic
 *
 */
public class FTPException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public FTPException(String message) {
		super(message);
	}
	
}
