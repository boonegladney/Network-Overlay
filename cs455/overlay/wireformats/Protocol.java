package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class Protocol implements Event{
	private int eventType = 5;
	private int pathSize;
	private LinkedList<String> path;
	private Message message;
	private String destNode;
	
	public Protocol(LinkedList<String> path, Message message)
	{
		this.message = message;
		this.path = path;
		pathSize = path.size();
	}
	
	public Protocol(DataInputStream din) throws IOException
	{
		// read pathSize
		pathSize = din.readInt();
		
		// read nodeInfo
		path = new LinkedList<String>();
		for(int i = 0; i < pathSize; i++)
		{
			int elementLength = din.readInt();
			byte[] pathBytes = new byte[elementLength];
			din.readFully(pathBytes);
			path.add(new String(pathBytes));
		}
		
		int temp = din.readInt();
		
		int elementLength = din.readInt();
		byte[] destBytes = new byte[elementLength];
		din.readFully(destBytes);
		destNode = new String(destBytes);
		
		message = new Message(temp, destNode);
	}
	
	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout =
		new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		// write eventType
		dout.writeInt(eventType);
		
		// write pathSize
		dout.writeInt(pathSize);
		
		// write peer nodes info
		for(int i = 0; i < path.size(); i++)
		{
			byte[] pathElementBytes = path.get(i).getBytes();
			int elementLength = pathElementBytes.length;
			dout.writeInt(elementLength);
			dout.write(pathElementBytes);
		}
		
		// write message
		dout.writeInt(message.getPayload());
		
		 //write destNode
		 byte[] destBytes = message.getDestNode().getBytes();
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

	@Override
	public int getEventType() {
		return eventType;
	}
	
	public LinkedList<String> getPath()
	{
		return path;
	}
	
	public Message getMessage()
	{
		return message;
	}
	
	public int getPathSize()
	{
		return pathSize;
	}
	
}
