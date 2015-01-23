package seps2014.mosergolser.model;

import javafx.scene.paint.Color;

/**
 * @author Michael Ferdinand Moser (MatrNr 1123077), Frederic Golser (1230216)
 * PS-Software Engineering (Gruppe Naderlinger, WS 2014/15): Assignment 9a, 11
 * ---------------------------------------------------------------------------
 * The class specifying the behaviour of a bonus cell.
 */

public class BonusCell extends Cell {
	
	/**
	 * Constructor setting the initial value of a bonus cell.
	 * @param value
	 * @param totalMoves
	 */
	public BonusCell(int value, int x, int y){
		this.value = value;
		this.prevX = x;
		this.prevY = y;
		this.x = x;
		this.y = y;
		displayIcon = "@";
	}
	
	/**
	 * Any bonus cell declares itself empty in the merging process
	 * such that normal cells can shift over it and pick up its value
	 * such that it can be added to the total score.
	 */
	@Override
	public void merge(String direction) {
		isEmpty = true;
		Cell neighbour = getNeighbour(direction);
		if (neighbour != null) neighbour.merge(direction);
	}
	
	/**
	 * Replacing a bonus cell by an empty cell passed as a parameter.
	 */
	public void degenerate(Cell newCell) {
		matrix[x][y] = newCell;
		matrix[x][y].setMatrix(matrix);
	}
	
	/**
	 * Makes the bonus cell check its surroundings for possibilies for movement.
	 * If a bonus cell equals any non-empty normal cells, the user can continue.
	 */
	@Override
	public boolean enablesMovement(){
		return false;
	}
	
	@Override
	public boolean inspect (Cell cell){
		return true;
	}
	
	@Override
	public String getDisplayIcon(){
		return displayIcon;
	}
	
	@Override
	public Color getBackgroundColor() {
		return Color.web("#57E964");
	}
}