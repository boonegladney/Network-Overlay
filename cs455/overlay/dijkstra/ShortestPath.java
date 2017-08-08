package cs455.overlay.dijkstra;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class ShortestPath {
	private DijkstraNode sourceNode;
	private Graph graph;
	private PriorityQueue<DijkstraNode> unvisitedNodes;
	private ArrayList<DijkstraNode> visitedNodes;
	
	public ShortestPath(Graph graph, DijkstraNode sourceNode)
	{
		this.graph = graph;
		this.sourceNode = sourceNode;
		Comparator<DijkstraNode> comparator = new NodeComparator();
		unvisitedNodes = new PriorityQueue(graph.getNumberOfNodes(), comparator);
		visitedNodes = new ArrayList<DijkstraNode>();
	}
	
	public boolean wasVisited(DijkstraNode node)
	{
		for(int i = 0; i < visitedNodes.size(); i++)
		{
			if(visitedNodes.get(i).getIdentifier().equals(node.getIdentifier()))
			{
				return true;
			}
		}
		return false;
	}
	
	public void calculateShortestPaths()
	{
		// set source node distance to 0
		sourceNode.updateDistance(0);
		
		// add all nodes to unvisited queue
		unvisitedNodes.add(sourceNode);
		
		
		while(!unvisitedNodes.isEmpty())
		{
			DijkstraNode current = unvisitedNodes.poll();
			
			ArrayList<DijkstraEdge> currentEdges = current.getEdges();
			for(int i = 0; i < currentEdges.size(); i++)
			{
				if(!wasVisited(currentEdges.get(i).getDestinationNode(current)) && !unvisitedNodes.contains(currentEdges.get(i).getDestinationNode(current)))
				{
					unvisitedNodes.add(currentEdges.get(i).getDestinationNode(current));
				}
			}
			
			for(int i = 0; i < currentEdges.size(); i++)
			{
				int tempDistance = current.getDistance() + currentEdges.get(i).getWeight();
				if(tempDistance < currentEdges.get(i).getDestinationNode(current).getDistance())
				{
					currentEdges.get(i).getDestinationNode(current).updateDistance(tempDistance);
					LinkedList<String> newShortestPath = new LinkedList<String>(current.getShortestPath());
					newShortestPath.add(current.getIdentifier());
					currentEdges.get(i).getDestinationNode(current).setPath(newShortestPath);
				}
			}
			visitedNodes.add(current);
		}
	}
	
	public LinkedList<String> getShortestPath(String destinationNode)
	{
		for(int i = 0; i < visitedNodes.size(); i++)
		{
			if(visitedNodes.get(i).getIdentifier().equals(destinationNode))
			{
				return visitedNodes.get(i).getShortestPath();
			}
		}
		System.out.println("ERROR: Could not get Shortest path....");
		return null;
	}
	
	public static void main(String[] args)
	{
		ArrayList<String> testString = new ArrayList<String>();
		testString.add("one two 1");
		testString.add("two three 9");
		testString.add("three four 3");
		testString.add("four five 5");
		testString.add("five six 2");
		testString.add("six seven 8");
		testString.add("seven eight 1");
		testString.add("eight nine 5");
		testString.add("nine ten 4");
		testString.add("ten one 6");
		testString.add("one three 4");
		testString.add("one four 6");
		testString.add("two four 8");
		testString.add("two five 3");
		testString.add("three five 2");
		testString.add("six eight 3");
		testString.add("six nine 9");
		testString.add("seven nine 6");
		testString.add("seven ten 7");
		testString.add("eight ten 1");
		
		
		Graph graph = new Graph(testString);
		ShortestPath sp = new ShortestPath(graph, graph.getNode("one"));
		sp.calculateShortestPaths();
		
		LinkedList<String> ls = sp.getShortestPath("six");
		int var = ls.size();
		for(int i = 0; i < var; i++)
		{
			System.out.print(ls.poll() + " ---> ");
		}
		System.out.print(graph.getNode("six").getIdentifier());
	}
}