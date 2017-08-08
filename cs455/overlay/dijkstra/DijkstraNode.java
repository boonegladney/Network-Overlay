package cs455.overlay.dijkstra;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;

public class DijkstraNode{
	private String identifier;
	private ArrayList<DijkstraEdge> adjacentNodes;
	private int distanceFromSource;
	private LinkedList<String> path;
	
	public DijkstraNode(String identifier)
	{
		this.identifier = identifier;
		adjacentNodes = new ArrayList<DijkstraEdge>();
		distanceFromSource = Integer.MAX_VALUE;
		path = new LinkedList<String>();
	}
	
	//MUTATOR METHODS
	
	public void setPath(LinkedList<String> ShortestPath)
	{
		path = ShortestPath;
	}
	
	public void updateDistance(int newDistance)
	{
		distanceFromSource = newDistance;
	}
	
	public void addNewEdge(DijkstraEdge newEdge)
	{
		adjacentNodes.add(newEdge);
	}
	
	//ACCESSOR METHODS
	
	public LinkedList<String> getShortestPath()
	{
		return path;
	}
	
	public String getIdentifier()
	{
		return identifier;
	}
	
	public ArrayList<DijkstraEdge> getEdges()
	{
		return adjacentNodes;
	}
	
	public int getDistance()
	{
		return distanceFromSource;
	}

}
