package at.uibk.dps.sds.t3.homework;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.opt4j.core.Objective;
import org.opt4j.core.Objectives;
import org.opt4j.core.Objective.Sign;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.optimization.ImplementationEvaluator;

/**
 * The evaluator used to enforce the security constraints by means of additional
 * objectives.
 * 
 * @author Fedor Smirnov
 *
 */
public class HwConstraintEvaluator implements ImplementationEvaluator {

	protected final Objective numConstraintViolations = new Objective("Num Constraint Violations", Sign.MIN);
	
	@Override
	public Specification evaluate(Specification implementation, Objectives objectives) {
		objectives.add(numConstraintViolations, countConstraintViolations(implementation));
		return null;
	}

	/**
	 * Counts the number of constraint violations in the given implementation
	 * 
	 * @param implementation the given implementation
	 * @return the number of constraint violations
	 */
	protected int countConstraintViolations(Specification implementation) {
		int violations = 0;

		Application<Task, Dependency> application = implementation.getApplication();
		Collection<Task> tasks = application.getVertices();
		Mappings<Task, Resource> mappings = implementation.getMappings();

		// HashMap to count the number of tasks on an edge resource
		HashMap<Resource, Integer> resourceTaskCount = new HashMap<Resource, Integer>();

		// check constraints for each task
		for (Task task : tasks) {
			// communication tasks - CONSTRAINT 2
			if (task.getClass() == Communication.class) {
				// CONSTRAINT 2: If two tasks exchange messages and are both annotated as
				// secrets, they both must be executed within the same region
				Collection<Task> predecessors = application.getPredecessors(task);
				Collection<Task> successors = application.getSuccessors(task);

				// check if a predecessor is secret, ...
				for (Task predecessor : predecessors) {
					if (PropertyService.isSecret(predecessor)) {
						// ... and the successor is secret, ...
						for (Task successor : successors) {
							if (PropertyService.isSecret(successor)) {
								// ... then compare their mappings
								Set<Mapping<Task, Resource>> predecessorMappings = mappings.get(predecessor);
								Set<Mapping<Task, Resource>> successorMappings = mappings.get(successor);

								for (Mapping<Task, Resource> predecessorMapping : predecessorMappings) {
									for (Mapping<Task, Resource> successorMapping : successorMappings) {
										Resource predecessorResource = predecessorMapping.getTarget();
										Resource successorResource = successorMapping.getTarget();
										if (!PropertyService.getRegion(predecessorResource)
												.equals(PropertyService.getRegion(successorResource))) {
											violations++;
										}
									}
								}
							}

						}

					}
				}
			}
			// computation tasks - CONTRAINTS 1 + 3 + 4
			else {
				Set<Mapping<Task, Resource>> taskMappings = mappings.get(task);

				// CONSTRAINT 4: Each task has to be mapped onto at least one resource
				if (taskMappings.size() < 1) {
					violations++;
				}

				for (Mapping<Task, Resource> mapping : taskMappings) {
					Resource resource = mapping.getTarget();

					// CONSTRAINT 1: Any task annotated as a secret must not be executed on cloud
					// resources
					if (PropertyService.isSecret(task) && PropertyService.isCloud(resource)) {
						violations++;
					}

					// CONSTRAINT 3: Due to their restricted capacity, at most 2 tasks can be
					// executed on a single edge resource
					if (PropertyService.isEdge(resource)) {
						Integer numTask = resourceTaskCount.get(resource);

						if (numTask == null) {
							resourceTaskCount.put(resource, 1);
						} else if (numTask < 2) {
							resourceTaskCount.put(resource, ++numTask);
						} else {
							violations++;
						}
					}
				}
			}
		}

		return violations;
	}

	@Override
	public int getPriority() {
		// independent of other stuff
		return 0;
	}
}
