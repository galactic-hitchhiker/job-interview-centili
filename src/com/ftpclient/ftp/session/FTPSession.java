package com.ftpclient.ftp.session;



import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.ftpclient.ftp.FTPCommons;
import com.ftpclient.ftp.exceptions.FTPException;
import com.ftpclient.ftp.exceptions.FTPServerException;
import com.ftpclient.ftp.exceptions.UnknownFTPMessageException;
import com.ftpclient.ftp.serverresponse.ServerMessageHandler;
import com.ftpclient.ftp.upload.FTPUploader;
import com.ftpclient.ftp.upload.IUploader;
import com.ftpclient.util.log.LogManager;


/**
 * Klasa sesije, sadrsi potrebne metode za komunikaciju sa serverom
 * 
 * @author Djordje Velickovic
 *
 */
public class FTPSession  {
	
	/**
	 * Komunikacioni socket koji komunicira na portu 21
	 */
	private Socket communicationSocket;
	/**
	 * Input sa servera
	 */
	private BufferedReader in;
	/**
	 * Output prema serveru
	 */
	private PrintWriter out;
	/**
	 * Server Message Handler
	 */
	private ServerMessageHandler messageHandler;
	/**
	 * Interfejs uploader-a, Poor man's dependency Injection 
	 */
	private IUploader uploader;
	
	/**
	 * Flag da li je sesija otvorena
	 */
	private boolean isStarted;
	
	/**
	 * Getter da li je zapoceta sesija
	 * @return
	 */
	public boolean isStarted() {
		return isStarted;
	}
	
	/**
	 * 
	 * Getter za uploader
	 * ako uploader nije kreiran, kreira novi
	 * 
	 * @return
	 */
	public IUploader getUploader() {
		if (uploader == null) {
			uploader = new FTPUploader(this);
		}
		return uploader;
	}
	
	/**
	 * Seter za uploader
	 * 
	 * @param uploader
	 */
	public void setUploader(IUploader uploader) {
		this.uploader = uploader;
	}
	
	/**
	 * Geter za input stream sa komunikacionim kanalom
	 * 
	 * @return
	 */
	public BufferedReader getIn() {
		return in;
	}
	
	/**
	 * Getter za output stream sa komunikacionim kanalom
	 * 
	 * @return
	 */
	public PrintWriter getOut() {
		return out;
	}
	
	/**
	 * Getter za Message handler
	 * 
	 * @return
	 */
	public ServerMessageHandler getMessageHandler() {
		return messageHandler;
	}
	
//	/**
//	 * Getter za komunikacioni Socket 
//	 * 
//	 * @return
//	 */
//	public Socket getCommunicationSocket() {
//		return communicationSocket;
//	}
	
	/**
	 * Konstruktor 
	 */
	public FTPSession(){
		super();
		messageHandler = new ServerMessageHandler(this);
		isStarted = false;
	}
	
	/**
	 * Konstruktor za Poor man's Dependency injection
	 * 
	 * @param uploader
	 */
	public FTPSession(IUploader uploader){
		this();
		this.uploader = uploader;
		
	}
	
	
	/**
	 * Metoda koja pokrece sesiju i uspostavlja konekciju sa serverom na komunikacionom kanalu
	 * 
	 * @param username korisnicko ime za ftp server
	 * @param password lozinka za ftp server
	 * @param host host name ftp servera
	 * @throws FTPServerException baca se kad server odgovri codom za gresku
	 * @throws UnknownFTPMessageException baca se kad server posalje poruku sa kodom koju client ne zna da razrezi
	 * @throws IOException Greska sa konekcijom
	 */
	public void startSession(String username, String password, String host) throws FTPServerException, UnknownFTPMessageException, IOException {
		if (isStarted) {
			return;
		}
		
		String response = null;
		// kreira socket
		communicationSocket = new Socket();
		communicationSocket.connect(new InetSocketAddress(host, FTPCommons.COMMUNICATION_PORT), 5000); // timeout 5 sekundi
		
		in = new BufferedReader(new InputStreamReader(communicationSocket.getInputStream()));
		out = new PrintWriter(new OutputStreamWriter(communicationSocket.getOutputStream()),true); // auto flush na true
		
		// ceka 220 odgovor od servera da se uspesno konektovao na communication port
		response = in.readLine();
		messageHandler.handleMessage(response);
		
		// salje komandu sa korisnickim imenom
		out.print(FTPCommons.USERNAME+" "+username+FTPCommons.CMD_ENDING);
		out.flush();
		
		// ceka odgovor
		response = in.readLine();
		// ako je username - ok onda nastavlja dalje, ako nije baca FTPServerException
		messageHandler.handleMessage(response);
		
		// salje lozinku
		out.print(FTPCommons.PASSWORD+" "+password+FTPCommons.CMD_ENDING);
		out.flush();
		
		// ceka odgovor od servera
		response = in.readLine();
		// ako je validan onda nastavlja dalje, ako nije baca excepiton
		messageHandler.handleMessage(response);
		
		isStarted = true;
	}
	
	
	/**
	 * Poziva upload fajla
	 * 
	 * @param fileForUpload fajl koji se upload-uje
	 * @throws FTPException baca excepiton ako dodje do neke greske pri komunikaciji sa serverom
	 */
	public void uploadFile(File fileForUpload) throws FTPException {
		getUploader().uploadFile(fileForUpload);
	}
	
	/**
	 * zavrssava sesiju i zatvra komunikacioni socket, sve strimove postavlja na null
	 */
	public void endSession() {
		String response;
		if (out != null) {
			try {
				// salje quit komandu
				out.print(FTPCommons.QUIT+FTPCommons.CMD_ENDING);
				out.flush();
				// ceka odgovor da je server zatvorio konekciju
				response = in.readLine();
				messageHandler.handleMessage(response);
			} catch (Exception e) {
				LogManager.addLog(e, e.getMessage());
			}
		}
		
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				LogManager.addLog(e, e.getMessage());
			}
		}
		
		if (out != null) {
			out.close();
		}
		
		if (communicationSocket != null) {
			try {
				communicationSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		in = null;
		out = null;
		communicationSocket = null;
		isStarted = false;
	}
	
	
}
	
	
	
	
	
	
	
	
	
