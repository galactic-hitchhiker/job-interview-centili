package com.ftpclient.ftp.upload.messages;


import java.io.File;

/**
 * Poruka sa Finish statusom slanja fajla
 * 
 * @author Djordje velickovic
 *
 */
public class FinishedStatus extends TransferStatus {

	/**
	 * Vreme za koje je obavljen transfer u nano sekundama
	 */
	private long timeForFileTransfer;
	
	/**
	 * Poruka o greski koja je nastala, ako je nastala
	 */
	private String errorMessage = null;
	
	/**
	 * Vreme u nanosekundama kada je zavrsen transfer
	 */
	private long timeOfFinishing;
	
	public long getTimeOfFinishing() {
		return timeOfFinishing;
	}
	
	public void setTimeOfFinishing(long timeOfFinishing) {
		this.timeOfFinishing = timeOfFinishing;
	}
	
	/**
	 * Setter za poruku o greski
	 * 
	 * @param errorMessage
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	/**
	 * Getter za poruku o greski
	 * @return
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	
	/**
	 * Getter za vreme transfera
	 * @return
	 */
	public long getTimeForFileTransfer() {
		return timeForFileTransfer;
	}
	
	/**
	 * Konstruktor 
	 * @param transferingFile fajl koji se salje
	 * @param timeForFileTransfer vreme koje je bilo potrebno za slanje
	 */
	public FinishedStatus(File transferingFile, long timeForFileTransfer, long timeOfFinishing) {
		super(transferingFile);
		this.timeForFileTransfer = timeForFileTransfer;
		this.timeOfFinishing = timeOfFinishing;
	}

	@Override
	public String toString() {
		return super.toString()+"FinishedStatus [timeForFileTransfer=" + timeForFileTransfer + ", errorMessage=" + errorMessage
				+ ", timeOfFinishing=" + timeOfFinishing + "]";
	}

	
}
