package org.mfd.communtiydetection.clientside;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.mfd.communtiydetection.Node;
import org.mfd.communtiydetection.NodeHandle;
import org.mfd.communtiydetection.Utils;

/*This class is the interface between the client applicaion and the workers. You first intantiate
 * this class and then pass the set of nodes to be worked on.*/
public class WorkerManager {
	
	//for spawning RUNTIME_CPU_COUNT threads
	private static final int RUNTIME_CPU_COUNT = Runtime.getRuntime().availableProcessors();

	
	private List<Node> nodes;
	private int[] nodeNumbers;
	private NodeHandle nodeHandle;

	private Cache cache;
	private ExecutorService exs;

	public WorkerManager(NodeHandle nodeHandle, NodeSupplyControl nsc) {
		this.nodeHandle = nodeHandle;
		cache = new Cache(nsc);
		exs = Executors.newWorkStealingPool();
	}

	/**
	 * 
	 * @param intNodes
	 * @return the average no. of node updates.
	 */
	public int runFor(int[] intNodes) {

		this.nodeNumbers = intNodes;
		
		cache.load();

		
		//keeps record of the maximum no. of updates performed by a thread for the current iteration
		int maxUpdates = 0;
		/*time to use that executor service and put some threads to work
		 *run detections until all threads report zero node updates
		 *as it involves running iteration of the algorithm, the task shouldn't
		 *be cancelled in between as it may leave some nodes un-updated and hence the graph
		 *in an inconsistent state, hence allow all worker threads to complete execution
		 **/

		/*	algortihm:
		 * 1) split the nodes into parts depending on the no. pf cpus availible
		 * 2) assign the executor service the work
		 * 3) use invokeall()
		 * 4) return maxUpdates
		 */

		List<Callable<Integer>> workerTasks = new ArrayList<>();

		for (List<Node> workNodes : Utils.partitionForListNodes(nodes, RUNTIME_CPU_COUNT)) {
			workerTasks.add(new DetectionWorker(n -> cache.getNeighbors(n), workNodes));
		}

		try {
			//invoke all the tasks and check the results, even if a thread reports false, run another iteration
			NEXT_ITER: while (true) {

				List<Future<Integer>> results = exs.invokeAll(workerTasks);
				for (Future<Integer> result : results) {
					Integer updatedNodes = result.get();
					//TODO: try replacing this too with a factor, perhaps a percent
					if (updatedNodes > 0) {//if even a single node is updated, go for another iteration
						maxUpdates = updatedNodes > maxUpdates ? updatedNodes : maxUpdates;
						continue NEXT_ITER;

					}
				}

				break;//this code gets executed if no node updates were made, hence just fall out of the loop

			}
			cache.flush();

		} catch (InterruptedException | ExecutionException e) {

			e.printStackTrace();
		}

		Utils.logI("WorkerManager.java : Updated a greatest of " + maxUpdates);
		
		return maxUpdates;

	}

	public void stop() {
		exs.shutdown();
	}

	//todo: give the responsibility of setting up nodeslist to Workermanager instead of cache class
	class Cache {

		Map<Node, Node[]> nodeToNeighborsMap = new HashMap<>();
		Map<Integer, Node> nodeNumberToNodeMap = new HashMap<>();
		NodeSupplyControl nsc;

		public Cache(NodeSupplyControl nsc) {
			this.nsc = nsc;

		}

		public void flush() {
			//stop the supply and start it only after the next fetching of nodes 
			//cause after flush the next iteraton will begin and will involve fetching
			//of nodes
			nsc.stopSupply();
			for (Node node : nodeToNeighborsMap.keySet())
				nodeHandle.putNode(node);

		}

		public Node[] getNeighbors(Node node) {
			return nodeToNeighborsMap.get(node);

		}

		/*
		 * 
		 * First fetch the nodes, then fetch its neighbors
		 */
		public void load() {
			//clear the cache
			nodeNumberToNodeMap.clear();
			nodeToNeighborsMap.clear();

			//supply was already stopped by the previous iterations flush
			for (int nodeNumber : nodeNumbers) {
				Node node = nodeHandle.getNode(nodeNumber);
				nodeNumberToNodeMap.put(nodeNumber, node);
				nodeToNeighborsMap.put(node, null);
			}
			//now to fetch its neighbors
			// just loop over the nodes in the map and inner loop over its neighbors
			//and add the neighbor

			//fetch its neighbors and cache them

			for (Node node : nodeToNeighborsMap.keySet()) {
				int c = 0;
				Node[] neighs = new Node[node.getNeighbours().length];
				for (int neighborNumber : node.getNeighbours()) {

					if (nodeNumberToNodeMap.containsKey(neighborNumber))
						neighs[c++] = nodeNumberToNodeMap.get(neighborNumber);
					else
						neighs[c++] = nodeHandle.getNode(neighborNumber);

				}
				nodeToNeighborsMap.put(node, neighs);
			}
			// also assign Nodes in the enclosing class
			nodes = new ArrayList<>(nodeToNeighborsMap.keySet());
			nsc.startSupply();

		}

		public void printRedisNodes() {
			for (int n : nodeNumbers) {
				System.out.println(nodeHandle.getNode(n));
			}

		}

	}

	interface NodeSupplyControl {
		void startSupply();//an indication to start the supply of nodes

		void stopSupply();//an indication to stop the supply of nodes
	}
}
