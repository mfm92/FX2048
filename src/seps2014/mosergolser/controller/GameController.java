package seps2014.mosergolser.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import seps2014.mosergolser.factory.DeterministicFactory;
import seps2014.mosergolser.factory.Factory;
import seps2014.mosergolser.factory.RandomizedFactory;
import seps2014.mosergolser.model.Cell;
import seps2014.mosergolser.view.Game2048GUI;
import seps2014.mosergolser.view.Observer;

/**
 * @author Michael Ferdinand Moser (MatrNr 1123077), Frederic Golser (1230216)
 * PS-Software Engineering (Gruppe Naderlinger, WS 2014/15) -- Assignment 9a, 11
 * ---------------------------------------------------------------------------
 * The controller class listening for user input and telling the cells in the
 * matrix how to behave as well as updating the view.
 * 
 * Tip:
 * ---
 * - CopyOnWriteArrayList: Causes no inconsistencies if updating a list of observers
 * while one of the observers are unregistered.
 *
 */

public class GameController implements Subject {
	
	Cell[][] matrix;
	ArrayList<Observer> observers = new ArrayList<>();
	Factory factory;
	Properties properties;
	
	int score;
	int moves;
	
	/**
	 * Constructor for the controller setting the factory, properties and matrix.
	 * Furthermore every cell in the matrix receives a link to that matrix.
	 */
	public GameController (int rows, int columns, int evils, 
			Stage primaryStage, boolean beDeterministic)
			throws IOException, FileNotFoundException {
		factory = beDeterministic ? new DeterministicFactory() : new RandomizedFactory();
		matrix = factory.makeMatrix(rows, columns, evils);
		properties = factory.controllerProperties;
		
		for (int i = 0; i < matrix.length; i++){
			for (int j = 0; j < matrix[0].length; j++){
				matrix[i][j].setMatrix(matrix);
			}
		}
		Game2048GUI FX_GUI = new Game2048GUI(primaryStage, this, factory.viewProperties);
		registerObserver(FX_GUI);		
	}
	
	/**
	 * Adding an observer.
	 */
	@Override
	public void registerObserver (Observer observer) {
		observers.add (observer);
		observer.setSubject(this);
	}

	/**
	 * Removing an observer.
	 */
	@Override
	public void unregisterObserver(Observer observer) {
		observers.remove (observer);
	}

	/**
	 * Updating all the registered observers.
	 */
	@Override
	public void notifyObservers() {
		for (Observer observer : observers) {
			observer.update();
		}
	}

	/**
	 * Returns the current state of the matrix including
	 * the total score, moves made and whether or not
	 * the player has won/lost the game.
	 */
	@Override
	public SubjectState getState() {
		return new SubjectState (matrix, score, moves, checkWin(), checkFail());
	}

	/**
	 * Reacting to user specified input.
	 */
	public void setState (KeyEvent event) {		
		if (event.getCode() == KeyCode.UP) executeRound (properties.getProperty("UP_MOVE"));
		else if (event.getCode() == KeyCode.DOWN) executeRound (properties.getProperty("DOWN_MOVE"));
		else if (event.getCode() == KeyCode.LEFT) executeRound (properties.getProperty("LEFT_MOVE"));
		else if (event.getCode() == KeyCode.RIGHT) executeRound (properties.getProperty("RIGHT_MOVE"));
	}

	/**
	 * Executing a single round of the game. The merge method returns
	 * a boolean value that is true iff a movement has been specified
	 * such that at least one cell changes its position.
	 * @param direction: Direction that is specified by the player.
	 */
	private void executeRound (String direction) {
		resetCellStatus();
		
		if (merge (direction)) {
			degenerateCells();
			calculateMoves();
			updateScore();
			checkFail();
			addNewCells();
			notifyObservers();
		}
	}

	/**
	 * @return True iff the user has won.
	 */
	private boolean checkWin() {
		return checkWinReached();
	}

	/**
	 * @return True iff the user is stuck.
	 */
	private boolean checkFail() {
		return !checkMovementPossible();
	}
	
	/**
	 * This method carries out the merge process given a direction specified by the user.
	 * @param direction: Specified by the user.
	 * @return: True iff a movement has been specified such that a new number shall be inserted.
	 */
	private boolean merge(String direction){
		boolean hasMoved = false;
		
		if (direction.equals(properties.getProperty("QUIT_CHAR"))) return false;
		
		if (direction.equals(properties.getProperty("LEFT_MOVE")) || 
				direction.equals(properties.getProperty("UP_MOVE"))) {
			hasMoved = merge(false, direction);
		}
		else hasMoved = merge(true, direction);
		return hasMoved;
	}
	
	private boolean merge(boolean directionFlag, String direction){
		boolean hasMoved = false;
		
		if (directionFlag){
			for (int row = matrix.length-1; row >= 0; row--){
				for (int column = matrix[0].length-1; column >= 0; column--){
					Cell cell = matrix[row][column];
					if (!cell.isEmpty()) cell.merge(direction);
					if (cell.hasEnabledShift()) hasMoved = true;
				}
			}
		} else {
			for (int row = 0; row < matrix.length; row++){
				for (int column = 0; column < matrix[0].length; column++){
					Cell cell = matrix[row][column];
					if (!cell.isEmpty()) cell.merge(direction);
					if (cell.hasEnabledShift()) hasMoved = true;
				}
			}
		}
		return hasMoved;
	}

	/**
	 * Replaces all old bonus cells with an empty cell generated by the factory
	 * as well as reduces the life counter of each blocking cell (+ if that
	 * counter is lower than a threshold the blocking cell is replaced by an
	 * empty cell as well)
	 */
	private void degenerateCells(){
		if (checkFail()) return;
		for (int row = 0; row < matrix.length; row++){
			for (int column = 0; column < matrix[0].length; column++){
				matrix[row][column].degenerate(factory.generateEmptyCell(matrix, row, column));
			}
		}
	}

	/**
	 * Introduces new cells to the matrix: A new normal cell (with value 2 or 4)
	 * if any cell of the matrix had to be moved in the previous merging process,
	 * as well as bonus/blocking cells if a randomly generated number between 0 and 1
	 * exceeds a configurable threshold.
	 */
	private void addNewCells() {
		factory.addNewCells(matrix);
	}

	private boolean checkWinReached(){
		int winThreshold = Integer.parseInt(properties.getProperty("WIN_THRESHOLD"));
		
		for (int row = 0; row < matrix.length; row++) {
			for (int column = 0; column < matrix[0].length; column++) {
				if (matrix[row][column].getValue() >= winThreshold) return true;
			}
		}
		return false;
	}
	
	private boolean checkMovementPossible(){
		for (int row = 0; row < matrix.length; row++){
			for (int column = 0; column < matrix[0].length; column++){
				if (matrix[row][column].enablesMovement()) return true;
			}
		}
		return false;
	}
	
	private void updateScore(){
		for (int row = 0; row < matrix.length; row++){
			for (int column = 0; column < matrix[0].length; column++){
				score += matrix[row][column].getTmpValue();
			}
		}
	}
	
	private void calculateMoves(){
		int sum = 0;
		for (int row = 0; row < matrix.length; row++){
			for (int column = 0; column < matrix[0].length; column++){
				sum += matrix[row][column].getShiftCounter();
			}
		}
		moves = sum;
	}
	
	private void resetCellStatus(){
		for (int row = 0; row < matrix.length; row++){
			for (int column = 0; column < matrix[0].length; column++){
				matrix[row][column].resetStatus(matrix);
			}
		}
	}
}