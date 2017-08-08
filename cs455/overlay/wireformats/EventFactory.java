package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class EventFactory {
	private static EventFactory eventFactory = new EventFactory();
	
	public static EventFactory getEventFactory()
	{
		return eventFactory;
	}
	
	public synchronized Event getEvent(byte[] marshalledBytes) throws IOException
	{
		ByteArrayInputStream baInputStream =
			new ByteArrayInputStream(marshalledBytes);
		DataInputStream din =
			new DataInputStream(new BufferedInputStream(baInputStream));
		
		int eventType = din.readInt();
		if(eventType == 0)
		{
			return new RegisterRequest(din);
		}
		else if(eventType == 1)
		{
			return new DeregisterRequest(din);
		}
		else if(eventType == 2)
		{
			return new MessagingNodesList(din);
		}
		else if(eventType == 3)
		{
			return new LinkWeightMessage(din);
		}
		else if(eventType == 4)
		{
			return new HandShake(din);
		}
		else if(eventType == 5)
		{
			return new Protocol(din);
		}
		else if(eventType == 6)
		{
			return new Message(din);
		}
		else if(eventType == 7)
		{
			return new TaskComplete();
		}
		else if(eventType == 8)
		{
			return new PullTrafficSummary();
		}
		else if(eventType == 9)
		{
			return new TrafficSummary(din);
		}
		else if(eventType == 10)
		{
			return new StartTask(din);
		}
		else if(eventType == 11)
		{
			return new Deregistered();
		}
			return null;
	}
}
