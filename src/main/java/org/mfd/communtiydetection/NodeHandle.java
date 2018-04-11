package org.mfd.communtiydetection;

/**
 * Interface for decoupling writing and reading of nodes to service.
 * @author mfd
 *
 */
public interface NodeHandle {
	/**
	 * Fetches the node identified by index.
	 * @param index
	 * @return node
	 */
	Node getNode(int index);
	
	/**
	 * Writes the node to the underlying implementation, nodes are updated immediately.
	 * @param node
	 */
	void putNode(Node node);
}