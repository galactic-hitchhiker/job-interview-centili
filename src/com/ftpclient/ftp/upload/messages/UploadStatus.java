package com.ftpclient.ftp.upload.messages;

import java.io.File;

/**
 * Status negde u procesu upload-a
 * Fajl je u stanju slanja, salju se delici informacija u zavisnosti od refresh rate-a slanja poruka
 * 
 * @author Djordje Velickovic
 *
 */
public class UploadStatus extends TransferStatus {
	/**
	 * Prebacen deo fajla u bajtovima
	 */
	private long transferedChunkInBytes;
	/**
	 * Vreme potrebno za slanje dela fajla, u nanosekundama
	 */
	private long timeForTransferedChunk;
	/**
	 * prebaceni bajtovi do sada 
	 */
	private long transferedBytesUntilNow;
	/**
	 * Razlika u vremenu od kada je poceo upload do trenutka slanja poruke u nanosekundama
	 */
	private long timeFromBeginning;
	
	/**
	 * Getter za poslate bajtove
	 * @return
	 */
	public long getTransferedChunkInBytes() {
		return transferedChunkInBytes;
	}

	
	public long getTimeForTransferedChunk() {
		return timeForTransferedChunk;
	}


	/**
	 * Getter za bajtove koji su poslati do sad
	 * @return
	 */
	public long getTransferedBytesUntilNow() {
		return transferedBytesUntilNow;
	}

	/**
	 * Getter za vreme proteklo od pocetka, u milisekundama
	 * @return
	 */
	public long getCurrentTime() {
		return timeFromBeginning;
	}

	public UploadStatus(File transferingFile, long transferedChunkInBytes,
			long timeForTransferedChunk, long transferedBytesUntilNow, long currentTime) {
		super(transferingFile);
		this.transferedChunkInBytes = transferedChunkInBytes;
		this.timeForTransferedChunk = timeForTransferedChunk;
		this.transferedBytesUntilNow = transferedBytesUntilNow;
		this.timeFromBeginning = currentTime;
		
	}


	@Override
	public String toString() {
		return super.toString()+ " UploadStatus [transferedChunkInBytes=" + transferedChunkInBytes + ", timeForTransferedChunk="
				+ timeForTransferedChunk + ", transferedBytesUntilNow=" + transferedBytesUntilNow + ", currentTime="
				+ timeFromBeginning + "]";
	}
}
