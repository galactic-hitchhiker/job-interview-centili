package com.ftpclient.bll.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.ftpclient.bll.configuration.parameters.FilesParameterReader;
import com.ftpclient.bll.configuration.parameters.ParameterReaderManager;
import com.ftpclient.bll.configuration.parameters.PasswordParameterReader;
import com.ftpclient.bll.configuration.parameters.ServerParameterReader;
import com.ftpclient.bll.configuration.parameters.UserParameterReader;
import com.ftpclient.om.AppConfiguration;
import com.ftpclient.util.log.LogManager;

// Example 1 of cfg file
//
//user:user
//password:pass
//server:127.0.0.1
//files:/var/file1;/var/file2
//
//
//Example 2 of cfg file, *defaults for user:user, for password:pass
//
//server:127.0.0.1
//files:/var/file1;/var/file2
//
//
//Example 2 of cfg file, *defaults for user:user, for password:pass, for server:localhost
//
//files:/var/file1;/var/file2

/**
 * 
 * Citac konfiguracije iz fajla
 * @author Djordje Velickovic
 *
 */
public class FileConfigurationReader implements ICongfigurationReader {
	
	/**
	 * Delimiter izmedju [name]?[value]
	 */
	private String nameValueDelimiter = ":";
	/**
	 * Delimiter izmedju fajlova
	 */
	private String filesDelimiter = ";";
	/**
	 * Maksimalni broj fajlova
	 * TODO: moze se izvuci u konfiguraciju
	 */
	private int maxFiles = 5;
	/**
	 * Fajl konfiguracije
	 */
	private File configurationFile;
	
	/**
	 * Menadzer citanja parametara
	 */
	private ParameterReaderManager parameterReaderManager;
	
	
	/**
	 * konstruktor
	 * @param cfgFile fajl za citanje
	 */
	public FileConfigurationReader(File cfgFile) {
		this.configurationFile = cfgFile;
		
		parameterReaderManager = new ParameterReaderManager(nameValueDelimiter);
		
		// dodaju se parametri i njihovi citaci
		parameterReaderManager.addParameterReader("username", new UserParameterReader());
		parameterReaderManager.addParameterReader("password", new PasswordParameterReader());
		parameterReaderManager.addParameterReader("server", new ServerParameterReader());
		parameterReaderManager.addParameterReader("files", new FilesParameterReader(filesDelimiter, maxFiles));
		// dodaje se obavezan parametar
		parameterReaderManager.addRequiredParameter("files");
	}


	@Override
	public AppConfiguration readConfiguration() throws InvalidConfigurationException {
		if (!configurationFile.exists()) {
			// upustvo za kreiranje cfg fajla
			StringBuilder sb = new StringBuilder();
			sb.append("Create "+configurationFile.getName()+" file.\n")
			.append("In folowing format: \n\n")
			.append("username:[username for ftp server]\n")
			.append("password:[password for ftp server]\n")
			.append("server:[server's host name or ip address]\n")
			.append("files:[file names delimited with \";\"]\n");
			System.out.println(sb);
			
			throw new InvalidConfigurationException("Application configuration file doesn't exists.");
		}
		
		AppConfiguration appConfiguration = null;
		
		// prolazi kroz fajl i cita konfiguraciju u kompleksnosti O(n)
		try (Scanner fileScanner = new Scanner(configurationFile)) {
			
			appConfiguration = new AppConfiguration();
			
			while (fileScanner.hasNextLine()) {
				String cfgLine = fileScanner.nextLine();
				if (!cfgLine.isEmpty()) {
					parameterReaderManager.readParameter(cfgLine,appConfiguration);
				}
			}
			
			if (!parameterReaderManager.areAllRequiredParametersRead()) {
				throw new InvalidConfigurationException("Required parameters are missing.");
			}
			
		} catch (IOException e) {
			System.err.println(e.getMessage());
			LogManager.addLog(e, e.getMessage());
		}
		return appConfiguration;
	}
	
	
	
	
}
