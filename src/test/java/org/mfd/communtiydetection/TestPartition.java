package org.mfd.communtiydetection;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

public class TestPartition {

	public static void main(String[] args) {

		Graph g = new SingleGraph("SingleGraph", false, true);
		g.addEdge("a", "1", "2");
		g.addEdge("b", "2", "3");
		g.addEdge("c", "3", "4");
		g.addEdge("d", "4", "5");
		g.addEdge("e", "5", "6");
		g.addEdge("f", "6", "7");
		g.addEdge("g", "7", "8");
		g.addEdge("h", "6", "8");
		g.addEdge("i", "5", "9");
		g.addEdge("j", "8", "9");
		g.addEdge("k", "1", "6");

		Node[] n = NodeMaker.makeNodes(g.iterator());
		int[] intnodes = new int[n.length];
		for (int i = 0; i < n.length; i++)
			intnodes[i] = n[i].getIn();

		for (int ns : intnodes)
			System.out.println(ns);

		for (int[] parts : Utils.partition(intnodes, 10)) {
			System.out.println();
			for (int ns : parts) {
				System.out.print(", " + ns);
			}

		}
	}

}
