package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegisterRequest implements Event{
	
	private final int eventType = 0;
	private String IPAddress;
	private int portNum;
	
	/**
	 * this constructor is used to fill the fields in the class without
	 * a byte array.
	 * @param IPAddress
	 * @param portNum
	 */
	public RegisterRequest(String IPAddress, int portNum)
	{
		this.IPAddress = IPAddress;
		this.portNum = portNum;
	}
	
	/**
	 * This constructor will take a DataInputStream that is received from
	 * the eventFactory and fill the fields in this class
	 * with it.
	 * @param din
	 * @throws IOException
	 */
	public RegisterRequest(DataInputStream din) throws IOException
	{
		// read IPAddress
		int IPAddressLength = din.readInt();
		byte[] IPAddressBytes = new byte[IPAddressLength];
		din.readFully(IPAddressBytes);
		IPAddress = new String(IPAddressBytes);
		
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
		
		// write IPAddress
		byte[] IPAddressBytes = IPAddress.getBytes();
		int elementLength = IPAddressBytes.length;
		dout.writeInt(elementLength);
		dout.write(IPAddressBytes);

		
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
	 * accessor method for eventType
	 * @return
	 */
	public String getIPAddress()
	{
		return IPAddress;
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
