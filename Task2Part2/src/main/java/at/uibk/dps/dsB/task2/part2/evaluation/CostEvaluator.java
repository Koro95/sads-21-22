package at.uibk.dps.dsB.task2.part2.evaluation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.opt4j.core.Objective;
import org.opt4j.core.Objectives;

import org.opt4j.core.Objective.Sign;

import at.uibk.dps.dsB.task2.part2.properties.PropertyProvider;
import at.uibk.dps.dsB.task2.part2.properties.PropertyProviderStatic;
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
import net.sf.opendse.optimization.ImplementationEvaluator;

/**
 * The {@link CostEvaluator} is used to calculate the costs of different
 * orchestrations of the PIW3000.
 * 
 * @author Fedor Smirnov
 *
 */
public class CostEvaluator implements ImplementationEvaluator {

	protected final Objective costObjective = new Objective("Costs [Distopistan Dorrar]", Sign.MIN);
	protected final PropertyProvider propertyProvider = new PropertyProviderStatic();

	@Override
	public Specification evaluate(Specification implementation, Objectives objectives) {
		double costs = calculateImplementationCost(implementation);
		objectives.add(costObjective, costs);
		// No changes to the implementation => return null
		return null;
	}

	/**
	 * Does the actual cost calculation
	 * 
	 * @param implementation the solution which is being evaluated
	 * @return the cost of the implementation
	 */
	protected double calculateImplementationCost(Specification implementation) {
		double cost = 0.0;
		
		Application<Task, Dependency> application = implementation.getApplication();
		Collection<Task> tasks = application.getVertices();
		Mappings<Task, Resource> mappings = implementation.getMappings();
		Routings<Task, Resource, Link> routings = implementation.getRoutings();
		
		// fog/edge resources only bought once
		Set<Resource> fogEdgeResources = new HashSet<>();
		
		// go over cost for each task
		for (Task task : tasks) {
			double taskCost = 0.0;
	
			// communication tasks
			if (task.getClass() == Communication.class) {		
				Collection<Link> routing = routings.get(task).getEdges();

				for (Link link : routing) {
					Double linkCost = link.getAttribute("COST");
					
					if (linkCost != null) {
						taskCost += linkCost;
					}
				}
			}
			// computation tasks
			else {
				// You can assume that exactly one resource type is chosen for every task
				Set<Mapping<Task, Resource>> taskMappings = mappings.get(task);
				for (Mapping<Task, Resource> mapping : taskMappings) {
					Resource resource = mapping.getTarget();

					if (resource.getType().equals("CLOUD")) {
						Double resourceCost = resource.getAttribute("COST");
						if (resourceCost != null) {
							taskCost = resourceCost * propertyProvider.getExecutionTime(mapping);							
						}
					}
					else {
						fogEdgeResources.add(resource);
					}
				}
			}
			
			// iterative tasks
			String taskType = task.getType();
			if (taskType != null) {
				if (task.getType().equals("ITERATIVE_CARS")) {
					taskCost *= propertyProvider.getCarNumber();
				} else if (task.getType().equals("ITERATIVE_PEOPLE")) {
					taskCost *= propertyProvider.getNumberOfPeople();
				}
			}
			
			cost += taskCost;
		}
		
		// only add prize for fog / edge resources once at the end
		for (Resource fogEdgeResource : fogEdgeResources) {
			Double resourceCost = fogEdgeResource.getAttribute("COST");
			if (resourceCost != null) {
				cost += resourceCost;				
			}
		}
		
		return cost;
	}

	@Override
	public int getPriority() {
		// To be executed after the timing evaluator
		return TimingEvaluator.priority + 1;
	}
}
