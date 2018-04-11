package org.mfd.communtiydetection;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/*
 * convert a file of the following form 
 * 0 1
 * 0 77
 * 0 87
 * 0 88
 * .
 * .
 * .
 * 
 * to :
 * 0 1 77 87 88 . . .
 * .
 * .
 * .
 */
public class TryFileMaker2 {
	public static void main(String[] args) throws InterruptedException, FileNotFoundException {
		/*
		 * Heres the algorithm,
		 * for the file of the following form
		 * m n
		 * 
		 * 	1)add n as neighbor of m
		 * 	2)add m as neighbour of n
		 * 	3) when going to the next node o do:
		 * 		1)write the entries of node m
		 * 		2) delete m
		 */
		String pathname = System.getProperty("user.home")+"/Downloads/com-orkut.ungraph.txt";
		///System.out.println(System.getProperty("user.home"));
		int currNode = 1;//the current node
		
		Scanner sc = new Scanner(new File(pathname));

		//use a hash to maintain a mapping from nodes to its neighbours
		ArrayList<ArrayList<Integer>> map=new ArrayList<>(3_072_441);
		System.out.println(sc.nextLine());
		sc.nextLine();
		sc.nextLine();
		sc.nextLine();
		
		map.add(1, new ArrayList<>());// hard code it for the first one

		while (sc.hasNextLine()) {

			//parse the line into m and n
			int m = sc.nextInt();
			int n = sc.nextInt();

			//if its the next node in the list 
			if (m != currNode) {

				//write the node to a new file
				ArrayList<Integer> neighs = map.get(currNode);
				//do whatever with the node
				System.out.println(currNode + " :: " +neighs);
				Thread.sleep(1000*2);

				//later remove the node
				map.get(currNode).clear();
				//map.remove(currNode);//removing is a costly operation and will mess up the order, instead just make the space null
				map.set(currNode, null);
				System.out.println(m);
				currNode = m;//update the current node to the new node
				//this will cut down checking for m again and again
				if (map.get(currNode) == null) 
					map.add(currNode, new ArrayList<>());

			}
			//add n as neigh of m
			
			
			map.get(m).add(n);
			
			//add m as neigh of n
			if (map.get(n) == null)
				map.add(n, new ArrayList<>());
			
			map.get(n).add(m);
			

		}

	}

	private static int[] makeIntArray(List<Integer> neighs) {
		int[] intArray = new int[neighs.size()];
		for (int i = 0; i < neighs.size(); i++)
			intArray[i] = neighs.get(i);
		return intArray;
	}

	/*private static int[] add(int[] arr, int num) {
		int[] narr = Arrays.copyOf(arr, arr.length + 1);
		narr[arr.length] = num;
		return narr;
	}*/
}
