package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Message implements Event{
	private int eventType = 6;
	private int payload;
	private String destNode;
	
	public Message(int payload, String destNode)
	{
		this.payload = payload;
		this.destNode = destNode;
	}
	
	public Message(DataInputStream din) throws IOException
	{
		payload = din.readInt();
		
		int elementLength = din.readInt();
		byte[] destBytes = new byte[elementLength];
		din.readFully(destBytes);
		destNode = new String(destBytes);
	}
	
	public byte[] getBytes() throws IOException
	{
		//INTITIAL TEST VERSION OF METHOD
		byte[] marshalledBytes = null;
		 ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		 DataOutputStream dout =
			new DataOutputStream(new BufferedOutputStream(baOutputStream));
		 
		 // write eventType
		 dout.writeInt(eventType);
		 
		 //write payload
		 dout.writeInt(payload);
		 
		 //write destNode
		 byte[] destBytes = destNode.getBytes();
		 int elementLength = destBytes.length;
		 dout.writeInt(elementLength);
		 dout.write(destBytes);
		 
		//return marshalled bytes
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}
	
	public int getEventType()
	{
		return eventType;
	}
	
	public int getPayload()
	{
		return payload;
	}
	
	public String getDestNode()
	{
		return destNode;
	}
}