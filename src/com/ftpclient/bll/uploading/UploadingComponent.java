package com.ftpclient.bll.uploading;

import java.io.File;

import com.ftpclient.bll.messaging.MessageQueues;
import com.ftpclient.ftp.session.FTPSession;
import com.ftpclient.ftp.upload.messages.FinishedStatus;
import com.ftpclient.ftp.upload.messages.StartingStatus;
import com.ftpclient.ftp.upload.messages.TransferStatus;
import com.ftpclient.ftp.upload.messages.UploadStatus;
import com.ftpclient.om.AppConfiguration;
import com.ftpclient.util.events.EventListener;
import com.ftpclient.util.log.LogManager;

/**
 * Klasa koja priprema thread za pocetak upload-a
 * 
 * 
 * @author Djordje Velickovic
 *
 */
public class UploadingComponent {
	
	
	/**
	 * 
	 * Metoda koja inicira upload fajlova
	 * @param appConfiguration
	 * @param messageQueues
	 * @return
	 */
	public boolean initiateFileUploading(AppConfiguration appConfiguration,MessageQueues messageQueues) {
		
		// prolazi kroz svaki fajl koji treba da se uploud-uje
		for (File fileForUpload : appConfiguration.getFilesForTransfer()) {
			
			// kreira thread sessije, jer ce se otvoriti onoliko paralelnih konekcija koliko ima fajlova za upload
			Thread sessionThread = new Thread(new Runnable() {
				
				/**
				 * Pomocna metoda za slanje poruka u queue
				 * @param ts
				 */
				private void sendMessage(TransferStatus ts) {
					try {
						messageQueues.geUploadTransferMessages().put(ts);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				@Override
				public void run() {
					
					FTPSession session = null;
					try {
						// kreira sesiju
						session = new FTPSession();
						
						// pokrece se sesija
						session.startSession(appConfiguration.getUsername(), appConfiguration.getPassword(), appConfiguration.getServer());
						
						// dodaje se listener za pocetak upload-a na event uploader-a
						session.getUploader().getOnStartingUploadEvent().addEventListener(new EventListener<StartingStatus>() {
							
							@Override
							public void performAction(Object sender, StartingStatus args) {
								sendMessage(args);
							}
						});

						// dodaje se listener za status u toku upload-a na event uploader-a
						session.getUploader().getOnUploadingEvent().addEventListener(new EventListener<UploadStatus>() {

							@Override
							public void performAction(Object sender, UploadStatus args) {
								sendMessage(args);
							}
							
						});
						
						// dodaje se listener za kraj upload-a na event uploader-a
						session.getUploader().getOnFinishedUploadEvent().addEventListener(new EventListener<FinishedStatus>() {

							@Override
							public void performAction(Object sender, FinishedStatus args) {
								sendMessage(args);
							}
						});
						
						// kaze sesiji da pokrene upload fajla
						session.uploadFile(fileForUpload);
						
					} catch (Exception e) {
//						System.err.println(e.getMessage());
						LogManager.addLog(e, e.getMessage());
						
						// ukoliko se desila neka greska, poslati odmah poruku greske u mailbox.
						FinishedStatus finishedStatus = new FinishedStatus(fileForUpload, 0, 0);
						finishedStatus.setErrorMessage(e.getMessage());
						sendMessage(finishedStatus);
					}
					finally {
						// zatvoriti u svakom slucaju sesiju
						if (session != null) {
							session.endSession();
						}
					}
				}
			});
			// pokrenuti thread sesije
			sessionThread.start();
		}
		return true;
	}
}
