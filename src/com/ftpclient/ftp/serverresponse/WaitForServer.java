package com.ftpclient.ftp.serverresponse;


import com.ftpclient.ftp.exceptions.FTPServerException;
import com.ftpclient.ftp.session.FTPSession;

/**
 * Ceka 220 kod od servera da nastavi sa konektovanjem
 * 
 * @author Djordje Velickovic
 *
 */
public class WaitForServer implements Action {

	@Override
	public void doAction(FTPSession session, String message) throws FTPServerException {
		// potencijalni infinity loop! ali ne moze se ocekivati od servera da salje samo 120 code! ako baguje server
		// prekinuti aplikaciju
		try { 
			String response = session.getIn().readLine();
			session.getMessageHandler().handleMessage(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
