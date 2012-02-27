package uk.ac.ed.inf.sdp2012.group7.control;

import java.io.*;
import java.net.*;

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
			System.out.println("Receiving command from simulator at "+addr()+" failed: "+e.toString());
		}
		return -1;
	}


	public void sendToRobot(int command) {
		try {
			os.write(command);
		} catch (Exception e) {
			System.out.println("Sending command '"+command+"' to simulator at "+addr()+" failed: "+e.toString());
		}
	}
	
	public void openConnection() throws IOException {
		System.out.print("Connecting to simulator at "+addr()+"...");
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
		System.out.print("Disconnecting from simulator at "+addr()+"...");
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
