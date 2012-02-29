/**
 * 
 */
package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;
import java.util.ArrayList;



import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

/**
 * @author s0955088
 * This class holds all static elements required for planning
 * including the grid setup, the grid is used in A* planning
 * It also contains the commands for running the Planning thread
 *
 */
public class AllStaticObjects {
	
	private double nodeInPixels;
	private int pitchTopBuffer;
	private int pitchLeftBuffer; 
	private int pitchBottomBuffer;
	private int pitchRightBuffer; 

	private int height;
	private int width;
	private int boundary;
	private int pitchHeight;
	private int pitchWidth;
	private double deceleration;
	
	private Point theirTopGoalPost;
	private Point theirBottomGoalPost;
	private Point ourTopGoalPost;
	private Point ourBottomGoalPost;
	private Point inFrontOfOurGoal;
	private Point inFrontOfTheirGoal;
	private Point centreOfTheirGoal;

	//worldstate getInstance
	public WorldState worldState = WorldState.getInstance();
	
	//changes the type of plan to be created
	private int planType;

	//controls planning thread
	private volatile boolean runFlag;
	
	
	public AllStaticObjects (){
		while(worldState.getLastUpdateTime() == 0){}
		this.theirTopGoalPost = worldState.getOpponentsGoal().getTopLeft();
		this.theirBottomGoalPost = worldState.getOpponentsGoal().getBottomLeft();
		this.ourTopGoalPost = worldState.getOurGoal().getTopLeft();
		this.ourBottomGoalPost = worldState.getOurGoal().getBottomLeft();
		this.pitchTopBuffer  = worldState.getPitch().getTopBuffer();
		this.pitchLeftBuffer = worldState.getPitch().getLeftBuffer();
		this.pitchBottomBuffer  = worldState.getPitch().getBottomBuffer();
		this.pitchRightBuffer = worldState.getPitch().getRightBuffer();
		this.pitchHeight = worldState.getPitch().getHeightInPixels();
		this.pitchWidth = worldState.getPitch().getWidthInPixels();
		
		
		//hard code setting of grid resolution (Grid is used in A*)
		this.height = 29;
		this.width = 58;
		this.nodeInPixels = (double)this.pitchWidth/(double)this.width;//width in pixels!
		Strategy.logger.info("Node size in pixels: " + nodeInPixels);
		//Boundary around the edges of the pitch, to prevent the robot from hitting the walls
		//So this is dependent on the resolution..
		this.boundary = 3;
		//set defence position
		this.pointInfrontOfGoal();
		this.pointInfrontOfTheirGoal();
		this.centreOfTheirGoal();
		
		this.deceleration = 0;
	}
	
	//Compacts WorldState position point into "Node" centre position
	public Point convertToNode(Point p){
		int x = (int)((double)(p.x - (this.pitchLeftBuffer - 1))/this.nodeInPixels);
		int y = (int)((double)(p.y - (this.pitchTopBuffer - 1))/this.nodeInPixels);

		
		return new Point(x,y);
	}
	
	//Compacts WorldState position points into "Node" centre positions
	public ArrayList<Point> convertToNodes(ArrayList<Point> l){
		ArrayList<Point> nodePoints = new ArrayList<Point>();

		for (Point p : l) {
			nodePoints.add(convertToNode(p));
		}

		return nodePoints;
	}
	
	//Method for finding the centre point just in front of our goal...
	//Return this as a node!
	private void pointInfrontOfGoal(){
		if(worldState.getShootingDirection() == 1){
			this.inFrontOfOurGoal = new Point(this.boundary,this.height/2);
			
		}
		else {
			this.inFrontOfOurGoal = new Point((this.width - this.boundary),this.height/2);
		}
	}
	
	//Method for finding the centre point just in front of their goal...
	//Return this as a node!
	private void pointInfrontOfTheirGoal(){
		if(worldState.getShootingDirection() == -1){
			this.inFrontOfTheirGoal = new Point(this.boundary,this.height/2);
			
		}
		else {
			this.inFrontOfTheirGoal = new Point((this.width - this.boundary),this.height/2);
		}
	}
	
	//Method for finding the centre point in their goal...
	//Return this as a node!
	private void centreOfTheirGoal(){
		if(worldState.getShootingDirection() == -1){
			this.inFrontOfTheirGoal = new Point(0,this.height/2);
			
		}
		else {
			this.inFrontOfTheirGoal = new Point(this.width,this.height/2);
		}
	}
	


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
	
	public Point getTheirTopGoalPost() {
		return theirTopGoalPost;
	}
	
	public Point getTheirBottomGoalPost() {
		return theirBottomGoalPost;
	}
	
	public Point getInFrontOfOurGoal() {
		return inFrontOfOurGoal;
	}
	
	public Point getInFrontOfTheirGoal() {
		return inFrontOfTheirGoal;
	}
	
	public Point getCentreOfTheirGoal() {
		return centreOfTheirGoal;
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

}
