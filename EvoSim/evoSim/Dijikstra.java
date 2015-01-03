package evoSim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dijikstra {
	private final List<Hex> hexList;
	private final List<Edge> edgeList;
	private Set<Hex> settledHexs;
	private Set<Hex> unSettledHexs;
	private Map<Hex,Hex> predecessors;
	private Map<Hex,Integer> distance;
	
	public Dijikstra(HexMap testMap){
		hexList = testMap.hexList;
		edgeList = testMap.edgeList;
	}
	
	public void run(Hex source){
		settledHexs = new HashSet<Hex>();
		unSettledHexs = new HashSet<Hex>();
		distance = new HashMap<Hex,Integer>();
		predecessors = new HashMap<Hex, Hex>();
		distance.put(source, 0);
		unSettledHexs.add(source);
		while (unSettledHexs.size() > 0){
			Hex node = getMin(unSettledHexs);
			settledHexs.add(node);
			unSettledHexs.remove(node);
			findMinimalDistances(node);
		}
	}
	
	public void findMinimalDistances(Hex node){
		List<Hex> adjacentHexs = this.getNeighbors(node);
		for(Hex target : adjacentHexs){
			if (getShortestDistance(target) > getShortestDistance(node)
			          + getDistance(node, target)) {
			        distance.put(target, getShortestDistance(node)
			            + getDistance(node, target));
			        predecessors.put(target, node);
			        unSettledHexs.add(target);
			      }
		}
		
	}
	
	private int getDistance(Hex node, Hex target) {
	    for (Edge edge : this.edgeList) {
	      if (edge.getSource().equals(node)
	          && edge.getDestination().equals(target)) {
	        return edge.getDist();
	      }
	    }
	    throw new RuntimeException("Should not happen");
	  }
	
	private List<Hex> getNeighbors(Hex node) {
	    List<Hex> neighbors = new ArrayList<Hex>();
	    for (Edge edge : this.edgeList) {
	      if (edge.getSource().equals(node)
	          && !isSettled(edge.getDestination())) {
	        neighbors.add(edge.getDestination());
	      }
	    }
	    return neighbors;
	  }
	
	private Hex getMin(Set<Hex> vertexes) {
		Hex minimum = null;
	    for (Hex vertex : vertexes) {
	      if (minimum == null) {
	        minimum = vertex;
	      } else {
	        if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
	          minimum = vertex;
	        }
	      }
	    }
	    return minimum;
	  }
	
	private boolean isSettled(Hex vertex) {
	    return settledHexs.contains(vertex);
	  }
	
	private int getShortestDistance(Hex destination) {
	    Integer d = distance.get(destination);
	    if (d == null) {
	      return Integer.MAX_VALUE;
	    } else {
	      return d;
	    }
	  }
	
	public LinkedList<Hex> getPath(Hex target) {
	    
		LinkedList<Hex> path = new LinkedList<Hex>();
	    Hex step = target;

	    path.add(step);
	    while (predecessors.get(step) != null) {
	      step = predecessors.get(step);
	      path.add(step);
	    }
	    // Put it into the correct order
	    Collections.reverse(path);

	    return path;
	  }
	
	public LinkedList<Hex> getPath(Hex source, Hex target) {
	    this.run(source);
		LinkedList<Hex> path = new LinkedList<Hex>();
	    Hex step = target;

	    path.add(step);
	    while (predecessors.get(step) != null) {
	      step = predecessors.get(step);
	      path.add(step);
	    }
	    // Put it into the correct order
	    Collections.reverse(path);

	    return path;
	  }
}
