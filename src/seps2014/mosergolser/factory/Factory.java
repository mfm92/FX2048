package seps2014.mosergolser.factory;

/**
 * @author Michael Ferdinand Moser (MatrNr 1123077), Frederic Golser (1230216)
 * PS-Software Engineering (Gruppe Naderlinger, WS 2014/15) -- Assignment 9a, 11
 * ---------------------------------------------------------------------------
 * The abstract factory class from which randomized and deterministic factory
 * inherit.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import seps2014.mosergolser.model.BonusCell;
import seps2014.mosergolser.model.Cell;
import seps2014.mosergolser.model.BlockingCell;
import seps2014.mosergolser.model.NormalCell;

public abstract class Factory {
	
	public Properties controllerProperties;
	public Properties viewProperties;
	public Properties modelProperties;
	
	public abstract Cell[][] makeMatrix(int rows, int columns, int evils);
	public abstract int getNewPosition(Cell[][] matrix);
	
	/**
	 * Constructor setting the properties file containing various configurable
	 * data.
	 */
	public Factory() throws FileNotFoundException, IOException{
		setProperties();
	}
	
	/**
	 * Loading the properties file.
	 */
	private void setProperties() throws FileNotFoundException, IOException {
		controllerProperties = new Properties();
		modelProperties = new Properties();
		viewProperties = new Properties();
		
		String controllerPropLocation = System.getProperty("user.dir") + "\\rsrc\\seps2014\\"
				+ "mosergolser\\controller\\controller.properties";
		String modelPropLocation = System.getProperty("user.dir") + "\\rsrc\\seps2014\\"
				+ "mosergolser\\model\\model.properties";
		String viewPropLocation = System.getProperty("user.dir") + "\\rsrc\\seps2014\\"
				+ "mosergolser\\view\\view.properties";
		
		controllerProperties.load(new FileInputStream(new File(controllerPropLocation)));
		modelProperties.load(new FileInputStream(new File(modelPropLocation)));
		viewProperties.load(new FileInputStream(new File(viewPropLocation)));
	}
	
	/**
	 * Introduces various types of new cells to the matrix.
	 */
	public void addNewCells(Cell[][] matrix) {
		final double GENERATE2_PROBABILITY = Double.parseDouble(
				controllerProperties.getProperty("GENERATE2_PROBABILITY"));
		final double BONUS_PROBABILITY = Double.parseDouble(
				controllerProperties.getProperty("BONUSCELL_PROBABILITY"));
		final double EVIL_PROBABILITY = Double.parseDouble(
				controllerProperties.getProperty("EVILCELL_PROBABILITY"));
		final int LOWER_BOUND = (int) (Double.parseDouble(
				controllerProperties.getProperty("LOWER_BONUS_VALUE")));
		final int UPPER_BOUND = (int) (Double.parseDouble(
				controllerProperties.getProperty("UPPER_BONUS_VALUE")));
		
		placeNewNormalCell(matrix, GENERATE2_PROBABILITY);
		placeNewBonusCell(matrix, BONUS_PROBABILITY, LOWER_BOUND, UPPER_BOUND);
		placeNewBlockingCell(matrix, EVIL_PROBABILITY);
	}
	
	/**
	 * Inserts a new normal cell. The probability such that the value of the new cell is 2
	 * is GENERATE2_PROBABILIY. Otherwise, it will be 4.
	 */
	void placeNewNormalCell(Cell[][] matrix, double GENERATE2_PROBABILITY){
		int newNormalPos = getNewPosition(matrix);
		
		if (newNormalPos >= matrix.length * matrix[0].length) return;
		
		int newX = newNormalPos/matrix[0].length;
		int newY = newNormalPos%matrix[0].length;
		NormalCell newNormalCell = new NormalCell
				(GENERATE2_PROBABILITY < Math.random() ? 4 : 2, newX, newY);
		matrix[newX][newY] = newNormalCell;
		matrix[newX][newY].setMatrix(matrix);
	}

	/**
	 * Inserts a new bonus cell with a given probability BONUS_PROBABILITY. LOWER_BOUND
	 * specifies the minimum value of that new bonus cell, UPPER_BOUND its maximum
	 * possible value.
	 */
	void placeNewBonusCell(Cell[][] matrix, final double BONUS_PROBABILITY, 
			final int LOWER_BOUND, final int UPPER_BOUND){
		if (Math.random() > 1 - BONUS_PROBABILITY){
			int newBonusPos = getNewPosition(matrix);
			
			if (newBonusPos >= matrix.length * matrix[0].length) return;
			
			int newX = newBonusPos/matrix[0].length;
			int newY = newBonusPos%matrix[0].length;
			int bonusValue = (int)(Math.random()*(UPPER_BOUND-LOWER_BOUND) + LOWER_BOUND);
			BonusCell newBonusCell = new BonusCell(bonusValue, newX, newY);
			matrix[newX][newY] = newBonusCell;
			matrix[newX][newY].setMatrix(matrix);
		}
	}
	
	/**
	 * Inserts a new blocking cell with a given probability EVIL_PROBABILITY.
	 */
	void placeNewBlockingCell(Cell[][] matrix, final double EVIL_PROBABILITY){
		if (Math.random() > 1 - EVIL_PROBABILITY){
			int newEvilPos = getNewPosition(matrix);
			
			if (newEvilPos >= matrix.length * matrix[0].length) return;
			
			int newX = newEvilPos/matrix[0].length;
			int newY = newEvilPos%matrix[0].length;
			BlockingCell newEvilCell = new BlockingCell(newX, newY);
			matrix[newX][newY] = newEvilCell;
			matrix[newX][newY].setMatrix(matrix);
		}
	}
	
	/**
	 * Introduces an empty cell (i.e., a normal cell with value 0) to a 
	 * predetermined position (x- and y-coordinates) to the matrix.
	 */
	public NormalCell generateEmptyCell(Cell[][] matrix, int x, int y){
		return new NormalCell(0, x, y);
	}
	
	/**
	 * @return: The number of empty cells in the matrix of the board.
	 */
	int determineNrOfEmptyCells(Cell[][] matrix){
		int nrOfEmptyCells = 0;
		for (int i = 0; i < matrix.length; i++){
			for (int j = 0; j < matrix[0].length; j++){
				if (matrix[i][j].isEmpty()) nrOfEmptyCells++;
			}
		}
		return nrOfEmptyCells;
	}
	
	/**
	 * Fills the matrix with empty cells.
	 */
	void initializeWithZero(Cell[][] matrix){
		for (int row = 0; row < matrix.length; row++){
			for (int column = 0; column < matrix[0].length; column++){
				matrix[row][column] = generateEmptyCell(matrix, row, column);
			}
		}
	}
}