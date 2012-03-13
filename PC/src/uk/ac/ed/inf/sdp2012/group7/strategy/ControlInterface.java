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
	
	private final int START_SPEED = 30;
	
	private static ControlInterface controlInterface = null;
	private static int lookahead;
	private RobotControl c;
	
	//So planning and us are working off the same page
	private int drive = PlanTypes.ActionType.DRIVE.ordinal();
	private int kick = PlanTypes.ActionType.KICK.ordinal();
	private int stop = PlanTypes.ActionType.STOP.ordinal();
	private int angle = PlanTypes.ActionType.ANGLE.ordinal();
	private int forwardWithDistance = PlanTypes.ActionType.FORWARD_WITH_DISTANCE.ordinal();
	private int backwardWithDistance = PlanTypes.ActionType.BACKWARD_WITH_DISTANCE.ordinal();
	

	private ControlInterface(int lookahead) {
		ControlInterface.lookahead = lookahead;
		this.c = new RobotControl();
		this.c.startCommunications();
		this.c.changeSpeed(START_SPEED);
	}
	
	public static ControlInterface getInstance(int lookahead){
		if(controlInterface == null){
			controlInterface = new ControlInterface(lookahead);
		}
		return controlInterface;
	}
	
	public static ControlInterface getInstance(){
		return getInstance(5);
	}
	
	/**
	 * Generates an arc which the robot needs to travel on to get to the goal point
	 * @param plan
	 * @return
	 */
	public static Arc chooseArc(Plan plan){
		Point2D p = new Point2D(plan.getOurRobotPositionVisual());
		double v = plan.getOurRobotAngle();
		return generateArc(p,plan.getPath(),v,plan.getAction(),lookahead, plan.getNodeInPixels());
	}
	
	/*
	 * Calculates the Arc that the robot has to follow for the set of points
	 * given using the pure pursuit algorithm
	 */

	public static Arc generateArc(Point2D p, ArrayList<Point> path, double v, 
			int planAction, int lookahead, double nodeInPixels) {
		// The paper where this maths comes from can be found here
		// http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.135.82&rep=rep1&type=pdf
		
		Point2D h = null;

		
		v = ControlInterfaceTools.convertAngle(v); 
		
        //Attempts to find a goal point. Searches along the path given by plan
        //and finds a point which is exactly a lookahead distance (euclidian)
        //away from the robot. This point can be a point on a line between two
        //of the path way point.
		try {
			h = findGoalPoint(path, p, lookahead);
		} catch(Exception e) {
            //Catches if the path is not long enough. Will just take one of the
            //points below the lookahead distance
			logger.debug(e);
			if(path.size() > 1){
				h = new Point2D(path.get(path.size() -1));
			} else {
				h = new Point2D(0,0);
			}
		}
		
		logger.debug(String.format("v: %f", v));
		
		double alpha = Math.atan2((h.getY() - p.getY()), (h.getX() - p.getX()))	- v;
		logger.debug(String.format("Alpha: %f", alpha));
        //alpha is the angle from a line through the axis of the robot to the
        //goal point

		double d = h.getDistance(p);
		logger.debug(String.format("d: %f",d));
        //This is the distance from the robot to the goal point. Should always
        //be te lookahead distance unless the path was too short.
	
		double xhc = d * Math.cos(alpha);
		logger.debug(String.format("xhc: %f",xhc));
        //This is the x coordinate in refrence to the viechales corrdinate
        //system. Ie the y axis is projected in the direction the robot is
        //facing and the x axis is orthognal to this and passes through the
        //axel system.

		double R = Math.abs((Math.pow(d, 2) / (2 * xhc)));
		logger.debug(String.format("R: %f",R));
        //THis is the radius of the circle the robot has to drive on. An arc is
        //a circle in essance.

		boolean dir;

		// If the arc is to the left (relative to the robot) then dir is true,
		// else if it is going to the right then it is false
		if (xhc >= 0) {
			dir = false;
		} else {
			dir = true;
		}
		
		double conversion = (double) VisionTools.pixelsToCM(nodeInPixels);
		Arc arc = new Arc(R*conversion, dir, planAction);

		return arc;

	}

	public void implimentArc(Arc path, Plan plan) {

		if (plan.getAction() == drive) {
			
			logger.info("Action is to drive");
			
			this.c.circleWithRadius((int)(path.getRadius()+0.5) , path.isLeft());
			logger.info(String.format("Command sent to robot: Drive on arc " +
					"radius %d with turn left: %b", 
					(int)(path.getRadius()+0.5), path.isLeft()));
		
		} else if (plan.getAction() == kick) {
			logger.info("Action is to kick");
			this.c.circleWithRadius((int)(path.getRadius()+0.5) , path.isLeft());
			logger.info(String.format("Command sent to robot: Drive on arc " +
					"radius %d with turn left: %b", 
					(int)(path.getRadius()+0.5), path.isLeft()));
			c.kick();
			logger.info("Command sent to robot: kick");
			
		} else if (plan.getAction() == stop) {
			logger.info("Action is to stop");
			c.stop();
			logger.info("Command sent to robot: stop");			
		
		} else if (plan.getAction() == angle) {
			logger.info("Action is to turn");
			c.stop();
			logger.info("Command sent to robot: stop");
			double turnAngle = ControlInterfaceTools.angleToTurn(plan.getAngleWanted(), 
					plan.getOurRobotAngle());
			c.rotateBy(turnAngle);
		}
	}
	
		
	
	/*
	 * Returns the goal point which is 1 lookahead distance away from the robot
	 * @param	points	The list of points on the path
	 * @param	robotPosition	The current robot position
	 * 
	 * @return The goal point
	 */
	@SuppressWarnings("null")
	public static Point2D findGoalPoint(ArrayList<Point> p, Point2D pos, int lookahead) throws Exception {
		
		Circle2D zone = new Circle2D(pos.getX(), pos.getY(), lookahead);
		logger.debug(String.format("Zone centre: (%f,%f)",pos.getX(),pos.getY()));
		int size = p.size();
		int i = 0;
		
		Point2D intersect = null;
		
		while(true) {
			
			if (i == size-1) {
				logger.error("Lookahead point unable to be found");
				throw new Exception("Lookahead point unable to be found");
			} 
			
			LineSegment2D line = new LineSegment2D(p.get(i).getX(), p.get(i).getY(), p.get(i+1).getX(), p.get(i+1).getY());
			logger.debug(String.format("Line Points: P1: (%f,%f) P2: (%f,%f)",p.get(i).getX(), p.get(i).getY(), p.get(i+1).getX(), p.get(i+1).getY() ));
			Collection<Point2D> intersections = zone.getIntersections(line);
			
			if (intersections.size() == 1 || intersections.size() == 2) {

				if (intersections.size() == 2) {
					logger.debug("I found 2 points, taking the first point.");
				}
				for (Point2D point : intersections) {
					intersect = new Point2D(point);
				}
				logger.debug(String.format("Goal point found at (%f,%f)", intersect.getX(), intersect.getY()));
				break;

			} else {
				logger.debug("No points found, going to next line segment");
			}
			i++;

		}
				
		return intersect;
	}
	

		
	
	public void kick() {
		c.kick();
	}
	
	public void stopKick() {
		c.stopKick();
	}
	
	public void drive() {
		c.moveForward();
	}

	public void stop() {
		c.stop();
	}
	
	
	@Override
	public void update(Observable arg0, Object arg1) {
		logger.debug("Got a new plan");
		Plan plan = (Plan) arg1;
		if(plan.getPlanType()==PlanTypes.PlanType.PENALTY_OFFENCE.ordinal()) {
			logger.info("Taking a penalty - first turn required angle");
			logger.info(String.format("Angles: %f", plan.getAngleWanted()));
			double turnAngle = plan.getAngleWanted();
			logger.info("Turn angle: " + turnAngle);
			c.rotateBy(turnAngle,true);
			logger.info("Now kick");
			this.kick(); //This line must be "this.kick()". It fails if you use "c.kick()"
			//this.stop();
			//this.stopKick();
		} else if (plan.getPlanType()==PlanTypes.PlanType.PENALTY_DEFENCE.ordinal()) {
			logger.info("Defending a penalty - will repeatedly use moveForward(distance) and moveBackward(int)");
			if (plan.getAction() == forwardWithDistance) {
				logger.info("Action is moveForwards("+plan.getDistanceInCM()+")"); 
				c.moveForward((int)plan.getDistanceInCM());
			} else {
				logger.info("Action is moveBackwards("+plan.getDistanceInCM()+")");
				c.moveBackward((int)plan.getDistanceInCM());
			}
		} else if (plan.getPlanType()==PlanTypes.PlanType.FREE_PLAY.ordinal()) {
				
			//This means go for it, usual case
			Arc arcToDrive = chooseArc(plan);
			implimentArc(arcToDrive, plan);	
			
		} else if (plan.getPlanType()==PlanTypes.PlanType.HALT.ordinal()) {
			
			logger.info("Action is to stop");
			c.stop();
			logger.info("Command sent to robot: stop");
			
		} else {}
		
	}


}
