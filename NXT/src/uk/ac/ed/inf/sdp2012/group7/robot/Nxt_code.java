package uk.ac.ed.inf.sdp2012.group7.robot;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.DifferentialPilot;

/**
 * Code that runs on the NXT brick
 */
public class Nxt_code implements Runnable, MoveListener {

	// class variables
	private static boolean spinnersOn = false;
	private static InputStream is;
	private static OutputStream os;
	private static DifferentialPilot pilot;
	private static boolean ignoringInput = false;
	private static volatile boolean blocking = false;
	private static volatile boolean kicking = false;

	// constants for the pilot class 
	private static final float TRACK_WIDTH = (float) 14.5;
	private static final float WHEEL_DIAMETER = (float) 81.6;

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
	private final static int QUIT = 0X11;

	public static void main(String[] args) throws Exception {

		DifferentialPilot pilot = new DifferentialPilot(WHEEL_DIAMETER, TRACK_WIDTH, Motor.B,Motor.C, false);
		
		// start the sensor thread
		new Thread(new Nxt_code(pilot)).start();

		// set initial pilot variables to produce maximum speed
		//pilot.regulateSpeed(true);
		pilot.setTravelSpeed(pilot.getMaxTravelSpeed());
		pilot.setRotateSpeed(pilot.getMaxRotateSpeed());

		while (true) {
			try {

				// wait for a connection and open streams
				pilot.stop();
				LCD.clear();
				LCD.drawString("Waiting...", 0, 2);
				LCD.drawString("Please connect", 0, 3);
				NXTConnection connection = Bluetooth.waitForConnection();
				is = connection.openInputStream();
				os = connection.openOutputStream();
				LCD.clear();
				LCD.drawString("Connected!", 0, 2);

				// begin reading commands
				int n = DO_NOTHING;

				while (n != QUIT) {

					// get the next command from the inputstream
					byte[] byteBuffer = new byte[4];
					is.read(byteBuffer);

					n = byteArrayToInt(byteBuffer);
					int opcode = ((n << 24) >> 24);

					LCD.drawString(String.valueOf(kicking), 0, 2);
					// If everything is alright, LCD should read "falsected"
					if (blocking) {
						os.write('o');
						os.flush();
						continue;
					}
					
					switch (opcode) {

					case FORWARDS:
						if (pilot.isMoving()) {
							break;
						} else {
							pilot.forward();
							break;
						}

					case BACKWARDS:
						if (pilot.isMoving()) {
							break;
						} else {
							pilot.backward();
							break;
						}

					case BACKWARDS_SLIGHTLY: // back up a little
						pilot.travel(-10);
						break;

					case STOP:
						pilot.stop();
						break;

					case CHANGE_SPEED:
						pilot.setTravelSpeed((n >> 8));
						break;

					case KICK:
						Thread Kick_thread = new Thread() {
							public void run() {
								Motor.A.setSpeed(900);
								
								Motor.A.rotate(-60, true);
								try {
									Thread.sleep(300);
								} catch (InterruptedException e) {
								}
								Motor.A.setSpeed(60);
								Motor.A.rotate(60, true);
								/*try {
									Thread.sleep(300);
								} catch (InterruptedException e) {
								}
								//Motor.A.rotate(-1, true);
								try {
									Thread.sleep(300);
								} catch (InterruptedException e) {
								}*/
								//Motor.A.stop();
								kicking = false;
							}
						};
						if (!kicking) {
							kicking = true;
							Kick_thread.start();
						}
						break;

					case ROTATE:

						int rotateBy = n >> 8;

						// if n > 360 change to negative (turn left)
						if (rotateBy > 360) {
							rotateBy = -(rotateBy - 360);
						}
						// by setting immediate return to false, this method
						// blocks
						// until the rotation is complete
						blocking = true;
						pilot.rotate(rotateBy, false);
						blocking = false;
						break;

					case ARC:

						int arcRadius = n >> 8;

						// if n > 1000 change to negative (turn left)
						if (arcRadius > 1000) {
							arcRadius = (arcRadius - 1000);
						}
						pilot.arcForward(arcRadius);
						break;

					case ADJUST_WHEEL_SPEEDS: // set wheel speeds and direction
						// independently

						boolean leftShouldBeGoingBackwards = false;
						boolean rightShouldBeGoingBackwards = false;

						// get the left wheel speed sign bit
						if (((n << 12) >> 31) == -1) {
							leftShouldBeGoingBackwards = true;
						}

						// get the right wheel speed sign bit
						if (n >> 31 == -1) {
							rightShouldBeGoingBackwards = true;
						}

						// set the left wheel speed
						Motor.B.setSpeed((n << 13) >> 21);

						// get the right wheel speed
						Motor.C.setSpeed((n << 1) >> 21);

						// set the left wheel direction
						if (leftShouldBeGoingBackwards
								&& pilot.isMoving()) {
							Motor.B.backward();
						} else if (!leftShouldBeGoingBackwards
								&& pilot.isMoving()) {
							Motor.B.forward();
						}

						// set the right wheel direction
						if (rightShouldBeGoingBackwards
								&& pilot.isMoving()) {
							Motor.B.backward();
						} else if (!rightShouldBeGoingBackwards
								&& pilot.isMoving()) {
							Motor.B.forward();
						}

						break;

					case LEFT_MOTOR_FORWARDS: // sweep right
						if (pilot.isMoving()) {
							Motor.B.forward();
						}
						break;

					case RIGHT_MOTOR_FORWARDS: // sweep left
						if (pilot.isMoving()) {
							Motor.C.forward();
						}
						break;

					case LEFT_MOTOR_BACKWARDS:
						if (pilot.isMoving()) {
							Motor.B.backward();
						}
						break;

					case RIGHT_MOTOR_BACKWARDS:
						if (pilot.isMoving()) {
							Motor.C.backward();
						}
						break;

					case STEER_WITH_RATIO:
						pilot.steer(n >> 8);
						break;

					case BEEP:
						Sound.beep();
						break;

					case CELEBRATE: // play a sound file
						try {
							File f = new File("Get_Item.wav");
							Sound.playSample(f, 0);
						} catch (Exception e) {
						}
						break;

					case QUIT: // close connection
						Sound.twoBeeps();
						break;

					}

					// respond to say command was acted on
					os.write('o');
					os.flush();

				}

				// close streams and connection
				is.close();
				os.close();
				connection.close();

			} catch (Exception e) {
				LCD.clear();
				LCD.drawString("EXCEPTION!", 0, 2);
			}
		}

	}

