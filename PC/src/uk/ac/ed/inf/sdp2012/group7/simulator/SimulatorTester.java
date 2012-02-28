package uk.ac.ed.inf.sdp2012.group7.simulator;

import java.io.*;

import uk.ac.ed.inf.sdp2012.group7.control.SimulatorCommunication;

public class SimulatorTester {

	public static void main(String[] args) {
		SimulatorCommunication sc;
		if (args.length == 2)
			sc = new SimulatorCommunication(args[0], Integer.parseInt(args[1]));
		else
			sc = new SimulatorCommunication();
		try{
			sc.openConnection();
		} catch (Exception ex){
			System.err.println("Unable to open connection: " + ex.getMessage());
			System.exit(0);
		}

		sc.sendToRobot(65);

		System.out.println(sc.receiveFromRobot());
		System.out.println(sc.receiveFromRobot());
		System.out.println(sc.receiveFromRobot());


		sc.closeConnection();
	}
	
}
