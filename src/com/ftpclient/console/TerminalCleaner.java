package com.ftpclient.console;

/**
 * 
 * Cisti terminal u Unix/Linux sistemima
 * @author Djordje Velickovic
 *
 */
public class TerminalCleaner implements ConsoleCleaner{

	@Override
	public void clear() {
		// postavlja kursor na pocetak terminala
        System.out.print("\033[H\033[2J");  
        System.out.flush(); 
	}

	
}
