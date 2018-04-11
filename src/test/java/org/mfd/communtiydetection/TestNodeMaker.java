package org.mfd.communtiydetection;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

public class TestNodeMaker {
	public static void main(String[] args) throws InterruptedException {
		Graph g = new SingleGraph("SingleGraph", false, true);
		

		File nodeFile = new File("/home/mfd/Downloads/com-dblp.ungraph.txt");
		int nodes[];
		Scanner sc = null;
		try {
			sc = new Scanner(nodeFile);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		int c = 0;

		

		//skip the first three lines of orkut
		sc.nextLine();
		sc.nextLine();
		sc.nextLine();
		sc.nextLine();

		while (sc.hasNextInt()) {
			int nextInt = sc.nextInt();
			int nextInt2=sc.nextInt();
			g.addEdge(c++ + "", nextInt + "", nextInt2 + "");
			System.out.println(nextInt + " "+nextInt2 + "");
			Thread.sleep(1000);

		}
		sc.close();
		

		Node[] n = NodeMaker.makeNodes(g.iterator());
		for (Node nc : n) {
			System.out.println(nc.toStringWithNeighbors());
			Thread.sleep(1000);
		}

		
	}
	public static int[] initNodes(NodeHandle nh, String nodeFilePath) {
		File nodeFile = new File(nodeFilePath);
		int nodes[];
		Scanner sc = null;
		try {
			sc = new Scanner(nodeFile);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		int c = 0;

		Graph g = new SingleGraph("graph");
		g.setAutoCreate(true);
		g.setStrict(false);

		//skip the first three lines of orkut
		sc.nextLine();
		sc.nextLine();
		sc.nextLine();
		sc.nextLine();

		while (sc.hasNextInt()) {
			g.addEdge(c++ + "", sc.nextInt() + "", sc.nextInt() + "");

		}
		sc.close();

		/*Graph g = new SingleGraph("SingleGraph", false, true);
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
		g.addEdge("k", "1", "6");*/

		Node[] n = NodeMaker.makeNodes(g.iterator());

		//RedisHandle rh = new RedisHandle(cli);

		nodes = new int[n.length];
		for (int i = 0; i < n.length; i++)
			nodes[i] = n[i].getIn();

		//loadRedis(n, nh);
		System.out.println("readiy");
		return nodes;

	}
}
