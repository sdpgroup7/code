package uk.ac.ed.inf.sdp2012.group7.control;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import uk.ac.ed.inf.sdp2012.group7.shared.ConstantsReuse;
import uk.ac.ed.inf.sdp2012.group7.shared.RobotDetails;

/**
 * This class holds the geometric location of our robot but is also responsible
 * for communicating with it.
 */
public class RobotControl extends RobotDetails {

	private CommunicationInterface comms;
	private NXTComm nxtComm;
	private NXTInfo info = new NXTInfo(NXTCommFactory.BLUETOOTH, ConstantsReuse.ROBOT_NAME,
			ConstantsReuse.ROBOT_MAC);
	private Queue<Integer> commandList = new LinkedList<Integer>();

	private boolean connectToSimulator;
	private boolean isConnected = false;
	private boolean keepConnected = true;
	public boolean askingToReset = false;
	private volatile int currentSpeed = 0;

	// NXT Opcodes
	private final static int DO_NOTHING = 0X00;
	private final static int FORWARDS = 0X01;
	private final static int BACKWARDS = 0X02;
	private final static int BACKWARDS_SLIGHTLY = 0X03;
	private final static int STOP = 0X04;
	private final static int CHANGE_SPEED = 0X05;
	private final static int KICK = 0X06;
	private final static int ROTATE = 0X07;
	private final static int ARC = 0X08;
	private final static int ADJUST_WHEEL_SPEEDS = 0X09;
	private final static int LEFT_MOTOR_FORWARDS = 0X0A;
	private final static int RIGHT_MOTOR_FORWARDS = 0X0B;
	private final static int LEFT_MOTOR_BACKWARDS = 0X0C;
	private final static int RIGHT_MOTOR_BACKWARDS = 0X0D;
	private final static int STEER_WITH_RATIO = 0X0E;
	private final static int BEEP = 0X0F;
	private final static int CELEBRATE = 0X10;

	/**
	 * The constructor takes a boolean to indicate if the object should
	 * communicate with NXT or the simulator.
	 */
	public RobotControl(boolean connectToSimulator) {
		this.connectToSimulator = connectToSimulator;
	}

	/**
	 * This method updates the location and angle of the robot.
	 */
	public void updateRobotDetails(RobotDetails me) {
		coors = me.getCoors();
		angle = me.getAngle();
		updateRect();
	}

	/**
	 * This method initialises the connection and starts the thread which sends
	 * data to the robot.
	 */
	public boolean startCommunications() {

		// start up the connection
		try {
			if (connectToSimulator) {
				//connectToSimulator();
			} else {
				connectToRobot();
			}
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
					if (commandList.isEmpty()) {
						sendToRobot(DO_NOTHING);
					} else {
						sendToRobot(commandList.remove());
					}
					receiveFromRobot();

					// Tools.rest(10);
				}
				// disconnect when we're done
				if (connectToSimulator) {
					disconnectFromSimulator();
				} else {
					disconnectFromRobot();
				}

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
		comms = new BluetoothCommunication(nxtComm, info);
		comms.openConnection();
		setConnected(true);
		beep();
	}

	/**
	 * Disconnect from the simulator
	 */
	private void disconnectFromSimulator() {
		try {
			comms.closeConnection();
			setConnected(false);
		} catch (Exception e) {
			System.err.println("Error Disconnecting from simulator");
			System.err.println(e.toString());
		}
	}

