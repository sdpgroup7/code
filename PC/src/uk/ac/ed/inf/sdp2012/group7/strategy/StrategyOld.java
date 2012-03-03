package uk.ac.ed.inf.sdp2012.group7.strategy;

import java.awt.Point;

import lejos.robotics.pathfinding.PathFinder;

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

	public void mileStone1(boolean kick) {
		if (kick) {
			controller.kick();
		} else {
			controller.changeSpeed(30);
			controller.moveForward(60);
		}
	}

	private boolean isBallOnThePitch() {
		Point ballPosition = allStaticObjects.convertToNode(allMovingObjects.getBallPosition());
		return ((ballPosition.x >= 0) && (ballPosition.x <= allStaticObjects.getWidth()) &&
				(ballPosition.y >= 0) && (ballPosition.y <= allStaticObjects.getHeight()));
	}
	
	/**
	 * click by click so far
	 */
	public void mileStone3NavigateOn() {

		if (worldState.getOurRobot().getPosition().getCentre().distance(worldState.getBall().getPosition().getCentre()) < 50) {
			double targetAngle = Tools.getAngleToFacePoint(worldState.getOurRobot().getPosition().getCentre(),worldState.getOurRobot().getAngle(), worldState.getBall().getPosition().getCentre());
			controller.rotateBy(targetAngle);
			controller.kick();
			
		} else {
			controller.stop();
			Plan pl = new Plan(allStaticObjects, allMovingObjects);
			Point navPoint = pl.getPointToGoTo();
			
			if (navPoint == null) {
				if (isBallOnThePitch()) {
					double targetAngle = Tools.getAngleToFacePoint(worldState.getOurRobot().getPosition().getCentre(),worldState.getOurRobot().getAngle(), worldState.getBall().getPosition().getCentre());
					controller.rotateBy(targetAngle);
					waitABit(500);
					double distance = allMovingObjects.getOurPosition().distance(allMovingObjects.getBallPosition());
					controller.moveForward(Math.round(VisionTools.pixelsToCM(distance)));
				}
			} else {
			
				double targetAngle = Tools.getAngleToFacePoint(allStaticObjects.convertToNode(worldState.getOurRobot().getPosition().getCentre()),worldState.getOurRobot().getAngle(), navPoint);
				controller.rotateBy(targetAngle);
				waitABit(500);
				double distance = allMovingObjects.getOurPosition().distance(allMovingObjects.getBallPosition());
				controller.moveForward(Math.round(VisionTools.pixelsToCM(distance))/3);
			}
			waitABit(1500);
		}
	}

	public void mileStone3NavigateOff() {

	}

	private RobotControl controller;

	public void waitABit(long value) {
		try {
			Thread.sleep(value);
		} catch (InterruptedException e) {
			logger.debug(e);
		}
	}


	public StrategyOld() {
		allStaticObjects = new AllStaticObjects();
		allMovingObjects = new AllMovingObjects();

		controller = new RobotControl();
		controller.startCommunications();


	}


}
