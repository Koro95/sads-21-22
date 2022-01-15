


## Task 5: "Optimizing the Reliability of an Application"

### Goals of the exercise:

* Implementing a reliability evaluator
* Learning to optimize reliability by using the _JReliability_ framework

### Part 0 - Setup

* Clone (or pull) the GitHub repository containing the exercise task
* Run the command _gradle build eclipse_ from within the _Task 5_ directory to build the project and set up the Eclipse workspace

### Part 1 - JReliability

* Read the [tutorial document](https://github.com/SDARG/jreliability/blob/master/files/tutorial.pdf) describing the functionality and the usage of the JReliability framework
* Make sure that you understand the code examples provided in the tutorial.

### Part 2 - Optimizing Reliability

#### Story

Although now secure, the applications used by the resistance struggle with reliability problems due to Distopistan's poor cloud-edge infrastructure. Bob tries to address this problem by implementing a reliability optimization.

### Use Case Description

*Specification*

Once again, Bob is relying on a specification generator to easily create different system specifications that he can use to test the reliability optimization (the specification generator is used by adding the _ReliabilitySpec_ module to the exploration).

*Evaluation - Reliability*

To enable an automatic reliability optimization, Bob needs a reliability evaluator capable of calculatnig the MTTF value of a given orchestration. Bob assumes the following failure model for resistance applications:

* The overall application is functional iff all of its tasks and all of its messages are functional
* A message is functional iff all resources and links which are part of the message's routing graph are functional
* A task is functional iff at least one of the resources that the task is mapped onto is functional (multi-mapping is allowed)
* Furthermore, to account for the low processing power of the available resources, Bob introduces a constraint on the number of tasks which can be placed on a single resource (this number can be adjusted using the _maxTasksPerResource_ parameter in the _ReliabilitySpec_ module)

### Task Notes

- Start by reviewing the [JReliability tutorial](https://github.com/SDARG/jreliability/blob/master/files/tutorial.pdf)  and writing a reliability evaluator. For the calculation of the reliability, it should consider **task placement** (in particular the question whether a task is mapped once or multiple times), **message routing**, and the **reliability of the resources and the communication links**.
- The reliability of the individual graph elements is specified by two values: The **mean** and the **deviation** of the *logarithmic MTTF* of the element (you should not need to directly access them, but you could do that using the provided *PropertyService*)    
- The starting code already features the skeletton of the realibility evaluator. Your task is to implement the function *generateStructureTerm* in the class *at.uibk.dps.sds.t5.reliability.StructureTermGenerator.class* which, based on the given implementation, generates the structure function which can be used to calculate the system reliability

### Task Objectives:

- Implement the reliability evaluator
- Run the optimization with the values given in the corresponding launch files. Experiment with different specifications and different settings for the maximal number of tasks per resource
- Discuss the use cases and their results (you should provide a short discussion document as part of your solution of this task):
  - Which setting results in higher reliability values? Does this make sense?
  - Does the optimization always find the optimal solutions? Which cases are particularly difficult?
