package cs455.overlay.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import cs455.overlay.util.*;
import cs455.overlay.wireformats.*;

import cs455.overlay.transport.*;

public class Registry implements Node {
	
	private TCPServerThread server;
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	private ArrayList<RegistrationForm> registrationForms = new ArrayList<RegistrationForm>();
	private ConsoleInputReader consoleReader;
	private int sentMessagesTot;
	private long sentMessagesSumTot;
	private int messagesReceivedTot;
	private long messagesReceivedSumTot;
	
	/**
	 * When a new connection is received from the server thread, the
	 * server thread will use this method to communicate the new socket
	 * that was returned for the connection. This method will take the
	 * socket and create a new TCPReceiverThread that is connected to
	 * the given socket.
	 * @param socket
	 */
	public void receiveNewConnection(Socket socket)
	{
		Connection newConnection = new Connection(this, socket);
		connections.add(newConnection);
		System.out.println("A new MessagingNode has connected to the registry");
	}
	
	/**
	 * registers a node when a register request is received. It uses
	 * a RegistrationForm object to store information about the node
	 * including the IPAddress/portNum of the node as well as the
	 * connection assosiated with it.
	 * @param event
	 * @param connection
	 */
	public synchronized void registerNode(Event event, Connection connection)
	{
		RegisterRequest registerRequest = (RegisterRequest)event;
		registrationForms.add(new RegistrationForm(registerRequest.getIPAddress(), registerRequest.getPortNum(), connection));
	}
	
	/**
	 * deregisters a node when a deregister request is received. it
	 * will do this by deleting the RegistrationForm correlated with
	 * the node requesting the deregistration.
	 * @param event
	 */
	public void deregisterNode(Event event)
	{
		DeregisterRequest deregisterRequest = (DeregisterRequest)event;
		for(int i = 0; i < registrationForms.size(); i++)
		{
			if(registrationForms.get(i).getIPAddress().equals(deregisterRequest.getIPAddress())
					&& (registrationForms.get(i).getPortNum() == deregisterRequest.getPortNum()))
			{
				registrationForms.remove(i);
				return;
			}
		}
		//if we get here, the registration request wasnt found
		System.out.println("ERROR: registration form was not found");
	}
	
	/**
	 * This method will define all of the connections needed for the overlay
	 * and then will initiate the sending of information to all nodes so that
	 * each node will initiate the proper connections for the construction of
	 * the overlay.
	 * @param numberOfConnections
	 */
	public synchronized void setupOverlay(int numberOfConnections)
	{
		// declare variables need for the method
		String initiatingNodeIP;
		int initiatingNodePortNum;
		String receivingNodeIP;
		int receivingNodePortNum;
		Link link;
		int random;
		// create a connection loop so that no partition exists in the overlay.
		for(int i = 0; i < (registrationForms.size() - 1); i++)
		{
			// connect each node to the next node in the list.
			initiatingNodeIP = registrationForms.get(i).getIPAddress();
			initiatingNodePortNum = registrationForms.get(i).getPortNum();
			receivingNodeIP = registrationForms.get(i+1).getIPAddress();
			receivingNodePortNum = registrationForms.get(i+1).getPortNum();
			link = new Link(initiatingNodeIP, initiatingNodePortNum, receivingNodeIP, receivingNodePortNum);
			random = (int )(Math.random() * 10 + 1);
			link.setLinkWeight(random);
			registrationForms.get(i).addToLinkList(link);
			registrationForms.get(i).incrementNumOfConnections();
			registrationForms.get(i+1).incrementNumOfConnections();
		}
			// connect the last node to the first node
			initiatingNodeIP = registrationForms.get(registrationForms.size() - 1).getIPAddress();
			initiatingNodePortNum = registrationForms.get(registrationForms.size() - 1).getPortNum();
			receivingNodeIP = registrationForms.get(0).getIPAddress();
			receivingNodePortNum = registrationForms.get(0).getPortNum();
			link = new Link(initiatingNodeIP, initiatingNodePortNum, receivingNodeIP, receivingNodePortNum);
			random = (int )(Math.random() * 10 + 1);
			link.setLinkWeight(random);
			registrationForms.get(registrationForms.size() - 1).addToLinkList(link);
			registrationForms.get(registrationForms.size() - 1).incrementNumOfConnections();
			registrationForms.get(0).incrementNumOfConnections();
			
		// finish connecting all of the nodes.
		for(int i = 0; i < registrationForms.size(); i++)
		{
			if(registrationForms.get(i).getNumOfConnections() >= numberOfConnections) continue;
			for(int j = 0; j < registrationForms.size(); j++)
			{
				// connect registration forms i and j, if j can take more connections, and if a
				// connection doesnt already exist between i and j, and if i and j are not the same.
				if(registrationForms.get(j).getNumOfConnections() >= numberOfConnections) continue;
				if(registrationForms.get(i).getNumOfConnections() >= numberOfConnections) continue;
				if(connectionExists(i, j)) continue;
				if(i == j) continue;
				initiatingNodeIP = registrationForms.get(i).getIPAddress();
				initiatingNodePortNum = registrationForms.get(i).getPortNum();
				receivingNodeIP = registrationForms.get(j).getIPAddress();
				receivingNodePortNum = registrationForms.get(j).getPortNum();
				link = new Link(initiatingNodeIP, initiatingNodePortNum, receivingNodeIP, receivingNodePortNum);
				random = (int )(Math.random() * 10 + 1);
				link.setLinkWeight(random);
				registrationForms.get(i).addToLinkList(link);
				registrationForms.get(i).incrementNumOfConnections();
				registrationForms.get(j).incrementNumOfConnections();
			}
		}
		
		// send connection info to all nodes.
		for(int i = 0; i < registrationForms.size(); i++)
		{
			registrationForms.get(i).sendPeerNodesList();
		}
	}
	
