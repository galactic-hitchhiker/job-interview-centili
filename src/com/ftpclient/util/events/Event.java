package com.ftpclient.util.events;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Event koji sadrzi kolekciju event listenera i koji ih obavestava kad se ovaj event fire-uje
 * 
 * @author Djordje velickovic
 *
 * @param <E>
 */
public class Event<E> {
	/**
	 * Objekat koji fire-uje event
	 */
	private Object sender;

	/**
	 * Kolekcija event listenera
	 */
	private Collection<EventListener<E>> eventListeners;

	/**
	 * Prolazi kroz sve event listener-e i izvrsava njihovu metodu
	 * @param e
	 */
	public void fireEvent(E e) {
		for (EventListener<E> eventListener : eventListeners){
			eventListener.performAction(sender, e);
		}
	}

	/**
	 * Konstruktor
	 * @param sender
	 */
	public Event(Object sender) {
		eventListeners = new CopyOnWriteArrayList<EventListener<E>>();
		this.sender = sender;
	}


	/**
	 * Geter za event listenere
	 * @return
	 */
	public java.util.Collection<EventListener<E>> getEventListeners() {
		return eventListeners;
	}

	/**
	 * Metoda koja dodaje event listener
	 * @param newEventListener
	 */
	public void addEventListener(EventListener<E> newEventListener) {
		if (newEventListener == null)
			return;
		if (!this.eventListeners.contains(newEventListener))
			this.eventListeners.add(newEventListener);
	}

	/**
	 * Metoda koja brise event listener
	 * @param oldEventListener
	 */
	public void removeEventListener(EventListener<E> oldEventListener) {
		if (oldEventListener == null)
			return;
		if (this.eventListeners != null)
			if (this.eventListeners.contains(oldEventListener))
				this.eventListeners.remove(oldEventListener);
	}

	/**
	 * Brise sve event listener-e
	 */
	public void removeAllEventListener() {
		if (eventListeners != null)
			eventListeners.clear();
	}

}