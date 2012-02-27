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
	private int pitch_top_buffer;
	private int pitch_left_buffer; 
	//private int pitch_bottom_buffer;
	//private int pitch_right_buffer; 
	private int height;
	private int width;
	private int boundary;
	private Point their_top_goal_post;
	private Point their_bottom_goal_post;
	private Point our_top_goal_post;
	private Point our_bottom_goal_post;
	private Point infront_of_our_goal;
	
	//worldstate getInstance
	public WorldState worldState = WorldState.getInstance();
	
	//changes the type of plan to be created
	private int plan_type;
	
	
	public AllStaticObjects (){
		while(worldState.getLastUpdateTime() == 0){}
		this.their_top_goal_post = worldState.getOpponentsGoal().getTopLeft();
		this.their_bottom_goal_post = worldState.getOpponentsGoal().getBottomLeft();
		this.our_top_goal_post = worldState.getOurGoal().getTopLeft();
		this.our_bottom_goal_post = worldState.getOurGoal().getBottomLeft();
		this.pitch_top_buffer  = worldState.getPitch().getTopBuffer();
		this.pitch_left_buffer = worldState.getPitch().getLeftBuffer();
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
		int x = (int)((double)(p.x - this.pitch_left_buffer)/this.nodeInPixels);
		int y = (int)((double)(p.y - this.pitch_top_buffer)/this.nodeInPixels);

		
		return new Point(x,y);
	}
	
	//Compacts WorldState position points into "Node" centre positions
	public ArrayList<Point> convertToNodes(ArrayList<Point> l){
		ArrayList<Point> node_points = new ArrayList<Point>();

		for (Point p : l) {
			node_points.add(convertToNode(p));
		}

		return node_points;
	}
	
	//Method for finding the centre point just in front of our goal...
	//Return this as a node!
	private void pointInfrontOfGoal(){
		if(worldState.getShootingDirection() == 1){
			this.infront_of_our_goal = new Point(	
					(this.width - (this.boundary - 1)),
					(int)(((this.our_bottom_goal_post.y - this.our_top_goal_post.y) - this.pitch_top_buffer)/this.nodeInPixels));
			
		}
		else {
			this.infront_of_our_goal = new Point(
					(this.boundary),
					(int)(((this.our_bottom_goal_post.y - this.our_top_goal_post.y) 
														- this.pitch_top_buffer)/this.nodeInPixels));
		}
	}
	


	public double getNodeInPixels() {
		return this.nodeInPixels;
	}

	public int getPitch_top_buffer() {
		return this.pitch_top_buffer;
	}

	public int getPitch_left_buffer() {
		return this.pitch_left_buffer;
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
	
	public Point getTheir_top_goal_post() {
		return their_top_goal_post;
	}
	
	public Point getTheir_bottom_goal_post() {
		return their_bottom_goal_post;
	}
	
	public Point getInfront_of_our_goal() {
		return infront_of_our_goal;
	}

	public int getPlanType(){
		return this.plan_type;
	}
	
	public void setPlanType(int pT){
		this.plan_type = pT;
	}

}
