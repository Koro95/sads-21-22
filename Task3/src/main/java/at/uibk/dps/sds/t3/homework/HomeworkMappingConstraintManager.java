package at.uibk.dps.sds.t3.homework;

import org.opt4j.core.start.Constant;

import com.google.inject.Inject;

import net.sf.opendse.encoding.mapping.MappingConstraintGenerator;
import net.sf.opendse.encoding.mapping.MappingConstraintManager;
import net.sf.opendse.model.properties.ProcessPropertyService.MappingModes;
import net.sf.opendse.optimization.SpecificationWrapper;

public class HomeworkMappingConstraintManager implements MappingConstraintManager {

	protected final HomeworkMappingEncoding homeWorkMappingEncoding;

	@Inject
	public HomeworkMappingConstraintManager(SpecificationWrapper specWrapper,
			@Constant(value = "noSecretTaskOnCloud", namespace = HomeworkMappingEncoding.class) boolean encodeNoSecretTaskOnCloud,
			@Constant(value = "secretMessagesSameRegion", namespace = HomeworkMappingEncoding.class) boolean encodeSecretMessagesSameRegion,
			@Constant(value = "maxTwoTasksEdgeResource", namespace = HomeworkMappingEncoding.class) boolean encodeMaxTwoTasksEdgeResource,
			@Constant(value = "taskMappingNecessity", namespace = HomeworkMappingEncoding.class) boolean encodeTaskMappingNecessity) {
		this.homeWorkMappingEncoding = new HomeworkMappingEncoding(specWrapper, encodeNoSecretTaskOnCloud,
				encodeSecretMessagesSameRegion, encodeMaxTwoTasksEdgeResource, encodeTaskMappingNecessity);
	}

	@Override
	public MappingConstraintGenerator getMappingConstraintGenerator(MappingModes mappingMode) {
		return homeWorkMappingEncoding;
	}
}