	/**
	 * Disconnect from the NXT
	 */
	private void disconnectFromRobot() {
		try {
			comms.closeConnection();
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
		while (commandList.size() > 3) {
			commandList.remove();
			System.out.print("<");
		}
		commandList.add(command);
	}

	/**
	 * Clear the queue of commands to be sent to the robot
	 */
	public void clearAllCommands() {
		commandList.clear();
	}

	/**
	 * Sends a command to the robot
	 */
	private void sendToRobot(int command) {
		// System.out.println("SENT "+command+" TO ROBOT");
		comms.sendToRobot(command);
	}

	/**
	 * Receive an integer from the robot
	 */
	private int receiveFromRobot() {

		int response = comms.recieveFromRobot();

		if (response == 'r') {
			askingToReset = true;
			// clearAllCommands();
			// System.out.println("STACK CLEARED");
		}

		return response;

	}

	/**
	 * Returns the last speed we set the robot to
	 */
	public int getSpeed() {
		return currentSpeed;
	}

	public boolean moving = true;

	/**
	 * Commands the robot to move forward
	 */
	public void moveForward() {
		moving = true;
		addCommand(FORWARDS);
	}

	/**
	 * Commands the robot to move backward
	 */
	public void moveBackward() {
		moving = true;
		addCommand(BACKWARDS);
	}

	/**
	 * Commands the robot to move back a little bit
	 */
	public void moveBackwardSlightly() {
		addCommand(BACKWARDS_SLIGHTLY);
	}

	/**
	 * Commands the robot to stop where it is
	 */
	public void stop() {
		moving = false;
		addCommand(STOP);
	}

	/**
	 * Sets the speed of the motors to a given integer (900 is the max)
	 */
	public void changeSpeed(int to) {
		int command = CHANGE_SPEED | (to << 8);
		currentSpeed = to;
		addCommand(command);
	}

	/**
	 * Commands the robot to kick
	 */
	public void kick() {
		System.out.println("kick");
		addCommand(KICK);
	}

	/**
	 * Rotates the robot by a given number of radians
	 */
	public void rotateBy(double radians) {

		System.out.println("Rotate by " + radians + ":  "
				+ Math.toDegrees(radians));

		if (radians < 0)
			radians = (2 * Math.PI - radians);
		if (radians != 0) {
			int command = ROTATE | ((int) Math.toDegrees(radians) << 8);
			addCommand(command);
		}

	}

	/**
	 * This method instructs the robot to move around a circle of given radius,
	 * the boolean is true when the robot should arc left.
	 */
	public void circleWithRadius(int radius, boolean arcLeft) {

		// interpreted on the robot as a negative
		if (arcLeft)
			radius += 1000;

		int command = ARC | (radius << 8);
		addCommand(command);

	}

	/**
	 * This method is used for navigating along a path, it sets the speeds of
	 * each motor independently. If a speed is negative, the motor is instructed
	 * to move forward, else it is put in reverse.
	 */
	public void adjustWheelSpeeds(int speedOfLeftWheel, int speedOfRightWheel) {

		System.out.println("adjustWheelpeeds " + speedOfLeftWheel + " "
				+ speedOfRightWheel);

		// set the values of the command
		int command = ADJUST_WHEEL_SPEEDS
				| ((int) Math.abs(speedOfLeftWheel) << 8)
				| ((int) Math.abs(speedOfRightWheel) << 20);

		// set the sign bit for the left wheel
		if (speedOfLeftWheel < 0) {
			command = command | (1 << 19);
		}

		// set the sign bit for the right wheel
		if (speedOfRightWheel < 0) {
			command = command | (1 << 31);
		}

		addCommand(command);

	}

	/**
	 * Commands the robot to move its left motor forwards
	 */
	public void sweepRight() {
		addCommand(LEFT_MOTOR_FORWARDS);
	}

	/**
	 * Commands the robot to move its right motor forwards
	 */
	public void sweepLeft() {
		addCommand(RIGHT_MOTOR_FORWARDS);
	}

	/**
	 * Commands the robot to move its left motor backwards
	 */
	public void backupLeft() {
		addCommand(LEFT_MOTOR_BACKWARDS);
	}

	/**
	 * Commands the robot to move its right motor backwards
	 */
	public void backupRight() {
		addCommand(RIGHT_MOTOR_BACKWARDS);
	}

	/**
	 * Commands steers the robot based on a given ratio
	 */
	public void steerWithRatio(float ratio) {
		int command = STEER_WITH_RATIO | ((int) ratio << 8);
		addCommand(command);
	}

	/**
	 * Commands the robot to make a noise
	 */
	public void beep() {
		addCommand(BEEP);
	}

	/**
	 * Commands the robot to play a tune
	 */
	public void celebrate() {
		addCommand(CELEBRATE);
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public boolean isConnected() {
		return isConnected;
	}

}
