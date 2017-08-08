package cs455.overlay.util;

public class Link {
	
	private String initiatingNodeIP;
	private int initiatingNodePortNum;
	private String receivingNodeIP;
	private int receivingNodePortNum;
	private int linkWeight = 0;
	
	public Link(String initiatingNodeIP, int initiatingNodePortNum, String receivingNodeIP, int receivingNodePortNum)
	{
		this.initiatingNodeIP = initiatingNodeIP;
		this.initiatingNodePortNum = initiatingNodePortNum;
		this.receivingNodeIP = receivingNodeIP;
		this.receivingNodePortNum = receivingNodePortNum;
	}
	
	public void print()
	{
		System.out.println(initiatingNodeIP + ":" + initiatingNodePortNum + " " + receivingNodeIP + ":" + receivingNodePortNum + " " + linkWeight);
	}
	
	public String toString()
	{
		String temp = initiatingNodeIP + ":" + initiatingNodePortNum + " " + receivingNodeIP + ":" + receivingNodePortNum + " " + linkWeight;
		return temp;
	}
	
	//MUTATOR METHODS
	public void setLinkWeight(int linkWeight)
	{
		this.linkWeight = linkWeight;
	}
	
	//ACCESSOR METHODS
	
	/**
	 * accessor method for initiatingNodeIP
	 * @return
	 */
	public String getInitiatingNodeIP()
	{
		return initiatingNodeIP;
	}
	
	/**
	 * accessor method for initiatingNodePortNum
	 * @return
	 */
	public int getInitiatingNodePortNum()
	{
		return initiatingNodePortNum;
	}
	
	/**
	 * accessor method for receivingNodeIP
	 * @return
	 */
	public String getReceivingNodeIP()
	{
		return receivingNodeIP;
	}
	
	/**
	 * accessor method for receivingNodePortNum
	 * @return
	 */
	public int getReceivingNodePortNum()
	{
		return receivingNodePortNum;
	}
	
	/**
	 * accessor method for linkWeight
	 * @return
	 */
	public int getLinkWeight()
	{
		return linkWeight;
	}
}
