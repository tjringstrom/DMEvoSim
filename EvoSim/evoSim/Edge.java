package evoSim;

public class Edge {
	private Hex sourceHex;
	private Hex destHex;
	private double weight;
	private int dist;

	public Edge(Hex source, Hex dest, double weight, int dist) {
		this.sourceHex = source;
		this.destHex = dest;
		this.weight = weight;
		this.dist = dist;
	}

	public Hex getDestination() {
		return destHex;
	}

	public Hex getSource() {
		return sourceHex;
	}

	public double getWeight() {
		return weight;
	}

	public int getDist() {
		return dist;
	}

}
