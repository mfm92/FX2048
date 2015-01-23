package seps2014.mosergolser.controller;


import javafx.scene.input.KeyEvent;
/**
 * @author Michael Ferdinand Moser (MatrNr 1123077), Frederic Golser (1230216)
 * PS-Software Engineering (Gruppe Naderlinger, WS 2014/15) -- Assignment 9a, 11
 * ---------------------------------------------------------------------------
 * An interface containing the necessary methods for a class acting as a subject
 * in the observer pattern.
 */
import seps2014.mosergolser.view.Observer;

public interface Subject {

	public void registerObserver (Observer observer);
	public void unregisterObserver (Observer observer);
	public void notifyObservers ();
	public SubjectState getState();
	public void setState(KeyEvent event);
}
