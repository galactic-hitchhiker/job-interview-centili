package com.ftpclient.ftp.serverresponse;

import com.ftpclient.ftp.exceptions.FTPServerException;
import com.ftpclient.ftp.session.FTPSession;

/**
 * Abort klasa koja nasledjuje Action interfejs, 
 * Prekida sesiju i baca ftp exception sa porukom
 * 
 * @author Djordje Velickovic
 *
 */
public class Abort implements Action {

	@Override
	public void doAction(FTPSession session, String message) throws FTPServerException {
		session.endSession();
		throw new FTPServerException(message);
	}
	
	
	
	
	
	
}
