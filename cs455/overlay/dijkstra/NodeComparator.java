package cs455.overlay.dijkstra;

import java.util.Comparator;

public class NodeComparator implements Comparator<DijkstraNode>{

	@Override
	public int compare(DijkstraNode node1, DijkstraNode node2) {
		return node1.getDistance() - node2.getDistance();
	}

}
