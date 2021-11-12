package at.uibk.dps.dsB.ex0.evaluators;

import org.opt4j.core.Objective;
import org.opt4j.core.Objective.Sign;
import org.opt4j.core.Objectives;
import org.opt4j.core.problem.Decoder;
import org.opt4j.core.problem.Evaluator;

/**
 * The {@link Evaluator} class which will be used to evaluate the phenotypes
 * returned by the {@link Decoder}.
 * 
 * @author Fedor Smirnov
 *
 */
public class MyFirstEvaluator implements Evaluator<int[][]> {

	// Changed Sing to MIN, because I count the errors
	// --> Objective=0 is fully solved Sudoku
	protected final Objective myObjective = new Objective("Objective to maximize", Sign.MIN);
	protected double oldFitness = Integer.MAX_VALUE;
	
	protected final int[][] sudokuToSolve1 =
			{ { 0, 0, 0,   6, 0, 0,   0, 0, 3 },
			  { 2, 3, 0,   0, 0, 8,   0, 0, 0 },
			  { 0, 9, 8,   0, 0, 0,   2, 7, 0 },
			  
			  { 0, 0, 9,   0, 0, 2,   0, 0, 0 },
			  { 0, 0, 0,   4, 0, 7,   0, 1, 2 },
			  { 4, 0, 0,   8, 0, 0,   3, 6, 0 },
			  
			  { 1, 2, 0,   0, 8, 4,   6, 5, 7 },
			  { 9, 4, 6,   0, 3, 5,   0, 2, 8 },
			  { 0, 0, 7,   0, 1, 6,   0, 0, 0 } };
	
	// for second version, where it compares against solution
	protected final int[][] sudokuToSolve1Solution =
		{ { 7, 5, 1,   6, 2, 9,   4, 8, 3 },
		  { 2, 3, 4,   1, 7, 8,   5, 9, 6 },
		  { 6, 9, 8,   5, 4, 3,   2, 7, 1 },
		  
		  { 8, 1, 9,   3, 6, 2,   7, 4, 5 },
		  { 3, 6, 5,   4, 9, 7,   8, 1, 2 },
		  { 4, 7, 2,   8, 5, 1,   3, 6, 9 },
		  
		  { 1, 2, 3,   9, 8, 4,   6, 5, 7 },
		  { 9, 4, 6,   7, 3, 5,   1, 2, 8 },
		  { 5, 8, 7,   2, 1, 6,   9, 3, 4 } };
	
	
	protected final int[][] sudokuToSolve2 =
		{ { 9, 1, 0,   4, 0, 0,   5, 0, 8 },
		  { 0, 0, 7,   9, 0, 0,   3, 0, 0 },
		  { 0, 0, 0,   6, 8, 3,   0, 9, 7 },
		  
		  { 0, 0, 9,   0, 0, 0,   0, 8, 5 },
		  { 1, 6, 0,   0, 9, 8,   0, 0, 4 },
		  { 0, 0, 0,   0, 6, 0,   0, 1, 0 },
		  
		  { 8, 9, 1,   2, 0, 0,   0, 7, 3 },
		  { 0, 2, 0,   0, 0, 9,   8, 5, 0 },
		  { 0, 4, 5,   0, 7, 0,   9, 2, 0 } };
	

	// choose Sudoku to solve, sudoku1 also has solution for second evaluation function
	protected final int[][] sudokuToSolve = sudokuToSolve1;
	
	@Override
	public Objectives evaluate(int[][] phenotype) {
		// No need to do anything in this method
		double fitness = calculatePhenotypeFitness(phenotype);

		// If checked against the solution, reaches perfect solution in < 500 iterations
		//double fitness = calculatePhenotypeFitnessWithSolution(phenotype);
		
		// Print new solutions with better fitness once
		if (fitness < 15 && (oldFitness - fitness) > 0.1) { oldFitness = fitness; printSudoku(phenotype);}
		
		Objectives result = new Objectives();
		result.add(myObjective, fitness);
		return result;
	}

	/**
	 * Calculates the fitness (smaller fitness -> better solution) of the given
	 * phenotype.
	 * 
	 * @param phenotype the given phenotype
	 * @return the fitness of the given phenotype
	 */
	protected double calculatePhenotypeFitness(int[][] phenotype) {
		double errorValue = 0.0;
		
		// Filled out cells add higher error, because they should always be that value
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (sudokuToSolve[i][j] != 0) {
					errorValue += (phenotype[i][j] == sudokuToSolve[i][j]) ? 0 : 2;
				}
			}
		}
		
		// Check rows
		for (int row = 0; row < 9; row++) {			
			int[] knownNumbers = new int[9];
			
			for (int col = 0; col < 9; col++) {
				int cellValue = phenotype[row][col];
				
				if (knownNumbers[cellValue-1] == 0) {
					knownNumbers[cellValue-1] = 1;
				}
				else {
					errorValue += 1;
				}
			}
		}
		
		// Check columns
		for (int col = 0; col < 9; col++) {
			int[] knownNumbers = new int[9];
			
			for (int row = 0; row <9; row++) {
				int cellValue = phenotype[row][col];
				
				if (knownNumbers[cellValue-1] == 0) {
					knownNumbers[cellValue-1] = 1;
				}
				else {
					errorValue += 1;
				}
			}
		}
		
		
		// Check 3x3 grids
		for (int gridRow = 0; gridRow < 3; gridRow++) {
			for (int gridCol = 0; gridCol < 3; gridCol++) {
				int[] knownNumbers = new int[9];
				
				// Iterate over rows and columns inside the 3x3 grid
				for (int row = 0; row < 3; row++) {
					for (int col = 0; col < 3; col++) {
						int cellValue = phenotype[gridRow*3 + row][gridCol*3 + col];

						if (knownNumbers[cellValue-1] == 0) {
							knownNumbers[cellValue-1] = 1;
						}
						else {
							errorValue += 1;
						}					
					}
				}
			}
		}

		return errorValue;
	}
	
	/**
	 * Calculates the fitness (smaller fitness -> better solution) of the given
	 * phenotype.
	 * 
	 * @param phenotype the given phenotype
	 * @return the fitness of the given phenotype
	 */
	protected double calculatePhenotypeFitnessWithSolution(int[][] phenotype) {
		double errorValue = 0.0;

		// Filled out cells add higher error , because they should always be that value
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				errorValue += (phenotype[i][j] == sudokuToSolve1Solution[i][j]) ? 0 : 1;
			}
		}

		return errorValue;
	}

	void printSudoku(int[][] phenotype) {
		System.out.println("\n---------------------\nErrors: " + (int)(oldFitness) + "\n");
		
		for (int i = 0; i < 9; i++) {
			System.out.println(
					phenotype[i][0] + " " + phenotype[i][1] + " " + phenotype[i][2] + "   " +
					phenotype[i][3] + " " + phenotype[i][4] + " " + phenotype[i][5] + "   " +
					phenotype[i][6] + " " + phenotype[i][7]	+ " " + phenotype[i][8]);
			
			if (i % 3 == 2) {
				System.out.println();
			}
		}
	}
}
