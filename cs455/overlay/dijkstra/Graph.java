package cs455.overlay.dijkstra;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Graph {
	ArrayList<DijkstraNode> nodes;
	
	/**
	 * instantiates the graph by creating nodes and edges from
	 * the linkInfo array passed to it.
	 * @param linkInfo
	 */
	public Graph(ArrayList<String> linkInfo)
	{
		nodes = new ArrayList<DijkstraNode>();
		// create nodes and edges of graph from input array
		for(int i = 0; i < linkInfo.size(); i++)
		{
			// create new nodes if they don't already exist
			DijkstraNode node1;
			DijkstraNode node2;
			String[] infoLine = linkInfo.get(i).split(" ");
			if(!nodeExists(infoLine[0]))
			{
				node1 = new DijkstraNode(infoLine[0]);
				nodes.add(node1);
			}
			else
			{
				node1 = getNode(infoLine[0]);
			}
			if(!nodeExists(infoLine[1]))
			{
				node2 = new DijkstraNode(infoLine[1]);
				nodes.add(node2);
			}
			else
			{
				node2 = getNode(infoLine[1]);
			}
			
			// create a new edge
			DijkstraEdge edge = new DijkstraEdge(node1, node2);
			edge.setWeight(Integer.parseInt(infoLine[2]));
			node1.addNewEdge(edge);
			node2.addNewEdge(edge);
		}
	}
	
	/**
	 * Method checks whether a node has already been created with
	 * this identifier.
	 * @param identifier
	 * @return true if a node was found. False otherwise.
	 */
	public boolean nodeExists(String identifier)
	{
		for(int i = 0; i < nodes.size(); i++)
		{
			if(nodes.get(i).getIdentifier().equals(identifier))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method returns the node with the corresponding identifier.
	 * @param identifier
	 * @return
	 */
	public DijkstraNode getNode(String identifier)
	{
		for(int i = 0; i < nodes.size(); i++)
		{
			if(nodes.get(i).getIdentifier().equals(identifier))
			{
				return nodes.get(i);
			}
		}
		return null;
	}
	
	// ACCESSOR METHODS
	
	public int getNumberOfNodes()
	{
		return nodes.size();
	}
	
	public ArrayList<DijkstraNode> getNodeArray()
	{
		return nodes;
	}
	
	public static void main(String[] args)
	{
		// TEST CODE ----------------------------------------------
		Comparator<DijkstraNode> comparator = new NodeComparator();
		PriorityQueue<DijkstraNode> pq = new PriorityQueue(10, comparator);
		
		DijkstraNode node1 = new DijkstraNode("smallDistance");
		node1.updateDistance(2);
		DijkstraNode node2 = new DijkstraNode("mediumDistance");
		node2.updateDistance(5);
		DijkstraNode node3 = new DijkstraNode("bigDistance");
		node3.updateDistance(9);
		
		pq.add(node1);
		pq.add(node2);
		pq.add(node3);
		
		System.out.println(pq.poll().getIdentifier());
		System.out.println(pq.poll().getIdentifier());
		System.out.println(pq.poll().getIdentifier());
		// ------------------------------------------------------------
	}
}
