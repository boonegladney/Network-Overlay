package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrafficSummary implements Event{
	private int eventType = 9;
	private int sentMessages;
	private long sentMessagesSum;
	private int messagesReceived;
	private long messagesReceivedSum;
	private int messagesRelayed;
	
	public TrafficSummary(int sentMessages, long sentMessagesSum, int messagesReceived,
			long messagesReceivedSum, int messagesRelayed)
	{
		this.sentMessages = sentMessages;
		this.sentMessagesSum = sentMessagesSum;
		this.messagesReceived = messagesReceived;
		this.messagesReceivedSum = messagesReceivedSum;
		this.messagesRelayed = messagesRelayed;
	}
	
	public TrafficSummary(DataInputStream din) throws IOException
	{
		//read in data
		sentMessages = din.readInt();
		sentMessagesSum = din.readLong();
		messagesReceived = din.readInt();
		messagesReceivedSum = din.readLong();
		messagesRelayed = din.readInt();
	}
	
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout =
		new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		// write eventType
		dout.writeInt(eventType);
		
		// write data
		dout.writeInt(sentMessages);
		dout.writeLong(sentMessagesSum);
		dout.writeInt(messagesReceived);
		dout.writeLong(messagesReceivedSum);
		dout.writeInt(messagesRelayed);
		
		//return marshalled bytes
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}

	public int getEventType() {
		return eventType;
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

}