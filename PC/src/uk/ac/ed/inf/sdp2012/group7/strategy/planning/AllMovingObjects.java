package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;

import uk.ac.ed.inf.sdp2012.group7.vision.VisionTools;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

public class AllMovingObjects {
	
	private Point ourPosition ;
	private Point theirPosition;
	private Point ballPosition;
	private double ourVelocity;
	private double theirVelocity;
	private double ballVelocity;
	private double ourAngle;
	private double theirAngle;
	private double ballAngle;
	
	//worldstate getInstance
	public WorldState worldState = WorldState.getInstance();
	
	public AllMovingObjects() {
		update();
	}
	
	
	public void update(){
		
		this.ourPosition = worldState.getOurRobot().getPosition().getCentre();
		
		this.theirPosition = worldState.getOpponentsRobot().getPosition().getCentre();
		
		this.ballPosition = worldState.getBall().getPosition().getCentre();
		
		this.ourVelocity = worldState.getOurRobot().getVelocity();
		
		this.theirVelocity = worldState.getOpponentsRobot().getVelocity();
		
		this.ballVelocity = worldState.getBall().getVelocity();
		
	    this.ourAngle = worldState.getOurRobot().getAngle();
	    
	    this.theirAngle = worldState.getOpponentsRobot().getAngle();
	    
	    this.ballAngle = worldState.getBall().getAngle();
	    
	}
	
	
	// method to trasform an angle between the coordinate system used in worldstate and the standard coordinate system used in strategy 
	public double convertAngle(double angle) {
		return Math.PI/2 - angle;
	}
	//   This uses the method from Vision to find angle between 2 points, then converts it into strategy coordinate system.
	public double angleBetween(Point p1, Point p2) {
		double angle = this.convertAngle(VisionTools.convertAngle(Math.atan2(p2.y - p1.y, p2.x - p1.x)));		
		return angle;
	}
	
	public Point getOurPosition() {
		return ourPosition;
	}
	
	public Point getTheirPosition() {
		return theirPosition;
	}
	
	public Point getBallPosition() {
		return ballPosition;
	}
	
	public double getOurVelocity() {
		return ourVelocity;
	}
	
	public double getTheirVelocity() {
		return theirVelocity;
	}
	
	public double getBallVelocity() {
		return ballVelocity;
	}
	
	public double getOurAngle() {
		return ourAngle;
	}
	
	public double getTheirAngle() {
		return theirAngle;
	}
	
	public double getBallAngle() {
		return ballAngle;
	}
	
	

}
