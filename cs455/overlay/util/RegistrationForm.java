package cs455.overlay.util;
import java.io.IOException;
import java.util.ArrayList;
import cs455.overlay.wireformats.*;

import cs455.overlay.transport.*;

public class RegistrationForm {
	private String IPAddress;
	private int portNum;
	private Connection connection; // the registry's connection to this node.
	private boolean taskComplete = false;
	
	/*ArrayList that contains the RegistrationForms of all Nodes
	 * that this node needs to connect to.
	 */
	private ArrayList<Link> linkList = new ArrayList<Link>();
	private int numOfConnections = 0; //number of elements in the connectionList.
	
	/**
	 * constructor that initializes the IP and port number
	 * @param IPAddress
	 * @param portNum
	 */
	public RegistrationForm(String IPAddress, int portNum, Connection connection)
	{
		this.IPAddress = IPAddress;
		this.portNum = portNum;
		this.connection = connection;
	}
	
	/**
	 * sends information to the messaging node about what other
	 * messaging nodes it should initiate a connection with.
	 */
	public void sendPeerNodesList()
	{
		ArrayList<String> peerNodesInfo = new ArrayList<String>();
		for(int i = 0; i < linkList.size(); i++)
		{
			String temp = (linkList.get(i).getReceivingNodeIP() + ":" + linkList.get(i).getReceivingNodePortNum());
			peerNodesInfo.add(temp);
		}
		MessagingNodesList msgNodeList = new MessagingNodesList(linkList.size(), peerNodesInfo);
		try {
			connection.sendData(msgNodeList.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//MUTATOR METHODS
	
	public void incrementNumOfConnections()
	{
		numOfConnections++;
	}
	
	public void completedTask()
	{
		taskComplete = true;
	}
	
	public void resetTaskCompletion()
	{
		taskComplete = false;
	}
	
	/**
	 * Add a link to the linkList, which represents
	 * the links in the overlay that this node should
	 * initiate.
	 * @param registrationForm
	 */
	public void addToLinkList(Link link)
	{
		linkList.add(link);
	}
	
	//ACCESSOR METHODS
	
	public boolean taskCompleted()
	{
		return taskComplete;
	}
	
	/**
	 * accessor method for linkList.
	 * @return
	 */
	public ArrayList<Link> getLinkList()
	{
		return linkList;
	}
	
	/**
	 * accessor method for IPAddress
	 * @return
	 */
	public String getIPAddress()
	{
		return IPAddress;
	}
	
	/**
	 * accessor method for portNum
	 * @return
	 */
	public int getPortNum()
	{
		return portNum;
	}
	
	/**
	 * accessor method for the connection assosiated with this registry request.
	 * @return
	 */
	public Connection getConnection()
	{
		return connection;
	}
	
	/**
	 * accessor method for linkSize.
	 * @return
	 */
	public int getNumOfConnections()
	{
		return numOfConnections;
	}
}
