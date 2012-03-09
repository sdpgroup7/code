package uk.ac.ed.inf.sdp2012.group7.strategy;

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
	
	

}
