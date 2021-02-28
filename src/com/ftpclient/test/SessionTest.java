package com.ftpclient.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Test;

import com.ftpclient.ftp.exceptions.FTPException;
import com.ftpclient.ftp.exceptions.FTPServerException;
import com.ftpclient.ftp.exceptions.UnknownFTPMessageException;
import com.ftpclient.ftp.session.FTPSession;
import com.ftpclient.ftp.upload.messages.FinishedStatus;
import com.ftpclient.ftp.upload.messages.StartingStatus;
import com.ftpclient.ftp.upload.messages.UploadStatus;
import com.ftpclient.util.events.EventListener;

/**
 * Za pokretanje testova, podesiti sopstvene parametre
 * neki fajlovi od 300-400 mb nisu poslati sa source kodom
 * podesiti i imena fajlova za upload
 * 
 * @author Djordje Velickovic
 *
 */
public class SessionTest {
	
	private String username = "ftpuser";
	private String password = "pass";
	private String host = "192.168.0.12";
	
	@Test
	public void validLoginTest() throws FTPServerException, IOException, UnknownFTPMessageException {
		
		FTPSession ftpSession = new FTPSession();
		ftpSession.startSession(username, password, host);
		ftpSession.endSession();
		assertTrue(true);
	}
	
	@Test(expected = FTPServerException.class)
	public void badCredentials() throws FTPServerException, IOException, UnknownFTPMessageException {
		
		FTPSession ftpSession = new FTPSession();
		ftpSession.startSession("zika", "mika", host);
		ftpSession.endSession();
		fail();
	}

	@Test(expected = UnknownHostException.class)
	public void badHost() throws FTPServerException, IOException, UnknownFTPMessageException {
		
		FTPSession ftpSession = new FTPSession();
		ftpSession.startSession(username, password, "asd");
		ftpSession.endSession();
	}
	
	@Test
	public void uploadFile() throws FTPException, IOException, UnknownFTPMessageException {
		FTPSession ftpSession = new FTPSession();
		ftpSession.startSession(username, password, host);
		
		ftpSession.uploadFile(new File("test.txt"));
		
		ftpSession.endSession();
		
		assertTrue(true);
	}
	
	@Test(expected = FTPException.class)
	public void fileDoesnNotExists() throws FTPException, IOException, UnknownFTPMessageException {
		FTPSession ftpSession = new FTPSession();
		ftpSession.startSession(username, password, host);
		try {
			ftpSession.uploadFile(new File("rama.zip"));
		}
		finally {
			ftpSession.endSession();
		}
		
		fail();
	}
	
	boolean failed;
	@Test
	public void paralelUploading() throws FTPException, IOException, UnknownFTPMessageException, InterruptedException {
		failed = false;
		
		Thread t1 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					FTPSession ftpSession = new FTPSession();
					ftpSession.startSession(username, password, host);
					
					ftpSession.getUploader().getOnStartingUploadEvent().addEventListener(new EventListener<StartingStatus>() {
						
						@Override
						public void performAction(Object sender, StartingStatus args) {
							System.out.println(args);
							
						}
					});
					
					ftpSession.getUploader().getOnUploadingEvent().addEventListener(new EventListener<UploadStatus>() {
						
						@Override
						public void performAction(Object sender, UploadStatus args) {
							System.out.println(args);
							
						}
					});
					
					ftpSession.getUploader().getOnFinishedUploadEvent().addEventListener(new EventListener<FinishedStatus>() {
						
						@Override
						public void performAction(Object sender, FinishedStatus args) {
							System.out.println(args);
						}
						
					});
					
					ftpSession.uploadFile(new File("test1.txt"));
					
					ftpSession.endSession();
				} catch (Exception e) {
					failed = true;
				}
				
			}
		});
		t1.start();
		
		Thread t2 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					FTPSession ftpSession = new FTPSession();
					ftpSession.startSession(username, password, host);
					
					ftpSession.getUploader().getOnStartingUploadEvent().addEventListener(new EventListener<StartingStatus>() {
						
						@Override
						public void performAction(Object sender, StartingStatus args) {
							System.out.println(args);
							
						}
					});
					
					ftpSession.getUploader().getOnUploadingEvent().addEventListener(new EventListener<UploadStatus>() {
						
						@Override
						public void performAction(Object sender, UploadStatus args) {
							System.out.println(args);
							
						}
					});
					
					ftpSession.getUploader().getOnFinishedUploadEvent().addEventListener(new EventListener<FinishedStatus>() {
						
						@Override
						public void performAction(Object sender, FinishedStatus args) {
							System.out.println(args);
						}
						
					});
					
					
					ftpSession.uploadFile(new File("test2.txt"));
					
					ftpSession.endSession();
				} catch (Exception e) {
					failed = true;
				}
				
			}
		});
		t2.start();
		
		Thread t3 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					FTPSession ftpSession = new FTPSession();
					ftpSession.startSession(username, password, host);
					
					ftpSession.getUploader().getOnStartingUploadEvent().addEventListener(new EventListener<StartingStatus>() {
						
						@Override
						public void performAction(Object sender, StartingStatus args) {
							System.out.println(args);
							
						}
					});
					
					ftpSession.getUploader().getOnUploadingEvent().addEventListener(new EventListener<UploadStatus>() {
						
						@Override
						public void performAction(Object sender, UploadStatus args) {
							System.out.println(args);
							
						}
					});
					
					ftpSession.getUploader().getOnFinishedUploadEvent().addEventListener(new EventListener<FinishedStatus>() {
						
						@Override
						public void performAction(Object sender, FinishedStatus args) {
							System.out.println(args);
						}
						
					});
					
					ftpSession.uploadFile(new File("test3.txt"));
					
					ftpSession.endSession();
				} catch (Exception e) {
					failed = true;
				}
				
			}
		});
		t3.start();
		
		t1.join();
		t2.join();
		t3.join();
		
		assertTrue(!failed);
	}
	
	
	
	
	
}
