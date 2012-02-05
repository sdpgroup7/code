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
			controller.moveForward(215);
		}
	}

	// to start/stop the navigation
	private boolean runFlag = false;

	public void mileStone2NavigateOn() {
		runFlag = true;
	}

	public void mileStone2NavigateOff() {
		runFlag = false;
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

		/**
		 * The vision can't get angles at the moment and I wanted to test it
		 */
		private void setAngleHack() {
			Point oldPoint = state.getOurRobot().getPosition().getCentre();
			logger.debug("Old point: " + oldPoint);
			controller.moveForward(5);
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				logger.error(e);
			}
			Point newPoint = state.getOurRobot().getPosition().getCentre();
			logger.debug("New point: " + newPoint);
			double angle = Math.atan2(newPoint.y-oldPoint.y, newPoint.x-oldPoint.x);
			state.getOurRobot().getVelocity().setDirection(angle);
			logger.debug("Calculated angle: " + Math.toDegrees(angle));
		}

		public void run() {
			while (true) {
				while (runFlag) {
					setAngleHack();
					controller.changeSpeed(100);
					Point robot = state.getOurRobot().getPosition().getCentre();
					Point ball = state.getBall().getPosition().getCentre();
					double targetangle = Tools.getAngleToFacePoint(robot, state.getOurRobot().getVelocity().getDirection(), ball);
					if (Point.distance(robot.x,robot.y,ball.x,ball.y) > 30) {
						if (Math.abs(Math.toDegrees(targetangle)) > 5) {
							controller.stop();
							logger.debug("Rotate by: " + Math.toDegrees(targetangle));
							controller.rotateBy(targetangle);
						} else {
							logger.debug("It seems fine within 5 degrees: " + Math.toDegrees(targetangle));
							controller.moveForward();
						}
					} else {
						controller.stop();
						runFlag = false;
					}

					try {
						sleep(1000);
					} catch (InterruptedException e) {
						logger.error(e);
					}
				}
			}
		}
	};

}
