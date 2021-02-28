package com.ftpclient.bll.configuration.parameters;


import java.util.HashMap;
import java.util.Map;

import com.ftpclient.bll.configuration.InvalidConfigurationException;
import com.ftpclient.om.AppConfiguration;

/**
 * Menadzer citanja konfiguracije
 * Implementira logiku validacije konfiguracionog fajla
 * 
 * @author Djordje Velickovic
 *
 */
public class ParameterReaderManager {
	/**
	 * Mapa koja u zavisnosti od naziva argumenta vraca i odgovarajuci handler
	 */
	private Map<String, ParameterReader> parameterReadersMap;
	
	/**
	 * Mapa iskoriscenosti obaveznih argumenata
	 */
	private Map<String, Boolean> usageOfRequiredParameters;
	
	/**
	 * delimiter izmedju imena i vrednosti name?value
	 */
	private String nameValueDelimiter;
	
	
	/**
	 * dodaje citac parametra i njegovo ime
	 * @param parameterName
	 * @param parameterReader
	 */
	public void addParameterReader(String parameterName, ParameterReader parameterReader) {
		parameterReadersMap.put(parameterName, parameterReader);
	}
	
	/**
	 * Dodaje obavezni parameter
	 * @param parameterName
	 */
	public void addRequiredParameter(String parameterName) {
		usageOfRequiredParameters.put(parameterName, false);
	}
	
	/**
	 * Konstruktor
	 */
	public ParameterReaderManager(String nameValueDelimiter) {
		
		this.nameValueDelimiter = nameValueDelimiter;
		
		parameterReadersMap = new HashMap<>();
		
		// povezivanje obaveznih argumenata i njihova iskoriscenost
		usageOfRequiredParameters = new HashMap<>();
	}
	
	
	/**
	 * Cita i parsira liniju u konfiguracionom fajlu
	 * 
	 * @param configurationLine
	 * @param appConfiguration
	 * @throws InvalidConfigurationException
	 */
	public void readParameter(String configurationLine, AppConfiguration appConfiguration) throws InvalidConfigurationException {
		
		String[] delimitedCfg = configurationLine.split(nameValueDelimiter);
		
		if (delimitedCfg.length != 2) {
			throw new InvalidConfigurationException("Invalid parameter configuration.");
		}
		
		String name = delimitedCfg[0];
		String value = delimitedCfg[1];
		
		ParameterReader reader = parameterReadersMap.get(name);
		if (reader == null) {
			throw new InvalidConfigurationException("Unknown configuration parameter \""+name+"\"");
		}
		
		reader.read(value, appConfiguration);
		
		if (usageOfRequiredParameters.containsKey(name)) {
			usageOfRequiredParameters.put(name, true);
		}
	}
	
	/**
	 * Proverava da li su svi obavezni parametri uneseni
	 * @return
	 */
	public boolean areAllRequiredParametersRead() {
		for (boolean read : usageOfRequiredParameters.values()) {
			if (!read) {
				return false;
			}
		}
		return true;
	}
	
	
}
