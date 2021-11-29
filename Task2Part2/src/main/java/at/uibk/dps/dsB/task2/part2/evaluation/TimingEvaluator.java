package at.uibk.dps.dsB.task2.part2.evaluation;

import java.util.Collection;
import java.util.Set;

import org.opt4j.core.Objective;
import org.opt4j.core.Objective.Sign;
import org.opt4j.core.Objectives;

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
 * Evaluator for the makespan of the Piw3000
 * 
 * @author Fedor Smirnov
 */
public class TimingEvaluator implements ImplementationEvaluator {

	protected final PropertyProvider propertyProvider = new PropertyProviderStatic();

	protected static final int priority = 0;

	protected final Objective makeSpanObjective = new Objective("Makespan [TU]", Sign.MIN);

	protected final String endTimeAttribute = "End Time";
	public static final String accumulatedUsageAttribute = "Accumulated Usage";

	@Override
	public Specification evaluate(Specification implementation, Objectives objectives) {
		objectives.add(makeSpanObjective, calculateMakespan(implementation));
		// Implementation annotated => return the impl
		return implementation;
	}

	/**
	 * Does the actual makespan calculation.
	 * 
	 * @param implementation the orchestration under evaluation
	 * @return the makespan of the orchestration
	 */
	protected double calculateMakespan(Specification implementation) {
		Application<Task, Dependency> application = implementation.getApplication();
		Collection<Task> tasks = application.getVertices();
		Mappings<Task, Resource> mappings = implementation.getMappings();
		Routings<Task, Resource, Link> routings = implementation.getRoutings();
		
		boolean finished = false;
		
		// continue until all tasks evaluated
		while (!finished) {
			finished = true;
			
			for (Task task : tasks) {
				// continue for already calculated tasks
				if  (task.getAttribute("finishTime") != null) {
					continue;
				}
				
				// not all tasks are evaluated
				finished = false;
				
				// get predecessors to determine next tasks to calculate
				Collection<Task> predecessors = application.getPredecessors(task);
				
				// check if all predecessors finished
				boolean allPredecessorsFinished = true;
				Double latestFinishTime = 0.0;
				
				for (Task predecessor : predecessors) {
					Double finishTime = predecessor.getAttribute("finishTime");
					
					// break if predecessor has not been evaluated
					if (finishTime == null) {
						allPredecessorsFinished = false;
						break;
					}
					// update latest finish time (start time of next task)
					else {
						if (finishTime > latestFinishTime) {
							latestFinishTime = finishTime;							
						}
					}
				}
				
				// wait until all predecessor finished in later iteration
				if (!allPredecessorsFinished) {
					continue;
				}
				
				// tasks where we can calculate the finish time
				task.setAttribute("startTime", latestFinishTime);
				double executionTime = 0.0;

				// communication tasks
				if (task.getClass() == Communication.class) {								
					Collection<Link> routing = routings.get(task).getEdges();

					for (Link link : routing) {
						executionTime += propertyProvider.getTransmissionTime((Communication) task, link);
					}
				}
				// computation tasks
				else {
					// You can assume that exactly one resource type is chosen for every task
					Set<Mapping<Task, Resource>> taskMappings = mappings.get(task);
					for (Mapping<Task, Resource> mapping : taskMappings) {
						executionTime = propertyProvider.getExecutionTime(mapping);
						
						Resource resource = mapping.getTarget();

						if (resource.getType().equals("CLOUD")) {
							executionTime /= propertyProvider.getNumberOfAvailableInstances(resource);
						}
					}
				}
				
				// iterative tasks
				String taskType = task.getType();
				if (taskType != null) {
					if (task.getType().equals("ITERATIVE_CARS")) {
						executionTime *= propertyProvider.getCarNumber();
					} else if (task.getType().equals("ITERATIVE_PEOPLE")) {
						executionTime *= propertyProvider.getNumberOfPeople();
					}
				}
				
				// finish time is start time (finish of previous task) + execution time current task
				task.setAttribute("finishTime", latestFinishTime + executionTime);
			}
		}
		
		// all tasks have finishTime
		double makespan = 0.0;
	
		// makespan is task with latest finish time
		for (Task task : tasks) {
			double finishTime = task.getAttribute("finishTime");
			
			if (finishTime > makespan) {
				makespan = finishTime;
			}
		}
		
		return makespan;
	}

	@Override
	public int getPriority() {
		return priority;
	}
}
