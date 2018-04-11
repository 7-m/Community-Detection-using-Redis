package org.mfd.communtiydetection;

import java.util.ArrayList;
import java.util.Iterator;

public class NodeMaker {

	public static Node[] makeNodes(Iterator<org.graphstream.graph.Node> gNodes) {
		ArrayList<Node> nodes = new ArrayList<>();
		ArrayList<org.graphstream.graph.Node> gNodelist = iteratorToList(gNodes);

		for (org.graphstream.graph.Node gNode : gNodelist) {
			nodes.add(new Node(gNode.getIndex(), gNode.getIndex()));
		}
		Node[] nodeArray = new Node[gNodelist.size()];
		nodes.toArray(nodeArray);
		assert nodes.size() > 0;

		ArrayList<Integer> tmpNeighs = new ArrayList<>();

		for (int i = 0; i < nodeArray.length; i++) {
			/*loop over every gnode, make an integer list of neighbours, store it*/
			for (org.graphstream.graph.Node gneigh : iterWrapper(gNodelist.get(i).getNeighborNodeIterator()))
				tmpNeighs.add(gneigh.getIndex());

			Integer[] tmpInteger = new Integer[tmpNeighs.size()];
			tmpNeighs.toArray(tmpInteger);
			int[] neighArray = IntegerToint(tmpInteger);
			nodeArray[i].setNieghbours(neighArray);
			tmpNeighs.clear();

		}

		return nodeArray;

	}

	static ArrayList<org.graphstream.graph.Node> iteratorToList(Iterator<org.graphstream.graph.Node> gNodes) {
		ArrayList<org.graphstream.graph.Node> list = new ArrayList<>();
		while (gNodes.hasNext()) {
			list.add(gNodes.next());

		}

		return list;

	}

	static <T> Iterable<T> iterWrapper(Iterator<T> it) {
		Iterable<T> itble = new Iterable<T>() {

			@Override
			public Iterator<T> iterator() {
				// TODO Auto-generated method stub
				return it;
			}
		};
		return itble;

	}

	static int[] IntegerToint(Integer[] Ints) {

		assert Ints.length > 0;

		int[] ints = new int[Ints.length];
		for (int i = 0; i < Ints.length; i++)
			ints[i] = Ints[i].intValue();

		return ints;

	}

}
