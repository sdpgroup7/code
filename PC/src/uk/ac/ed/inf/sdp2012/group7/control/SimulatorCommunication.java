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
			System.out.println("SC: Receiving command from simulator at "+addr()+" failed: "+e.toString());
		}
		return -1;
	}


	public OpCodes sendToRobot(byte[] command) {
		try {
			System.out.println("SC: Sending command '"+command.toString()+"' to simulator at "+addr());
			os.write(command);
			os.flush();
			int response = recieveFromRobot();
			return OpCodes.values()[response];
		} catch (Exception e) {
			System.out.println("SC: Sending command '"+command.toString()+"' to simulator at "+addr()+" failed: "+e.toString());
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
