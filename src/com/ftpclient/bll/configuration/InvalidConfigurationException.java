package com.ftpclient.bll.configuration;

/**
 * Exception koji se baca kad nesto nije uredu sa konfiguracijom
 * 
 * @author Djordje Velickovic
 *
 */
public class InvalidConfigurationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidConfigurationException() {}
	
	public InvalidConfigurationException(String message) {
		super(message);
	}
	// Example 1 of cfg file
	//
	//user:user
	//password:pass
	//server:127.0.0.1
	//files:/var/file1;/var/file2
	//
	
	
}
