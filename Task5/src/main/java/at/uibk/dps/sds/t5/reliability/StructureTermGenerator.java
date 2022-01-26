package at.uibk.dps.sds.t5.reliability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jreliability.booleanfunction.Term;
import org.jreliability.booleanfunction.common.ANDTerm;
import org.jreliability.booleanfunction.common.LinearTerm;
import org.jreliability.booleanfunction.common.LinearTerm.Comparator;
import org.jreliability.booleanfunction.common.LiteralTerm;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;

/**
 * The {@link StructureTermGenerator} is used to generate the boolean structure
 * term for a given implementation.
 * 
 * @author fedor
 *
 */
public class StructureTermGenerator {

	/**
	 * Generates the boolean structure term for the given implementation
	 * 
	 * @param impl the given implementation
	 * @return the boolean structure term for the given implementation
	 */
	public Term generateStructureTerm(Specification impl) {
		Application<Task, Dependency> application = impl.getApplication();
		Collection<Task> tasks = application.getVertices();
		Mappings<Task, Resource> mappings = impl.getMappings();
		Routings<Task, Resource, Link> routings = impl.getRoutings();

		// overall list of terms to combine them at the end
		List<Term> termList = new ArrayList<Term>();

		for (Task task : tasks) {
			// communication tasks
			if (task.getClass() == Communication.class) {
				Collection<Task> predecessors = application.getPredecessors(task);
				Collection<Task> successors = application.getSuccessors(task);
				Collection<Link> routing = routings.get(task).getEdges();

				List<Term> communicationTaskTerms = new ArrayList<Term>();

				// add terms for links of messages
				for (Link link : routing) {
					communicationTaskTerms.add(new LiteralTerm<Link>(link));
				}

				// add terms for resources of messages
				for (Task predecessor : predecessors) {
					for (Task successor : successors) {
						Set<Mapping<Task, Resource>> predecessorMappings = mappings.get(predecessor);
						Set<Mapping<Task, Resource>> successorMappings = mappings.get(successor);

						for (Mapping<Task, Resource> predecessorMapping : predecessorMappings) {
							for (Mapping<Task, Resource> successorMapping : successorMappings) {
								Resource predecessorResource = predecessorMapping.getTarget();
								Resource successorResource = successorMapping.getTarget();

								communicationTaskTerms.add(new LiteralTerm<Resource>(predecessorResource));
								communicationTaskTerms.add(new LiteralTerm<Resource>(successorResource));
							}
						}
					}
				}

				// A message is functional iff all resources and links which are part of the
				// message's routing graph are functional
				termList.add(new ANDTerm(communicationTaskTerms));
			}
			// computation tasks
			else {
				Set<Mapping<Task, Resource>> taskMappings = mappings.get(task);

				List<Term> computationTaskTerms = new ArrayList<Term>();
				List<Integer> coefficients = new ArrayList<Integer>();

				// add terms for resources of computations
				for (Mapping<Task, Resource> mapping : taskMappings) {
					Resource resource = mapping.getTarget();
					computationTaskTerms.add(new LiteralTerm<Resource>(resource));
					coefficients.add(1);
				}

				// A task is functional iff at least one of the resources that the task is
				// mapped onto is functional (multi-mapping is allowed)
				termList.add(new LinearTerm(coefficients, computationTaskTerms, Comparator.GREATEREQUAL, 1));
			}
		}

		// The overall application is functional iff all of its tasks and all of its
		// messages are functional
		Term structureTerm = new ANDTerm(termList);

		return structureTerm;
	}
}
