package com.ftpclient.util.events;

/**
 * Interfejs event listener-a
 * 
 * @author Djordje Velickovic
 *
 * @param <E>
 */
public interface EventListener<E> {
	/**
	 * Metoda koja se poziva pri fire-ovanju event-a
	 * 
	 * @param sender
	 * @param args
	 */
	void performAction(Object sender, E args);

}