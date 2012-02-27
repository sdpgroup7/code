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
 *
 */
public class AllStaticObjects {
	
	private double nodeInPixels;
	private int pitchTopBuffer;
	private int pitchLeftBuffer; 
	//private int pitch_bottom_buffer;
	//private int pitch_right_buffer; 
	private int height;
	private int width;
	private int boundary;
	private Point theirTopGoalPost;
	private Point theirBottomGoalPost;
	private Point ourTopGoalPost;
	private Point ourBottomGoalPost;
	private Point inFrontOfOurGoal;
	
	//worldstate getInstance
	public WorldState worldState = WorldState.getInstance();
	
	//changes the type of plan to be created
	private int planType;
	
	
	public AllStaticObjects (){
		while(worldState.getLastUpdateTime() == 0){}
		this.theirTopGoalPost = worldState.getOpponentsGoal().getTopLeft();
		this.theirBottomGoalPost = worldState.getOpponentsGoal().getBottomLeft();
		this.ourTopGoalPost = worldState.getOurGoal().getTopLeft();
		this.ourBottomGoalPost = worldState.getOurGoal().getBottomLeft();
		this.pitchTopBuffer  = worldState.getPitch().getTopBuffer();
		this.pitchLeftBuffer = worldState.getPitch().getLeftBuffer();
		//this.pitch_bottom_buffer  = worldState.getPitch().getBottomBuffer();
		//this.pitch_right_buffer = worldState.getPitch().getRightBuffer();
		
		//hard code setting of grid resolution (Grid is used in A*)
		this.height = 29;
		this.width = 58;
		this.nodeInPixels = (double)worldState.getPitch().getWidthInPixels()/(double)this.width;//width in pixels!
		Strategy.logger.info("Node size in pixels: " + nodeInPixels);
		//Boundary around the edges of the pitch, to prevent the robot from hitting the walls
		//So this is dependent on the resolution..
		this.boundary = 3;
		//set defence position
		this.pointInfrontOfGoal();
	}
	
	//Compacts WorldState position point into "Node" centre position
	public Point convertToNode(Point p){
		int x = (int)((double)(p.x - this.pitchLeftBuffer)/this.nodeInPixels);
		int y = (int)((double)(p.y - this.pitchTopBuffer)/this.nodeInPixels);

		
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
			this.inFrontOfOurGoal = new Point(	
					(this.width - (this.boundary - 1)),
					(int)(((this.ourBottomGoalPost.y - this.ourTopGoalPost.y) - this.pitchTopBuffer)/this.nodeInPixels));
			
		}
		else {
			this.inFrontOfOurGoal = new Point(
					(this.boundary),
					(int)(((this.ourBottomGoalPost.y - this.ourTopGoalPost.y) 
														- this.pitchTopBuffer)/this.nodeInPixels));
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

	public int getPlanType(){
		return this.planType;
	}
	
	public void setPlanType(int pT){
		this.planType = pT;
	}

}
