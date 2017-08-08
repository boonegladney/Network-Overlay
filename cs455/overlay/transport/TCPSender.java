package cs455.overlay.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSender {

	private Socket socket;
	private DataOutputStream dout;
	
	public TCPSender(Socket socket) throws IOException {
		this.socket = socket;
		//creates an output stream that can be used to send bytes to the node/s connected to the port
		dout = new DataOutputStream(socket.getOutputStream());
	}
	
	public synchronized void sendData(byte[] dataToSend) throws IOException {
		int dataLength = dataToSend.length; //get the number of bytes in the array
		dout.writeInt(dataLength); //write the number of bytes to the output stream
		dout.write(dataToSend, 0, dataLength); //write the data that needs to get sent to the outputstream.
		dout.flush();
	}
}