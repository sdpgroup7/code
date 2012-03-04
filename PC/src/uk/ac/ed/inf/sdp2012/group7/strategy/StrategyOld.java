package uk.ac.ed.inf.sdp2012.group7.strategy;

import java.awt.Point;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.control.RobotControl;
import uk.ac.ed.inf.sdp2012.group7.control.Tools;
import uk.ac.ed.inf.sdp2012.group7.strategy.planning.AllMovingObjects;
import uk.ac.ed.inf.sdp2012.group7.strategy.planning.AllStaticObjects;
import uk.ac.ed.inf.sdp2012.group7.strategy.planning.Plan;
import uk.ac.ed.inf.sdp2012.group7.vision.VisionTools;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

/**
 * 
 * Milestones so far
 *
 */
public class StrategyOld {

	public static final Logger logger = Logger.getLogger(StrategyOld.class);
	private WorldState worldState = WorldState.getInstance();
	private AllMovingObjects allMovingObjects;
	private AllStaticObjects allStaticObjects;
	private DummyPlanning planner;
	private ScheduledFuture<?> plannerTask;
	private ScheduledThreadPoolExecutor scheduler;

	public void mileStone1(boolean kick) {
		if (kick) {
			controller.kick();
		} else {
			controller.changeSpeed(30);
			controller.moveForward(60);
		}
	}
	
	/**
	 * click by click so far
	 */
	public synchronized void mileStone3NavigateOn() {
		controller.changeSpeed(30);
		plannerTask = scheduler.scheduleWithFixedDelay(planner, 500, 2000, TimeUnit.MILLISECONDS);
		
	}

	public synchronized void mileStone3NavigateOff() {
		controller.stop();
		scheduler.remove(planner);
		plannerTask.cancel(true);
		scheduler.purge();
	}

	private RobotControl controller;


	public StrategyOld() {
		allStaticObjects = new AllStaticObjects();
		allMovingObjects = new AllMovingObjects();

		controller = new RobotControl();
		controller.startCommunications();
		planner = new DummyPlanning();
		scheduler = new ScheduledThreadPoolExecutor(1);

	}
	
	private class DummyPlanning implements Runnable {

		private boolean isBallOnThePitch() {
			Point ballPosition = allStaticObjects.convertToNode(allMovingObjects.getBallPosition());
			return ((ballPosition.x >= 0) && (ballPosition.x <= allStaticObjects.getWidth()) &&
					(ballPosition.y >= 0) && (ballPosition.y <= allStaticObjects.getHeight()));
		}
		
		public void waitABit(long value) {
			try {
				Thread.sleep(value);
			} catch (InterruptedException e) {
				logger.debug(e);
			}
		}
		
		@Override
		public void run() {
			if (worldState.getOurRobot().getPosition().getCentre().distance(worldState.getBall().getPosition().getCentre()) < 40) {
				double targetAngle = Tools.getAngleToFacePoint(worldState.getOurRobot().getPosition().getCentre(),worldState.getOurRobot().getAngle(), worldState.getBall().getPosition().getCentre());
				controller.rotateBy(targetAngle);
				controller.moveForward(5);
				
				controller.kick();
				
			} else {
				controller.stop();
				Plan pl = new Plan(allStaticObjects, allMovingObjects);
				Point navPoint = pl.getPointToGoTo();
				
				if (navPoint == null) {
					if (isBallOnThePitch()) {
						Strategy.logger.debug("Robot: " + worldState.getOurRobot().getPosition().getCentre());
						Strategy.logger.debug("Robot's angle: " + worldState.getOurRobot().getAngle());
						Strategy.logger.debug("Ball: " + worldState.getBall().getPosition().getCentre());
						
						
						double targetAngle = Tools.getAngleToFacePoint(worldState.getOurRobot().getPosition().getCentre(),worldState.getOurRobot().getAngle(), worldState.getBall().getPosition().getCentre());
						controller.rotateBy(targetAngle);
						waitABit(500);
						double distance = allMovingObjects.getOurPosition().distance(allMovingObjects.getBallPosition());
						controller.moveForward(Math.round(VisionTools.pixelsToCM(distance))/4);
					}
				} else {
					Strategy.logger.debug("Robot in the grid: " + allStaticObjects.convertToNode(worldState.getOurRobot().getPosition().getCentre()));
					Strategy.logger.debug("Robot's angle: " + worldState.getOurRobot().getAngle());
					Strategy.logger.debug("Go to this point: " + navPoint);
					Strategy.logger.debug("Ball in the grid: " + allStaticObjects.convertToNode(worldState.getBall().getPosition().getCentre()));
					double targetAngle = Tools.getAngleToFacePoint(allStaticObjects.convertToNode(worldState.getOurRobot().getPosition().getCentre()),worldState.getOurRobot().getAngle(), navPoint);
					controller.rotateBy(targetAngle);
					waitABit(500);
					double distance = allMovingObjects.getOurPosition().distance(allMovingObjects.getBallPosition());
					controller.moveForward(Math.round(VisionTools.pixelsToCM(distance))/4);
				}
			}
			
		}
		
	}


}
