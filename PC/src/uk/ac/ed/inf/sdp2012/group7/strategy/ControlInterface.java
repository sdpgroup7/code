package uk.ac.ed.inf.sdp2012.group7.strategy;

import java.awt.Point;
import java.util.ArrayList;
import uk.ac.ed.inf.sdp2012.group7.strategy.Arc;
import org.apache.log4j.Logger;

/**
 * This takes a small section of the plan from Control Management and turns it
 * into actual commands.
 * 
 * @author David Fraser -s0912336
 * 
 */
public class ControlInterface {

	
	public static final Logger logger = Logger.getLogger(ControlInterface.class);
	
	private int lookahead;

	public ControlInterface(int lookahead) {
		this.lookahead = lookahead;
	}

	/**
	 * Takes a small number of waypoints from Control Management. These are in
	 * an ArrayList<Points>
	 */
	public void getNextMovement() {

	}

	public void getDistanceTravelled() {

	}

	/*
	 * Calculates the Arc that the robot has to follow for the set of points
	 * given using the pure pursuit algorithm
	 */
	public Arc chooseArc(ArrayList<Point> pointPath, double v) {
		// The paper where this maths comes from can be found here
		// http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.135.82&rep=rep1&type=pdf

		Point p = new Point();
		// TODO: Ask Grid for this position

		if (pointPath.size() < 5) {
			Point h = pointPath.get(pointPath.size() - 1);
		} else {
			Point h = pointPath.get(this.lookahead - 1);
		}
		
		double alpha = Math.atan2((h.getY() - p.getY()), (h.getX() - p.getX()))
				- v;

		double d = p.distance(h);
	
		double xhc = d * Math.cos(alpha);

		double R = (Math.pow(d, 2) / 2 * xhc);

		boolean dir;

		// If the arc is to the left (relative to the robot) then dir is true,
		// else if it is going to the right then it is false
		if (xhc >= 0) {
			dir = false;
		} else {
			dir = true;
		}

		Arc arc = new Arc(R, dir);

		return arc;

	}

	public void implimentArc(Arc path) {
		
		controller.
		
		
	}
	
	/*
	 * Changes the angle provided by vision into one required by the calculations
	 * @param	angle	the original angle to be converted in radians
	 * 
	 *  @return The converted angle so it is measured off the y axis rather than the x
	 */
	private double convertAngle(double angle) {
		double newAngle = 0;
		logger.debug(String.format("Converted angle from %f to %f", angle, newAngle));
		return newAngle;
	}

}
