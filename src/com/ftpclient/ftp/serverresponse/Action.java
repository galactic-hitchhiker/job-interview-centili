package com.ftpclient.ftp.serverresponse;

import com.ftpclient.ftp.exceptions.FTPServerException;
import com.ftpclient.ftp.session.FTPSession;

/**
 * 
 * Interfejs koji kreira ugovor o "akcijama" koje se preduzimaju na odgovor servera
 * 
 * @author Djordje Velickovic
 *
 */
public interface Action {
	void doAction(FTPSession sessionData, String message) throws FTPServerException;

}
