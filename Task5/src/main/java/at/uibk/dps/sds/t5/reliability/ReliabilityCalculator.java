package at.uibk.dps.sds.t5.reliability;

import net.sf.opendse.model.Element;
import net.sf.opendse.model.Specification;

import org.jreliability.bdd.BDDProvider;
import org.jreliability.bdd.BDDProviderFactory;
import org.jreliability.bdd.BDDTTRF;
import org.jreliability.bdd.javabdd.JBDDProviderFactory;
import org.jreliability.booleanfunction.Term;
import org.jreliability.evaluator.MomentEvaluator;
import org.jreliability.function.ReliabilityFunction;

import com.google.inject.Inject;

/**
 * The {@link ReliabilityCalculator} uses the JReliability framework to
 * calculate the MTTF of a given implementation.
 * 
 * @author fedor
 *
 */
public class ReliabilityCalculator {

	protected final BDDTTRF<Element> bddTtrf;
	protected final MomentEvaluator momentEval = new MomentEvaluator(1);
	protected final ElementTransformer transformer = new ElementTransformer();
	protected final StructureTermGenerator stGenerator = new StructureTermGenerator();

	/**
	 * The injection constructor.
	 */
	@Inject
	public ReliabilityCalculator() {
		this.bddTtrf = initializeBddTtrf();
	}

	/**
	 * Generates the object used to convert the boolean term to a reliability
	 * function.
	 * 
	 * @return the object used to convert the boolean term to a reliability function
	 */
	protected final BDDTTRF<Element> initializeBddTtrf() {
		BDDProviderFactory bProvFactory = new JBDDProviderFactory();
		BDDProvider<Element> bProv = bProvFactory.getProvider();
		return new BDDTTRF<>(bProv);
	}

	/**
	 * Calculates the MTTF of the application deployment modeled by the provided
	 * implementation.
	 * 
	 * @param impl the provided implementation
	 * @return the calculated MTTF value
	 */
	public double calculateRelibility(Specification impl) {
		Term structureTerm = generateStructureTerm(impl);
		ReliabilityFunction relFuncImpl = generateReliabilityFunction(structureTerm);
		return calculateMTTF(relFuncImpl);
	}

	/**
	 * Generates the reliability function based on the provided structure term.
	 * 
	 * @param structureTerm the provided structure term.
	 * @return the reliability function generated based on the provided structure
	 *         term
	 */
	protected ReliabilityFunction generateReliabilityFunction(Term structureTerm) {
		return bddTtrf.convert(structureTerm, transformer);
	}

	/**
	 * Calculates the MTTF based on the provided reliability function.
	 * 
	 * @param relFunc the provided reliability function
	 * @return the MTTF of the system described by the provided reliability function
	 */
	protected double calculateMTTF(ReliabilityFunction relFunc) {
		return momentEval.evaluate(relFunc);
	}

	/**
	 * Generates the boolean term representing the structure function of the given
	 * implementation.
	 * 
	 * @param impl the given implementation
	 * @return the boolean term representing the structure function
	 */
	protected Term generateStructureTerm(Specification impl) {
		return stGenerator.generateStructureTerm(impl);
	}
}
