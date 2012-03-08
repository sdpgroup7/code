package uk.ac.ed.inf.sdp2012.group7.control;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.MainRunner;
import uk.ac.ed.inf.sdp2012.group7.strategy.planning.Plan;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

/**
 * This class holds the geometric location of our robot but is also responsible
 * for communicating with it.
 */
public class RobotControl implements ConstantsReuse, ControlCodes {

	private CommunicationInterface comms;
	private NXTComm nxtComm;
	private NXTInfo info = new NXTInfo(NXTCommFactory.BLUETOOTH, ROBOT_NAME,ROBOT_MAC);
	public static final Logger logger = Logger.getLogger(RobotControl.class);
	private int command;

	private boolean isConnected = false;
	private boolean keepConnected = true;
	public boolean askingToReset = false;
	private volatile int currentSpeed = 0;
	private boolean simulator = false;
	private boolean bumped = false;

	public RobotControl() {}

	/**
	 * This method initialises the connection and starts the thread which sends
	 * data to the robot.
	 */
	public boolean startCommunications() {

		// start up the connection
		try {
			connectToRobot();
		} catch (IOException ex) {
			System.err.println("Robot Connection Failed: ");
			System.err.println(ex.toString());
			return false;
		}

		// start up the thread which sends commands
		new Thread(new Runnable() {
			public void run() {

				// send data when necessary
				while (keepConnected) {
					sendToRobot(command);
				}
				// disconnect when we're done
				disconnectFromRobot();
				
			}
		}).start();

		return true;

	}

	/**
	 * Stops the connection with the Robot
	 */
	public void stopCommunications() {
		keepConnected = false;
	}

	/**
	 * Connects to the NXT
	 */
	private void connectToRobot() throws IOException {
		simulator = MainRunner.simulator;
		if (simulator)
			comms = new SimulatorCommunication();
		else 
			comms = new BluetoothCommunication(nxtComm, info);

		comms.openConnection();
		setConnected(true);
		beep();
	}

	/**
	 * Disconnect from the NXT
	 */
	private void disconnectFromRobot() {
		try {
			comms.closeConnection();
			if (!simulator)			
				nxtComm.close();
			setConnected(false);
		} catch (IOException ex) {
			System.err.println("Error Disconnecting from NXT");
			System.err.println(ex.toString());
		}
	}

	/**
	 * Add a command to the queue to be sent to the robot
	 */
	private void addCommand(int command) {
		this.command = command;
	}



	/**
	 * Sends a command to the robot
	 */
	private void sendToRobot(int command) {
		
		if(!bumped){
			comms.sendToRobot(command);
			switch(getResponse()){
				case BUMPED_OBJECT:
					bumped = true;
					logger.debug("Robot hit object!");
					break;
				case MOVED_FORWARDS:
					logger.info("Robot moved forwards");
					break;
				case MOVED_BACKWARDS:
					logger.info("Robot moved forwards");
					break;
				case ARCED_LEFT:
					logger.info("Robot moved forwards");
					break;
				case ARCED_RIGHT:
					logger.info("Robot moved forwards");
					break;
				case KICKED:
					logger.info("Robot moved forwards");
					break;
			}
		} else {
			while(getResponse() != COMPLETED_BUMP_PROCEDURE){}
			logger.debug("Completed bump procedure");
			//We don't need anything in the loop as getResponse is blocking anyway
		}
		
	}

	/**
	 * Receive an integer from the robot
	 */
	private int getResponse() {
		//the below method is blocking
		return comms.recieveFromRobot();
	}
	
	public void changeSpeed(int speed) {
		addCommand(OpCodes.CHANGE_SPEED.ordinal() | (speed << 8));
	}
	

	/**
	 * Commands the robot to move forward
	 */
	public void moveForward() {
		addCommand(OpCodes.FORWARDS.ordinal());
	}
	
	public void moveForward(int distance) {
		addCommand(OpCodes.FORWARDS_WITH_DISTANCE.ordinal() | (distance << 8));
	}
	

	/**
	 * Commands the robot to move backward
	 */
	public void moveBackward() {
		addCommand(OpCodes.BACKWARDS.ordinal());
	}

	/**
	 * Commands the robot to move back a little bit
	 */
	public void moveBackwardSlightly() {
		addCommand(OpCodes.BACKWARDS_SLIGHTLY.ordinal());
	}

	/**
	 * Commands the robot to stop where it is
	 */
	public void stop() {
		addCommand(OpCodes.STOP.ordinal());
	}


	/**
	 * Commands the robot to kick
	 */
	public void kick() {
		System.out.println("kick");
		addCommand(OpCodes.KICK.ordinal());
	}

	/**
	 * Rotates the robot by a given number of radians
	 */
	public void rotateBy(double radians) {

		System.out.println("Rotate by " + radians + ":  "
				+ Math.toDegrees(radians));

		if (radians < 0) radians = (2 * Math.PI - radians);
		if (radians != 0) {
			int command = OpCodes.ROTATE.ordinal() | ((int) Math.toDegrees(radians) << 8);
			addCommand(command);
		}

	}

	/**
	 * This method instructs the robot to move around a circle of given radius,
	 * the boolean is true when the robot should arc left.
	 */
	public void circleWithRadius(int radius, boolean arcLeft) {

		int command = radius << 8;
		if (arcLeft) {
			command = command | OpCodes.ARC_LEFT.ordinal();
		} else {
			command = command | OpCodes.ARC_RIGHT.ordinal();
		}

		addCommand(command);

	}

	/**
	 * Commands the robot to make a noise
	 */
	public void beep() {
		addCommand(OpCodes.BEEP.ordinal());
	}
	
	/**
	 * Commands the robot to move 65 cm forward and start odometry from its initial position
	 * (if the bluetooth connection is lost, it'll try to move back to the initial position)
	 */
	public void startMatch() {
		addCommand(OpCodes.START_MATCH.ordinal());
	}
	
	/**
	 * Commands the robot to stop movement and odometry
	 */
	public void stopMatch() {
		addCommand(OpCodes.STOP_MATCH.ordinal());
	}


	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public boolean isConnected() {
		return isConnected;
	}

}
