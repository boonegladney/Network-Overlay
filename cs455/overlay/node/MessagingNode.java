package cs455.overlay.node;

import java.io.IOException;
import cs455.overlay.dijkstra.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import cs455.overlay.wireformats.*;

import cs455.overlay.transport.*;
import cs455.overlay.util.ConsoleInputReader;

public class MessagingNode implements Node{
	
	private TCPServerThread server;
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	private ShortestPath shortestPath;
	private ArrayList<String> otherMessagingNodes;
	private ConsoleInputReader consoleReader;
	private volatile boolean isDeregistered = false;
	private int sentMessages = 0;
	private long sentMessagesSum = 0;
	private int messagesReceived = 0;
	private long messagesReceivedSum = 0;
	private int messagesRelayed = 0;
	
	/**
	 * When a new connection is received from the server thread, the
	 * server thread will use this method to communicate the new socket
	 * that was returned for the connection. This method will take the
	 * socket and create a new Connection that is connected to
	 * the given socket.
	 * @param socket
	 */
	public synchronized void receiveNewConnection(Socket socket)
	{
		Connection newConnection = new Connection(this, socket);
		connections.add(newConnection);
		System.out.println("new connection established"); //TEST CODE
	}
	
	/**
	 * This method recieves a MessagingNodesList and initiates a connection
	 * to all Nodes specified in the list.
	 * @param event
	 * @throws IOException 
	 * @throws UnknownHostException 
	 * @throws NumberFormatException 
	 */
	public void getMessagingNodesList(Event event)
	{
		MessagingNodesList messagingNodesList = ((MessagingNodesList)event);
		ArrayList<String> peerNodes = messagingNodesList.getNodeInfo();
		for(int i = 0; i < peerNodes.size(); i++)
		{
			String[] temp = peerNodes.get(i).split(":");
			try {
				Socket socket = new Socket(temp[0], Integer.parseInt(temp[1]));
				receiveNewConnection(socket);
			} catch (NumberFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try{
			TimeUnit.SECONDS.sleep(1);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		System.out.println("All connections are established. Number of connections: " + (connections.size() - 1));
	}
	
	/**
	 * this method retrieves the link weight message sent from the
	 * registry.
	 * @param event
	 */
	public void getLinkWeights(Event event)
	{
		LinkWeightMessage LWMessage = ((LinkWeightMessage)event);
		ArrayList<String> linkInfo = LWMessage.getLinkInfo();
		
		// fill otherMessagingNodes
		TCPServerThread server = getServer();
		ArrayList<String> nodes = new ArrayList<String>();
		for(int i = 0; i < linkInfo.size(); i++)
		{
			String[] line = linkInfo.get(i).split(" ");
			if(!nodes.contains(line[0]))
			{
				if(!line[0].equals(server.getIPAddress() + ":" + server.getPortNumber()))
				{
					nodes.add(line[0]);
				}
			}
			if(!nodes.contains(line[1]))
			{
				if(!line[1].equals(server.getIPAddress() + ":" + server.getPortNumber()))
				{
					nodes.add(line[1]);
				}
			}
		}
		setOtherMessagingNodes(nodes);
		
		//create shortestPath object for path references.
		Graph graph = new Graph(linkInfo);
		this.shortestPath = new ShortestPath(graph, graph.getNode(server.getIPAddress() + ":" + server.getPortNumber()));
		shortestPath.calculateShortestPaths();
		
		System.out.println("Link weights are received and processed. Ready to send messages.");
	}
	
	/**
	 * This method returns the server thread for this node.
	 * @return
	 */
	public TCPServerThread getServer()
	{
		return server;
	}
	
	/**
	 * This method will send messages to nodes that are neighbors
	 * if the destination node is a neighbor, and will send a protocol
	 * otherwise, which contains information about what path to take in order
	 * to get to the destination node. This method will iterate as many times
	 * as the parameter 'iterations' specifies. After completion of the message
	 * sending, it will send a TaskComplete message to the registry.
	 * @param iterations
	 */
	public void startTask(int iterations)
	{
		ShortestPath shortestPath = getShortestPath();

		// for the number of iterations
		for(int i = 0; i < iterations; i++)
		{
			//pick a random sink node.
			ArrayList<String> nodes = getOtherMessagingNodes();
			//int random = (int )(Math.random() * (nodes.size() + 1));
			Random random = new Random();
			String destNode = nodes.get(random.nextInt(nodes.size()));
			
			// get the path to the sinkNode
			LinkedList<String> path = new LinkedList<String>(shortestPath.getShortestPath(destNode));
			
			// if the destNode is a neighbor, send message
			if(path.size() == 1)
			{
				for(int j = 0; j < connections.size(); j++)
				{
					if(destNode.equals(connections.get(j).getName() + ":" + connections.get(j).getPort()))
					{
						
						Random rand = new Random();
						int payload = rand.nextInt();
						Message message = new Message(payload, destNode);
					
						try {
							connections.get(j).sendData(message.getBytes());
							incrementSentMessages();
							addToSentMessagesSum(payload);
						} catch (IOException e) {
							System.out.println("ERROR IN: startTask()");
							e.printStackTrace();
						}
					}
				}
			}
			//else send protocol
			else
			{
				
				path.poll();
				for(int j = 0; j < connections.size(); j++)
				{
					if(path.peek().equals(connections.get(j).getName() + ":" + connections.get(j).getPort()))
					{
						
						Random rand = new Random();
						int payload = rand.nextInt();
						Message message = new Message(payload, destNode);
						Protocol protocol = new Protocol(path, message);
						
						try {
							connections.get(j).sendData(protocol.getBytes());
							incrementSentMessages();
							addToSentMessagesSum(payload);
						} catch (IOException e) {
							System.out.println("ERROR IN: startTask()");
							e.printStackTrace();
						}
					}
				}
				
			}
		}
		
		TaskComplete taskComplete = new TaskComplete();
		try {
			connections.get(0).sendData(taskComplete.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void printShortestPath()
	{
		ArrayList<String> otherNodes = getOtherMessagingNodes();
		for(int i = 0; i < otherNodes.size(); i++)
		{
			getShortestPathToNode(otherNodes.get(i));
		}
	}
	
	public void getShortestPathToNode(String destNode)
	{
		LinkedList<String> path = shortestPath.getShortestPath(destNode);
		for(int i = 0; i < path.size(); i++)
		{
			System.out.print(path.get(i) + "--->");
		}
		System.out.println(destNode);
	}
	
	/**
	 * This method will relay a message to an adjacent node. If
	 * The adjacent node is the destination node, it will just send
	 * the Message. Otherwise, it will construct a new Protocol and
	 * send it to the next node in the path.
	 * @param event
	 */
	public void relayMessage(Event event)
	{
		Protocol protocol = ((Protocol)event);
		
		// get the protocol path
		LinkedList<String> path = new LinkedList<String>(protocol.getPath());
		
		// if the path contains one element, send message to destination node
		if(path.size() == 1)
		{
			for(int i = 0; i < connections.size(); i++)
			{
				if(protocol.getMessage().getDestNode().equals(connections.get(i).getName() + ":" + connections.get(i).getPort()))
				{
					try {
						connections.get(i).sendData(protocol.getMessage().getBytes());
						incrementMessagesRelayed();
					} catch (IOException e) {
						System.out.println("ERROR IN: relayMessage()");
						e.printStackTrace();
					}
				}
			}
		}
		// else send protocol message to next node in the path
		else
		{
			path.poll();
			Protocol newProtocol = new Protocol(path, protocol.getMessage());
			for(int i = 0; i < connections.size(); i++)
			{
				if(path.peek().equals(connections.get(i).getName() + ":" + connections.get(i).getPort()))
				{
					try {
						connections.get(i).sendData(newProtocol.getBytes());
						incrementMessagesRelayed();
					} catch (IOException e) {
						System.out.println("ERROR IN: relayMessage()");
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void sendTrafficSummary()
	{
		TrafficSummary trafficSummary = new TrafficSummary(sentMessages, sentMessagesSum, messagesReceived,
				messagesReceivedSum, messagesRelayed);
		try {
			connections.get(0).sendData(trafficSummary.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sentMessages = 0;
		sentMessagesSum = 0;
		messagesReceived = 0;
		messagesReceivedSum = 0;
		messagesRelayed = 0;
	}
	
	/**
	 * This method will received a Message and increment
	 * the corresponding variables.
	 * @param event
	 */
	public void receiveMessage(Event event)
	{
		Message message = ((Message)event);
		incrementMessagesReceived();
		addToMessagesReceivedSum(message.getPayload());
	}
	
	public void shutdown() throws IOException, InterruptedException
	{
		DeregisterRequest deregisterRequest = new DeregisterRequest(server.getIPAddress(), server.getPortNumber());
		connections.get(0).sendData(deregisterRequest.getBytes());
		while(!isDeregistered()){continue;}
		server.shutdown();
		for(int i = 0; i < connections.size(); i++)
		{
			connections.get(i).shutdown();
		}
		consoleReader.shutdown();
	}
	
	// ACCESSOR METHODS
	
	public synchronized ArrayList<String> getOtherMessagingNodes()
	{
		return otherMessagingNodes;
	}
	
	public ShortestPath getShortestPath()
	{
		return shortestPath;
	}
	
	public int getSentMessages()
	{
		return sentMessages;
	}
	
	public long getSentMessagesSum()
	{
		return sentMessagesSum;
	}
	
	public int getMessagesReceived()
	{
		return messagesReceived;
	}
	
	public long getMessagesReceivedSum()
	{
		return messagesReceivedSum;
	}
	
	public int getMessagesRelayed()
	{
		return messagesRelayed;
	}
	
	public boolean isDeregistered()
	{
		return isDeregistered;
	}

	
	// MUTATOR METHODS
	
	public void wasDeregistered()
	{
		isDeregistered = true;
	}
	
	public void setOtherMessagingNodes(ArrayList<String> nodes)
	{
		this.otherMessagingNodes = nodes;
	}
	
	public synchronized void incrementSentMessages()
	{
		sentMessages++;
	}
	
	public synchronized void addToSentMessagesSum(int payload)
	{
		sentMessagesSum = sentMessagesSum + payload;
	}
	
	public synchronized void incrementMessagesReceived()
	{
		messagesReceived++;
	}
	
	public synchronized void addToMessagesReceivedSum(int payload)
	{
		messagesReceivedSum = messagesReceivedSum + payload;
	}
	
	public synchronized void incrementMessagesRelayed()
	{
		messagesRelayed++;
	}
	
	public static void main(String[] args)
	{
		// collect input data
		String hostname = args[0];
		int hostport = Integer.parseInt(args[1]);
		
		//create new MessagingNodeInstance
		MessagingNode msgNode = new MessagingNode();
		
		//start a server thread to listen for new connections
		msgNode.server = new TCPServerThread(msgNode, 0);
		Thread serverThread = new Thread(msgNode.server);
		serverThread.start();
		
		//create a connection to the Registry
			try {
				Socket registryConnectionSocket = new Socket(hostname, hostport);
				msgNode.receiveNewConnection(registryConnectionSocket);
			} catch (UnknownHostException e) {
				System.out.println("The host is unknown...");
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		//create and start the console reader thread
		msgNode.consoleReader = new ConsoleInputReader(msgNode);
		Thread consoleReaderThread = new Thread(msgNode.consoleReader);
		consoleReaderThread.start();
			
		// send registerRequest to the registry
		//while(msgNode.server.getIPAddress() ==  null) {System.out.print("");}
		while(!msgNode.server.isInitialized()){
			try
			{
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		RegisterRequest registerRequest = new RegisterRequest(msgNode.server.getIPAddress(), msgNode.server.getPortNumber());
		System.out.println(registerRequest.getIPAddress() + registerRequest.getPortNum()); // TEST CODE
		try {
			msgNode.connections.get(0).sendData(registerRequest.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
