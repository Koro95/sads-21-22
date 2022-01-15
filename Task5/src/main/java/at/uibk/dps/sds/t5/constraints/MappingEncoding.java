package at.uibk.dps.sds.t5.constraints;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Term;
import org.opt4j.satdecoding.Constraint.Operator;

import net.sf.opendse.encoding.mapping.MappingConstraintGenerator;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
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
public class MappingEncoding implements MappingConstraintGenerator {

	protected final Specification spec;

	protected final int maximumTasksOnResource;

	public MappingEncoding(SpecificationWrapper specWrapper, int maximumTasksOnResource) {
		this.spec = specWrapper.getSpecification();
		this.maximumTasksOnResource = maximumTasksOnResource;
	}

	@Override
	public Set<Constraint> toConstraints(Set<T> processVariables, Mappings<Task, Resource> mappings) {
		Set<Constraint> result = new HashSet<>();
		result.addAll(encodeTaskMappingNecessityConstraints(processVariables, mappings));
		result.addAll(encodeResourceCapacityConstraints());
		return result;
	}

	/**
	 * Encodes the constraints specifying that at most a certain number of tasks can
	 * be mapped to a resource.
	 * 
	 * @return the constraints specifying that at most a certain number of tasks can
	 *         be mapped to a resource
	 */
	protected Set<Constraint> encodeResourceCapacityConstraints() {
		Set<Constraint> result = new HashSet<>();
		spec.getArchitecture().getVertices().stream().filter(res -> spec.getMappings().get(res).size() > 0)
				.forEach(res -> result.add(encodeResourceCapacityConstraint(spec.getMappings().get(res))));
		return result;
	}

	/**
	 * Encodes that no more than the configured number N of tasks can be placed on a
	 * resource.
	 * 
	 * sum(M) <= N
	 * 
	 * @param resMappings the mappings on the specified resource
	 * @return the constraint constraining the number of resources placed on a
	 *         resource.
	 */
	protected Constraint encodeResourceCapacityConstraint(Set<Mapping<Task, Resource>> resMappings) {
		Constraint result = new Constraint(Operator.LE, maximumTasksOnResource);
		resMappings.forEach(mapping -> result.add(Variables.p(Variables.varM(mapping))));
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
														// encoding
		// project, not from the optimization project
		for (Mapping<Task, Resource> mapping : taskMappings) {
			M mVar = Variables.varM(mapping);
			result.add(Variables.p(mVar));
		}
		return result;
	}
}
