package com.ftpclient.ftp.upload;

import java.io.File;

import com.ftpclient.ftp.exceptions.FTPException;
import com.ftpclient.ftp.upload.messages.FinishedStatus;
import com.ftpclient.ftp.upload.messages.StartingStatus;
import com.ftpclient.ftp.upload.messages.UploadStatus;
import com.ftpclient.util.events.Event;

/**
 * Interfejs za uploader-a
 * 
 * @author corax
 *
 */
public interface IUploader {
	
	/**
	 * Uploaduje prosledjeni faj;
	 * 
	 * @param fileForUpload
	 * @return
	 * @throws FTPException
	 */
	boolean uploadFile(File fileForUpload) throws FTPException;
	
	/**
	 * vraca event za pocetak upload-a
	 * @return
	 */
	Event<StartingStatus> getOnStartingUploadEvent();
	
	/**
	 * Vraca event za azuriranje statusa pri upload-u
	 * @return
	 */
	Event<UploadStatus> getOnUploadingEvent();
	
	/**
	 * Vraca event za kraj transfera
	 * @return
	 */
	Event<FinishedStatus> getOnFinishedUploadEvent();
}
