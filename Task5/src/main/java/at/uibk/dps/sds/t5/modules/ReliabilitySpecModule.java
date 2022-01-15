package at.uibk.dps.sds.t5.modules;

import org.opt4j.core.config.annotations.Info;
import org.opt4j.core.config.annotations.Order;
import org.opt4j.core.start.Constant;

import at.uibk.dps.sds.t5.constraints.MappingEncoding;
import at.uibk.dps.sds.t5.specgenerator.SpecificationGenerator;
import net.sf.opendse.optimization.SpecificationWrapper;
import net.sf.opendse.optimization.io.IOModule;

public class ReliabilitySpecModule extends IOModule {

	@Order(1)
	@Info("Number of application functions, i.e., disconnected graph parts.")
	@Constant(value = "functionNumber", namespace = SpecificationGenerator.class)
	public int functionNumber = 5;

	@Order(2)
	@Info("Minimum length of a function (the depth of the graph).")
	@Constant(value = "minFuncLength", namespace = SpecificationGenerator.class)
	public int minFunctionLength = 1;

	@Order(3)
	@Info("Maximum length of a function (the depth of the graph).")
	@Constant(value = "maxFuncLength", namespace = SpecificationGenerator.class)
	public int maxFunctionLength = 3;

	@Order(4)
	@Info("Maximum number of successors that one task has.")
	@Constant(value = "maxNumSucc", namespace = SpecificationGenerator.class)
	public int maxNumSucc = 3;

	@Order(5)
	@Info("The probability that a task will be assigned as scret.")
	@Constant(value = "secrecyProb", namespace = SpecificationGenerator.class)
	public double secrecyProbability = .5;

	@Order(6)
	@Info("The number of edge clusters.")
	@Constant(value = "numEdgeClusters", namespace = SpecificationGenerator.class)
	public int numEdgeClusters = 3;

	@Order(7)
	@Info("The number of cloud clusters.")
	@Constant(value = "numCloudClusters", namespace = SpecificationGenerator.class)
	public int numCloudClusters = 3;

	@Order(8)
	@Info("Maximum number of resources per edge cluster")
	@Constant(value = "maxNumResEdge", namespace = SpecificationGenerator.class)
	public int maxEdgeResPerCluster = 5;

	@Order(9)
	@Info("Maximum number of resources per cloud cluster")
	@Constant(value = "maxNumResCloud", namespace = SpecificationGenerator.class)
	public int maxCloudResPerCluster = 5;

	@Order(10)
	@Info("Mean of the Mttf of each resource [h]")
	@Constant(value = "mttfMeanRes", namespace = SpecificationGenerator.class)
	public double mttfMeanResource = 24;
	@Order(11)
	@Info("Deviation of the Mttf of each resource [h]")
	@Constant(value = "mttfDevRes", namespace = SpecificationGenerator.class)
	public double mttfDevResource = 2;
	@Order(12)
	@Info("Mean of the Mttf of each task [h]")
	@Constant(value = "mttfMeanTask", namespace = SpecificationGenerator.class)
	public double mttfMeanTask = 24;
	@Order(13)
	@Info("Deviation of the Mttf of each task [h]")
	@Constant(value = "mttfDevTask", namespace = SpecificationGenerator.class)
	public double mttfDevTask = 2;
	@Order(14)
	@Info("Mean of the Mttf of each link [h]")
	@Constant(value = "mttfMeanLink", namespace = SpecificationGenerator.class)
	public double mttfMeanLink = 24;
	@Order(15)
	@Info("Deviation of the Mttf of each link [h]")
	@Constant(value = "mttfDevLink", namespace = SpecificationGenerator.class)
	public double mttfDevLink = 2;

	@Order(16)
	@Info("The maximal number of tasks that can be placed onto the same resource")
	@Constant(value = "maxTaskPerRes", namespace = MappingEncoding.class)
	public int maxTasksPerRes = 2;

	public int getMaxTasksPerRes() {
		return maxTasksPerRes;
	}

	public void setMaxTasksPerRes(int maxTasksPerRes) {
		this.maxTasksPerRes = maxTasksPerRes;
	}

	public int getFunctionNumber() {
		return functionNumber;
	}

	public void setFunctionNumber(int functionNumber) {
		this.functionNumber = functionNumber;
	}

	public int getMinFunctionLength() {
		return minFunctionLength;
	}

	public void setMinFunctionLength(int minFunctionLength) {
		this.minFunctionLength = minFunctionLength;
	}

	public int getMaxFunctionLength() {
		return maxFunctionLength;
	}

	public void setMaxFunctionLength(int maxFunctionLength) {
		this.maxFunctionLength = maxFunctionLength;
	}

	public int getMaxNumSucc() {
		return maxNumSucc;
	}

	public void setMaxNumSucc(int maxNumSucc) {
		this.maxNumSucc = maxNumSucc;
	}

	public double getSecrecyProbability() {
		return secrecyProbability;
	}

	public void setSecrecyProbability(double secrecyProbability) {
		this.secrecyProbability = secrecyProbability;
	}

	public int getNumEdgeClusters() {
		return numEdgeClusters;
	}

	public void setNumEdgeClusters(int numEdgeClusters) {
		this.numEdgeClusters = numEdgeClusters;
	}

	public int getNumCloudClusters() {
		return numCloudClusters;
	}

	public void setNumCloudClusters(int numCloudClusters) {
		this.numCloudClusters = numCloudClusters;
	}

	public int getMaxEdgeResPerCluster() {
		return maxEdgeResPerCluster;
	}

	public void setMaxEdgeResPerCluster(int maxEdgeResPerCluster) {
		this.maxEdgeResPerCluster = maxEdgeResPerCluster;
	}

	public int getMaxCloudResPerCluster() {
		return maxCloudResPerCluster;
	}

	public void setMaxCloudResPerCluster(int maxCloudResPerCluster) {
		this.maxCloudResPerCluster = maxCloudResPerCluster;
	}

	public double getMttfMeanResource() {
		return mttfMeanResource;
	}

	public void setMttfMeanResource(double mttfMeanResource) {
		this.mttfMeanResource = mttfMeanResource;
	}

	public double getMttfDevResource() {
		return mttfDevResource;
	}

	public void setMttfDevResource(double mttfDevResource) {
		this.mttfDevResource = mttfDevResource;
	}

	public double getMttfMeanTask() {
		return mttfMeanTask;
	}

	public void setMttfMeanTask(double mttfMeanTask) {
		this.mttfMeanTask = mttfMeanTask;
	}

	public double getMttfDevTask() {
		return mttfDevTask;
	}

	public void setMttfDevTask(double mttfDevTask) {
		this.mttfDevTask = mttfDevTask;
	}

	public double getMttfMeanLink() {
		return mttfMeanLink;
	}

	public void setMttfMeanLink(double mttfMeanLink) {
		this.mttfMeanLink = mttfMeanLink;
	}

	public double getMttfDevLink() {
		return mttfDevLink;
	}

	public void setMttfDevLink(double mttfDevLink) {
		this.mttfDevLink = mttfDevLink;
	}

	@Override
	protected void config() {
		bind(SpecificationWrapper.class).to(SpecificationGenerator.class);
	}
}
