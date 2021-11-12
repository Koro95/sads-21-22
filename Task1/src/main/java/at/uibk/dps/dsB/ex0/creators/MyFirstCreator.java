package at.uibk.dps.dsB.ex0.creators;

import java.util.Random;

import org.opt4j.core.genotype.IntegerGenotype;
import org.opt4j.core.problem.Creator;

/**
 * The {@link Creator} class which will be used to initialize the genotypes
 * encoding individual problem solutions.
 * 
 * @author Fedor Smirnov
 *
 */
public class MyFirstCreator implements Creator<IntegerGenotype> {

	protected Random random = new Random();
	
	// Initialize IntegerGenotype with 81 numbers for Sudoku
	@Override
	public IntegerGenotype create() {
		IntegerGenotype genotype = new IntegerGenotype(1,9);
		genotype.init(random, 81);
		return genotype;
	}

}
