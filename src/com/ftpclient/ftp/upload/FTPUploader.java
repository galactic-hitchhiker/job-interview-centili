package com.ftpclient.ftp.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.ftpclient.ftp.FTPCommons;
import com.ftpclient.ftp.exceptions.FTPException;
import com.ftpclient.ftp.exceptions.FTPServerException;
import com.ftpclient.ftp.session.FTPSession;
import com.ftpclient.ftp.upload.messages.FinishedStatus;
import com.ftpclient.ftp.upload.messages.StartingStatus;
import com.ftpclient.ftp.upload.messages.UploadStatus;
import com.ftpclient.util.events.Event;
import com.ftpclient.util.log.LogManager;

/**
 * Uploader za FTP
 * 
 * @author Djordje Velickovic
 *
 */
public class FTPUploader implements IUploader{
	
	/**
	 * Velicina bafera 
	 */
	private final int BUFFER_SIZE = 4*1024;
	
	/**
	 * Okvirno vreme za slanje poruke
	 */
	private int statusRefreshNanoseconds;
	/**
	 * Port za data port
	 */
	private int port;
	/**
	 * Host na koji se salju podaci, dobija se iz odgovora posle PASV komande
	 */
	private String host;
	
	
	/**
	 * Event za pocetak upload-a
	 */
	private Event<StartingStatus> onStartingUploadEvent;
	/**
	 * Event koji se fire-uje u toku prenosa u odredjenom vremenskom intervalu
	 */
	private Event<UploadStatus> onUploadingEvent;
	/**
	 * Event koji se fire-uje po zavrsetku upload-a
	 */
	private Event<FinishedStatus> onFinishedUploadEvent;
	
	/**
	 * Sesija 
	 */
	private FTPSession session;
	
	@Override
	public Event<StartingStatus> getOnStartingUploadEvent() {
		return onStartingUploadEvent;
	}
	
	@Override
	public Event<UploadStatus> getOnUploadingEvent() {
		return onUploadingEvent;
	}
	
	@Override
	public Event<FinishedStatus> getOnFinishedUploadEvent() {
		return onFinishedUploadEvent;
	}	
	
	
	/**
	 * Konstruktor
	 * @param session otvorena sesija
	 */
	public FTPUploader(FTPSession session) {
		super();
		this.session = session;
		
		// zakucano vreme refresha slanja statusa, moglo je da izvuce i u neku od konfiguracija
		this.statusRefreshNanoseconds = 100*1000000; // na 200 milisekundi. 0.1 s 
		
		onStartingUploadEvent = new Event<>(this);
		onUploadingEvent = new Event<>(this);
		onFinishedUploadEvent = new Event<>(this);
	}
	
	/**
	 * Konstruktor
	 * @param session otvorena sesija
	 * @param statusRefreshMilliSec okvirno vreme za slanje statusne poruke
	 */
	public FTPUploader(FTPSession session,int statusRefreshMilliSec) {
		this(session);
		this.statusRefreshNanoseconds = statusRefreshMilliSec;
	}
	
	
	/**
	 * 
	 * @param filename
	 * @return
	 * @throws FTPException
	 */
	private boolean prepareServerForUpload(String filename) throws FTPException {
		
		boolean prepared = false;
		try {
			String response;
	
			// salje poruku da se radi o binarnom tipu podataka
			session.getOut().print(FTPCommons.BINNARY_DATATYPE+FTPCommons.CMD_ENDING);
			session.getOut().flush();
			
			// ceka potvrdan odgovor
			response = session.getIn().readLine();
			session.getMessageHandler().handleMessage(response);
			
			// salje poruku da se radi o STREAM MODU slanja podataka
			session.getOut().print(FTPCommons.STREAM_MODE+FTPCommons.CMD_ENDING);
			session.getOut().flush();
			
			// ceka potvrdan odgovor
			response = session.getIn().readLine();
			session.getMessageHandler().handleMessage(response);
			
			
			// salje da se radi o passivnom modu
			session.getOut().print(FTPCommons.PASSIVE_MODE+FTPCommons.CMD_ENDING);
			session.getOut().flush();
				
			// ceka potvrdan odgovor, sa adresom hosta i porta
			response = session.getIn().readLine();
			session.getMessageHandler().handleMessage(response);
			
			// izvlaci podatke iz poruke *** ( xxx,xxx,xxx,xxx,xxx,xxx ) ** 
			String addressAndPort = response.substring(response.indexOf('(')+1, response.indexOf(')'));
			String[] responseValues = addressAndPort.split(",");
			
			// kreira host 
			host = responseValues[0]+"."+responseValues[1]+"."+responseValues[2]+"."+responseValues[3];
			// racuna port 256 * xxx,xxx,xxx,xxx,XXX,xxx + xxx,xxx,xxx,xxx,xxx,XXX
			port = Integer.parseInt(responseValues[4]) * 256 + Integer.parseInt(responseValues[5]);
			
			// salje STOR komandu sa imenom fajla
			session.getOut().print(FTPCommons.UPLOAD+" "+filename+FTPCommons.CMD_ENDING);
			session.getOut().flush();
			
			// prepare other parameters!
			prepared = true;
		} catch (IOException e) {
			throw new FTPException(e.getMessage());
		}
		return prepared;
	}
	
	
	
