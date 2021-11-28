package at.uibk.dps.dsB.task2.part1;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;

/**
 * The {@link SpecificationGenerator} generates the {@link Specification}
 * modeling the orchestration of the customer modeling application discussed in
 * Lecture 1.
 * 
 * @author Fedor Smirnov
 */
public final class SpecificationGenerator {

	private SpecificationGenerator() {
	}

	/**
	 * Generates the specification modeling the orchestration of the customer
	 * monitoring application.
	 * 
	 * @return the specification modeling the orchestration of the customer
	 *         monitoring application
	 */
	public static Specification generate() {
		Application<Task, Dependency> appl = generateApplication();
		Architecture<Resource, Link> arch = generateArchitecture();
		Mappings<Task, Resource> mappings = generateMappings(arch, appl);
		return new Specification(appl, arch, mappings);
	}

	/**
	 * Generates the application graph
	 * 
	 * @return the application graph
	 */
	private static Application<Task, Dependency> generateApplication() {
		Application<Task, Dependency> application = new Application<Task, Dependency>();
		
		// create vertices
		Task t0 = new Task("t0");
		Task t1 = new Task("t1");
		Task t2 = new Task("t2");
		Task t3 = new Task("t3");
		Task t4 = new Task("t4");
		Task t5 = new Task("t5");
		Communication c0 = new Communication("c0");
		Communication c1 = new Communication("c1");
		Communication c2 = new Communication("c2");
		Communication c3 = new Communication("c3");
		Communication c4 = new Communication("c4");
		
		// add vertices
		application.addVertex(t0);
		application.addVertex(t1);
		application.addVertex(t2);
		application.addVertex(t3);
		application.addVertex(t4);
		application.addVertex(t5);
		application.addVertex(c0);
		application.addVertex(c1);
		application.addVertex(c2);
		application.addVertex(c3);
		application.addVertex(c4);
		
		// add dependencies
		application.addEdge(new Dependency("d0"), t0, c0);
		application.addEdge(new Dependency("d1"), c0, t1);
		application.addEdge(new Dependency("d2"), t1, c1);
		application.addEdge(new Dependency("d3"), t1, c2);
		application.addEdge(new Dependency("d4"), c1, t2);
		application.addEdge(new Dependency("d5"), c2, t3);
		application.addEdge(new Dependency("d6"), t2, c3);
		application.addEdge(new Dependency("d7"), t3, c4);
		application.addEdge(new Dependency("d8"), c3, t4);
		application.addEdge(new Dependency("d9"), c3, t5);
		application.addEdge(new Dependency("d10"), c4, t4);
		application.addEdge(new Dependency("d11"), c4, t5);
		
		return application;
	}

	/**
	 * Generates the architecture graph
	 * 
	 * @return the architecture graph
	 */
	private static Architecture<Resource, Link> generateArchitecture() {
		Architecture<Resource, Link> architecture = new Architecture<Resource, Link>();
		
		// create resources
		Resource r0 = new Resource ("r0");
		r0.setAttribute("costs", 5);
		Resource r1 = new Resource ("r1");
		r1.setAttribute("costs", 10);
		Resource r2 = new Resource ("r2");
		r2.setAttribute("costs", 15);
		Resource r3 = new Resource ("r3");
		r3.setAttribute("costs", 1);
		Resource r4 = new Resource ("r4");
		r4.setAttribute("costs", 1);
		Resource r5 = new Resource ("r5");
		r5.setAttribute("costs", 1);
		Resource r6 = new Resource ("r6");
		r6.setAttribute("costs", 20);
		Resource r7 = new Resource ("r7");
		r7.setAttribute("costs", 30);
		
		// add resources
		architecture.addVertex(r0);
		architecture.addVertex(r1);
		architecture.addVertex(r2);
		architecture.addVertex(r3);
		architecture.addVertex(r4);
		architecture.addVertex(r5);
		architecture.addVertex(r6);
		architecture.addVertex(r7);
		
		// add connections
		architecture.addEdge(new Link("l0"), r0, r2);
		architecture.addEdge(new Link("l1"), r0, r2);
		architecture.addEdge(new Link("l2"), r1, r2);
		architecture.addEdge(new Link("l3"), r1, r2);
		architecture.addEdge(new Link("l4"), r3, r2);
		architecture.addEdge(new Link("l5"), r4, r2);
		architecture.addEdge(new Link("l6"), r5, r2);
		architecture.addEdge(new Link("l7"), r6, r2);
		architecture.addEdge(new Link("l8"), r7, r2);
		architecture.addEdge(new Link("l9"), r3, r4);
		architecture.addEdge(new Link("l10"), r3, r5);
		architecture.addEdge(new Link("l11"), r4, r5);
		
		return architecture;
	}

