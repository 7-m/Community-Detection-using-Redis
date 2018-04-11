package org.mfd.communtiydetection.clientside;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.mfd.communtiydetection.Node;
import org.mfd.communtiydetection.Utils;

/**
 * 
 * Does the hard work of detecting communities by updating the nodes labels. All
 * changes done are reflected i <code>nodes</code>.
 * 
 * @author mfd
 *
 */

public class DetectionWorker implements Callable<Integer> {

	private List<Node> nodes;
	private NodeNeighbors nodeNeighbors;

	int totalUpdated = 0;

	public DetectionWorker(NodeNeighbors cache, List<Node> nodes) {

		this.nodeNeighbors = cache;
		this.nodes = nodes;
	}

	@Override
	public Integer call() {
		//reset the totalupdated
		totalUpdated = 0;

		//boolean nodesChanged = false;
		while (runIteration() > 0) {
			//nodesChanged = true;

		}
		/*** print the nodes before writing ****************/

		//now we'll return the total number of updated nodes instead of just true or false
		Utils.logI("DetectionWorker.java : updated a total of : " + totalUpdated);
		/*if (nodesChanged)
			cache.writeCache();*/
		return totalUpdated;

	}

	private int runIteration() {
		int currUpdated = 0;
		//2) shuffle
		Collections.shuffle(nodes);

		//3) assign it to the most popular community among the neighbors
		for (Node node : nodes) {

			int maxLabel = getMaxNeighborLabelCount(node);

			if (node.getLabel() != maxLabel) {
				node.setLabel(maxLabel);
				currUpdated++;
			}

		}
		Utils.logI("Server.java : updated locally : " + currUpdated);
		totalUpdated += currUpdated;
		return currUpdated;

	}

	int getMaxNeighborLabelCount(Node n) {
		//TODO: Rewrite this using streams api, will look and run pretty sweet
		
		//enumerate all neighbors

		//map neighbor labels to their count
		Map<Integer, Integer> commCount = new HashMap<Integer, Integer>();

		for (Node neigh : nodeNeighbors.getNeighborsFor(n)) {
			//get counts of each node
			commCount.putIfAbsent(neigh.getLabel(), 0);// initialize the count
			//***************************************************************
			int intValue = commCount.get(neigh.getLabel()).intValue();
			commCount.put(neigh.getLabel(), intValue + 1); // fetch the count, increment and put it back
			//***************************************************************
		}

		//time to find the largest cardinality
		//use a linear search algorithm

		List<Integer> labels = new ArrayList<Integer>(commCount.keySet());
		int maxLabelCount = -1;

		for (Integer label : labels) {
			if (commCount.get(label) > maxLabelCount)
				maxLabelCount = commCount.get(label);
		}
		//heres the thing if  n has the largest label, then return itself, else return the last label in the key, hopefully
		//this is random enough, this will allow us to use updated nodes i.e the nodes that changed and hence allow us to get rid of verification()

		int maxLabel = -2;
		for (Integer label : labels) {
			if (commCount.get(label) == maxLabelCount) {
				if (label == n.getLabel())
					return n.getLabel();

				maxLabel = label;
			}
		}
		assert maxLabel != -2;
		return maxLabel;
	}

	//decouple getting nodes from the implementation.
	public interface NodeNeighbors {
		Node[] getNeighborsFor(Node n);
	}

}
