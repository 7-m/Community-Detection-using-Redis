package org.mfd.communtiydetection;

import java.io.Serializable;

public class Node implements Serializable {
	/**
	 * Leave the fetching of nodes and updating them to their users.
	 */
	private static final long serialVersionUID = 693395663705152568L;
	
	
	protected int label;
	protected int[] neighbours;
	final private int in;
	
	public Node(int in, int label) {
		this(in, label, null);
	}

	public Node(int in, int label, int[] nieghbours) {
		super();
		this.in = in;
		this.label = label;
		setNieghbours(nieghbours);
	}

	

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;

	}

	public int getIn() {
		return in;
	}

	public int[] getNeighbours() {
		return neighbours;
	}

	public void setNieghbours(int[] nieghbours) {
		this.neighbours = nieghbours;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "index :  " + in + " label: " + label;
	}
	

	@Override
	public int hashCode() {
		return in;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (in != other.in)
			return false;
		return true;
	}

	public String toStringWithNeighbors() {
		StringBuilder sb = new StringBuilder(toString());
		for (int n : neighbours)
			sb.append("\n\t").append(n);
		return sb.toString();

	}

}