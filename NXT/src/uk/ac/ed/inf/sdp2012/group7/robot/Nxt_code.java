package uk.ac.ed.inf.sdp2012.group7.robot;
import java.io.InputStream;
import java.io.OutputStream;

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
import lejos.util.Delay;
import uk.ac.ed.inf.sdp2012.group7.control.ConstantsReuse;

/**
 * Code that runs on the NXT brick
 */
public class Nxt_code implements Runnable, ConstantsReuse {

	// class variables
	private static InputStream is;
	private static OutputStream os;
	private static DifferentialPilot pilot;

	// constants for the pilot class 
	private static final float TRACK_WIDTH = (float) 13.7;
	private static final float WHEEL_DIAMETER = (float) 8.16;

	private static Pose initial;
	private static boolean fallback = false;
	private static Nxt_code instance;
	private static KickerThread kicker;

	public volatile static boolean bumped = false;

	public static final Object mutex = new Object();

	public static void main(String[] args) throws Exception {


		DifferentialPilot pilot = new DifferentialPilot(WHEEL_DIAMETER, TRACK_WIDTH, Motor.B,Motor.C, false);
		OdometryPoseProvider odometry = new OdometryPoseProvider(pilot);
		initial = new Pose(0, 0, 0);
		instance = new Nxt_code(pilot);
		int commandCount = 0;
		Motor.A.setAcceleration(6000);
		// start the sensor thread
		new Thread(instance).start();
		kicker = instance.new KickerThread();
		// start the kicker
		new Thread(kicker).start();


		// set initial pilot variables to produce maximum speed
		pilot.setTravelSpeed(pilot.getMaxTravelSpeed()*0.7);
		pilot.setRotateSpeed(pilot.getMaxRotateSpeed()*0.35);
		pilot.setAcceleration(300);

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
				Sound.beep();
				LCD.clear();
				LCD.drawString("Connected!", 0, 2);

				// begin reading commands
				OpCodes n = OpCodes.DO_NOTHING;

				while (n != OpCodes.QUIT) {
					// get the next command from the inputstream
					byte[] byteBuffer = new byte[4];
					is.read(byteBuffer);
					
					if (byteBuffer[0] != 0) {
						kicker.kick();
					}

					
					synchronized(mutex) {
						n = OpCodes.values()[byteBuffer[1]];
						int magnitude = bytesToInt(byteBuffer[2],byteBuffer[3]);
						LCD.clear();
						LCD.drawString("CMD: " + n.toString(), 0, 4);
						LCD.drawString("MAG: " + Integer.toString(magnitude), 0, 5);
						LCD.drawString("ID:  " + Integer.toString(commandCount), 0,6);
						commandCount++;
						try {
							while(bumped) {
								mutex.wait();
							}
						} catch (Exception ex) {
							// the bump sensors aren't on (didn't notify us)
						}


						switch (n) {

						case FORWARDS:
							if(magnitude != 0){
								pilot.setTravelSpeed(magnitude);
							}
							pilot.forward();
							break;

						case BACKWARDS:
							if(magnitude != 0){
								pilot.setTravelSpeed(magnitude);
							}
							pilot.backward();
							break;

						case BACKWARDS_WITH_DISTANCE:
							pilot.travel(-magnitude);
							break;

						case STOP:
							pilot.stop();
							kicker.stopKicking();
							break;

						case CHANGE_SPEED:
							pilot.setTravelSpeed(magnitude);
							break;

						case CHANGE_ROTATE_SPEED:
							pilot.setRotateSpeed(magnitude);
							break;

						case FORWARDS_WITH_DISTANCE:
							pilot.travel(magnitude);
							break;

						case ROTATE_LEFT:
							pilot.rotate(magnitude,true);
							break;

						case ROTATE_RIGHT:
							pilot.rotate(-magnitude,true);
							break;

						case ROTATE_BLOCK_LEFT:
							pilot.rotate(magnitude,false);
							break;

						case ROTATE_BLOCK_RIGHT:
							pilot.rotate(-magnitude,false);
							break;

						case ARC_LEFT:
							pilot.arcForward(-magnitude);
							break;

						case ARC_RIGHT:
							pilot.arcForward(magnitude);
							break;

						case BEEP:
							Sound.beep();
							break;

						case START_MATCH:
							pilot.reset();
							odometry.setPose(initial);
							fallback = true;
							pilot.forward();
							break;

						case STOP_MATCH:
							pilot.quickStop();
							fallback = false;
							break;

						case QUIT: // close connection
						Sound.twoBeeps();
						break;
						}

						// respond to say command was acted on
						os.write(n.ordinal());
					}
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

		TouchSensor touchA = new TouchSensor(SensorPort.S1);
		TouchSensor touchB = new TouchSensor(SensorPort.S2);

		while (true) {
			synchronized(mutex) {
				try {
					bumped = true;
					if (touchA.isPressed() || touchB.isPressed()) {	



						// flag sensor hit as being dealt with and save the speed
						// we were going before the collision occurred
						try{
							os.write(OpCodes.BUMP_ON.ordinal());
							os.flush();
						} catch (Exception ex){}
						pilot.stop();
						Delay.msDelay(100);
						// move back a little bit away from the wall
						pilot.travel(-20);
						LCD.clear();
						LCD.drawString("End travel -20", 0, 0);
						pilot.stop();
						LCD.drawString("End stop", 0, 1);
						// reset speed back to what it was before the collision
						LCD.drawString("Restored travelSpeed", 0, 2);
						try{
							os.write(OpCodes.BUMP_OFF.ordinal());
							os.flush();
						} catch (Exception ex){
							LCD.drawString("bump off failed", 0, 2);
						}
						LCD.drawString("Sent BUMP_OFF", 0, 3);
					}
				} finally {
					bumped = false;
					mutex.notifyAll();
				}
			}
		}
	}


	/**
	 * Kicking thread: when received a kick and not kicking
	 */
	private class KickerThread implements Runnable {

		private volatile boolean kicking = false;
		private volatile boolean toKick = false;

		public synchronized void kick() {
			toKick = true;
		}

		public synchronized void stopKicking(){
			toKick = false;
		}

		public void run() {
			while (true) {
				if (toKick && !kicking) {
					toKick = false;
					kicking = true;
					Motor.A.setSpeed(900);
					Motor.A.rotate(-30, false);

					Motor.A.setSpeed(45);
					Motor.A.rotate(35, false);

					kicking = false;
				}
			}
		}
	}

}
