package com.ftpclient.console;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import com.ftpclient.bll.messaging.MessageQueues;
import com.ftpclient.ftp.upload.messages.FinishedStatus;
import com.ftpclient.ftp.upload.messages.StartingStatus;
import com.ftpclient.ftp.upload.messages.TransferStatus;
import com.ftpclient.ftp.upload.messages.UploadStatus;
import com.ftpclient.util.log.LogManager;

/**
 * Klasa konzole koja osluskuje poruke prenosa i azurira prikaz
 * 
 * @author Djordje Velickovic
 *
 */
public class Console {
		
	/**
	 * Mapa sa poslednje azuriranom porukom za stampanje i fajlom koji joj odgovara 
	 */
	private HashMap<File, String> lastMessageForPrinting; 
	/**
	 * Mapa sa fajlom i redom poruka za obradu u fazi Upload-a
	 */
	private HashMap<File, Queue<UploadStatus>> fileUploadStatusMap;
	/**
	 * Lista fajlova u redosledu kako su stizale startne poruke
	 */
	private List<File> updatingList;

	/**
	 * Okvirno vreme refresh-a konzole. Refresh je brisanje svega i ponovni ispis poruka na konzolu.
	 * U milisekundama
	 */
	private int refreshTime;
	
	/**
	 * Zbog razlike u brisanju sadrzaja sa konzole, morale su nastati za sad dve implementacije
	 * brisanja, jedna za windows cmd i druga za terminal
	 */
	private ConsoleCleaner consoleRefresher;
	
	/**
	 * Zavrsne poruke
	 */
	private Collection<FinishedStatus> finishedMessages;
	
	public void setRefreshTime(int refreshTime) {
		this.refreshTime = refreshTime;
	}
	
	/**
	 * Ocekivan broj fajlova za slanje
	 */
	private int expectedFiles = 0;
	
	/**
	 * Konstruktor
	 */
	public Console(List<File> uploadingFiles) {
		super();
		this.expectedFiles = uploadingFiles.size();
		this.updatingList = uploadingFiles;
		
		fileUploadStatusMap = new HashMap<>();
		lastMessageForPrinting = new HashMap<>();
//		updatingList = new ArrayList<>();
		finishedMessages = new ArrayList<>();
		
		
		// ististnuti u konfiguraciju
		refreshTime = 300; // u milisekundama
		
		if (System.getProperty("os.name").contains("Windows")){
			consoleRefresher = new WindowsCmdCleaner();
        }
		else {
			consoleRefresher = new TerminalCleaner();
		}
	}
	
	
	/**
	 * Metoda koja pokrece osluskivanje messageQueue-a za upload
	 * @param messageQueues
	 */
	public void startListening(MessageQueues messageQueues) {
		BlockingQueue<TransferStatus> bq = messageQueues.geUploadTransferMessages();

//		int files = appConfiguration.getFilesForTransfer().size();
		
		// vreme checkPointa za refresh
		long chkpointTime = System.currentTimeMillis();
		// trenutno vreme
		long currentTime;
		// delta vremena chkpointa i trenutnog
		long delta = 0;
		
		while (true) {
			
			TransferStatus ts;
			try {
				// blokirajuca metoda koja ceka ukoliko je queue prazan dok ne stigne bar neka poruka
				ts = bq.take();
				
				// hendluje poruku u zavisnosti od tipa poruke
				handleMessage(ts);
				
				
				// ceka dok za sve ocekivane fajlove nije primio poruku za kraj
				if (expectedFiles == 0) {
					// poslednje azuriranje u slucaju da neka od poruka nije obradjena i ispisana pre break-a
					clearConsole();
					refresh();
					
					System.out.println(creatingSummary());
					break;
				}
				
				// racunanje delte za refresh
				currentTime = System.currentTimeMillis();
				delta = currentTime -chkpointTime;
				
				
				if (delta >= refreshTime) {
					clearConsole();
					
					for (File f : updatingList) {
						Queue<UploadStatus> messageQueue = fileUploadStatusMap.get(f);
						// ukoliko je messageQueue null, fajl se ne uploud-uje
						// ukoliko je prazan onda nema potrebe azurirati poruku, stampa se poslednje dodata
						if (messageQueue != null && !messageQueue.isEmpty()) {
							lastMessageForPrinting.put(f, formatUploadMessage(messageQueue, f));
						}
					}
					
					refresh();
					chkpointTime = currentTime;
				}
				
			} catch (Exception e) {
				LogManager.addLog(e, e.getMessage());
			}
		}


		
	}
	
