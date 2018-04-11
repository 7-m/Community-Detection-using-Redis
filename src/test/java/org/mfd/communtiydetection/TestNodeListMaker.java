package org.mfd.communtiydetection;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

public class TestNodeListMaker {
	
	public static void main(String[] args) {
		String path="/home/mfd/Downloads/as-skitter.txt";
		initNodes(path);
		
	}
	public static int[] initNodes(String nodeFilePath) {

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
				
		
		ArrayList<Integer> nodes=new ArrayList<>();
		for (Entry<Integer, ArrayList<Integer>> entry : map.entrySet()) {
			int in=entry.getKey();
			int[] neighs=makeIntArray(entry.getValue());
			System.out.println(new Node(in, in, neighs).toStringWithNeighbors());
			try {
				Thread.sleep(1000*2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			nodes.add(in);
			
			
		}

		

		
		
		System.out.println("Nodes Uploaded to Redis. total "+nodes.size());
		return makeIntArray(nodes);

	}

	private static int[] makeIntArray(List<Integer> listOfIntObj) {
		int[] intArray = new int[listOfIntObj.size()];
		for (int i = 0; i < listOfIntObj.size(); i++)
			intArray[i] = listOfIntObj.get(i);
		return intArray;
	}
}
