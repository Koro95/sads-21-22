package at.uibk.dps.sds.t5.reliability;

import org.apache.commons.collections15.Transformer;
import org.jreliability.function.ReliabilityFunction;
import org.jreliability.function.common.NormalReliabilityFunction;

import at.uibk.dps.sds.t5.properties.PropertyService;
import net.sf.opendse.model.Element;

/**
 * The {@link ElementTransformer} is used to generate
 * {@link ReliabilityFunction}s for the elements in the specification.
 * 
 * @author fedor
 */
public class ElementTransformer implements Transformer<Element, ReliabilityFunction> {

	@Override
	public ReliabilityFunction transform(Element input) {
		double reliabilityAverage = PropertyService.getReliabilityAverage(input);
		double reliabilityDeviation = PropertyService.getReliabilityDeviation(input);
		return new NormalReliabilityFunction(reliabilityAverage, reliabilityDeviation);
	}
}