	@Override
	public boolean uploadFile(File fileForUpload) throws FTPException{
		boolean success = false;
		if (!fileForUpload.exists()) {
			// opet proverava existenciju fajlova
			throw new FTPException("File does not exists!");
		}
		else if (fileForUpload.isDirectory()) {
			// proverava da li je direktorijum
			throw new FTPException("FTP cannot transfer directory!");
		}
		
		if (!session.isStarted()) {
			// proverava da li je sesija startovana
			throw new IllegalArgumentException("Session must be started!");
		}
		
		String response;
		
		long sum = 0; // suma svih poslatih bajtova
		int read = 0; // broj batova koji se poslao u jednoj iteraciji u while-u, max velicina bafera
		
		long startingTime = 0; // vreme kad je startovan upload u nanosekundama
		
		long chkPointTime = 0; // vreme poslednjeg fire-ovanja event-a pri upload-u
		long transferedChkPoint = 0; // zbir bajtova koji su poslati izmedju fire-ovanja eventa ( u refresh time-u )
		long deltaTime = 0; // razlika izmedju chkpointTime-a i vremena trenutne iteracije u nanosekundama
		
		long currentTime = 0; // trenutno vreme u iteraciji
		
		long finishTime = 0;
		
		// poziva metodu za pripremanje servera za upload
		if (!prepareServerForUpload(fileForUpload.getName())) {
			return success;
		}
		
		try (
				Socket fileSendingSocket = new Socket(host, port);
				OutputStream dataOut = fileSendingSocket.getOutputStream();
				FileInputStream fileStream = new FileInputStream(fileForUpload)
		) {
			
			// ceka odgovor servera da moze da salje fajl, tj da se otvoren data port
			response = session.getIn().readLine();
			session.getMessageHandler().handleMessage(response);
			
			byte[] buffer = new byte[BUFFER_SIZE];
			
			
			
			startingTime = System.nanoTime();
//			startingTime = System.currentTimeMillis();
			
			chkPointTime = startingTime;
			
			onStartingUploadEvent.fireEvent(new StartingStatus(fileForUpload, startingTime));
			
			
			while ((read = fileStream.read(buffer)) > -1) {
				sum += read;
				
				// trenutno vreme iteracije
				currentTime = System.nanoTime();
				
				// vreme proteklo od od poslednjeg slanja fire-ovanja eventa do trenutne iteracije
				deltaTime = currentTime - chkPointTime;

				// zbir bajtova koji su poslati izmedju fire-ovanja eventa ( u refresh time-u )
				transferedChkPoint += read;
				
				// kad delta premasi refresh vreme onda se kreira poruka, i fire-uje se event koji obavestava
				// svoje listener-e
				if (deltaTime > statusRefreshNanoseconds) {
					
					onUploadingEvent.fireEvent(
							new UploadStatus(
									fileForUpload,
									transferedChkPoint,
									deltaTime,
									sum,
									currentTime-startingTime)
							);
					// chkpoint se postavlja na trenutno vreme
					chkPointTime = currentTime;
					// poslati podaci na 0
					transferedChkPoint = 0;
				}
				// slanje buffer-a
				dataOut.write(buffer, 0, read);
				dataOut.flush();
				
			}
			
//			finishTime = System.currentTimeMillis();
			
			finishTime = System.nanoTime();
			
//			System.out.println(finishTime - startingTime);
			
			success = true;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			LogManager.addLog(e, e.getMessage());
			throw new FTPServerException(e.getMessage());
		}
		finally {
			try {
				// ceka se odgovor, da se uspesno poslala poruka
				response = session.getIn().readLine();
				session.getMessageHandler().handleMessage(response);
				// ako jeste salje se zavrsna poruka bez greske
				onFinishedUploadEvent.fireEvent(new FinishedStatus(
						fileForUpload,
						finishTime - startingTime,
						finishTime
						)
					);
			} catch (IOException e) { // ako nije
				System.err.println(e.getMessage());
				LogManager.addLog(e, e.getMessage());
				success = false;
				// desila se greska i baca se exception,
				// poruka za zavrsetak sa greskom se kreira i salje onamo gde se hvata izuzetak
				throw new FTPServerException(e.getMessage());
			}

		}
		return success;
	}
	
}
