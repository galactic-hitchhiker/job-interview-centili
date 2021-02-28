package com.ftpclient;

import java.io.File;

import com.ftpclient.bll.configuration.FileConfigurationReader;
import com.ftpclient.bll.configuration.ICongfigurationReader;
import com.ftpclient.bll.messaging.MessageQueues;
import com.ftpclient.bll.uploading.UploadingComponent;
import com.ftpclient.console.Console;
import com.ftpclient.om.AppConfiguration;
import com.ftpclient.util.log.LogManager;

/**
 * Main klasa aplikacije
 * 
 * @author Djordje Velickovic
 *
 */
public class App {
	
	public static void main(String[] args) {
		
		File configurationFile = new File("conf.txt");
		
		// instancira citac konfiguracije
		ICongfigurationReader configurationReader = new FileConfigurationReader(configurationFile);
		
		
		// instancijra konzolu, za ispis poruka
		
		
		try {
			// cita konfiguraciju
			AppConfiguration appConfiguration = configurationReader.readConfiguration();
			
			Console console = new Console(appConfiguration.getFilesForTransfer());
			
			// kreira redove poruka
			MessageQueues messageQueues = new MessageQueues();
			
			// kreria uploading komponentu
			UploadingComponent uploadingComponent = new UploadingComponent();
			
			
			// pokrece upload fajlova
			uploadingComponent.initiateFileUploading(appConfiguration, messageQueues);
			
			// kaze konzoli da krene sa osluskivanjem poruka
			console.startListening(messageQueues);
			
		} catch (Exception e) {
			LogManager.addLog(e, e.getMessage());
			System.err.println(e.getMessage());
		}		
	}
	
}
