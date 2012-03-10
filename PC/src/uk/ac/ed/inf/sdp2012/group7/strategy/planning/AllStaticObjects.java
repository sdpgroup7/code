/**
 * 
 */
package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;
import java.util.ArrayList;

import org.apache.log4j.Logger;



import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.strategy.newastar.Node;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

/**
 * @author s0955088
 * This class holds all static elements required for planning
 * including the grid setup, the grid is used in A* planning
 * It also contains the commands for running the Planning thread
 *
 */
public class AllStaticObjects {
	
	public static final Logger logger = Logger.getLogger(PlanningThread.class);
	
	//Straight from Visual and remains as visual data :: DO NOT USE IN PLANNING THREAD.
	private int pitchTopBuffer;
	private int pitchLeftBuffer; 
	private int pitchBottomBuffer;
	private int pitchRightBuffer; 
	private int pitchHeight;
	private int pitchWidth;
	
	//Node related
	private int height;
	private int width;
	private int boundary;
	private int centreToEndOfKicker;
	private double nodeInPixels;
	private double widthOfPitchInRobotsCM;
	private double widthOfRobotInNodes;
	
	//In Nodes :: ONLY FOR USE IN PLANNING THREAD
	private Node theirTopGoalPost;
	private Node theirBottomGoalPost;
	private Node inFrontOfOurGoal;
	private Node inFrontOfTheirGoal;
	private Node centreOfTheirGoal;
	private Node centreOfOurGoal;
	private Node ourTopGoalPost;
	private Node ourBottomGoalPost;
	
	//physics
	private double deceleration;
	//time it takes robot on average to get from one node to another
	private double dt;

	//worldstate getInstance
	public WorldState worldState = WorldState.getInstance();
	
	//changes the type of plan to be created
	private int planType;

	//controls planning thread
	private volatile boolean runFlag;
	
	public AllStaticObjects (){
		while(worldState.getLastUpdateTime() == 0){}
		
		//VISUAL
		this.pitchTopBuffer  = worldState.getPitch().getTopBuffer();
		this.pitchLeftBuffer = worldState.getPitch().getLeftBuffer();
		this.pitchBottomBuffer  = worldState.getPitch().getBottomBuffer();
		this.pitchRightBuffer = worldState.getPitch().getRightBuffer();
		this.pitchHeight = worldState.getPitch().getHeightInPixels();
		this.pitchWidth = worldState.getPitch().getWidthInPixels();
		
		//NODE
		//new Node(Point, cost)
		//None of these are used in Astar, so the cost should be irrelevant
		
		this.theirTopGoalPost = convertToNode(worldState.getOpponentsGoal().getTopLeft());
		this.theirBottomGoalPost = convertToNode(worldState.getOpponentsGoal().getBottomLeft());
		this.ourTopGoalPost = convertToNode(worldState.getOurGoal().getTopLeft());
		this.ourBottomGoalPost = convertToNode(worldState.getOurGoal().getBottomLeft());
		
		//A* Settings
		this.height = 29;
		this.width = 58;
		this.nodeInPixels = (double)this.pitchWidth/(double)this.width;//width in pixels!
		
		
		//assume robot is 20cm^2
		//pitch is 243.84 wide
		//pitch is 121.92 high
		//pW / mW = node
		//~How to work out number of nodes in a robot...
		//pW / rW = width of pitch in robots
		//widthOfPitchInRobots / widthOfPitchInNodes = number of nodes per robot...
		this.widthOfPitchInRobotsCM = 243.84 / 20;
		this.widthOfRobotInNodes = this.widthOfPitchInRobotsCM / this.nodeInPixels;
		this.boundary = (int)(this.widthOfRobotInNodes / 2);
		
		
		this.centreToEndOfKicker = 6;
		
		//set defence position
		this.pointInfrontOfGoal();
		this.pointInfrontOfTheirGoal();
		this.centreOfTheirGoal();
		
		this.deceleration = 0;
		//this needs to be found experimentally
		this.dt = 0.1;
	}
	
	
	//Compacts WorldState double into "Node" double
	public double convertDoubleToNode(double d){
		return (d/this.nodeInPixels);
	}
	
	
	//Compacts WorldState position point into "Node" centre position
	//So Vision gives us a Point, we convert to a Node.
	public Node convertToNode(Point p){
		int x = (int)((double)(p.x - (this.pitchLeftBuffer - 1))/this.nodeInPixels);
		int y = (int)((double)(p.y - (this.pitchTopBuffer - 1))/this.nodeInPixels);
		return new Node(new Point(x,y));
	}
	
