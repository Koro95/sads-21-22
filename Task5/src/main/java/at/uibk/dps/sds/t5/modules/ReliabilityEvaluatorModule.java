package at.uibk.dps.sds.t5.modules;

import at.uibk.dps.sds.t5.evaluators.ReliabilityEvaluator;
import net.sf.opendse.optimization.evaluator.EvaluatorModule;

/**
 * Module to configure the usage of the {@link ReliabilityEvaluator}
 * 
 * @author fedor
 *
 */
public class ReliabilityEvaluatorModule extends EvaluatorModule {

	@Override
	protected void config() {
		bindEvaluator(ReliabilityEvaluator.class);
	}
}