	/**
	 * Returns an integer from a byte array
	 */
	public static int byteArrayToInt(byte[] b) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i] & 0x000000FF) << shift;
		}
		return value;
	}

	/**
	 * The constructor accepts a reference to the pilot object, which is set in
	 * main(), so that pilot is accessible within the sensor thread.
	 */
	public Nxt_code(DifferentialPilot pilot) {
		this.pilot = pilot;
	}

	/**
	 * Sensor thread: if a touch sensor is pushed then move back a little and
	 * inform the PC what has happened
	 */
	public void run() {

		boolean reacting = false;
		float tempCurSpeed;
		TouchSensor touchA = new TouchSensor(SensorPort.S1);
		TouchSensor touchB = new TouchSensor(SensorPort.S2);

		while (true) {
			try {

				if (!reacting && (touchA.isPressed() || touchB.isPressed())) {

					// flag sensor hit as being dealt with and save the speed
					// we were going before the collision occurred
					reacting = true;
					tempCurSpeed = (float) pilot.getTravelSpeed();

					// move back a little bit away from the wall
					pilot.setTravelSpeed(200);
					pilot.travel(-20);
					Thread.sleep(10);
					pilot.stop();

					// let the PC know that the sensors were hit
					os.write('r');
					os.flush();

					// reset speed back to what it was before the collision
					pilot.setTravelSpeed(tempCurSpeed);

				} else if (reacting
						&& !(touchA.isPressed() || touchB.isPressed())) {
					reacting = false;
				}

				Thread.sleep(50);

			} catch (Exception ex) {
			}

		}
	}

	@Override
	public void moveStarted(Move event, MoveProvider mp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moveStopped(Move event, MoveProvider mp) {
		// TODO Auto-generated method stub
		
	}

}
