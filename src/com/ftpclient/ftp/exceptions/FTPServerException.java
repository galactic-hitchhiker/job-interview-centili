package com.ftpclient.ftp.exceptions;

/**
 * Exception koji se formira ukoliko dodje bilo koja poruka sa greskom sa servera
 * 
 * 
 * @author Djordje Velickovics
 *
 */
public class FTPServerException extends FTPException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public FTPServerException(String message) {
		super(message);
	}
	
}