	/**
	 * This method checks whether a connection has already been defined between
	 * two nodes corresponding to indexOne and indexTwo
	 * @param indexOne
	 * @param indexTwo
	 * @return true if a connection already exists, and false otherwise.
	 */
	private boolean connectionExists(int indexOne, int indexTwo)
	{
		String nodeOneIP = registrationForms.get(indexOne).getIPAddress();
		int nodeOnePortNum = registrationForms.get(indexOne).getPortNum();
		String nodeTwoIP = registrationForms.get(indexTwo).getIPAddress();
		int nodeTwoPortNum = registrationForms.get(indexTwo).getPortNum();
		for(int i = 0; i < registrationForms.size(); i++)
		{
			ArrayList<Link> links = registrationForms.get(i).getLinkList();
			for(int j = 0; j < links.size(); j++)
			{
				if(links.get(j).getInitiatingNodeIP().equals(nodeOneIP)
						|| links.get(j).getInitiatingNodeIP().equals(nodeTwoIP))
				{
					if(links.get(j).getReceivingNodeIP().equals(nodeOneIP)
						|| links.get(j).getReceivingNodeIP().equals(nodeTwoIP))
					{
						if(links.get(j).getInitiatingNodePortNum() == (nodeOnePortNum)
								|| links.get(j).getInitiatingNodePortNum() == (nodeTwoPortNum))
						{
							if(links.get(j).getReceivingNodePortNum() == (nodeOnePortNum)
								|| links.get(j).getReceivingNodePortNum() == (nodeTwoPortNum))
							{
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public void printLinkWeights()
	{
		for(int i = 0; i < registrationForms.size(); i++)
		{
			ArrayList<Link> links = registrationForms.get(i).getLinkList();
			for(int j = 0; j < links.size(); j++)
			{
				links.get(j).print();
			}
		}
	}
	
	public void printMessagingNodes()
	{
		for(int i = 0; i < connections.size(); i++)
		{
			System.out.println("Hostname: " + connections.get(i).getName() + " PortNum: " + connections.get(i).getPort());
		}
	}
	
	public void sendLinkWeights()
	{
		// gather all link info
		ArrayList<String> linkInfo = new ArrayList<String>();
		for(int i = 0; i < registrationForms.size(); i++)
		{
			ArrayList<Link> links = registrationForms.get(i).getLinkList();
			for(int j = 0; j < links.size(); j++)
			{
				linkInfo.add(links.get(j).toString());
			}
		}
		
		//send link info to messaging nodes
		LinkWeightMessage LWMessage = new LinkWeightMessage(linkInfo.size(), linkInfo);
		for(int i = 0; i < connections.size(); i++)
		{
			try {
				connections.get(i).sendData(LWMessage.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * updates taskComplete in registration form corresponding to
	 * the node that sent the message.
	 * @param name
	 * @param portNum
	 */
	public synchronized void nodeCompletedTask(String name, int portNum)
	{
		for(int i = 0; i < registrationForms.size(); i++)
		{
			if(registrationForms.get(i).getIPAddress().equals(name) && (registrationForms.get(i).getPortNum() == portNum))
			{
				registrationForms.get(i).completedTask();
			}
		}
		// if all nodes have completed the task, then ask for traffic summaries.
		if(allNodesCompletedTask())
		{
			try {
				TimeUnit.SECONDS.sleep(15);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i = 0; i < connections.size(); i++)
			{
				PullTrafficSummary pullTrafficSummary = new PullTrafficSummary();
				try {
					connections.get(i).sendData(pullTrafficSummary.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * returns false if any node has yet to complete its task.
	 * returns true otherwise.
	 * @return
	 */
	public boolean allNodesCompletedTask()
	{
		for(int i = 0; i < registrationForms.size(); i++)
		{
			if(!registrationForms.get(i).taskCompleted())
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * sends all nodes a StartTask message so they all start their tasks.
	 * @param iterations
	 */
	public void startNodeTasks(int iterations)
	{
		StartTask startTask = new StartTask(iterations);
		for(int i = 0; i < connections.size(); i++)
		{
			try {
				connections.get(i).sendData(startTask.getBytes());
			} catch (IOException e) {
				System.out.println("ERROR IN: startNodeTasks");
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void getTrafficSummary(Event event, Connection connection)
	{
		TrafficSummary trafficSummary = ((TrafficSummary)event);
		sentMessagesTot += trafficSummary.getSentMessages();
		sentMessagesSumTot += trafficSummary.getSentMessagesSum();
		messagesReceivedTot += trafficSummary.getMessagesReceived();
		messagesReceivedSumTot += trafficSummary.getMessagesReceivedSum();
		
		if(NoNodesHaveReported())
		{
			System.out.printf("%-15s%-15s%-25s%-25s%-15s%n", "sent", "received", "sum of sent", "sum of received", "relayed");
			System.out.printf("%-15s%-15s%-25s%-25s%-15s%n", "messages", "messages", "messages", "messages", "messages");
			//System.out.println("Sent\treceived\tsum of sent\tsum of received\trelayed");
			//System.out.println("messages\tmessages\tmessages\tmessages\tmessages");
		}
		
		System.out.printf("%-15d%-15d%,-25d%,-25d%-15d %n", trafficSummary.getSentMessages(), trafficSummary.getMessagesReceived(),
				trafficSummary.getSentMessagesSum(), trafficSummary.getMessagesReceivedSum(), trafficSummary.getMessagesRelayed());
		//System.out.println(trafficSummary.getSentMessages() + "\t" + trafficSummary.getMessagesReceived() + "\t" +
			//	trafficSummary.getSentMessagesSum() + "\t" + trafficSummary.getMessagesReceivedSum() + "\t" +
				//trafficSummary.getMessagesRelayed());
		
		connection.reportedTraffic();
		
		if(allNodesReportedTraffic())
		{
			System.out.println("TOTALS:");
			System.out.printf("%-15d%-15d%,-25d%,-25d%n", sentMessagesTot, messagesReceivedTot, sentMessagesSumTot, messagesReceivedSumTot);
			sentMessagesTot = 0;
			sentMessagesSumTot = 0;
			messagesReceivedTot = 0;
			messagesReceivedSumTot = 0;
			for(int i = 0; i < connections.size(); i++)
			{
				connections.get(i).resetTraffic();
			}
			for(int i = 0; i < registrationForms.size(); i++)
			{
				registrationForms.get(i).resetTaskCompletion();
			}
		}
	}
	
	public boolean allNodesReportedTraffic()
	{
		for(int i = 0; i < connections.size(); i++)
		{
			if(!connections.get(i).hasReportedTraffic())
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean NoNodesHaveReported()
	{
		for(int i = 0; i < connections.size(); i++)
		{
			if(connections.get(i).hasReportedTraffic())
			{
				return false;
			}
		}
		return true;
	}
	
	public TCPServerThread getServer()
	{
		return server;
	}
	
	public void shutdown() throws IOException
	{
		server.shutdown();
		for(int i = 0; i < connections.size(); i++){
			connections.get(i).shutdown();
		}
		consoleReader.shutdown();
	}
	
	public static void main(String[] args)
	{
		// get specified portnumber
		int portnum = Integer.parseInt(args[0]);
		
		// create a server socket for the registry server
		try {
			// create a registry object
			Registry registry = new Registry();
			
			//create and start the server thread
			registry.server = new TCPServerThread(registry, portnum);
			Thread serverThread = new Thread(registry.server);
			serverThread.start();
			
			//create and start the console reader thread
			registry.consoleReader = new ConsoleInputReader(registry);
			Thread consoleReaderThread = new Thread(registry.consoleReader);
			consoleReaderThread.start();
			
		} catch (IOException e) {
			//print error messages
			System.out.println("Something went wrong!!");
			e.printStackTrace();
		}
	}
}