	/**
	 * Ponovo ispisuje poslednje azuriranu poruku na konzolu fajlova koji su aktuelni u upload-u
	 */
	private void refresh() {
		for (File f : updatingList) {
			String ts = lastMessageForPrinting.get(f);
			if (ts != null) {
				System.out.println(ts);
			}
		}
		System.out.flush();
	}
	
	/**
	 * Cisti konzolu
	 */
	private void clearConsole() {
		consoleRefresher.clear();
	}
	
	/**
	 * Hendluje poruku u zavisnosti od tipa poruke
	 * @param ts
	 */
	private void handleMessage(TransferStatus ts) {
		// najcesca je UploadStatus pa je ona i prva
		if (ts instanceof UploadStatus) {
			// poruka se dodaje u Queue za odredjeni fajl
			fileUploadStatusMap.get(ts.getTransferingFile()).add((UploadStatus)ts);
		}
		else if (ts instanceof StartingStatus) {
			// kreira se novi red poruka za fajl
			fileUploadStatusMap.put(ts.getTransferingFile(), new LinkedList<>());
			// dodaje se fajl u listu fajlova za azuriranje
//			updatingList.add(ts.getTransferingFile());
		}
		else if (ts instanceof FinishedStatus) {
			// ako je krajnja poruka dodaje formatiranu kranju poruku u mapu sa porukama
			lastMessageForPrinting.put(ts.getTransferingFile(), formatFinishedMessage((FinishedStatus)ts,ts.getTransferingFile()));
			// brise red poruka 
			fileUploadStatusMap.remove(ts.getTransferingFile());
//			updatingList.remove(ts.getTransferingFile());
			expectedFiles--;
			
			finishedMessages.add((FinishedStatus)ts);
		}
		else {
			// ovde nikad ne bi trebalo da dodje.
			LogManager.addLog(new Exception("Unhandled Trasfer Message"), "Internal error!");
		}
	}
	
	/**
	 * Formatira i obavlja kalkulacije nad redom poruka koje su stigle u fazi upload-a fajla
	 * 
	 * @param messagesQueue red poruka koje su stigle za odredjeni fajl
	 * @param fileForTransfer fajl koji se salje
	 * @return
	 */
	private String formatUploadMessage(Queue<UploadStatus> messagesQueue, File fileForTransfer) {
		// srednja brzina
		double avgSpeed = 0;
		// zbirno vreme proteklo za transfer svakog chunk-a iz poruka
		long milliseconds = 0;
		// zbirni broj upload-ovanih bajtova
		long transferedBytes = 0;
		// vreme statusa transfera fajla poslednje poruke
		long lastTime = 0;
		long currentTransfered = 0;
		
		while (!messagesQueue.isEmpty()) {
			UploadStatus us = messagesQueue.poll();
			milliseconds += us.getTimeForTransferedChunk();
			transferedBytes += us.getTransferedChunkInBytes();
			
			lastTime = us.getCurrentTime();
			currentTransfered = us.getTransferedBytesUntilNow();
		}
		// racuna se prosecna brzina za vreme svih poruka u redu
		avgSpeed = ((double)bytesToKilobytes(transferedBytes))/(nanoToSeconds((double)milliseconds));
		
		// procenat transfera
		int percent = (int) (currentTransfered * 100 / fileForTransfer.length());
		
		
		// formiranje poruke
		StringBuilder sb = new StringBuilder();
		sb.append("File: "+fileForTransfer.getAbsolutePath()+"\n")
		  	.append("Speed: "+String.format("%.2f", avgSpeed)+" KB/s\n")
		  	.append("Time: "+timeFormat(lastTime)+"\n")
		  	.append(creatingProgressBar(percent))
		  	.append("\n");

		return sb.toString();
	}
	
	/**
	 * Formiranje kranje poruke. Regularne i ako je doslo do greske
	 * 
	 * @param finishedMessage
	 * @param fileForTransfer
	 * @return vraca formiranu poruku za ispis
	 */
	private String formatFinishedMessage(FinishedStatus finishedMessage, File fileForTransfer) {
		StringBuilder sb = new StringBuilder();
		
		if (finishedMessage.getErrorMessage() == null) {
			
			double avgSpeed = 0;
			long nanseconds = finishedMessage.getTimeForFileTransfer();
			long transferedBytes = fileForTransfer.length();
			
			avgSpeed = ((double)bytesToKilobytes(transferedBytes))/(nanoToSeconds((double)nanseconds));
			
			int percent = 100;
			
			sb.append("File "+fileForTransfer.getAbsolutePath()+" is finished.\n")
			  	.append("Avgerage: "+String.format("%.2f", avgSpeed)+" KB/s\n")
			  	.append("Time: "+timeFormat(nanseconds)+"\n")
			  	.append(creatingProgressBar(percent))
			  	.append("\n");
		}
		else { // ukoliko je doslo do greske ispisati drugaciju poruku
			sb.append("Transfer of \""+fileForTransfer.getAbsolutePath()+"\" failed.\n"+finishedMessage.getErrorMessage()+"\n");
		}
		return sb.toString();
	}
	
