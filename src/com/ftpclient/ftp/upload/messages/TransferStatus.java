package com.ftpclient.ftp.upload.messages;

import java.io.File;

/**
 * Apstraktna klasa Statusa Transfera
 * 
 * @author Djordje Velickovic
 *
 */
public abstract class TransferStatus {
	/**
	 * Fajl koji se salje
	 */
	private File transferingFile;
	
	/**
	 * Getter za fajl koji se salje
	 * @return
	 */
	public File getTransferingFile() {
		return transferingFile;
	}
	
	/**
	 * Konstruktor 
	 * @param transferingFile fajl koji se salje
	 */
	public TransferStatus(File transferingFile) {
		this.transferingFile = transferingFile;
	}

	@Override
	public String toString() {
		return "TransferStatus [transferingFile=" + transferingFile.getName() + "]";
	}
	
}
