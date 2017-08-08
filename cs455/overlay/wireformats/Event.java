package cs455.overlay.wireformats;

import java.io.IOException;

public interface Event {
	public byte[] getBytes() throws IOException;
	public int getEventType();
}
