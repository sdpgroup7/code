package uk.ac.ed.inf.sdp2012.group7.strategy;

import java.awt.Point;

import uk.ac.ed.inf.sdp2012.group7.control.RobotControl;
import uk.ac.ed.inf.sdp2012.group7.control.Tools;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

public class Strategy {

	public void mileStone1(boolean kick) {
		if (kick) {
			controller.kick();
		} else {
			controller.moveForward(215);
		}
	}
	
	// a complete bogoff flag :)
	private boolean cbFlag;
	
	public void mileStone2NavigateOn() {
		cbFlag = true;
		navigateThread.start();
	}
	
	public void mileStone2NavigateOff() {
		cbFlag = false;
	}
	
	private RobotControl controller;

	private final WorldState state;
	
	public Strategy(WorldState ws) {
		state = ws;
		controller = new RobotControl();
		controller.startCommunications();
		
	}

	private Thread navigateThread = new Thread() {
		public void run() {
			while (cbFlag) {
				double targetangle = Tools.getAngleToFacePoint(new Point(state.getBlueX(),state.getBlueY()), state.getBlueOrientation(), state.getBallPosition());
				System.out.println(Math.toDegrees(targetangle));
				if (Math.toDegrees(targetangle) > 5) {
					controller.rotateBy(targetangle);
				}
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					System.out.println("Something bad happened.");
				}
			}
		}
	};
		
}

