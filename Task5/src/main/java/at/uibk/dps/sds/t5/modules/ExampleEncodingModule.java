package at.uibk.dps.sds.t5.modules;

import at.uibk.dps.sds.t5.constraints.MConstraintManager;
import net.sf.opendse.encoding.mapping.MappingConstraintManager;
import net.sf.opendse.optimization.DesignSpaceExplorationModule;

/**
 * Module to switch to example/homework encoding of the mappings.
 * 
 * @author Fedor Smirnov
 *
 */
public class ExampleEncodingModule extends DesignSpaceExplorationModule {

	@Override
	protected void config() {
		bind(MappingConstraintManager.class).to(MConstraintManager.class);
	}
}
