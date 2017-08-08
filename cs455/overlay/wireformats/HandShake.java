package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class HandShake implements Event{
		
	private final int eventType = 4;
	private String name;
	private int portNum;
	
	/**
	 * this constructor is used to fill the fields in the class without
	 * a byte array.
	 * @param IPAddress
	 * @param portNum
	 */
	public HandShake(String name, int portNum)
	{
		this.name = name;
		this.portNum = portNum;
	}
	
	/**
	 * This constructor will take a DataInputStream that is received from
	 * the eventFactory and fill the fields in this class
	 * with it.
	 * @param din
	 * @throws IOException
	 */
	public HandShake(DataInputStream din) throws IOException
	{
		// read IPAddress
		int nameLength = din.readInt();
		byte[] nameBytes = new byte[nameLength];
		din.readFully(nameBytes);
		name = new String(nameBytes);
		
		// read portNum
		portNum = din.readInt();
		din.close();
	}
	
	/**
	 * This method will take all of the fields in this class and
	 * pack them into a byte array to be send to other nodes
	 * in the overlay.
	 */
	public byte[] getBytes() throws IOException
	{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout =
		new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		// write eventType
		dout.writeInt(eventType);
		
		// write name
		byte[] nameBytes = name.getBytes();
		int elementLength = nameBytes.length;
		dout.writeInt(elementLength);
		dout.write(nameBytes);

		
		// write portNum
		dout.writeInt(portNum);
		
		//return marshalled bytes
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
		
	}
	
	/**
	 * Accessor method for eventType
	 * @return
	 */
	public int getEventType()
	{
		return eventType;
	}
	
	/**
	 * accessor method for name.
	 * @return
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * accessor method for portNum
	 * @return
	 */
	public int getPortNum()
	{
		return portNum;
	}
}
