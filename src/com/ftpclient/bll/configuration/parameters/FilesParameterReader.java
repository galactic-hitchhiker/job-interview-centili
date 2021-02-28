package com.ftpclient.bll.configuration.parameters;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ftpclient.bll.configuration.InvalidConfigurationException;
import com.ftpclient.om.AppConfiguration;

/**
 * Cita parametar "files"
 * 
 * @author Djordje Velickovic
 *
 */
public class FilesParameterReader implements ParameterReader {

	/**
	 * Delimiter izmedju fajlova
	 */
	private String fileDelimiter;
	/**
	 * Maximalni broj fajlova
	 */
	private int maxFiles;
	
	/**
	 * Konstruktor 
	 * 
	 * @param fileDelimiter
	 * @param maxFiles
	 */
	public FilesParameterReader(String fileDelimiter, int maxFiles) {
		super();
		this.fileDelimiter = fileDelimiter;
		this.maxFiles = maxFiles;
	}

	@Override
	public void read(String value, AppConfiguration appConfiguration) throws InvalidConfigurationException {
		
		String[] files = value.split(fileDelimiter);
		
		if (files.length > maxFiles) {
			throw new InvalidConfigurationException("You have specified more than "+maxFiles+" files.");
		}
		
		
		Set<String> fileNameSet = new HashSet<>();
		List<File> filesForTransfer = new ArrayList<>();
		
		
		// proverava da li fajlovi postoje
		for (String fileName : files) {
			File file = new File(fileName);
			if (!file.exists()) {
				throw new InvalidConfigurationException(String.format("File %s does not exists.", fileName));
			}
			filesForTransfer.add(file);
		}
		
		// proverava da li ne postoje bar dva fajla sa isim imenom
		for (File f : filesForTransfer) {
			if (!fileNameSet.contains(f.getName())) {
				fileNameSet.add(f.getName());
			}
			else {
				throw new InvalidConfigurationException("Two or more files have the same name!");
			}
		}
		
		appConfiguration.getFilesForTransfer().addAll(filesForTransfer);
		
	}

}
