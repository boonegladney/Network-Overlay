package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import cs455.overlay.node.*;

public class TCPServerThread implements Runnable{
	
	private int portnum;
	private ServerSocket serverSocket;
	private Socket connectionSocket;
	private DataInputStream din;
	private Registry registry = null;
	private MessagingNode msgNode = null;
	private String IPAddress = null;
	private boolean isDone = false;
	private boolean isInitialized = false;
	
	/**
	 * This is a constructor for the use of a Registry constructing this object.
	 * @param registry
	 * @throws IOException
	 */
	public TCPServerThread(Registry registry, int portnum) throws IOException {
		this.registry = registry;
		this.portnum = portnum;
	}
	
	/**
	 * This is a constructor for the use of a MessagingNode constructing this object.
	 * @param msgNode
	 * @throws IOException
	 */
	public TCPServerThread(MessagingNode msgNode, int portnum)
	{
		this.msgNode = msgNode;
		this.portnum = portnum;
	}
	
	public int getPortNumber()
	{
		return portnum;
	}
	
	public String getIPAddress()
	{
		return IPAddress;
	}
	
	public void shutdown() throws IOException
	{
		isDone = true;
		serverSocket.close();
	}
	
	public boolean isInitialized()
	{
		return isInitialized;
	}
	
	public void run() {
		try {
			serverSocket = new ServerSocket(portnum);
			//IPAddress = serverSocket.getInetAddress().getHostAddress();
			IPAddress = InetAddress.getLocalHost().getHostName();
			isInitialized = true;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		do
		{
			try {
				// create new ServerSocket and start listening for connections
				portnum = serverSocket.getLocalPort();
				connectionSocket = serverSocket.accept();
				
				// send the new connectionSocket to the object that created the thread
				if(registry != null)
				{
					registry.receiveNewConnection(connectionSocket);
				}
				else if(msgNode != null)
				{
					msgNode.receiveNewConnection(connectionSocket);
				}
				else // Something went strangely wrong if we get here....
				{
					System.out.println("The TCPServerThread wasn't initialized properly?");
				}
			} catch (IOException e) {
				System.out.println("An attemptted connection has failed");
				e.printStackTrace();
			}
		}while(!isDone);
	}
	
}
