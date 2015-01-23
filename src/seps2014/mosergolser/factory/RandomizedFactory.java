package seps2014.mosergolser.factory;

/**
 * @author Michael Ferdinand Moser (MatrNr 1123077), Frederic Golser (1230216)
 * PS-Software Engineering (Gruppe Naderlinger, WS 2014/15) -- Assignment 9a, 11
 * ---------------------------------------------------------------------------
 * The randomized factory used by the controller in a "normal" gameplay scenario.
 * It inserts random cell types to random positions in the matrix.
 */
import java.io.FileNotFoundException;
import java.io.IOException;

import seps2014.mosergolser.model.Cell;
import seps2014.mosergolser.model.BlockingCell;
import seps2014.mosergolser.model.NormalCell;

public class RandomizedFactory extends Factory {

	/**
	 * Empty constructor.
	 */
	public RandomizedFactory() throws FileNotFoundException, IOException {
		super();
	}

	/**
	 * Creates a new matrix with a predetermined number of rows (height)
	 * and columns (width) and number of blocking cells (evils) that
	 * the matrix should contain before the first round of the game.
	 */
	@Override
	public Cell[][] makeMatrix(int width, int height, int evils) {
		Cell[][] matrix = new Cell[width][height];
		initializeWithZero(matrix);
		
		for (int i = 0; i < evils; i++){
			int startEvil = getNewPosition(matrix);
			int startEvilX = startEvil/matrix[0].length;
			int startEvilY = startEvil%matrix[0].length;
			BlockingCell newEvil = new BlockingCell(startEvilX, startEvilY);	
			matrix[startEvil/matrix[0].length][startEvil%matrix[0].length] = newEvil;
		}
		
		int startPos = getNewPosition(matrix);
		double gen2prob = Double.parseDouble(controllerProperties.
				getProperty("GENERATE2_PROBABILITY"));
		int newNormalX = startPos/matrix[0].length; 
		int newNormalY = startPos%matrix[0].length;
		NormalCell startNormal = new NormalCell(Math.random() > gen2prob ? 4 : 2, 
				newNormalX, newNormalY);
		matrix[newNormalX][newNormalY] = startNormal;
		return matrix;
	}

	/**
	 * Returns an integer between 0 and (board.width * board.height)-1 inclusive
	 * specifying the position of a cell to be inserted in the matrix. However,
	 * if the matrix is full (board.width * board.height) is returned. Methods
	 * calling this method have to check for that.
	 */
	@Override
	public int getNewPosition(Cell[][] matrix) {
		int nrOfFreeSpots = determineNrOfEmptyCells(matrix);
		
		if (nrOfFreeSpots == 0) return matrix.length * matrix[0].length;
		
		int newPosition = (int)(Math.random()*nrOfFreeSpots);
		int newPos = 0;
		int counter = 0;
		
		for (int row = 0; row < matrix.length; row++){
			for (int column = 0; column < matrix[0].length; column++){
				if (matrix[row][column].isEmpty() && counter++ == newPosition) {
					newPos = row*matrix[0].length + column;
				}
			}
		}
		return newPos;
	}
}
