package cs455.overlay.dijkstra;

public class DijkstraEdge {
	private DijkstraNode node1;
	private DijkstraNode node2;
	private int weight;
	
	public DijkstraEdge(DijkstraNode node1, DijkstraNode node2)
	{
		this.node1 = node1;
		this.node2 = node2;
	}
	
	// MUTATOR METHODS
	
	public void setWeight(int weight)
	{
		this.weight = weight;
	}
	
	// ACCESSOR METHODS
	
	public int getWeight()
	{
		return weight;
	}
	
	public DijkstraNode getDestinationNode(DijkstraNode thisNode)
	{
		if(node1.getIdentifier().equals(thisNode.getIdentifier()))
		{
			return node2;
		}
		else if(node2.getIdentifier().equals(thisNode.getIdentifier()))
		{
			return node1;
		}
		else
		{
			System.out.println("AN ERROR OCCURED IN getDestinationNode()");
			return null;
		}
	}
}
