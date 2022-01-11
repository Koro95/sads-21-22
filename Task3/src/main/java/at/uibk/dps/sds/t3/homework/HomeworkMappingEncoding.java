package at.uibk.dps.sds.t3.homework;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;
import org.opt4j.satdecoding.Term;

import net.sf.opendse.encoding.mapping.MappingConstraintGenerator;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.optimization.SpecificationWrapper;

/**
 * 
 * Class for the implementation of the homework.
 * 
 * @author Fedor Smirnov
 */
public class HomeworkMappingEncoding implements MappingConstraintGenerator {

	protected final Specification spec;
	protected final boolean encodeNoSecretTaskOnCloud;
	protected final boolean encodeSecretMessagesSameRegion;
	protected final boolean encodeMaxTwoTasksEdgeResource;
	protected final boolean encodeTaskMappingNecessity;

	public HomeworkMappingEncoding(SpecificationWrapper specWrapper, boolean encodeNoSecretTaskOnCloud,
			boolean encodeSecretMessagesSameRegion, boolean encodeMaxTwoTasksEdgeResource,
			boolean encodeTaskMappingNecessity) {
		this.spec = specWrapper.getSpecification();
		this.encodeNoSecretTaskOnCloud = encodeNoSecretTaskOnCloud;
		this.encodeSecretMessagesSameRegion = encodeSecretMessagesSameRegion;
		this.encodeMaxTwoTasksEdgeResource = encodeMaxTwoTasksEdgeResource;
		this.encodeTaskMappingNecessity = encodeTaskMappingNecessity;
	}

	@Override
	public Set<Constraint> toConstraints(Set<T> processVariables, Mappings<Task, Resource> mappings) {
		Set<Constraint> result = new HashSet<>();

		if (encodeNoSecretTaskOnCloud) {
			result.addAll(encodeNoSecretTaskOnCloudConstraints(mappings));
		}
		if (encodeSecretMessagesSameRegion) {
			result.addAll(encodeSecretMessagesSameRegionConstraints(mappings));
		}
		if (encodeMaxTwoTasksEdgeResource) {
			result.addAll(encodeMaxTwoTasksEdgeResourceConstraints(mappings));
		}
		if (encodeTaskMappingNecessity) {
			result.addAll(encodeTaskMappingNecessityConstraints(processVariables, mappings));
		}

		return result;
	}

	/**
	 * Encodes the constraints of not executing secret tasks in the cloud.
	 * 
	 * @param processVariables the variables encoding the activation of processes
	 * @param mappings         the mappings
	 * @return constraint set encoding that secret tasks are not executed in the
	 *         cloud
	 */
	protected Set<Constraint> encodeNoSecretTaskOnCloudConstraints(Mappings<Task, Resource> mappings) {
		Set<Constraint> result = new HashSet<>();
		for (Mapping<Task, Resource> m : mappings) {
			if (PropertyService.isSecret(m.getSource()) && PropertyService.isCloud(m.getTarget())) {
				result.add(encodeNoSecretTaskOnCloudConstraint(m));
			}
		}
		return result;
	}

	/**
	 * Encodes that secret tasks cannot be executed on cloud resources.
	 * 
	 * Mapping.TR = 0
	 * 
	 * @param resMapping the resource mappings on that resource
	 * @return the constraint preventing secret tasks to be executed on cloud
	 *         resources
	 */
	protected Constraint encodeNoSecretTaskOnCloudConstraint(Mapping<Task, Resource> m) {
		Constraint result = new Constraint(Operator.EQ, 0);

		M mVar = Variables.varM(m);
		result.add(Variables.p(mVar));

		return result;
	}

