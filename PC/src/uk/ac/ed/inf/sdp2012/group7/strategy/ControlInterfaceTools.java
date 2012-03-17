package uk.ac.ed.inf.sdp2012.group7.strategy;

import uk.ac.ed.inf.sdp2012.group7.vision.VisionTools;

public class ControlInterfaceTools {

	/*
	 * Changes the angle provided by vision into one required by the calculations
	 * @param	angle	the original angle to be converted in radians
	 * 
	 * @return The converted angle so it is measured off the y axis rather than the x
	 */
	public static double convertAngle(double angle) {
		
		double newAngle = angle;
		if (angle > Math.PI) {
			newAngle = angle - (2*Math.PI);
		}
		newAngle = -newAngle;
		
		ControlInterface.logger.debug(String.format("Converted angle from %f to %f", angle, newAngle));
		return newAngle;
	}

	public static double angleToTurn(double ourAngle, double angleWanted) {
			
		double howMuchToTurn = ourAngle - angleWanted;
	
		// now adjust it so that it turns in the shortest direction (clockwise
		// or counter clockwise)
		if (howMuchToTurn < -Math.PI) {
			howMuchToTurn = 2 * Math.PI + howMuchToTurn;
		} else if  (howMuchToTurn > Math.PI) {
			howMuchToTurn = -(2 * Math.PI - howMuchToTurn);
		}
	
		return howMuchToTurn;
	
	}
	
	/**
	 * Checks if the goal point is behind the robot
	 * @param alpha
	 * @return If the goal point is behind the robot or not
	 */
	public static boolean checkIfBehind(double alpha) {
		alpha = VisionTools.convertAngle(alpha);
		if ((alpha > (Math.PI/2)) && (alpha > (3*Math.PI)/2)) {
			return true;
		} else {
			return false;
		}
		
	}

	
	

}
