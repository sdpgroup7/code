package uk.ac.ed.inf.sdp2012.group7.robot;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.geom.Point;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Pose;

/**
 * Code that runs on the NXT brick
 */
public class Nxt_code implements Runnable {

	// class variables
	private static InputStream is;
	private static OutputStream os;
	private static DifferentialPilot pilot;
	private static volatile boolean blocking = false;
	private static volatile boolean kicking = false;

	// constants for the pilot class 
	private static final float TRACK_WIDTH = (float) 13.7;
	private static final float WHEEL_DIAMETER = (float) 8.16;

	// NXT Opcodes
	private static enum OpCodes {
		DO_NOTHING,
		FORWARDS,
		BACKWARDS,
		BACKWARDS_SLIGHTLY,
		STOP,
		CHANGE_SPEED,
		KICK,
		ROTATE_LEFT,
		ROTATE_RIGHT,
		ROTATE_BY,
		ARC_LEFT,
		ARC_RIGHT,
		STEER_WITH_RATIO,
		BEEP,
		CELEBRATE,
		FORWARDS_WITH_DISTANCE,
		START_MATCH,
		STOP_MATCH,
		QUIT
	}
	
	private static Pose initial;
	private static boolean fallback = false;

	public static void main(String[] args) throws Exception {

		
		DifferentialPilot pilot = new DifferentialPilot(WHEEL_DIAMETER, TRACK_WIDTH, Motor.B,Motor.C, false);
		OdometryPoseProvider odometry = new OdometryPoseProvider(pilot);
		initial = new Pose(0, 0, 0);
		int returnCode;
		// start the sensor thread
		new Thread(new Nxt_code(pilot)).start();
		
		// set initial pilot variables to produce maximum speed
		//pilot.regulateSpeed(true);
		pilot.setTravelSpeed(pilot.getMaxTravelSpeed()*0.7);
		pilot.setRotateSpeed(pilot.getMaxRotateSpeed()*0.7);
		pilot.setAcceleration(1000);
		
		while (true) {
			try {
				// wait for a connection and open streams
				pilot.stop();
				LCD.clear();
				LCD.drawString("Waiting...", 0, 2);
				LCD.drawString("Please connect", 0, 3);
				if (fallback) {
					pilot.stop();
					pilot.rotate(odometry.getPose().angleTo(initial.getLocation())-odometry.getPose().getHeading());
					pilot.travel(odometry.getPose().distanceTo(initial.getLocation()));
				}
				NXTConnection connection = Bluetooth.waitForConnection();
				
				is = connection.openInputStream();
				os = connection.openOutputStream();
				LCD.clear();
				LCD.drawString("Connected!", 0, 2);

				// begin reading commands
				OpCodes n = OpCodes.DO_NOTHING;
				
				while (n != OpCodes.QUIT) {

					// get the next command from the inputstream
					byte[] byteBuffer = new byte[4];
					is.read(byteBuffer);
					
					n = OpCodes.values()[byteBuffer[1]];
					int magnitude = bytesToInt(byteBuffer[2],byteBuffer[3]);
					switch (n) {

						case FORWARDS:
							pilot.forward();
							
							break;
	
						case BACKWARDS:
							pilot.backward();
							
							break;
	
						case BACKWARDS_SLIGHTLY: // back up a little
							pilot.travel(-10);
							
							break;
	
						case STOP:
							pilot.stop();
							
							break;
	
						case CHANGE_SPEED:
							pilot.setTravelSpeed(magnitude);
							
							break;
							
						case FORWARDS_WITH_DISTANCE:
							pilot.travel(magnitude);
							
							break;
	
						case ROTATE_LEFT:
	
							pilot.rotate(magnitude);
							
							break;

						case ROTATE:
	
							int rotateBy = inp >> 8;
							// if n > 360 change to negative (turn left)
							if (rotateBy > 360) {
								rotateBy = -(rotateBy - 360);
							}
							//The below method is NOT blocking
							pilot.rotate(rotateBy, true);
							returnCode = ROTATE;
							break;
	
						case ARC_LEFT:
	
							int arcRadius = inp >> 8;
							pilot.arcForward(arcRadius);
							returnCode = ARC_LEFT;
							break;
	
						case ARC_RIGHT:
	
							int arcRadius = -(inp >> 8);
							pilot.arcForward(arcRadius);
							returnCode = ARC_RIGHT;
							break;
	
						case STEER_WITH_RATIO:
							pilot.steer(inp >> 8);
							returnCode = STEER;
							break;
	
						case BEEP:
							Sound.beep();
							returnCode = BEEP;
							break;
	

						case START_MATCH:
							pilot.reset();
							odometry.setPose(initial);
							fallback = true;
							pilot.travel(80);
							returnCode = START_MATCH;
							break;
							
						case STOP_MATCH:
							pilot.quickStop();
							fallback = false;
							returnCode = STOP_MATCH;
							break;
						
						case QUIT: // close connection
							Sound.twoBeeps();
							returnCode = QUIT;
							break;
					}

					// respond to say command was acted on
					os.write(returnCode);
					os.flush();

				}

				// close streams and connection
				is.close();
				os.close();
				connection.close();

			} catch (Exception e) {
				LCD.clear();
				LCD.drawString("EXCEPTION!", 0, 2);
				System.err.println("Something went wrong in the main thread: " + e.getMessage());
			}
		}

	}

	/**
	 * Returns an integer from a byte array
	 */
	public static int bytesToInt(byte b1, byte b2) {
		
		return ((b1 & 0xFF) << 8) | (b2 & 0xFF);
	}

	/**
	 * The constructor accepts a reference to the pilot object, which is set in
	 * main(), so that pilot is accessible within the sensor thread.
	 */
	public Nxt_code(DifferentialPilot pilot) {
		Nxt_code.pilot = pilot;
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
					pilot.stop();
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
				System.err.println("Something went wrong in the sensor thread: " + ex.getMessage());
			}

		}
		/*case KICK:
		Thread Kick_thread = new Thread() {
			public void run() {
				Motor.A.setSpeed(900);
				
				Motor.A.rotate(-30, true);
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					System.err.println("Kick: interrupted during waiting: " + e.getMessage());
				}
				Motor.A.setSpeed(45);
				Motor.A.rotate(30, true);
				
				kicking = false;
			}
		};
		if (!kicking) {
			kicking = true;
			Kick_thread.start();
		}
		break;*/
	}

}
