package org.mfd.communtiydetection.serverside;
//prepares and supplies random nodes, its the responsibility of the server to stop it

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.mfd.communtiydetection.Communicator;
import org.mfd.communtiydetection.Message;
import org.mfd.communtiydetection.Message.MessageType;
import org.mfd.communtiydetection.Utils;

public class NodeSupplier implements Runnable {
	//make the communicator streams synchronized
	List<Communicator> clientCommunicators;
	int[] nodes;
	Count iterationCount;
	int supplyIterations = 0;//keeps a count for how many iteratons wrth of nodes were supplied, it should always be 2 more than iterationCount
	private boolean paused = false;

	public NodeSupplier(List<Communicator> clientCommunicators, int[] nodes, Count iterationCount) {

		this.clientCommunicators = clientCommunicators;
		this.nodes = nodes;
		this.iterationCount = iterationCount;
	}

	@Override
	public void run() {
		/*
		 * 
		 * just goes on creating new nodes and sending them, will cause network congestion, optimize 
		 * by sending only after end of fetching nodes by clients and the subquent fetching of nodes by clients
		 */
		while (true) {
			try {
				Thread.sleep(200);//give it a break
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (paused || supplyIterations >= iterationCount.c + 2)//you just want to be 2 supply iterations ahead
				continue;//if paused then dont supply nodes continue waiting

			Iterator<Communicator> itr = clientCommunicators.iterator();
			for (int[] workNode : Utils.partition(nodes, clientCommunicators.size())) {
				try {
					itr.next().send(new Message(MessageType.NODESET, workNode));
					Utils.logI("NodeSupplier.java: Set size is "+workNode.length);
				} catch (IOException e) {

					e.printStackTrace();
				}

			}
			supplyIterations++;
		}

	}

	/**
	 * Pauses the the supplier as soon as possible by finishing the current node
	 * supply round.
	 */
	void pause() {
		paused = true;
	}

	/**
	 * Resumes the supplying of nodes to the clients.
	 */
	void resume() {
		paused = false;
	}

}
