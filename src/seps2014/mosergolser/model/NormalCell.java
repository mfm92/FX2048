package seps2014.mosergolser.model;

import javafx.scene.paint.Color;

/**
 * @author Michael Ferdinand Moser (MatrNr 1123077), Frederic Golser (1230216)
 * PS-Software Engineering (Gruppe Naderlinger, WS 2014/15): Assignment 9a, 11
 * ---------------------------------------------------------------------------
 * The class specifying the behaviour of a normal cell.
 */
public class NormalCell extends Cell {
	
	protected boolean destroyFlag;
	
	/**
	 * Constructor.
	 * @param value: Setting an initial value.
	 * @param totalMoves: Setting the shift counter.
	 */
	public NormalCell(int value, int x, int y){
		this.value = value;
		isEmpty = (value == 0 ? true : false);
		prevX = x;
		prevY = y;
		this.x = x;
		this.y = y;
	}
	
	public void degenerate(Cell newCell){
		// NOOP
	}
	
	/**
	 * Makes the cell check its surroundings for possibilities to move.
	 * If a normal cell borders an empty cell, bonus cell or another normal
	 * cell with the same value (over zero) than further movements can be made.
	 */
	@Override
	public boolean enablesMovement(){
		return checkMovementCondition(getNeighbour("l")) || 
				checkMovementCondition(getNeighbour("r")) ||
				checkMovementCondition(getNeighbour("u")) || 
				checkMovementCondition(getNeighbour("d"));
  	}
	
	private boolean checkMovementCondition(Cell cell){
		if (cell == null) return false;
		else if (cell.value == value) return true;
		else if (!isEmpty() && cell.isEmpty()) return true;
		else return cell.inspect(this);
	}
	
	@Override
	public boolean inspect (Cell cell) {
		return false;
	}
	
	/**
	 * Specifies the behaviour of a normal cell when it is shifted in a specific
	 * direction.
	 */
	@Override
	public void merge (String direction) {
		performShift(getNeighbour(direction), direction);
	}
	
	private void performShift (Cell toBeMerged, String direction){
		Cell toMerge = toBeMerged;
		
		if (toMerge == null) return;

		if (toMerge.value == value && !isEmpty && !toMerge.hasClashed && !hasClashed){
			value *= 2;
			hasClashed = true;
			shiftCounter += ++(toMerge.shiftCounter);
			enabledShift = true;
			tmpValue += value;
			
			toMerge.value = 0;
			toMerge.shiftCounter = 0;
			toMerge.isEmpty = true;
			
			swapCells (this, toMerge);
			merge(direction);
			
		} else if (toMerge.isEmpty() && !isEmpty()){
			shiftCounter++;
			tmpValue += toMerge.value;
			enabledShift = true;
			
			toMerge.value = 0;
			toMerge.enabledShift = true;
			
			swapCells (this, toMerge);
			merge(direction);
		}
	}
	
	private void swapCells (Cell cell1, Cell cell2) {
		int cell1X = cell1.x;
		int cell1Y = cell1.y;
		int cell2X = cell2.x;
		int cell2Y = cell2.y;
		
		matrix[cell1X][cell1Y] = cell2;
		matrix[cell2X][cell2Y] = cell1;
		
		cell2.x = cell1X;
		cell2.y = cell1Y;
		cell1.x = cell2X;
		cell1.y = cell2Y;
	}
	
	@Override
	public String getDisplayIcon(){
		return (isEmpty ? "" : value + "");
	}
	
	@Override
	public Color getBackgroundColor() {
		switch (value) {
			case 2: return Color.web("#FFFFCC");
			case 4: return Color.web("#FFFF99");
			case 8: return Color.web("#FFCC66");
			case 16: return Color.web("#FF9900");
			case 32: return Color.web("#CC5200");
			case 64: return Color.web("#E55451");
			case 128: return Color.web("#2554C7");
			case 256: return Color.web("#151B8D");
			case 512: return Color.web("#151B54");
			case 1024: return Color.web("#387C44");
			case 2048: return Color.web("#347C17");
			case 4096: return Color.web("#4E9258");
			default: return Color.web("#7C8082");
		}
	}
}