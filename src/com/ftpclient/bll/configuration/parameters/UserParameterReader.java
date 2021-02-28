package com.ftpclient.bll.configuration.parameters;

import com.ftpclient.om.AppConfiguration;

/**
 * Cita parametar "user", smesta korisnicko ime u konfiguraciju
 * 
 * 
 * @author Djordje Velickovic
 *
 */
public class UserParameterReader implements ParameterReader {
	
	@Override
	public void read(String value, AppConfiguration appConfiguration) {
		appConfiguration.setUsername(value);
	}

}
