package com.ftpclient.ftp.serverresponse;

import com.ftpclient.ftp.session.FTPSession;

/**
 * Akcija koja ne treba nista da radi, ne baca nikakav izuzetak kad se udje u nju
 * 
 * @author corax
 *
 */
public class Procceed implements Action {

	@Override
	public void doAction(FTPSession sessionData, String message) {
		// ne treba nista da radi, samo prolazi kroz ovo; mozda dodati nesto za logovanje.. 
	}

}
