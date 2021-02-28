package com.ftpclient.ftp.upload.messages;

import java.io.File;

/**
 * Status pocetka slanja fajla
 * 
 * @author Djordje Velickovic
 *
 */
public class StartingStatus extends TransferStatus{
	
	/**
	 * Checkpoint kada je zapocelo slanje fajla u nanosekundama
	 */
	private long startingTime;
	
	/**
	 * Vraca vreme pocetka slanja u nanosekundama
	 * 
	 * @return
	 */
	public long getStartingTime() {
		return startingTime;
	}
	
	/**
	 * Konstruktor
	 * 
	 * @param transferingFile
	 * @param startingTime
	 */
	public StartingStatus(File transferingFile, long startingTime) {
		super(transferingFile);
		this.startingTime = startingTime;
	}

	@Override
	public String toString() {
		return super.toString()+"StartingStatus [startingTime=" + startingTime + "]";
	}

	
	
}
