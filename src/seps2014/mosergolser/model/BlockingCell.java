package seps2014.mosergolser.model;

import javafx.scene.paint.Color;

/**
 * @author Michael Ferdinand Moser (MatrNr 1123077), Frederic Golser (1230216)
 * PS-Software Engineering (Gruppe Naderlinger, WS 2014/15): Assignment 9a, 11
 * ---------------------------------------------------------------------------
 * The class specifying the behaviour of a blocking cell.
 */
public class BlockingCell extends Cell {
	
	/**
	 * Constructor specifying the x- and y- coordinates of the blocking cell
	 * in the matrix. Any blocking cell contains a lifeCounter variable
	 * that is initiated to randomly selected value between 5 and 15. It
	 * is equivalent to the number of rounds a blocking cell gets to live in
	 * the matrix before it is replaced by an empty cell.
	 */
	public BlockingCell(int x, int y){
		prevX = x;
		prevY = y;
		this.x = x;
		this.y = y;
		lifeCounter = 5 + (int)(Math.random()*11);
		displayIcon = "X";
	}
	
	/**
	 * The instant the lifeCounter is lower than zero a blocking cell
	 * will be replaced by an empty (normal) cell passed as a parameter.
	 */
	public void degenerate (Cell newCell){
		if (--matrix[prevX][prevY].lifeCounter == -1) { 
			matrix[prevX][prevY] = newCell;
			matrix[prevX][prevY].matrix = matrix;
		}
	}
	
	/**
	 * A blocking cell doesn't ever merge with any other cell.
	 */
	@Override
	public void merge (String direction) {
		performShift(getNeighbour(direction), direction);
	}
	
	private void performShift(Cell toBeMerged, String direction){
		// NOOP
	}
	
	/**
	 * Neither does a blocking cell ever allow for further movement.
	 */
	@Override
	public boolean enablesMovement(){
		return false;
	}
	
	@Override
	public boolean inspect (Cell cell) {
		return false;
	}
	
	@Override
	public String getDisplayIcon(){
		return displayIcon;
	}
	
	@Override
	public Color getBackgroundColor() {
		return Color.web("#C11B17");
	}
}