	/**
	 * Encodes the constraints that secret messages have to be in the same region.
	 * 
	 * @param mappings    the mappings
	 * @param application the application
	 * @return constraint set encoding that secret messages are in the same region
	 */
	protected Set<Constraint> encodeSecretMessagesSameRegionConstraints(Mappings<Task, Resource> mappings) {
		Set<Constraint> result = new HashSet<>();
		Application<Task, Dependency> application = this.spec.getApplication();
		Collection<Task> tasks = application.getVertices();

		// Check pre- and successor of communication tasks
		for (Task task : tasks) {
			if (task.getClass() == Communication.class) {
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

								// If they don't have the same region, add SAT constraint
								for (Mapping<Task, Resource> predecessorMapping : predecessorMappings) {
									Resource predecessorResource = predecessorMapping.getTarget();
									for (Mapping<Task, Resource> successorMapping : successorMappings) {
										Resource successorResource = successorMapping.getTarget();
										if (!PropertyService.getRegion(predecessorResource)
												.equals(PropertyService.getRegion(successorResource))) {
											result.add(encodeSecretMessagesSameRegionConstraint(predecessorMapping,
													successorMapping));
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * Encodes that tasks of secret messages are in the same region
	 * 
	 * M1 + M2 <= 1
	 * 
	 * @param task        the communication task
	 * @param application the application
	 * @return the constraint preventing secret message tasks from being in
	 *         different regions
	 */
	protected Constraint encodeSecretMessagesSameRegionConstraint(Mapping<Task, Resource> predecessorMapping,
			Mapping<Task, Resource> successorMapping) {
		Constraint result = new Constraint(Operator.LE, 1);

		M mVar1 = Variables.varM(predecessorMapping);
		M mVar2 = Variables.varM(successorMapping);

		result.add(Variables.p(mVar1));
		result.add(Variables.p(mVar2));
		
		return result;
	}

	/**
	 * Encodes the constraints of at most 2 tasks per edge resource.
	 * 
	 * @param mappings the mappings
	 * @return constraint set encoding that each edge resource has at most 2 tasks
	 *         mapped to it
	 */
	protected Set<Constraint> encodeMaxTwoTasksEdgeResourceConstraints(Mappings<Task, Resource> mappings) {
		Set<Constraint> result = new HashSet<>();
		Set<Resource> resources = new HashSet<>();
		for (Mapping<Task, Resource> m : mappings) {
			resources.add(m.getTarget());
		}
		for (Resource res : resources) {
			if (PropertyService.isEdge(res)) {
				result.add(encodeMaxTwoTasksEdgeResourceConstraint(mappings.get(res)));
			}
		}
		return result;
	}

	/**
	 * Encodes that edge resources can only execute 2 tasks.
	 * 
	 * sum (M(R)) <= 2
	 * 
	 * @param resMapping the resource mappings on that resource
	 * @return the constraint preventing more than 2 tasks to be executed on that
	 *         resource
	 */
	protected Constraint encodeMaxTwoTasksEdgeResourceConstraint(Set<Mapping<Task, Resource>> resMappings) {
		Constraint result = new Constraint(Operator.LE, 2);
		for (Mapping<Task, Resource> m : resMappings) {
			M mVar = Variables.varM(m);
			result.add(Variables.p(mVar));
		}
		return result;
	}

	/**
	 * Encodes that each task is mapped at least once.
	 * 
	 * @param processVariables the variables encoding the activation of processes
	 * @param mappings         the mappings
	 * @return constraint set encoding that each task is mapped at least once
	 */
	protected Set<Constraint> encodeTaskMappingNecessityConstraints(Set<T> processVariables,
			Mappings<Task, Resource> mappings) {
		Set<Constraint> result = new HashSet<>();
		for (T tVar : processVariables) {
			Set<Mapping<Task, Resource>> taskMappings = mappings.get(tVar.getTask());
			result.add(encodeTaskMappingNecessityConstraint(tVar, taskMappings));
		}
		return result;
	}

	/**
	 * Encodes the constraint stating that the task encoded by the given variable is
	 * mapped on at least one resource.
	 * 
	 * - T + sum (M) >= 0
	 * 
	 * @param tVar         The encoding variable of the task
	 * @param taskMappings a set of the task mappings
	 * @return the constraint stating that the task encoded by the given variable is
	 *         mapped on at least one resource
	 */
	protected Constraint encodeTaskMappingNecessityConstraint(T tVar, Set<Mapping<Task, Resource>> taskMappings) {
		Constraint result = new Constraint(Operator.GE, 0);
		result.add(new Term(-1, Variables.p(tVar))); // Here you have to pay attention to use the Variables from the
														// encoding project, not from the optimization project
		for (Mapping<Task, Resource> mapping : taskMappings) {
			M mVar = Variables.varM(mapping);
			result.add(Variables.p(mVar));
		}
		return result;
	}
}
