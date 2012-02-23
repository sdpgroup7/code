package uk.ac.ed.inf.sdp2012.group7.strategy;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ed.inf.sdp2012.group7.strategy.Arc;
import org.apache.log4j.Logger;

import math.geom2d.Point2D;
import math.geom2d.conic.Circle2D;
import math.geom2d.line.LineSegment2D;

/**
 * This takes a plan from Control Management and turns it
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

	/*
	 * Calculates the Arc that the robot has to follow for the set of points
	 * given using the pure pursuit algorithm
	 */
	public Arc chooseArc(ArrayList<Point> pointPath, double v) {
		// The paper where this maths comes from can be found here
		// http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.135.82&rep=rep1&type=pdf
		
		//TODO: Add this point in when the data standards are finalised
		Point2D h = null;
		Point2D p = new Point2D();
		// TODO: Ask Grid for this position
		
		try {
			h = this.findGoalPoint(pointPath, p);
		} catch(Exception e) {
		}
		
		double alpha = Math.atan2((h.getY() - p.getY()), (h.getX() - p.getX()))
				- v;

		double d = h.getDistance(p);
	
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
		
		//TODO
		/*
		c.clearAllCommands();
		c.circleWithRadius(path.getRadius(), path.isDirection());
		*/
		logger.info(String.format("Command sent to robot: Drive on arc radius %d with turn left: %b", path.getRadius(), path.isDirection()));

		
		
	}
	
	/*
	 * Changes the angle provided by vision into one required by the calculations
	 * @param	angle	the original angle to be converted in radians
	 * 
	 * @return The converted angle so it is measured off the y axis rather than the x
	 */
	public double convertAngle(double angle) {
		
		double newAngle;
		if (angle == 0) {
			newAngle = 0;
		} else {
			newAngle = 2*Math.PI - angle;
		}
		
		logger.debug(String.format("Converted angle from %f to %f", angle, newAngle));
		return newAngle;
	}
	
	/*
	 * Returns the goal point which is 1 lookahead distance away from the robot
	 * @param	points	The list of points on the path
	 * @param	robotPosition	The current robot position
	 * 
	 * @return The goal point
	 */
	public Point2D findGoalPoint(ArrayList<Point> points, Point2D p) throws Exception {
		
		Circle2D zone = new Circle2D(p.getX(), p.getY(), this.lookahead);
		boolean run = true;
		int size = points.size();
		int i = size -1;
		
		Point2D intersect = null;
		
		while(run) {
			
			if (i == size) {
				logger.error("Lookahead point unable to be found");
				throw new Exception("Lookahead point unable to be found");
			}
			
			LineSegment2D line = new LineSegment2D(points.get(i).getX(), points.get(i).getY(), points.get(i+1).getX(), points.get(i+1).getY());
			Collection<Point2D> intersections = zone.getIntersections(line);
			
			
			if (intersections.size() == 1 || intersections.size() == 2) {
				
				if (intersections.size() == 2) {
					logger.debug("I found 2 points, taking the last point.");
				}
				for (Point2D point : intersections) {
					intersect = new Point2D(point);
				}
				logger.debug(String.format("Goal point found at (%f,%f)", intersect.getX(), intersect.getY()));
				run = false;
				
			} else {
				logger.debug("No points found, going to next line segment");
			}
			i++;

		}
				
		return intersect;
	}
	


}