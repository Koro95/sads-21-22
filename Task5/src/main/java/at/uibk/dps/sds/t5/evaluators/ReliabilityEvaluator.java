package at.uibk.dps.sds.t5.evaluators;

import org.opt4j.core.Objective;
import org.opt4j.core.Objective.Sign;
import org.opt4j.core.Objectives;

import com.google.inject.Inject;

import at.uibk.dps.sds.t5.reliability.ReliabilityCalculator;
import net.sf.opendse.model.Specification;
import net.sf.opendse.optimization.ImplementationEvaluator;

/**
 * Evaluator to (a) create a BDD representation of the structure function of the
 * given implementation and (b) to process this BDD to calculate the MTTF of the
 * implementation.
 * 
 * @author fedor
 *
 */
public class ReliabilityEvaluator implements ImplementationEvaluator {

	protected final Objective reliabilityObjective = new Objective("MTTF", Sign.MAX);

	protected final ReliabilityCalculator reliabilityCalc;

	/**
	 * Injection constructor.
	 * 
	 * @param reliabilityCalc object handling the MTTF calculation.
	 */
	@Inject
	public ReliabilityEvaluator(ReliabilityCalculator reliabilityCalc) {
		this.reliabilityCalc = reliabilityCalc;
	}

	@Override
	public Specification evaluate(Specification implementation, Objectives objectives) {
		double MTTFvalue = reliabilityCalc.calculateRelibility(implementation);
		objectives.add(reliabilityObjective, MTTFvalue);
		return implementation;
	}

	@Override
	public int getPriority() {
		// No relation to other evaluators => does not matter
		return 0;
	}

}
