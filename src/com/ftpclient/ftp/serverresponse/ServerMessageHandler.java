package com.ftpclient.ftp.serverresponse;

import java.util.HashMap;
import java.util.Map;

import com.ftpclient.ftp.exceptions.FTPServerException;
import com.ftpclient.ftp.exceptions.UnknownFTPMessageException;
import com.ftpclient.ftp.session.FTPSession;

/**
 * 
 * Klasa koja vodi hendluje odgovorima sa servera, prepoznaje ih i daje odgovarajuci odgovor na njih
 * 
 * @author Djordje Velickovic
 *
 */
public class ServerMessageHandler {
	// ukoliko je u DEBUG modu onda se stampaju u konzolu odgovori sa servera
	private static final boolean DEBUG = false;
	
	
	/**
	 * Mapa kodova i akcija koje trebaju da se preduzmu u zavisnosti od odgovora
	 */
	private Map<String, Action> codeActionMap;
	/**
	 * Sesija 
	 */
	private FTPSession sessionData;
	
	/**
	 * Konstruktor
	 * 
	 * @param sessionData
	 */
	public ServerMessageHandler(FTPSession sessionData) {
		this.sessionData = sessionData;
		
		Action abortAction = new Abort();
		Action waitForServer = new WaitForServer(); // akcija za cekanje xx minuta dok server nije opet dostupan, kad bude, server odgovara sa 220 kodom.
		Action procceed = new Procceed();
//		Action loggedOut = new LoggedOut(); 
		
		codeActionMap = new HashMap<>();
		
		codeActionMap.put("120", waitForServer); // ceka na server da bude opet dostupan
		
		//OK messagez
		codeActionMap.put("220", procceed); // uspesno uspostavljena konekcija sa serverom preko komunikacionog kanala
		codeActionMap.put("331", procceed);	// pod normalnim uslovima nakon unosna username-a
		codeActionMap.put("200", procceed);	// OK kod, javlja se nakon setovanja nekih od parametara serveru
		codeActionMap.put("227", procceed);	// kod nakon PASV komande sa parametrima za data kanal
		codeActionMap.put("150", procceed); // ok to send data, ocekivano nakon STOR
		codeActionMap.put("226", procceed); //? ? ? transfer uspesno obavljen. Mozda dodati novu akciju?!
		codeActionMap.put("230", procceed); // successfully logged in
		codeActionMap.put("125", procceed);	// data port already oppened
		
		codeActionMap.put("221", procceed); // posle quit komande
		
		//ERRORz
		codeActionMap.put("421", abortAction);	// server puko
		
		// kodovi gresaka
		codeActionMap.put("500", abortAction);
		codeActionMap.put("501", abortAction);
		codeActionMap.put("530", abortAction);
		codeActionMap.put("500", abortAction);
		codeActionMap.put("550", abortAction);
	}	
	
	
	/**
	 * Metoda koja henduje poruke sa servera
	 * 
	 * @param message
	 * @throws UnknownFTPMessageException ukoliko ne postoji odgovor
	 * @throws FTPServerException ukoliko se dobije odgovor koji odgovara greski
	 */
	public void handleMessage(String message) throws UnknownFTPMessageException, FTPServerException {
		if (DEBUG)
			System.out.println(message);
		
		if (message == null) {
			throw new FTPServerException("Server is not available.");
		}
		
		String messageCode = message.substring(0, 3); // odstranjuje prva tri karaktera za code poruke
		
		
		if (!codeActionMap.containsKey(messageCode)) {
			throw new UnknownFTPMessageException();
		}
		
		Action action = codeActionMap.get(messageCode);
		action.doAction(sessionData,message);
		
	}
	
}
