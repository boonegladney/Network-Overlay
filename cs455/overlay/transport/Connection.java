package cs455.overlay.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import cs455.overlay.node.*;
import cs455.overlay.wireformats.*;

public class Connection {
	private TCPSender sender = null;
	private TCPReceiverThread receiver = null;
	private Registry registry = null;
	private MessagingNode msgNode = null;
	private Socket socket = null;
	private String name; //name of the node for this connection
	private int portNum; //portNum of the node for this connection
	private boolean reportedTraffic = false;
	
	/**
	 * This is a constructor for use by a Registry
	 * @param registry
	 * @param socket
	 */
	public Connection(Registry registry, Socket socket)
	{
		this.registry = registry;
		this.socket = socket;
		try {
			initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This is a constructor for use by a MessagingNode
	 * @param msgNode
	 * @param socket
	 */
	public Connection(MessagingNode msgNode, Socket socket)
	{
		this.msgNode = msgNode;
		this.socket = socket;
		try {
			initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * A helper method used by the constructors to initialize
	 * some of the fields in the Connection class. Particularly
	 * the sender and receiver thread.
	 * @throws IOException
	 */
	public void initialize() throws IOException
	{
		// create the sender
		sender = new TCPSender(socket);
		
		// create and start the receiver thread
		receiver = new TCPReceiverThread(socket, this);
		Thread receiverThread = new Thread(receiver);
		receiverThread.start();
		
		// Send HandShake
		String hostName = InetAddress.getLocalHost().getHostName();
		HandShake handShake;
		if(msgNode != null)
		{
			handShake = new HandShake(hostName, msgNode.getServer().getPortNumber());
		}
		else if(registry != null)
		{
			handShake = new HandShake(hostName, registry.getServer().getPortNumber());
		}
		else
		{
			handShake = null;
		}
		sender.sendData(handShake.getBytes());
	}
	
	/**
	 * This method's purpose is to allow the registry/messagingNode to
	 * communicated with the connections sender.
	 * @param dataToSend
	 */
	public void sendData(byte[] dataToSend)
	{
		try {
			sender.sendData(dataToSend);
		} catch (IOException e) {
			System.out.println("Connection failed to send data");
			e.printStackTrace();
		}
	}
	
	public void shutdown() throws IOException
	{
		receiver.shutdown();
	}
	
	public void receiveHandShake(Event event)
	{
		HandShake handShake = ((HandShake)event);
		name = handShake.getName();
		portNum = handShake.getPortNum();
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getPort()
	{
		return portNum;
	}
	
	public boolean hasReportedTraffic()
	{
		return reportedTraffic;
	}
	
	public void reportedTraffic()
	{
		reportedTraffic = true;
	}
	
	public void resetTraffic()
	{
		reportedTraffic = false;
	}
	
	/**
	 * This method handles an incoming event from the receiver thread
	 * @param event
	 */
	public void handleEvent(Event event)
	{
		int eventType = event.getEventType();
		if(eventType == 0) // RegisterRequest
		{
			registry.registerNode(event, this);
		}
		else if(eventType == 1) // DeregisterRequest
		{
			registry.deregisterNode(event);
			Deregistered deregistered = new Deregistered();
			try {
				sender.sendData(deregistered.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(eventType == 2) // messagingNodesList
		{
			msgNode.getMessagingNodesList(event);
		}
		else if(eventType == 3) // LinkWeightMessage
		{
			msgNode.getLinkWeights(event);
		}
		else if(eventType == 4) // HandShake
		{
			receiveHandShake(event);
		}
		else if(eventType == 5) // Protocol
		{
			msgNode.relayMessage(event);
		}
		else if(eventType == 6) // Message
		{
			msgNode.receiveMessage(event);
		}
		else if(eventType == 7) // TaskComplete
		{
			registry.nodeCompletedTask(name, portNum);
		}
		else if(eventType == 8) // PullTrafficSummary
		{
			msgNode.sendTrafficSummary();
		}
		else if(eventType == 9) // TrafficSummary
		{
			registry.getTrafficSummary(event, this);
		}
		else if(eventType == 10) // StartTask
		{
			msgNode.startTask(((StartTask)event).getIterations());
		}
		else if(eventType == 11) // Deregistered
		{
			msgNode.wasDeregistered();
		}
	}
}
