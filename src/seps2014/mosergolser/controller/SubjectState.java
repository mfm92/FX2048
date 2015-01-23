package seps2014.mosergolser.controller;

/**
 * @author Michael Ferdinand Moser (MatrNr 1123077), Frederic Golser (1230216)
 * PS-Software Engineering (Gruppe Naderlinger, WS 2014/15) -- Assignment 9a, 11
 * ---------------------------------------------------------------------------
 * A class describing the state of a subject (Observer Pattern).
 */
import seps2014.mosergolser.model.Cell;

public class SubjectState {
	
	private Cell[][] matrix;
	private int score;
	private int moves;
	private boolean hasWon;
	private boolean hasLost;
	
	/**
	 * The state consists of the board with its cells + the total score, shift counters
	 * added together and whether or not the player has lost/won the game.
	 */
	public SubjectState (Cell[][] matrix, int score, int moves, boolean hasWon, boolean hasLost) {
		this.setMatrix(matrix);
		this.setScore(score);
		this.setMoves(moves);
		this.setWon(hasWon);
		this.setLost(hasLost);
	}

	public Cell[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(Cell[][] matrix) {
		this.matrix = matrix;
	}

	public boolean hasLost() {
		return hasLost;
	}

	public void setLost(boolean hasLost) {
		this.hasLost = hasLost;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getMoves() {
		return moves;
	}

	public void setMoves(int moves) {
		this.moves = moves;
	}

	public boolean hasWon() {
		return hasWon;
	}

	public void setWon(boolean hasWon) {
		this.hasWon = hasWon;
	}

}
