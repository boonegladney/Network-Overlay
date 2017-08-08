package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MessagingNodesList implements Event{
	private int eventType = 2;
	private int peerNodes = 0;
	private ArrayList<String> nodeInfo;
	
	/**
	 * constructor for MessagingNodesList.
	 * @param peerNodes
	 * @param nodeInfo is an array List of the format hostName:Port
	 */
	public MessagingNodesList(int peerNodes, ArrayList<String> nodeInfo)
	{
		this.peerNodes = peerNodes;
		this.nodeInfo = nodeInfo;
	}
	
	/**
	 * This constructor take in a DataInputStream and fills the
	 * fields in this class with it.
	 * @param din
	 * @throws IOException
	 */
	public MessagingNodesList(DataInputStream din) throws IOException
	{
		// read peerNodes
		peerNodes = din.readInt();
		
		// read nodeInfo
		nodeInfo = new ArrayList<String>();
		for(int i = 0; i < peerNodes; i++)
		{
			int elementLength = din.readInt();
			byte[] nodeInfoBytes = new byte[elementLength];
			din.readFully(nodeInfoBytes);
			nodeInfo.add(new String(nodeInfoBytes));
		}
	}
	
	/**
	 * turns the fields in this class into a byte array.
	 */
	public byte[] getBytes() throws IOException
	{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout =
		new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		// write eventType
		dout.writeInt(eventType);
		
		// write peerNodes
		dout.writeInt(peerNodes);
		
		// write peer nodes info
		for(int i = 0; i < nodeInfo.size(); i++)
		{
			byte[] infoLineBytes = nodeInfo.get(i).getBytes();
			int elementLength = infoLineBytes.length;
			dout.writeInt(elementLength);
			dout.write(infoLineBytes);
		}
		
		//return marshalled bytes
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}

	// ACCESSOR METHODS
	
	/**
	 * accessor method for eventType
	 */
	public int getEventType()
	{
		return eventType;
	}
	
	/**
	 * accessor method for peerNodes
	 * @return
	 */
	public int getPeerNodes()
	{
		return peerNodes;
	}
	
	/**
	 * accessor for nodeInfo
	 * @return
	 */
	public ArrayList<String> getNodeInfo()
	{
		return nodeInfo;
	}
}
