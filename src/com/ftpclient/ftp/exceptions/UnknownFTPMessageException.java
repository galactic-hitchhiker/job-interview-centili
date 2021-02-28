package com.ftpclient.ftp.exceptions;

/**
 * Poruka ukoliko je dosla neka poruka sa servera za koju ne postoji implementacija
 * 
 * @author Djordje Velickovics
 *
 */
public class UnknownFTPMessageException  extends FTPException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UnknownFTPMessageException() {
		super("Server sent unknown message!");
	}
	
	
}
