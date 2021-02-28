package com.ftpclient.bll.configuration.parameters;

import com.ftpclient.om.AppConfiguration;

/**
 * Cita hostname iz konfiguracije "server" parametra
 * 
 * @author Djordje Velickovic
 *
 */
public class ServerParameterReader implements ParameterReader {

	@Override
	public void read(String value, AppConfiguration appConfiguration) {
		appConfiguration.setServer(value);
	}

}
