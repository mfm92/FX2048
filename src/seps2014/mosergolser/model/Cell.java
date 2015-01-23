package seps2014.mosergolser.model;

import javafx.scene.paint.Color;

/**
 * @author Michael Ferdinand Moser (MatrNr 1123077), Frederic Golser (1230216)
 * PS-Software Engineering (Gruppe Naderlinger, WS 2014/15) -- Assignment 9a, 11
 * ---------------------------------------------------------------------------
 * The abstract base class describing a cell of the board.
 */
public abstract class Cell {
	
	/**
	 * X, Y- coordinates at the start of each round.
	 */
	protected int prevX;
	protected int prevY;
	
	/**
	 * X, Y- coordinates at the end of each round.
	 */
	protected int x;
	protected int y;
	
	protected int value;
	protected int tmpValue; //the increase in value per round that is relevant for the total score
	
	protected int lifeCounter;
	protected int shiftCounter;
	
	protected Cell[][] matrix;
	protected String displayIcon;
	
	protected boolean hasClashed; //true iff a cell is the product of a merging process
	protected boolean isEmpty; 

	protected boolean enabledShift; //true iff a cell has changed its position
	
	/**
	 * Describes the behaviour in the merging process.
	 * @param direction
	 */
	public abstract void merge (String direction);

	/**
	 * Returns true iff a specific cell allows the player to continue.
	 * E.g., a normal cell allows a player to continue if it borders
	 * an empty cell (then the player can shift in that direction)
	 */
	public abstract boolean enablesMovement();
	public abstract boolean inspect (Cell neighbour);

	/**
	 * Returns a string that is to be displayed in the view.
	 * @return
	 */
	public abstract String getDisplayIcon();

	/**
	 * Replaces a cell by an empty cell under certain conditions.
	 */
	public abstract void degenerate(Cell newCell);

	/**
	 * Returns a color object used by the view for display.
	 */
	public abstract Color getBackgroundColor();

	/**
	 * Returns the neighbour of each cell in a specified direction.
	 */
	public Cell getNeighbour(String direction){
		switch (direction){
			case "l":
				if (getY()-1 >= 0) return getMatrix()[getX()][getY()-1];
				else return null;
			case "r":
				if (getY()+1 < getMatrix()[0].length) return getMatrix()[getX()][getY()+1]; 
				else return null;
			case "u":
				if (getX()-1 >= 0) return getMatrix()[getX()-1][getY()];
				else return null;
			case "d":
				if (getX()+1 < getMatrix().length) return getMatrix()[getX()+1][getY()];
				else return null;
			default: return null;
		}
	}

	/**
	 * Some resets that have to be carried out after each round.
	 * @param matrix
	 */
	public void resetStatus(Cell[][] matrix){
		setClashed(false);
		setEnabledShift(false);
		setPrevX(x);
		setPrevY(y);
		setTmpValue(0);
		this.setMatrix(matrix);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isEmpty() {
		return isEmpty;
	}
	
	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}
	
	public int getPrevX() {
		return prevX;
	}

	public void setPrevX(int x) {
		this.prevX = x;
	}

	public int getPrevY() {
		return prevY;
	}

	public void setPrevY(int y) {
		this.prevY = y;
	}

	public Cell[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(Cell[][] matrix) {
		this.matrix = matrix;
	}

	public int getX() {
		return x;
	}

	public void setX(int nextX) {
		this.x = nextX;
	}

	public int getY() {
		return y;
	}

	public void setY(int nextY) {
		this.y = nextY;
	}

	public int getTmpValue() {
		return tmpValue;
	}

	public void setTmpValue(int tmpValue) {
		this.tmpValue = tmpValue;
	}

	public boolean hasEnabledShift() {
		return enabledShift;
	}

	public void setEnabledShift(boolean enabledShift) {
		this.enabledShift = enabledShift;
	}

	public int getShiftCounter() {
		return shiftCounter;
	}

	public int setShiftCounter(int shiftCounter) {
		this.shiftCounter = shiftCounter;
		return shiftCounter;
	}

	public boolean hasClashed() {
		return hasClashed;
	}

	public void setClashed(boolean hasClashed) {
		this.hasClashed = hasClashed;
	}
}