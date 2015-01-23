package seps2014.mosergolser.factory;

/**
 * @author Michael Ferdinand Moser (MatrNr 1123077), Frederic Golser (1230216)
 * PS-Software Engineering (Gruppe Naderlinger, WS 2014/15) -- Assignment 9a, 11
 * ---------------------------------------------------------------------------
 * The deterministic factory which creates the same configurable matrix at start
 * and inserts cells at predetermined positions.
 */
import java.io.FileNotFoundException;
import java.io.IOException;

import seps2014.mosergolser.model.BonusCell;
import seps2014.mosergolser.model.Cell;
import seps2014.mosergolser.model.NormalCell;

public class DeterministicFactory extends Factory {
	
	/**
	 * Empty constructor.
	 */
	public DeterministicFactory() throws FileNotFoundException, IOException {
		super();
	}

	private int[] newPos = {1, 6, 4, 8, 2, 6, 3, 10, 3, 4, 5, 
			4, 8, 12, 3, 5, 1, 5, 8, 4, 9, 12, 3};
	private int newPosPointer = 0;
	
	/**
	 * Creates a matrix with cells in predetermined positions at start.
	 */
	@Override
	public Cell[][] makeMatrix(int rows, int height, int evils) {
		int defaultLength = Integer.parseInt(controllerProperties.getProperty("MATRIX_DEFAULT_LENGTH"));
		Cell[][] matrix = new Cell[defaultLength][defaultLength];
		initializeWithZero(matrix);
		
		int magicNumber = 42;
		
		matrix[0][0] = new BonusCell(magicNumber, 0, 0);
		matrix[0][1] = new NormalCell(0, 0, 1);
		matrix[0][2] = new NormalCell(4, 0, 2);
		matrix[0][3] = new NormalCell(2, 0, 3);
		return matrix;
	}
	
	/**
	 * Returns an integer between 0 and (board.width * board.height)-1 inclusive
	 * describing the position of a new cell to be inserted.
	 */
	@Override
	public int getNewPosition(Cell[][] matrix) {
		return newPos[newPosPointer++];
	}
}