package org.mfd.communtiydetection;

import com.lambdaworks.redis.cluster.api.sync.RedisClusterCommands;

/*
 * 
 * 
 * A wrapper class to make reading and writing nodes a peaceful
 * affair.
 */
public class RedisHandle implements NodeHandle {

	
	protected RedisClusterCommands<Integer, Node> commands;

	public RedisHandle(RedisClusterCommands<Integer, Node> commands) {
		
		this.commands = commands;
	}

	@Override
	public Node getNode(int index) {
		//Utils.log("RedisHandle.java: Fetching node : "+index);
		Node node = commands.get(index);
		//to prevent null bombs from taking down the system
		//just create an isolated node
		if (node == null) {
			Utils.logE("RedisHandle.java : Null node of index " +index);
			node = new Node(index, index, new int[0]);
		}
		return node;
	}

	@Override
	public void putNode(Node node) {
		assert node != null;
		commands.set(node.getIn(), node);

	}

}
