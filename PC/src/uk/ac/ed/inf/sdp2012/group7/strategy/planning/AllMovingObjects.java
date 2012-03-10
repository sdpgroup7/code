package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import uk.ac.ed.inf.sdp2012.group7.vision.VisionTools;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;
import uk.ac.ed.inf.sdp2012.group7.strategy.newastar.Node;

public class AllMovingObjects {
	
	//REQUIRED TO TURN ALL TO NODES
	private AllStaticObjects allStaticObjects;
	
	
	//ALL SHOULD BE CONVERTED TO NODE
	private Node ourPosition ;
	private Node theirPosition;
	private Node ballPosition;
	
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
		//new Node(Point, Cost);
		
		//We don't have a cost at all
		this.ourPosition = allStaticObjects.convertToNode(worldState.getOurRobot().getPosition().getCentre());
		
		//We don't want to drive into them, so we set the cost 'high'
		this.theirPosition = allStaticObjects.convertToNode(worldState.getOpponentsRobot().getPosition().getCentre());
		this.theirPosition.setgCost(1000);
		
		//We don't want to nudge the ball as we drive around it, so we set the cost relatively high
		this.ballPosition = allStaticObjects.convertToNode(worldState.getBall().getPosition().getCentre());
		this.ballPosition.setgCost(100);
		
		//Angles from Vision are in the same system as ours, so no conversion required
		
	    this.ourAngle = worldState.getOurRobot().getAngle();
	    
	    this.theirAngle = worldState.getOpponentsRobot().getAngle();
	    
	    this.ballAngle = worldState.getBall().getAngle();
		
	    //VELOCITY which is in Pixels per Second, hence we use conversion into node
	    
		this.ourVelocity = allStaticObjects.convertDoubleToNode(worldState.getOurRobot().getVelocity());
		
		this.theirVelocity = allStaticObjects.convertDoubleToNode(worldState.getOpponentsRobot().getVelocity());
		
		this.ballVelocity = allStaticObjects.convertDoubleToNode(worldState.getBall().getVelocity());	    
	}
	
	
	public double angleBetweenBasic(Node p1, Node p2) {
		double angle = Math.atan2(p2.y - p1.y, p2.x - p1.x);
		return angle;
	}
	
	//ALL GETTERS SHOULD RETURN IN NODE SYSTEM
	
	public Node getOurPosition() {
		return ourPosition;
	}
	
	public Node getTheirPosition() {
		return theirPosition;
	}
	
	public Node getBallPosition() {
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
