package com.ftpclient.bll.configuration.parameters;

import com.ftpclient.om.AppConfiguration;

/**
 * Cita lozinku iz konfiguracije "password" parametra
 * 
 * @author Djordje Velickovic
 *
 */
public class PasswordParameterReader implements ParameterReader {

	@Override
	public void read(String value, AppConfiguration appConfiguration) {
		appConfiguration.setPassword(value);
	}

}