	/**
	 * Generates the mapping edges
	 * 
	 * @param arch the architecture graph
	 * @param appl the application graph
	 * @return the mapping edges
	 */
	private static Mappings<Task, Resource> generateMappings(Architecture<Resource, Link> arch,
			Application<Task, Dependency> appl) {
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		
		// create mappings
		// task 0
		Mapping<Task, Resource> m0 = new Mapping<Task, Resource>("m0", appl.getVertex("t0"), arch.getVertex("r0"));
		Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", appl.getVertex("t0"), arch.getVertex("r1"));
		// task 1
		Mapping<Task, Resource> m2 = new Mapping<Task, Resource>("m2", appl.getVertex("t1"), arch.getVertex("r1"));
		Mapping<Task, Resource> m3 = new Mapping<Task, Resource>("m3", appl.getVertex("t1"), arch.getVertex("r2"));
		Mapping<Task, Resource> m4 = new Mapping<Task, Resource>("m4", appl.getVertex("t1"), arch.getVertex("r6"));
		Mapping<Task, Resource> m5 = new Mapping<Task, Resource>("m5", appl.getVertex("t1"), arch.getVertex("r7"));
		// task 2
		Mapping<Task, Resource> m6 = new Mapping<Task, Resource>("m6", appl.getVertex("t2"), arch.getVertex("r2"));
		Mapping<Task, Resource> m7 = new Mapping<Task, Resource>("m7", appl.getVertex("t2"), arch.getVertex("r6"));
		Mapping<Task, Resource> m8 = new Mapping<Task, Resource>("m8", appl.getVertex("t2"), arch.getVertex("r7"));
		// task 3
		Mapping<Task, Resource> m9 = new Mapping<Task, Resource>("m9", appl.getVertex("t3"), arch.getVertex("r6"));
		Mapping<Task, Resource> m10 = new Mapping<Task, Resource>("m10", appl.getVertex("t3"), arch.getVertex("r7"));
		// task 4
		Mapping<Task, Resource> m11 = new Mapping<Task, Resource>("m11", appl.getVertex("t4"), arch.getVertex("r3"));
		Mapping<Task, Resource> m12 = new Mapping<Task, Resource>("m12", appl.getVertex("t4"), arch.getVertex("r4"));
		Mapping<Task, Resource> m13 = new Mapping<Task, Resource>("m13", appl.getVertex("t4"), arch.getVertex("r5"));
		// task 5
		Mapping<Task, Resource> m14 = new Mapping<Task, Resource>("m14", appl.getVertex("t5"), arch.getVertex("r3"));
		Mapping<Task, Resource> m15 = new Mapping<Task, Resource>("m15", appl.getVertex("t5"), arch.getVertex("r4"));
		Mapping<Task, Resource> m16 = new Mapping<Task, Resource>("m16", appl.getVertex("t5"), arch.getVertex("r5"));
		
		// add mappings
		mappings.add(m0);
		mappings.add(m1);
		mappings.add(m2);
		mappings.add(m3);
		mappings.add(m4);
		mappings.add(m5);
		mappings.add(m6);
		mappings.add(m7);
		mappings.add(m8);
		mappings.add(m9);
		mappings.add(m10);
		mappings.add(m11);
		mappings.add(m12);
		mappings.add(m13);
		mappings.add(m14);
		mappings.add(m15);
		mappings.add(m16);
		
		return mappings;
	}

}