	//Compacts WorldState position points into "Node" centre positions
	public ArrayList<Node> convertToNodes(ArrayList<Point> l){
		ArrayList<Node> nodePoints = new ArrayList<Node>();

		for (Point p : l) {
			nodePoints.add(convertToNode(p));
		}

		return nodePoints;
	}
	
	//Method for finding the centre point just in front of our goal...
	//Return this as a node!
	private void pointInfrontOfGoal(){
		if(worldState.getShootingDirection() == 1){
			this.inFrontOfOurGoal = new Node(new Point(this.boundary,this.height/2));
			
		}
		else {
			this.inFrontOfOurGoal = new Node(new Point((this.width - this.boundary),this.height/2));
		}
	}
	
	//Method for finding the centre point just in front of their goal...
	//Return this as a node!
	private void pointInfrontOfTheirGoal(){
		if(worldState.getShootingDirection() == 1){
			this.inFrontOfTheirGoal = new Node(new Point((this.width - this.boundary),this.height/2));
		}
		else {
			this.inFrontOfTheirGoal = new Node(new Point(this.boundary,this.height/2));
		}
	}
	
	//Method for finding the centre point in their goal...
	//Return this as a node!
	private void centreOfTheirGoal(){
		if(worldState.getShootingDirection() == -1){
			this.centreOfTheirGoal = new Node(new Point(1,this.height/2));
		}
		else {
			this.centreOfTheirGoal = new Node(new Point(this.width - 2,this.height/2));
		}
	}
	
	//Method for finding the centre point in their goal...
	//Return this as a node!
	private void centreOfOurGoal(){
		if(worldState.getShootingDirection() == -1){
			this.centreOfOurGoal = new Node(new Point(this.width - 2,this.height/2));
		}
		else {
			this.centreOfOurGoal = new Node(new Point(1,this.height/2));
		}
	}
	

//ALL GETTERS SHOULD BE RETURNING AS NODES! NOT AS VISUAL POINTS!
	
	
	public double getNodeInPixels() {
		return this.nodeInPixels;
	}

	public int getPitchTopBuffer() {
		return this.pitchTopBuffer;
	}

	public int getPitchLeftBuffer() {
		return this.pitchLeftBuffer;
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}
	
	public int getBoundary() {
		return this.boundary;
	}
	
	public Node getTheirTopGoalPost() {
		return theirTopGoalPost;
	}
	
	public Node getTheirBottomGoalPost() {
		return theirBottomGoalPost;
	}
	
	public Node getInFrontOfOurGoal() {
		this.pointInfrontOfGoal();
		return inFrontOfOurGoal;
	}
	
	public Node getInFrontOfTheirGoal() {
		this.pointInfrontOfTheirGoal();
		return inFrontOfTheirGoal;
	}
	
	public Node getCentreOfTheirGoal() {
		this.centreOfTheirGoal();
		return centreOfTheirGoal;
	}
	
	public Node getCentreOfOurGoal() {
		this.centreOfOurGoal();
		return centreOfOurGoal;
	}

	public int getPlanType(){
		return this.planType;
	}
	
	public void setPlanType(int pT){
		synchronized(this){
			Strategy.logger.info("PLAN CHANGED : " + this.planType);
			this.planType = pT;
		}
	}

	public void stopRun() {
		synchronized(this){
			Strategy.logger.info("STOPPED : " + this.runFlag);
			this.runFlag = false;
		}
	}
	
	public void startRun() {
		synchronized(this){
			Strategy.logger.info("STARTED : " + this.runFlag);
			this.runFlag = true;
		}
	}
	
	public boolean getRunFlag(){
		return this.runFlag;

	}

	public int getPitchHeight() {
		return this.pitchHeight;
	}

	public int getPitchWidth() {
		return this.pitchWidth;
	}
	
	public double getDeceleration(){
		return this.deceleration;
	}
	
	public int getPitchBottomBuffer() {
		return pitchBottomBuffer;
	}

	public int getPitchRightBuffer() {
		return pitchRightBuffer;
	}


	public double getCentreToEndOfKicker() {
		// TODO Auto-generated method stub
		return this.centreToEndOfKicker;
	}

}
