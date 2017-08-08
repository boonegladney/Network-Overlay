package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import cs455.overlay.wireformats.*
;
public class TCPReceiverThread implements Runnable {
	
		private Socket socket;
		private DataInputStream din;
		private EventFactory eventFactory = null;
		private Connection connection;
		
		public TCPReceiverThread(Socket socket, Connection connection) throws IOException {
			this.socket = socket;
			this.connection = connection;
			din = new DataInputStream(socket.getInputStream());
			eventFactory = EventFactory.getEventFactory();
		}
		
		public void shutdown() throws IOException
		{
			socket.close();
		}
		
		public void run() {

			 int dataLength;
			 while (socket != null) {
				 try {
					 dataLength = din.readInt();
	
					 byte[] data = new byte[dataLength];
					 din.readFully(data, 0, dataLength);
					 receiveEvent(data);
				 } catch (SocketException se) {
					 System.out.println(se.getMessage());
					 break;
				 } catch (IOException ioe) {
					 System.out.println(ioe.getMessage()) ;
					 break;
				 }
			 }
			 System.out.println("A connection has been terminated.");
			}
		
		public void receiveEvent(byte[] data) throws IOException
		{
			Event event = eventFactory.getEvent(data);
			connection.handleEvent(event);
		}
}
