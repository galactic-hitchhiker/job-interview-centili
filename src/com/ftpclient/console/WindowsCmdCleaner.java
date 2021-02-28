package com.ftpclient.console;

import java.io.IOException;

/**
 * Cisti terminal u windows stemima
 * @author Djordje Velickovic
 *
 */
public class WindowsCmdCleaner implements ConsoleCleaner {

	@Override
	public void clear() {
		try {
			new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
