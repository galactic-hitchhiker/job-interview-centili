package com.ftpclient.bll.messaging;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ftpclient.ftp.upload.messages.TransferStatus;

/**
 * Redovi poruka, simulacija nekog mailbox-a
 * Potencijalno dodavanje redova i za druge operacije ako se bude prosirivala aplikacije (download, listing ...)
 * 
 * @author Djordje Velickovic
 */
public class MessageQueues {
	
	/**
	 * Blokirajuci red za poruke uploud-a 
	 */
	private BlockingQueue<TransferStatus> uploadTransferMessages;
	
	/**
	 * Getter za mailbox upload-a
	 * @return
	 */
	public BlockingQueue<TransferStatus> geUploadTransferMessages() {
		return uploadTransferMessages;
	}
	
	/**
	 * Konstruktor
	 */
	public MessageQueues() {
		uploadTransferMessages = new LinkedBlockingQueue<>();
	}
	
}