	/**
	 * Kreira progress bar 
	 * @param percent
	 * @return
	 */
	private String creatingProgressBar(int percent) {
		StringBuilder sb = new StringBuilder();
		
	  	// dodaje praznine na pocetak ispred procenta
		sb.append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
	  	.append(String.format(" %d%% [", percent))
	  	.append(String.join("", Collections.nCopies(percent, "#")))
	  	.append(String.join("", Collections.nCopies(100 - percent, ".")))
	  	.append(']');
		return sb.toString();
	}
	
	/**
	 * Kreira summary prenosa
	 * ispisuje koliko se fajlova uspesno prenelo jer moze da se desi neka greska u toku prenosa
	 * ispisuje koja je prosecna brzina
	 * 
	 * ispisuje vreme koliko je trajao prenos svih fajlova
	 * kao vreme prenosa je uzeta razlika pocetka fajla koji je prvi zapoceo prenos
	 * i vremena kada je zavrsio prenos poslednji fajl
	 * 
	 * zato se moze desiti da pise da je vreme prenosa 3 mala fajla po 0s
	 * a u summary-ju 3s, jer je mozda izgubljeno vreme zbog konektovanja na server,
	 * a i prenos nije bas tacno 0s, vec mozda 0.3-0.4
	 * 
	 * @return
	 */
	private String creatingSummary() {
		// zbir svih prenesenih bajtova
		long transferedBytes = 0;
		// zbir vremena prenosa svih fajlova
		long sumOfTimes = 0;
		
		//najmanje pocetno vreme
		long minStartTime = Long.MAX_VALUE;
		//maksimalno pocetno vreme
		long maxFinishTime = 0;
		
		long successfulyTransfered = 0;
		
		for (FinishedStatus fs : finishedMessages) {
			if (fs.getErrorMessage() != null) {
				continue;
			}
			
			successfulyTransfered++;
			
			transferedBytes += fs.getTransferingFile().length();
			sumOfTimes += fs.getTimeForFileTransfer();
			
			if (minStartTime > fs.getTimeOfFinishing()-fs.getTimeForFileTransfer()) {
				minStartTime = fs.getTimeOfFinishing()-fs.getTimeForFileTransfer();
			}
			
			if (maxFinishTime < fs.getTimeOfFinishing()) {
				maxFinishTime = fs.getTimeOfFinishing();
			}
		}
		
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("Successfuly transfered "+successfulyTransfered+" files.\n");
		
		if (successfulyTransfered > 0) {
			double avgSpeed =  ((double)bytesToKilobytes(transferedBytes))/(nanoToSeconds((double)sumOfTimes));
			sBuilder.append("Average speed: "+String.format("%.2f", avgSpeed)+" KB/s\n");
			sBuilder.append("Time for transfer: "+ timeFormat(maxFinishTime-minStartTime)+"\n");
		}
		
		return sBuilder.toString();
	}
	
	
	
	/**
	 * Pomocna metoda koja formatira nanosekunde u sekunde sa dve decimale
	 * 
	 * @param nanoseconds
	 * @return
	 */
	private String timeFormat(long nanoseconds) {
		
		return Long.toString(nanoToSeconds(nanoseconds))+" s";
		
//		long converted = nanoseconds/1000000000;
		
//		int seconds = (int) (converted % 60);
//		converted /= 60;
//		int minutes = (int) (converted % 60);
//		converted /= 60;
//		int hours = (int) (converted);
//		return String.format("%02d:%02d:%02d", hours,minutes,seconds);
		
//		double seconds = ((double)nanoseconds)/1000000000;
//		
//		return String.format("%.2f", seconds);
	}
	
	
	long nanoToMili(long nano) {
		return nano/1000000;
	}
	
	long nanoToSeconds(long nano) {
		return nano/1000000000;
	}
	
	long miliToSeconds(long mili) {
		return mili/1000000;
	}
	
	double nanoToMili(double nano) {
		return nano/1000000;
	}
	
	double nanoToSeconds(double nano) {
		return nano/1000000000;
	}
	
	double miliToSeconds(double mili) {
		return mili/1000000;
	}
	
	long bytesToKilobytes(long bytes) {
		return bytes/1024;
	}
	
}
