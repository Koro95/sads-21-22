package at.uibk.dps.dsB.ex0.decoders;

import org.opt4j.core.genotype.IntegerGenotype;
import org.opt4j.core.problem.Decoder;

/**
 * The {@link Decoder} class which will be used to decode the genotypes, i.e.,
 * transform them into a representation which can be processed by the evaluator.
 * 
 * @author Fedor Smirnov
 *
 */
public class MyFirstDecoder implements Decoder<IntegerGenotype, int[][]> {

	// Turn IntegerGenotype into nested array to represent Sudoku grid
	@Override
	public int[][] decode(IntegerGenotype genotype) {
		int[][] phenotype = new int[9][9];
		
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				phenotype[i][j] = genotype.get(i*9 + j);
			}
		}
		
		return phenotype;
	}
}
