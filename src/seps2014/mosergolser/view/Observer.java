package seps2014.mosergolser.view;

/**
 * @author Michael Ferdinand Moser (MatrNr 1123077), Frederic Golser (1230216)
 * PS-Software Engineering (Gruppe Naderlinger, WS 2014/15) -- Assignment 6, 7
 * ---------------------------------------------------------------------------
 * An interface containing the necessary methods for a class acting as an observer
 * (Observer Pattern)
 */
import seps2014.mosergolser.controller.Subject;

public interface Observer {
	
	public void setSubject (Subject subject);
	public void update ();

}
