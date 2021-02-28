package com.ftpclient.util.events;

/**
 * Default-ni event argument
 * 
 * @author Djordje Velickovic
 *
 */
public class EventArgs {
	/**
	 * Helper objekat, za cuvanje bilo kog podatka
	 */
	private Object helpObject;
	
	/**
	 * Getter za helper objekat
	 * @return
	 */
	public Object getHelpObject() {
		return helpObject;
	}
	
	/**
	 * Konstruktor
	 * @param helpObject
	 */
	public EventArgs(Object helpObject) {
		this.helpObject = helpObject;
	}

}