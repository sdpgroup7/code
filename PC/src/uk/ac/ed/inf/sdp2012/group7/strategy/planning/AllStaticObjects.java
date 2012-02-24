/**
 * 
 */
package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;
import java.util.ArrayList;


import uk.ac.ed.inf.sdp2012.group7.vision.Vision;

/**
 * @author s0955088
 * This class holds all static elements required for planning
 * including the grid setup, the grid is used in A* planning
 *
 */
public class AllStaticObjects {
	
	private int nodeInPixels; 
	private int pitch_top_buffer;
	private int pitch_left_buffer; 
	private int height;
	private int width;
	private int boundary;
	private Point their_top_goal_post;
	private Point their_bottom_goal_post;
	
	public AllStaticObjects (){
		
		this.their_top_goal_post = Vision.worldState.getOpponentsGoal().getTopLeft();
		this.their_bottom_goal_post = Vision.worldState.getOpponentsGoal().getBottomLeft();
		this.nodeInPixels = Vision.worldState.getPitch().getWidthInPixels()/50;//width in pixels!
		this.pitch_top_buffer  = Vision.worldState.getPitch().getTopBuffer();
		this.pitch_left_buffer = Vision.worldState.getPitch().getLeftBuffer();
		//hard code setting of grid resolution (Grid is used in A*)
		this.height = 25;
		this.width = 50;
		//Boundary around the edges of the pitch, to prevent the robot from hitting the walls
		//So this is dependent on the resolution..
		this.boundary = 3;
	}
	
	//Compacts WorldState position point into "Node" centre position
	public Point convertToNode(Point p){
		int x = (int)Math.floor((p.x - this.pitch_left_buffer)/this.nodeInPixels);
		int y = (int)Math.floor((p.y - this.pitch_top_buffer)/this.nodeInPixels);
		Point grid_point = new Point(x,y);
		return grid_point;
	}
	
	//Compacts WorldState position points into "Node" centre positions
	public ArrayList<Point> convertToNodes(ArrayList<Point> p){

		ArrayList<Point> node_points = new ArrayList<Point>();

		for (Point obstacle : p) {
			int x = (int)Math.floor((obstacle.x - this.pitch_left_buffer)/this.nodeInPixels);
			int y = (int)Math.floor((obstacle.y - this.pitch_top_buffer)/this.nodeInPixels);
			Point grid_point = new Point(x,y);
			node_points.add(grid_point);
		}

		return node_points;
	}
	
	
	//This method is for creating a buffer around all of the walls
	//and has a different signature so that we can add in 
	//However this method does create obstacles in front of both goals...
	public ArrayList<Point> addBoundary(ArrayList<Point> obstacles){
		
		for(int y = 0; y < this.height; y++){
			for (int b=0; b < boundary; b++) {
				obstacles.add(new Point(b,y));
				obstacles.add(new Point(this.width - b,y));
			}
		}
		for(int x = boundary; x < this.width -boundary; x++){
			for (int b=0; b < boundary; b++) {
				obstacles.add(new Point(x,b));
				obstacles.add(new Point(x,this.height-b));
			}
		}
		
		return obstacles;

	}

	public int getNodeInPixels() {
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

}
