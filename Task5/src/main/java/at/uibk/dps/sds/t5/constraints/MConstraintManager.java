package at.uibk.dps.sds.t5.constraints;

import org.opt4j.core.start.Constant;

import com.google.inject.Inject;

import net.sf.opendse.encoding.mapping.MappingConstraintGenerator;
import net.sf.opendse.encoding.mapping.MappingConstraintManager;
import net.sf.opendse.model.properties.ProcessPropertyService.MappingModes;
import net.sf.opendse.optimization.SpecificationWrapper;

public class MConstraintManager implements MappingConstraintManager {

	protected final MappingEncoding homeWorkMappingEncoding;

	@Inject
	public MConstraintManager(SpecificationWrapper specWrapper,
			@Constant(value = "maxTaskPerRes", namespace = MappingEncoding.class) int maxTasksOnRes) {
		this.homeWorkMappingEncoding = new MappingEncoding(specWrapper, maxTasksOnRes);
	}

	@Override
	public MappingConstraintGenerator getMappingConstraintGenerator(MappingModes mappingMode) {
		return homeWorkMappingEncoding;
	}
}
