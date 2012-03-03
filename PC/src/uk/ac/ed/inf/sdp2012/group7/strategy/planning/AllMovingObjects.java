package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;

import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.ObjectPosition;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

public class AllMovingObjects {

	//worldstate getInstance
	private WorldState worldState = WorldState.getInstance();


	public Point getOurPosition() {
		return worldState.getOurRobot().getPosition().getCentre();
	}

	public ObjectPosition getTheirPosition() {
		return worldState.getOpponentsRobot().getPosition();
	}

	public Point getBallPosition() {
		return  worldState.getBall().getPosition().getCentre();
	}

	public double getOurAngle() {
		return worldState.getOurRobot().getAngle();
	}

	public double getTheirAngle() {
		return worldState.getOpponentsRobot().getAngle();
	}

}
