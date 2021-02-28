package com.ftpclient.om;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Podaci iz konfiguracije aplikacije
 * 
 * @author Djordje Velickovic
 *
 */
public class AppConfiguration {
	/**
	 * korisnicko ime za ftp server
	 */
	private String username;
	/**
	 * Lozinka za ftp server
	 */
	private String password;
	/**
	 * Host name servera
	 */
	private String server;
	/**
	 * Fajlovi koji trebaju da se posalju
	 */
	private List<File> filesForTransfer;

	
	/**
	 * Getter za lozinku
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Setter za lozinku
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Getter za hostname
	 * @return
	 */
	public String getServer() {
		return server;
	}

	/**
	 * Setter za hostname
	 * @param server
	 */
	public void setServer(String server) {
		this.server = server;
	}

	/**
	 * Getter za fajlove za slanje
	 * @return
	 */
	public List<File> getFilesForTransfer() {
		return filesForTransfer;
	}

	/**
	 * Setter za fajlove za slanje
	 * @param filesForTransfer
	 */
	public void setFilesForTransfer(List<File> filesForTransfer) {
		this.filesForTransfer = filesForTransfer;
	}

	/**
	 * Getter za korisnicko ime
	 * @return
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Setter za korisnicko ime
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Konstruktor
	 */
	public AppConfiguration() {
		// predefinisane vrednosti
		username = "user";
		password = "pass";
		server = "localhost";
		filesForTransfer = new ArrayList<>();
	}

	@Override
	public String toString() {
		return "AppConfiguration [username=" + username + ", password=" + password + ", server=" + server
				+ ", filesForTransfer=" + filesForTransfer + "]";
	}
}
