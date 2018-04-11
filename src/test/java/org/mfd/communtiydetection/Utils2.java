package org.mfd.communtiydetection;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Utils2 {
	static int[] loadRedis(String path, RedisHandle rh) throws FileNotFoundException {

		/*
		 * 
		 * The problem is that the input file accounts only for edges and not for neighbors i.e every line is an edge
		 * and not the neighbors of the file. Hence we send the file through 2 passes to construct the entire graph
		 * */

		//File nodeFile=new File(path);
		Scanner sc = new Scanner(new File(path));
		List<Integer> neighs = new ArrayList<Integer>(), nodes = new ArrayList<>();
		
		//skip the beginning few lines with comments
		sc.nextLine();
		sc.nextLine();
		sc.nextLine();
		sc.nextLine();

		int currNode = 0;
		while (sc.hasNextInt()) {
			int nodeIndex = sc.nextInt();
			int neigh = sc.nextInt();

			if (nodeIndex != currNode) {

				//create a new node set it up with neighbor and index and carry on
				Node node = new Node(nodeIndex, nodeIndex, makeIntArray(neighs));
				rh.putNode(node);

				nodes.add(nodeIndex);

				//update state variables for next node
				neighs.clear();
				currNode = nodeIndex;

			}
			neighs.add(neigh);

		}
		sc.close();
		
		System.out.println("BAM!");
		//open he scanner again for the second pass
		sc = new Scanner(new File(path));
		
		sc.nextLine();
		sc.nextLine();
		sc.nextLine();
		sc.nextLine();
		
		
		while (sc.hasNextInt()) {
			int field1 = sc.nextInt();
			int field2 = sc.nextInt();

			Node nodeByIndex = rh.getNode(field2);
			if (nodeByIndex == null) {
				int tmp[] = { field1 };
				rh.putNode(new Node(field2, field2, tmp));
			} else
				rh.putNode(new Node(nodeByIndex.getIn(), nodeByIndex.getLabel(),
						add(nodeByIndex.getNeighbours(), field1)));

		}
		sc.close();

		return makeIntArray(nodes);

	}

	private static int[] makeIntArray(List<Integer> neighs) {
		int[] intArray = new int[neighs.size()];
		for (int i = 0; i < neighs.size(); i++)
			intArray[i] = neighs.get(i);
		return intArray;
	}

	private static int[] add(int[] arr, int num) {
		int[] narr = Arrays.copyOf(arr, arr.length + 1);
		narr[arr.length] = num;
		return narr;
	}

}
