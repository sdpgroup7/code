package uk.ac.ed.inf.sdp2012.group7.strategy;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;


import uk.ac.ed.inf.sdp2012.group7.strategy.Arc;
import uk.ac.ed.inf.sdp2012.group7.strategy.planning.Plan;
import org.apache.log4j.Logger;
import uk.ac.ed.inf.sdp2012.group7.control.RobotControl;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;
import uk.ac.ed.inf.sdp2012.group7.vision.VisionTools;

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
public class ControlInterface implements Observer {

	
	public static final Logger logger = Logger.getLogger(ControlInterface.class);
	
	private WorldState world = WorldState.getInstance();
	private VisionTools vtools = new VisionTools();
	
	private int lookahead;
	private RobotControl c;
	
	//So planning and us are working off the same page
	private int drive = PlanTypes.ActionType.DRIVE.ordinal();
	private int kick = PlanTypes.ActionType.KICK.ordinal();
	private int stop = PlanTypes.ActionType.STOP.ordinal();
	
	private int waitTime = 200; //Time robot waits between commands
	

	public ControlInterface(int lookahead) {
		this.lookahead = lookahead;
		this.c = new RobotControl();
		this.c.startCommunications();
		this.c.changeSpeed(30);
		
		
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
	public Arc chooseArc(Plan plan) {
		// The paper where this maths comes from can be found here
		// http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.135.82&rep=rep1&type=pdf
		
		Point2D h = null;
		Point2D p = new Point2D(plan.getOurRobotPositionVisual());
		double v = plan.getOurRobotAngle();
		
		v = this.convertAngle(v);
		
		try {
			h = this.findGoalPoint(plan);
		} catch(Exception e) {
			logger.debug(e);
			if(plan.getPath().size() > 1){
				h = new Point2D(plan.getPath().get(plan.getPath().size() -1));
			} else {
				h = new Point2D(plan.getPath().get(0));
			}
		}
		
		double alpha = Math.atan2((h.getY() - p.getY()), (h.getX() - p.getX()))	- v;
		logger.debug(String.format("Alpha: %f", alpha));

		double d = h.getDistance(p);
		logger.debug(String.format("d: %f",d));
	
		double xhc = d * Math.cos(alpha);
		logger.debug(String.format("xhc: %f",xhc));

		double R = Math.abs((Math.pow(d, 2) / (2 * xhc)));
		logger.debug(String.format("R: %f",R));

		boolean dir;

		// If the arc is to the left (relative to the robot) then dir is true,
		// else if it is going to the right then it is false
		if (xhc >= 0) {
			dir = false;
		} else {
			dir = true;
		}

		Arc arc = new Arc(R, dir, plan.getAction());

		return arc;

	}

	public void implimentArc(Arc path, Plan plan) {
		
		
		
		
		int pixelsPerNode = world.getPitch().getHeight()/plan.getHeightInNodes();
		logger.debug(String.format("pixelsPerNode: %d", pixelsPerNode));
		double conversion = (double) vtools.pixelsToCM(pixelsPerNode);
		
		logger.debug(String.format("Conversion value: %f", conversion));

		if (plan.getAction() == drive) {
			
			int converted = (int)(conversion*path.getRadius());
			logger.info("Action is to drive");
			c.clearAllCommands();
			this.c.circleWithRadius(converted , path.isDirection());
			logger.info(String.format("Command sent to robot: Drive on arc radius %d with turn left: %b", converted, path.isDirection()));
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {}
		
		} else if (plan.getAction() == kick) {
			logger.info("Action is to kick");
			int converted = (int)(conversion*path.getRadius());
			this.c.circleWithRadius(converted , path.isDirection());
			logger.info(String.format("Command sent to robot: Drive on arc radius %d with turn left: %b", converted, path.isDirection()));
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {}
			c.kick();
			logger.info("Command sent to robot: kick");
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {}
			
		} else if (plan.getAction() == stop) {
			logger.info("Action is to stop");
			c.stop();
			logger.info("Command sent to robot: stop");
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {}
			
		
		}
		
		
		
		
		
		
		
		
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
	public Point2D findGoalPoint(Plan p) throws Exception {
		
		Circle2D zone = new Circle2D(p.getOurRobotPositionVisual().getX(), p.getOurRobotPositionVisual().getY(), this.lookahead);
		logger.debug(String.format("Zone centre: (%f,%f)",p.getOurRobotPositionVisual().getX(),p.getOurRobotPositionVisual().getY()));
		boolean run = true;
		int size = p.getPath().size();
		int i = 0;
		
		Point2D intersect = null;
		
		while(run) {
			
			if (i == size-1) {
				logger.error("Lookahead point unable to be found");
				throw new Exception("Lookahead point unable to be found");
			} 
			
			LineSegment2D line = new LineSegment2D(p.getPath().get(i).getX(), p.getPath().get(i).getY(), p.getPath().get(i+1).getX(), p.getPath().get(i+1).getY());
			logger.debug(String.format("Line Points: P1: (%f,%f) P2: (%f,%f)",p.getPath().get(i).getX(), p.getPath().get(i).getY(), p.getPath().get(i+1).getX(), p.getPath().get(i+1).getY() ));
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
	
	public void kick() {
		c.clearAllCommands();
		c.kick();
	}
	
	public void drive() {
		c.clearAllCommands();
		c.changeSpeed(30);
		c.moveForward(60);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		logger.debug("Got a new plan");
		Plan plan = (Plan) arg1;
		Arc arcToDrive = this.chooseArc(plan);
		this.implimentArc(arcToDrive, plan);
		
	}

	

	


}
