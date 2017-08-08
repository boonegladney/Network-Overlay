package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StartTask implements Event{
	private int eventType = 10;
	private int iterations;
	
	public StartTask(int iterations)
	{
		this.iterations = iterations;
	}
	
	public StartTask(DataInputStream din) throws IOException
	{
		//read iterations
		iterations = din.readInt();
	}
	
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout =
		new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		// write eventType
		dout.writeInt(eventType);
		
		// write iterations
		dout.writeInt(iterations);
		
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
	
	public int getIterations()
	{
		return iterations;
	}

}