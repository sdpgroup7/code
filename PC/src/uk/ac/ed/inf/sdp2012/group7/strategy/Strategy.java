package uk.ac.ed.inf.sdp2012.group7.strategy;

import java.awt.Point;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.control.RobotControl;
import uk.ac.ed.inf.sdp2012.group7.control.Tools;
import uk.ac.ed.inf.sdp2012.group7.vision.Vision;

/**
 * 
 * Milestones so far
 *
 */
public class Strategy {

	public static final Logger logger = Logger.getLogger(Strategy.class);

	public void mileStone1(boolean kick) {
		if (kick) {
			controller.kick();
		} else {
			controller.changeSpeed(30);
			controller.moveForward(60);
		}
	}

	// to start/stop the navigation
	private boolean runFlag = false;

	public synchronized void mileStone2NavigateOn() {
		runFlag = true;
	}

	public synchronized void mileStone2NavigateOff() {
		runFlag = false;
		controller.stop();
	}

	private RobotControl controller;


	public Strategy() {
		controller = new RobotControl();
		controller.startCommunications();
		navigateThread.start();

	}


	/**
	 * quick 'n' dirty way - if reusing some bits of it, rewrite in a proper manner 
	 */
	private Thread navigateThread = new Thread() {

		/* angle calculated by the "hack" */
		private double angle;
		
		/* cm/pixels ratio */
		private double ratio = 0;

		/**
		 * The vision can't get angles at the moment and I wanted to test it
		 */
		private void setAngleHack() {
			if (runFlag) {
				controller.stop();
				try {
					sleep(100);
				} catch (InterruptedException e) {
					logger.error(e);
				}
				Point oldPoint = (Point) Vision.worldState.getOurRobot().getPosition().getCentre().clone();
				logger.debug("Old point: " + oldPoint);	

				controller.moveForward(10);
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					logger.error(e);
				}
				Point newPoint = (Point) Vision.worldState.getOurRobot().getPosition().getCentre().clone();
				logger.debug("New point: " + newPoint);
				angle = Math.atan2(newPoint.y-oldPoint.y, newPoint.x-oldPoint.x);
				ratio = 10/Point.distance(newPoint.x, newPoint.y, oldPoint.x, oldPoint.y);
				logger.debug("Calculated angle: " + Math.toDegrees(angle));
				
			}
		}

		public void run() {
			while (true) {
				while (runFlag) {
					controller.changeSpeed(30);
					Point ball = Vision.worldState.getBall().getPosition().getCentre();
					if (ball.x == 0 && ball.y == 0) { //"bullshit checks"
						runFlag = false;
					}
					setAngleHack();
					Point robot = Vision.worldState.getOurRobot().getPosition().getCentre();
					
					double targetangle = Tools.getAngleToFacePoint(robot, angle, ball);
					/* should we drive to the robot? */
					if (Point.distance(robot.x,robot.y,ball.x,ball.y) > 50) {
						/* should we turn? */
						if (Math.abs(Math.toDegrees(targetangle)) > 5) {
							logger.debug("We need to rotate and travel to the ball.");
							controller.stop();
							try {
								sleep(500);
							} catch (InterruptedException e) {
								logger.error(e);
							}
							logger.debug("Robot: " + robot);
							logger.debug("Ball: " + ball);
							logger.debug("Rotate by: " + Math.toDegrees(targetangle));
							if ((runFlag) && ((ball.x != 0 && ball.y != 0))) {
								controller.rotateBy(targetangle);
							}
							try {
								sleep(200);
							} catch (InterruptedException e) {
								logger.error(e);
							}
							if ((runFlag) && ((ball.x != 0 && ball.y != 0))) {
								controller.moveForward((int) (ratio*Point.distance(robot.x,robot.y,ball.x,ball.y)/3));
							}
						} else {
							logger.debug("It seems fine within 5 degrees: " + Math.toDegrees(targetangle));
							if ((runFlag) && ((ball.x != 0 && ball.y != 0))) {
								controller.moveForward((int) (ratio*Point.distance(robot.x,robot.y,ball.x,ball.y)/3));
							}
						}
					} else {
						/* we're close to the robot, so we should just turn if needed */
						controller.stop();
						if (Math.abs(Math.toDegrees(targetangle)) > 5) {
							logger.debug("We need to rotate to the ball.");
							try {
								sleep(150);
							} catch (InterruptedException e) {
								logger.error(e);
							}
							logger.debug("Robot: " + robot);
							logger.debug("Ball: " + ball);
							logger.debug("Rotate by: " + Math.toDegrees(targetangle));
							controller.rotateBy(targetangle);
							

						}
						runFlag = false;
					}


					try {
						sleep(1500);
					} catch (InterruptedException e) {
						logger.error(e);
					}


				}
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					logger.error(e);
				}
			}
		} 
	};

}
