package at.uibk.dps.sds.t3.modules;

import org.opt4j.core.start.Constant;

import at.uibk.dps.sds.t3.homework.HomeworkMappingConstraintManager;
import at.uibk.dps.sds.t3.homework.HomeworkMappingEncoding;
import net.sf.opendse.encoding.mapping.MappingConstraintManager;
import net.sf.opendse.optimization.DesignSpaceExplorationModule;

/**
 * The mode which binds the classes implementing the secrecy encoding (to be
 * implemented as the homework).
 * 
 * @author Fedor Smirnov
 *
 */
public class HomeworkEncodingModel extends DesignSpaceExplorationModule {

	@Constant(value = "noSecretTaskOnCloud", namespace = HomeworkMappingEncoding.class)
	public boolean encodeNoSecretTaskOnCloud;

	@Constant(value = "secretMessagesSameRegion", namespace = HomeworkMappingEncoding.class)
	public boolean encodeSecretMessagesSameRegion;

	@Constant(value = "maxTwoTasksEdgeResource", namespace = HomeworkMappingEncoding.class)
	public boolean encodeMaxTwoTasksEdgeResource;

	@Constant(value = "taskMappingNecessity", namespace = HomeworkMappingEncoding.class)
	public boolean encodeTaskMappingNecessity;

	public boolean isEncodeNoSecretTaskOnCloud() {
		return encodeNoSecretTaskOnCloud;
	}

	public void setEncodeNoSecretTaskOnCloud(boolean encodeNoSecretTaskOnCloud) {
		this.encodeNoSecretTaskOnCloud = encodeNoSecretTaskOnCloud;
	}

	public boolean isEncodeSecretMessagesSameRegion() {
		return encodeSecretMessagesSameRegion;
	}

	public void setEncodeSecretMessagesSameRegion(boolean encodeSecretMessagesSameRegion) {
		this.encodeSecretMessagesSameRegion = encodeSecretMessagesSameRegion;
	}

	public boolean isEncodeMaxTwoTasksEdgeResource() {
		return encodeMaxTwoTasksEdgeResource;
	}

	public void setEncodeMaxTwoTasksEdgeResource(boolean encodeMaxTwoTasksEdgeResource) {
		this.encodeMaxTwoTasksEdgeResource = encodeMaxTwoTasksEdgeResource;
	}

	public boolean isEncodeTaskMappingNecessity() {
		return encodeTaskMappingNecessity;
	}

	public void setEncodeTaskMappingNecessity(boolean encodeTaskMappingNecessity) {
		this.encodeTaskMappingNecessity = encodeTaskMappingNecessity;
	}

	@Override
	protected void config() {
		bind(MappingConstraintManager.class).to(HomeworkMappingConstraintManager.class);
	}
}
