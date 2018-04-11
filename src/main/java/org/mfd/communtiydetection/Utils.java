package org.mfd.communtiydetection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

public class Utils {

	static public int getRandIndex(int inclusiveUpperBound) {
		return (int) Math.round(Math.random() * (inclusiveUpperBound));
	}

	static public void shuffle(int[] nodes) {
		int length = nodes.length;
		for (int i = 0; i < length; i++) {
			//traverse the array and randomly swap positions
			int rand = getRandIndex(length - 1);
			int tmp = nodes[rand];
			nodes[rand] = nodes[i];
			nodes[i] = tmp;

		}

	}

	static public List<int[]> partition(int[] nodes, int noOfParts) {

		List<int[]> nodesList = new ArrayList<>();
		shuffle(nodes);
		int sizeOfEachList = (int) Math.ceil((float) nodes.length / noOfParts);

		int curr = 0;
		while (curr + sizeOfEachList < nodes.length) {// make sure to leave out the last element for
			nodesList.add(Arrays.copyOfRange(nodes, curr, curr + sizeOfEachList));
			curr += sizeOfEachList;
		}

		nodesList.add(Arrays.copyOfRange(nodes, curr, nodes.length));
		return nodesList;
	}

	/**
	 * Splits the given node list into noOfParts
	 * 
	 * @param nodes
	 * @param noOfParts
	 * @return List<List<Node>> where size() = noOfParts
	 */
	static public List<List<Node>> partitionForListNodes(List<Node> nodes, int noOfParts) {
		Collections.shuffle(nodes);

		List<List<Node>> nodesList = new ArrayList<>();
		//Collections.shuffle(nodes);
		int sizeOfEachList = (int) Math.ceil((float) nodes.size() / noOfParts);

		int curr = 0;
		while (curr + sizeOfEachList < nodes.size()) {// make sure to leave out the last element for
			//nodesList.add(Arrays.copyOfRange(nodes, curr, curr + sizeOfEachList));
			nodesList.add(nodes.subList(curr, curr + sizeOfEachList));
			curr += sizeOfEachList;
		}

		//nodesList.add(Arrays.copyOfRange(nodes, curr, nodes.length));
		nodesList.add(nodes.subList(curr, nodes.size()));
		return nodesList;
	}

	public static void logI(String message) {

		System.err.println("[Logger=INFO]: " + message);
	}

	public static void logE(String message) {
		System.err.println("[Logger=ERROR]: " + message);
	}

	public static void writeNodesToFile(int[] nodes, NodeHandle nh, String filePath) {
		try {
			BufferedWriter bw = new BufferedWriter(
					new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filePath)))));
			bw.write("*Vertices " + nodes.length);

			int nodesWritten = 0;

			for (int i : nodes) {
				Node n = nh.getNode(i);
				int label = n.getLabel();
				bw.write(i + " \"" + label + "\"\n");
				nodesWritten++;

			}

			System.out.println("Wrote a total of " + nodesWritten);
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);//not handling as ioexception cause of null pointer exceptions and others which are passed onto the thread handled by 
			//the threadExecutorService, this causes the exception to get masked unless Future#get() is called on the task

		}

	}

	//quick function to check wether the file can be create and written to. Saves a lot of last moment headaches
	public static boolean testOutputFile(String pathName) {
		File f = new File(pathName);
		try {
			return f.createNewFile();
		} catch (IOException e) {

			return false;
		}
	}

	/*public static int[] initNodes(NodeHandle nh, String nodeFilePath) {
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
	
		//RedisHandle rh = new RedisHandle(cli);
	
		nodes = new int[n.length];
		for (int i = 0; i < n.length; i++)
			nodes[i] = n[i].getIn();
	
		loadRedis(n, nh);
		System.out.println("readiy");
		return nodes;
	
	}
	
	private static void loadRedis(Node[] nodes, NodeHandle nh) {
		for(Node n : nodes)
			nh.putNode(n);
		
	}*/

	public static int[] initNodes(NodeHandle nh, String nodeFilePath) {

		Scanner sc = null;

		try {
			sc = new Scanner(new File(nodeFilePath));
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();

		//to skip the first few lines
		System.out.println(sc.nextLine());
		sc.nextLine();
		sc.nextLine();
		sc.nextLine();

		while (sc.hasNextInt()) {

			//parse the line into m and n
			int m = sc.nextInt();
			int n = sc.nextInt();
			//System.out.println(++count);

			//if its the next node in the list 

			map.putIfAbsent(m, new ArrayList<>());
			map.get(m).add(n);

			//add n as neigh of m

			map.putIfAbsent(n, new ArrayList<>());
			//add m as neigh of n
			map.get(n).add(m);

		}
		sc.close();

		ArrayList<Integer> nodes = new ArrayList<>();
		for (Entry<Integer, ArrayList<Integer>> entry : map.entrySet()) {
			int in = entry.getKey();
			int[] neighs = makeIntArray(entry.getValue());
			nh.putNode(new Node(in, in, neighs));

			nodes.add(in);

		}

		System.out.println("Nodes Uploaded to Redis. total " + nodes.size());
		return makeIntArray(nodes);

	}

	private static int[] makeIntArray(List<Integer> listOfIntObj) {
		int[] intArray = new int[listOfIntObj.size()];
		for (int i = 0; i < listOfIntObj.size(); i++)
			intArray[i] = listOfIntObj.get(i);
		return intArray;
	}

}
