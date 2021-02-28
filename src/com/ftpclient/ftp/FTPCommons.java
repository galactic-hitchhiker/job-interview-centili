package com.ftpclient.ftp;

/**
 * FTP komande
 * 
 * 
 * @author corax
 *
 */
public class FTPCommons {
	
	
	// ako se komunicira sa Linux-a prema windows ftp serveru, linux ne salje \r\n vec samo
	// \n pri ... .println(...), posto linux server ignorise \r karakter, onda je ovo dodato iza svake komande
	// zbog interoperabilnosti
	public static final String CMD_ENDING = "\r\n";
	
	public static final int COMMUNICATION_PORT = 21;
	
	public static final String PASSIVE_MODE = "PASV"; 
	
	public static final String USERNAME = "USER";
	
	public static final String PASSWORD = "PASS";
	
	public static final String UPLOAD = "STOR";
	
	public static final String QUIT = "QUIT";
	
	public static final String BINNARY_DATATYPE = "TYPE I";
	
	public static final String ASCII_DATATYPE = "TYPE A"; // not used
	
	public static final String STREAM_MODE = "MODE S";
	
}
