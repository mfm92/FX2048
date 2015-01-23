package seps2014.mosergolser.model;

/**
 * Michael F. Moser (MatrNr 1123077), Frederic Golser (1230216)
 * PS Software Engineering
 * ------------------------------------------------------------------
 * Assignment 12b: Tester class for the model of the 2048 game.
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ModelTester {

	Cell[][] testMatrix = new Cell[][]{
		{new NormalCell(0, 0, 0), new NormalCell(2, 0, 1), new NormalCell(0, 0, 2), new NormalCell(2, 0, 3)},
		{new BonusCell(42, 1, 0), new NormalCell(0, 1, 1), new NormalCell(0, 1, 2), new NormalCell(8, 1, 3)},
		{new NormalCell(0, 2, 0), new BlockingCell(2, 1), new NormalCell(4, 2, 2), new NormalCell(4, 2, 3)}
	};
	
	Cell[][] failMatrix = new Cell[][] {
		{new NormalCell(2, 0, 0), new BlockingCell(0, 1), new NormalCell(2, 0, 2), new NormalCell(4, 0, 3)},
		{new BlockingCell(1, 0), new BonusCell(42, 1, 1), new BlockingCell(1, 2), new NormalCell(2, 1, 3)}
	};
	
	@Before
	public void setMatrices() {
		for (Cell[] row : testMatrix) {
			for (Cell cell : row) cell.matrix = testMatrix;
		}
		for (Cell[] row : failMatrix) {
			for (Cell cell : row) cell.matrix = failMatrix;
		}
	}
	
	/**
	 * Testing Cell.degenerate(Cell) for all cell types.
	 */
	@Test
	public void testDegenerate() {
		NormalCell normalCell = new NormalCell(0, 0, 0);
		BonusCell bonusCell = (BonusCell) testMatrix[1][0]; //BonusCell in that position
		BlockingCell blockingCell = (BlockingCell) testMatrix[2][1]; 
		//BlockingCell in that position
		
		bonusCell.degenerate(normalCell);
		assertTrue(bonusCell.value == 0);
		
		blockingCell.degenerate(normalCell);
		assertTrue(blockingCell.x == 2 && blockingCell.y == 1); 
		
		blockingCell.lifeCounter = 0;
		blockingCell.degenerate(normalCell); 
		assertTrue(blockingCell.lifeCounter == 0); 
		assertTrue(blockingCell.value == 0);
	}
	
	/**
	 * Testing Cell.merge(String) for all cell types.
	 */
	@Test
	public void testValues(){
		Cell[][] matrix = testMatrix.clone();
		merge (matrix, "l");
		
		assertEquals(4, matrix[0][0].value);
		assertEquals(0, matrix[0][3].value);
		assertEquals(8, matrix[2][2].value);
	}
	
	/**
	 * Testing Cell.hasClashed()
	 */
	@Test
	public void testClashDetection() { 
		Cell[][] matrix = testMatrix.clone();
		merge(matrix, "l");
		
		boolean[][] expectedClash = new boolean[][]{
				{true, false, false, false},
				{false, false, false, false},
				{false, false, true, false}
		};
		
		for (int row = 0; row < matrix.length; row++) {
			for (int column = 0; column < matrix[0].length; column++) {
				assertTrue(matrix[row][column].hasClashed == expectedClash[row][column]);
			}
		}
	}
	
	/**
	 * Testing Cell.getShiftCounter()
	 */
	@Test
	public void testShiftCounters() {
		Cell[][] matrix = testMatrix.clone();
		merge(matrix, "l");
		assertEquals(4, matrix[0][0].shiftCounter);
		assertEquals(0, matrix[2][1].shiftCounter);
		assertEquals(1, matrix[2][2].shiftCounter);
	}
	
	/**
	 * Testing Cell.isEmpty()
	 */
	@Test
	public void testEmptyDetection() {
		Cell[][] matrix = testMatrix.clone();
		merge(matrix, "r");
		
		boolean[][] expectedEmpty = new boolean[][] {
				{true, true, true, false},
				{true, true, true, false},
				{true, false, true, false}
		};

		for (int row = 0; row < matrix.length; row++) {
			for (int column = 0; column < matrix[0].length; column++) { 
				assertTrue(matrix[row][column].isEmpty == expectedEmpty[row][column]);
			}
		}
	}
	
	/**
	 * Testing Cell.getX() and Cell.getY()
	 */
	@Test
	public void testCoordinates() {
		Cell[][] matrix = testMatrix.clone();
		merge(matrix, "l");
		
		for (int row = 0; row < matrix.length; row++) {
			for (int column = 0; column < matrix[0].length; column++) {
				assertTrue(matrix[row][column].x == row);
				assertTrue(matrix[row][column].y == column); 
			} 
		} 
	} 
	
	/**
	 * Testing Cell.tmpValue()
	 */
	@Test
	public void testTmpValues() {
		Cell[][] matrix = testMatrix.clone();
		merge(matrix, "l");
		
		assertEquals(8, matrix[2][2].tmpValue);
		assertEquals(4, matrix[0][0].tmpValue);
		assertEquals(0, matrix[2][1].tmpValue);
	}
	
	/**
	 * Testing Cell.getNeighbour(String)
	 */
	@Test
	public void testNeighbourInfo() {
		Cell[][] matrix = testMatrix.clone(); 
		
		for (int i = 0; i < matrix.length; i++){ 
			for (int j = 0; j < matrix[0].length; j++){ 
				Cell neighbour; 
				if ((neighbour = matrix[i][j].getNeighbour("l")) != null){
					assertTrue(neighbour.getNeighbour("r") == matrix[i][j]);
					assertTrue(neighbour.y == matrix[i][j].y - 1);
				} else if ((neighbour = matrix[i][j].getNeighbour("r")) != null){
					assertTrue(neighbour.getNeighbour("l") == matrix[i][j]);
					assertTrue(neighbour.y == matrix[i][j].y + 1); 
				} else if ((neighbour = matrix[i][j].getNeighbour("u")) != null){
					assertTrue(neighbour.getNeighbour("d") == matrix[i][j]);
					assertTrue(neighbour.x == matrix[i][j].x - 1);
				} else if ((neighbour = matrix[i][j].getNeighbour("d")) != null){
					assertTrue(neighbour.getNeighbour("u") == matrix[i][j]);
					assertTrue(neighbour.x == matrix[i][j].x + 1);
				}
			} 
		}
	}
	
	/**
	 * Testing Cell.enablesMovement(Cell)
	 */
	@Test
	public void testEnablesMovement() {
		Cell[][] matrix = testMatrix.clone();
		Cell[][] failMatrix = this.failMatrix.clone();
		
		boolean matrixCheckFail = true;
		boolean failMatrixCheckFail = true;
		
		for (Cell[] row : matrix) {
			for (Cell cell : row) if (cell.enablesMovement()) matrixCheckFail = false;
		}
		
		for (Cell[] row : failMatrix) {
			for (Cell cell : row) if (cell.enablesMovement()) failMatrixCheckFail = false;
		}
		
		assertTrue (!matrixCheckFail);
		assertTrue (failMatrixCheckFail);
	}
	
	private void merge (Cell[][] matrix, String direction) {
		for (Cell[] row : matrix) {
			for (Cell cell : row) cell.merge(direction);
		} 
	}
}
