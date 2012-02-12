package uk.ac.ed.inf.sdp2012.group7.strategy;

import java.awt.Point;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.control.RobotControl;
import uk.ac.ed.inf.sdp2012.group7.control.Tools;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

/**
 * 
 * TODO: rewrite - also WorldState doesn't need to be passed anymore, it's publicly accessed from Vision
 *
 */
public class Strategy {

	public static final Logger logger = Logger.getLogger(Strategy.class);

	public void mileStone1(boolean kick) {
		if (kick) {
			controller.kick();
		} else {
			controller.moveForward(135);
		}
	}

	// to start/stop the navigation
	private boolean runFlag = false;

	public void mileStone2NavigateOn() {
		runFlag = true;
	}

	public void mileStone2NavigateOff() {
		runFlag = false;
		controller.stop();
		
	}

	private RobotControl controller;

	private final WorldState state;

	public Strategy(WorldState ws) {
		state = ws;
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
		private double ratio;
		
		/**
		 * The vision can't get angles at the moment and I wanted to test it
		 */
		private void setAngleHack() {
			Point oldPoint = state.getOurRobot().getPosition().getCentre();
			logger.debug("Old point: " + oldPoint);
			controller.moveForward(10);
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				logger.error(e);
			}
			Point newPoint = state.getOurRobot().getPosition().getCentre();
			logger.debug("New point: " + newPoint);
			angle = Math.atan2(newPoint.y-oldPoint.y, newPoint.x-oldPoint.x);
			ratio = 10/Point.distance(newPoint.x, newPoint.y, oldPoint.x, oldPoint.y);
			logger.debug("Calculated angle: " + Math.toDegrees(angle));
		}		


		public void run() {
			while (true) {
				
				while (runFlag) {
					
					setAngleHack();
					Point robot = state.getOurRobot().getPosition().getCentre();
					Point ball = state.getBall().getPosition().getCentre();
					
					//logger.debug("Vision angle: " + Math.toDegrees(state.getOurRobot().getAngle()));
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
							controller.rotateBy(targetangle);

							try {
								sleep(200);
							} catch (InterruptedException e) {
								logger.error(e);
							}
							controller.moveForward((int) (ratio*Point.distance(robot.x,robot.y,ball.x,ball.y)/3));	
						} else {
							logger.debug("It seems fine within 5 degrees: " + Math.toDegrees(targetangle));
							controller.moveForward((int) (ratio*Point.distance(robot.x,robot.y,ball.x,ball.y)/3));	
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
						sleep(1000);
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
