package uk.ac.ed.inf.sdp2012.group7.strategy;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;


import uk.ac.ed.inf.sdp2012.group7.strategy.Arc;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.Node;
import uk.ac.ed.inf.sdp2012.group7.strategy.planning.AllMovingObjects;
import uk.ac.ed.inf.sdp2012.group7.strategy.planning.Plan;
import net.phys2d.raw.shapes.Line;

import org.apache.log4j.Logger;
import uk.ac.ed.inf.sdp2012.group7.control.RobotControl;
import uk.ac.ed.inf.sdp2012.group7.control.Tools;
import uk.ac.ed.inf.sdp2012.group7.vision.VisionTools;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

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

	private final int START_SPEED = 90;

	private static ControlInterface controlInterface = null;
	private static int lookahead;
	private RobotControl c;

	volatile private boolean blocking = false; 
	//Variable which we use to set so only one plan can be fired at once

	//So planning and us are working off the same page
	private int drive = PlanTypes.ActionType.DRIVE.ordinal();
	private int kick = PlanTypes.ActionType.KICK.ordinal();
	private int stop = PlanTypes.ActionType.STOP.ordinal();
	private int angle = PlanTypes.ActionType.ANGLE.ordinal();
	private int forwardWithDistance = PlanTypes.ActionType.FORWARD_WITH_DISTANCE.ordinal();
	private int backwardWithDistance = PlanTypes.ActionType.BACKWARD_WITH_DISTANCE.ordinal();
	private int forwards = PlanTypes.ActionType.FORWARDS.ordinal();
	private int backwards = PlanTypes.ActionType.BACKWARDS.ordinal();

	private boolean firstTime = true;
	private ArrayList<Point> navPoints = new ArrayList<Point>();


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
		Point2D p = new Point2D(plan.getOurRobotPosition());
		double v = plan.getOurRobotAngle();
		return generateArc(p,plan.getPath(),v,lookahead, plan.getNodeWidthInPixels());
	}

	/*
	 * Calculates the Arc that the robot has to follow for the set of points
	 * given using the pure pursuit algorithm
	 */

	public static Arc generateArc(Point2D p, ArrayList<Node> path, double v, int lookahead, double nodeInPixels) {
		// The paper where this maths comes from can be found here
		// http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.135.82&rep=rep1&type=pdf

		Point2D h = null;



		//v = ControlInterfaceTools.convertAngle(v); / No need for this conversion

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
		double anglewithLpoint = (Math.atan2((h.getY() - p.getY()), (h.getX() - p.getX())));
		logger.debug(String.format("Pre-alphaL %f: ", anglewithLpoint));
		double convertedAngle = ControlInterfaceTools.convertAngleAsStrategyDoes( anglewithLpoint);
		logger.debug(String.format("Converted preAngle: %f", convertedAngle));
		double alpha = convertedAngle  - v;
		logger.debug(String.format("Alpha: %f", alpha));
		//alpha is the angle from a line through the axis of the robot to the
		//goal point

		//Here we need to check if this point is actually in front of the 
		//robot. If it is behind then we need to turn the robot around






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
		Arc arc = new Arc(R*conversion, dir);

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



	/**
	 * Returns the goal point which is 1 lookahead distance away from the robot
	 * @param	points	The list of points on the path
	 * @param	robotPosition	The current robot position
	 * 
	 * @return The goal point
	 */
	@SuppressWarnings("null")
	public static Point2D findGoalPoint(ArrayList<Node> p, Point2D pos, int lookahead) throws Exception {

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

	public void drive() {
		c.moveForward();
	}

	public void stop() {
		c.stop();
	}


	@Override
	public void update(Observable arg0, Object arg1) {
		logger.debug("Got a new plan");

		if (!blocking) {
			blocking = true;
			Plan plan = (Plan) arg1;
			if(plan.getPlanType()==PlanTypes.PlanType.PENALTY_OFFENCE.ordinal()) {
				logger.info("Taking a penalty - first turn required angle");
				double turnAngle = ControlInterfaceTools.angleToTurn(plan.getAngleWanted(), plan.getOurRobotAngle());
				c.rotateBy(turnAngle);
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {}
				logger.info("Now kick");
				c.kick();
				c.stop();
			} else if (plan.getPlanType()==PlanTypes.PlanType.PENALTY_DEFENCE.ordinal()) {
				logger.info("Defending a penalty - will repeatedly use non-blocking forwards and backwards");

				if (plan.getAction() == forwards) {
					logger.info("Action is forwards (non-blocking)");
					c.moveForward(11);
				} else if (plan.getAction() == backwards){
					logger.info("Action is backwards (non-blocking)");
					c.moveBackward(11);
				} else {
					logger.info("Action is stop, we don't need to move");
					c.stop();
				}
			} else if (plan.getPlanType()==PlanTypes.PlanType.FREE_PLAY.ordinal()) {

				//This means go for it, usual case
				Arc arcToDrive = chooseArc(plan);
				double checkAngle = Tools.getAngleToFacePoint(plan.getOurRobotPosition(), 
						plan.getOurRobotAngle(), arcToDrive.getGoal());
				
				logger.debug(String.format("Angle to turn to get to goal point: %f", checkAngle));
				
				if (checkAngle > Math.PI/4 || checkAngle < -Math.PI/4) {
					c.rotateBy(checkAngle, true);
				} else {
					implimentArc(arcToDrive, plan);
				}

			} else if (plan.getPlanType()==PlanTypes.PlanType.HALT.ordinal()) {

				logger.info("Action is to stop");
				firstTime = true;
				c.stop();
				logger.info("Command sent to robot: stop");

			}  else if (plan.getPlanType()==PlanTypes.PlanType.MILESTONE_4.ordinal()) {
				logger.info("Inside Milestone 4");
				Point ourPosition = plan.getOurRobotPosition();
				Point navPoint = plan.getNavPoint();
				Point ballPosition = plan.getBallPosition();
				double distance;
				double myAngle = 0;;
				
				if(WorldState.getInstance().useTurning){
					logger.info("Using turning");
					double x1 = ballPosition.getX();
					double y1 = ballPosition.getY();
					double theta1 = plan.getBallAngle();
					double x2 = ourPosition.getX();
					double y2 = ourPosition.getY();
					double theta2 = (plan.getBallAngle() + (Math.PI / 2.0)) % Math.PI;
					double m = y1 - Math.tan(theta1)*x1;
					double n = y2 - Math.tan(theta2)*x2;
					double a;
					double b;
	
					double tantheta1 = Math.tan(theta1);
					double tantheta2 = Math.tan(theta2);
	
	
	
					if(tantheta2 == 0) tantheta2 = 0.000001;
	
					b = (m - n*tantheta1/tantheta2);
					b = b / (1 - tantheta1/tantheta2);
	
					a = (b - n)/tantheta2;
	
					Point interceptPoint = new Point((int)(a + 0.5),(int)(b + 0.5));
	
					myAngle = Tools.getAngleToFacePoint(ourPosition, plan.getOurRobotAngle(), interceptPoint);
					
					if(Math.abs(myAngle) > (Math.PI / 3)){
						logger.info("Need to turn");
						distance = VisionTools.pixelsToCM(ourPosition.distance(interceptPoint)*plan.getNodeWidthInPixels());
					} else {
						logger.info("Don't need to turn");
						distance = VisionTools.pixelsToCM(ourPosition.distance(navPoint)*plan.getNodeWidthInPixels());
					}
					
				} else {
					logger.info("Not using turning");
					distance = VisionTools.pixelsToCM(ourPosition.distance(navPoint)*plan.getNodeWidthInPixels());
					myAngle = Tools.getAngleToFacePoint(ourPosition, plan.getOurRobotAngle(), navPoint);
				}
				
				if(Math.abs(myAngle) > (Math.PI/2.0)){
					distance *= -1;
				}

				if (Math.abs(distance) < 15) {
					logger.info("In place, Stopping");
					c.stop();	
				} else {
					logger.info("Have to travel");
					if(WorldState.getInstance().useTurning){
						logger.info("Using turning");
						if(Math.abs(myAngle) > (Math.PI / 3)){
							logger.info("Need to turn");
							if(firstTime){
								logger.debug("Turning with angle: " + myAngle);
								c.rotateBy(myAngle, true);
								firstTime = false;
							} else {
								logger.info("Wasn't first time so not turning.");
							}
						} else {
							logger.info("Facing close enough");
							firstTime = false;
							if(distance > 0){
								logger.info("Moving forward");
								c.moveForward();
							} else {
								logger.info("Moving backwards");
								c.moveBackward();
							}
						}
					} else {
						logger.info("Not using turning");
						if(distance > 0){
							logger.info("Moving forward");
							c.moveForward();
						} else {
							logger.info("Moving backwards");
							c.moveBackward();
						}
					}
				}
			}
			blocking = false;

		} else {
			logger.info("Plan aready being excuted passing through");
		}
	}

}
