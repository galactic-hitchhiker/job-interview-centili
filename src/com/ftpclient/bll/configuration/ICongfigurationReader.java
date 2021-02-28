package com.ftpclient.bll.configuration;

import com.ftpclient.om.AppConfiguration;

/**
 * 
 * Interfejs za citanje konfiguracije. Konkretno postoji jedan nacin za citanje
 * konfiguracije. Iz fajla. Ali moze posluziti za kreiranje FakeConfigurationReader-a
 * ili za citanje konfiguracije sa nekog drugog izvora
 * 
 * @author Djordje Velickovic
 *
 */
public interface ICongfigurationReader {
	/**
	 * Cita konfiguraciju i vraca objekat klase AppConfiguration
	 * @return vraca null ako je bezuspesno procitana konfiguracija
	 * @throws InvalidConfigurationException
	 */
	AppConfiguration readConfiguration() throws InvalidConfigurationException;
}
