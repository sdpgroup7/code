package uk.ac.ed.inf.sdp2012.group7.strategy;

import uk.ac.ed.inf.sdp2012.group7.control.RobotControl;
import uk.ac.ed.inf.sdp2012.group7.vision.WorldState;

public class Strategy {

	public void mileStone1(boolean kick) {
		if (kick) {
			controller.kick();
		} else {
			controller.moveForward(200);
		}
	}
	
	private RobotControl controller;

	private final WorldState state;
	
	public Strategy(WorldState ws) {
		state = ws;
		controller = new RobotControl();
		controller.startCommunications();
		
	}

		
}

