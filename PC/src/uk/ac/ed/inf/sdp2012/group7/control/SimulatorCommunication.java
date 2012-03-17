package uk.ac.ed.inf.sdp2012.group7.control;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

import uk.ac.ed.inf.sdp2012.group7.control.ConstantsReuse.OpCodes;

public class SimulatorCommunication implements CommunicationInterface {

	private String simHost = "localhost";
	private int    simPort = 10000;

	private Socket       socket;
	private OutputStream os;
	private InputStream  is;

	public SimulatorCommunication(String host, int port) {
		this.simHost = host;
		this.simPort = port;
	}

	public SimulatorCommunication(){}
	
	/* Check your spelling guys. */
	public int recieveFromRobot() {
		return receiveFromRobot();
	}

	public int receiveFromRobot() {
		try {
			return is.read();
		} catch (Exception e) {
			System.out.println("SC: Receiving command from simulator at "+addr()+" failed: "+e.toString());
		}
		return -1;
	}


	public OpCodes sendToRobot(byte[] command) {
		try {
			System.out.printf("SC: Sending command 0x%02x%02x%02x%02x to simulator at %s\n", command[0], command[1], command[2], command[3], addr());
			os.write(command);
			os.flush();
			int response = recieveFromRobot();
			return OpCodes.values()[response];
		} catch (Exception e) {
			System.out.println("SC: Sending command failed: "+e.toString());
			return OpCodes.CONTINUE;
		}
	}
	
	public void openConnection() throws IOException {
		System.out.print("SC: Connecting to simulator at "+addr()+"...");
		try {
			socket = new Socket(simHost, simPort);
			os = socket.getOutputStream();
			os.flush();
			is = socket.getInputStream();
			System.out.println("connected.");
		} catch (Exception e) {
			System.out.println("failed: "+e.toString());
			throw new IOException("Connecting to simulator at "+addr()+" failed: "+e.toString());
		}
	}
	
	public void closeConnection() {
		System.out.print("SC: Disconnecting from simulator at "+addr()+"...");
		try {
			is.close();
			os.close();
			socket.close();
		} catch (Exception e) {
			System.out.println("failed: "+e.toString());
		}
		System.out.println("disconnected.");
	}

	private String addr() {
		return simHost+":"+simPort;
	}

}
