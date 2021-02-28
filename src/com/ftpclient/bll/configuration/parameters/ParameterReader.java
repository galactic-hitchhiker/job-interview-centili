package com.ftpclient.bll.configuration.parameters;

import com.ftpclient.bll.configuration.InvalidConfigurationException;
import com.ftpclient.om.AppConfiguration;

/**
 * Interfejs koji predstavlja ugovor izmedju citaca parametara
 * 
 * @author Djordje Velickovic
 *
 */
public interface ParameterReader {
	/**
	 * Cita parametar na osnovu vrednosti iz konfiguracije
	 * @param value vrednost [name]:[value]
	 * @param appConfiguration konfiguracija aplikacije
	 * @throws InvalidConfigurationException ukoliko konfiguracija nije validna baca se izuzetak
	 */
	void read(String value, AppConfiguration appConfiguration) throws InvalidConfigurationException;
}
