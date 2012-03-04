package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;

import uk.ac.ed.inf.sdp2012.group7.vision.VisionTools;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

public class AllMovingObjects {
	
	//REQUIRED TO TURN ALL TO NODES
	private AllStaticObjects allStaticObjects;
	
	
	//ALL SHOULD BE CONVERTED TO NODE
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
	
	public AllMovingObjects(AllStaticObjects aSO) {
		update(aSO);
	}
	
	
	public void update(AllStaticObjects aSO){
		
		this.allStaticObjects = aSO;
		
		//CONVERT ALL TO NODES
		
		this.ourPosition = allStaticObjects.convertToNode(worldState.getOurRobot().getPosition().getCentre());
		
		this.theirPosition = allStaticObjects.convertToNode(worldState.getOpponentsRobot().getPosition().getCentre());
		
		this.ballPosition = allStaticObjects.convertToNode(worldState.getBall().getPosition().getCentre());
		
		//CONVERT ANGLES REQUIRED? DARIE
		
	    this.ourAngle = convertAngle(worldState.getOurRobot().getAngle());
	    
	    this.theirAngle = convertAngle(worldState.getOpponentsRobot().getAngle());
	    
	    this.ballAngle = convertAngle(worldState.getBall().getAngle());
		
	    //CONVERSION? DARIE
	    
		this.ourVelocity = worldState.getOurRobot().getVelocity();
		
		this.theirVelocity = worldState.getOpponentsRobot().getVelocity();
		
		this.ballVelocity = worldState.getBall().getVelocity();	    
	}
	
	
	//SO ARE THESE CORRECT DARIE?
	
	// method to trasform an angle between the coordinate system used in worldstate and the standard coordinate system used in strategy 
	public double convertAngle(double angle) {
		return Math.PI/2 - angle;
	}
	//   This uses the method from Vision to find angle between 2 points, then converts it into strategy coordinate system.
	public double angleBetween(Point p1, Point p2) {
		double angle = this.convertAngle(VisionTools.convertAngle(Math.atan2(p2.y - p1.y, p2.x - p1.x)));		
		return angle;
	}
	
	public double angleBetweenBasic(Point p1, Point p2) {
		double angle = Math.atan2(p2.y - p1.y, p2.x - p1.x);
		return angle;
	}
	
	//ALL GETTERS SHOULD RETURN IN NODE SYSTEM
	
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
