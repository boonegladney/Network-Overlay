package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LinkWeightMessage implements Event{
	private int eventType = 3;
	private int numOfLinks;
	private ArrayList<String> linkInfo;
	
	/**]
	 * constructor for LinkWeightMessage.
	 * @param numOfLinks
	 * @param linkInfo
	 */
	public LinkWeightMessage(int numOfLinks, ArrayList<String> linkInfo)
	{
		this.numOfLinks = numOfLinks;
		this.linkInfo = linkInfo;
	}
	
	/**
	 * This constructor take in a DataInputStream and fills the
	 * fields in this class with it.
	 * @param din
	 * @throws IOException
	 */
	public LinkWeightMessage(DataInputStream din) throws IOException
	{
		// read peerNodes
		numOfLinks = din.readInt();
				
				// read nodeInfo
		linkInfo = new ArrayList<String>();
		for(int i = 0; i < numOfLinks; i++)
		{
			int elementLength = din.readInt();
			byte[] linkInfoBytes = new byte[elementLength];
			din.readFully(linkInfoBytes);
			linkInfo.add(new String(linkInfoBytes));
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
		dout.writeInt(numOfLinks);
		
		// write peer nodes info
		for(int i = 0; i < linkInfo.size(); i++)
		{
			byte[] infoLineBytes = linkInfo.get(i).getBytes();
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
	
	//ACCESSOR METHODS
	
	/**
	 * accessor method for eventType.
	 */
	public int getEventType()
	{
		return eventType;
	}
	
	/**
	 * accessor method for numOfLinks.
	 * @return
	 */
	public int getNumOfLinks()
	{
		return numOfLinks;
	}
	
	/**
	 * accessor method for linkInfo.
	 * @return
	 */
	public ArrayList<String> getLinkInfo()
	{
		return linkInfo;
	}
}
