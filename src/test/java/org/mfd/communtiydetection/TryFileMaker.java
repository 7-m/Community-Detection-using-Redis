package org.mfd.communtiydetection;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
public class TryFileMaker {
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
		String pathname = System.getProperty("user.home")+"/Downloads/com-lj.ungraph.txt";
		
		int currNode = -1; //-1 acts as a dummy
		
		Scanner sc = new Scanner(new File(pathname));
		
		

		//use a hash to maintain a mapping from nodes to its neighbours
		HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
		
		System.out.println(sc.nextLine());
		sc.nextLine();
		sc.nextLine();
		sc.nextLine();
		
		
		map.put(currNode, new ArrayList<>());//hard code the first dummy value
		
		
		while (sc.hasNextLine()) {

			//parse the line into m and n
			int m = sc.nextInt();
			int n = sc.nextInt();
			//System.out.println(++count);

			//if its the next node in the list 
			if (m != currNode) {

				//write the node to a new file or redis right away
				
				//do something with the value
				
				

				//then remove  it
				map.get(currNode).clear();
				map.remove(currNode);
				
				currNode = m;//update the current node to the new node
				
				//this will cut down checking for m again and again
				map.putIfAbsent(m, new ArrayList<>());

			}
			//add n as neigh of m
			
			
			map.get(m).add(n);
			
			
			map.putIfAbsent(n, new ArrayList<>());
			//add m as neigh of n
			map.get(n).add(m);
			

		}
		sc.close();

	}

	private static int[] makeIntArray(List<Integer> listOfIntObj) {
		int[] intArray = new int[listOfIntObj.size()];
		for (int i = 0; i < listOfIntObj.size(); i++)
			intArray[i] = listOfIntObj.get(i);
		return intArray;
	}

	/*private static int[] add(int[] arr, int num) {
		int[] narr = Arrays.copyOf(arr, arr.length + 1);
		narr[arr.length] = num;
		return narr;
	}*/
}
