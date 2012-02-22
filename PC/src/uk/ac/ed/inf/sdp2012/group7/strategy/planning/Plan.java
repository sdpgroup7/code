/**
 * 
 */
package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;
import java.util.ArrayList;

import uk.ac.ed.inf.sdp2012.group7.strategy.astar.*;
import uk.ac.ed.inf.sdp2012.group7.vision.Vision;

/**
 * @author s0955088
 *
 */
public class Plan {
	
	private BallPrediction ball;
	private OppositionPrediction opposition;
	private ArrayList<Node> path;
	private AStarRun astar;
	private int height = 25;
	private int width = 50;
	private float nodeInPixels = Vision.worldState.getPitch().getWidthInPixels()/50;//width in pixels!
	
	//Worldstate info
	private int pitch_top_buffer;
	private int pitch_right_buffer;
	private int pitch_bottom_buffer;
	private int pitch_left_buffer;
	private Point us;

	//Vision.worldState.getBlueRobot().getPosition().getCentre().clone();
	
	/**
	 * 
	 */
	public Plan() {
		// TODO Auto-generated constructor stub
		ball = new BallPrediction();
		opposition = new OppositionPrediction(nodeInPixels);
		
		//Set up world
		this.pitch_top_buffer = Vision.worldState.getPitch().getTopBuffer();
		this.pitch_right_buffer = Vision.worldState.getPitch().getLeftBuffer();
		this.pitch_bottom_buffer = Vision.worldState.getPitch().getTopBuffer();
		this.pitch_left_buffer = Vision.worldState.getPitch().getLeftBuffer();
		
		//get our robot.... We need to make sure we get the right method
		//not sure if we got the latest file?
		//this.us = Vision.worldState.getOurRobot().getPosition().getCentre();
		
		astar = new AStarRun(height, width, convertToNode(ball.getPosition()), convertToNode(us), convertToNodes(opposition.getDefaultObstacles()));
		
		//Requires method to convert from path to ArrayList<Point>
		//path = astar.getPathInPoints();
		
	}
		
	//Compacts WorldState position point into "Node" center position
	public Point convertToNode(Point p){
		int x = (int)Math.floor((p.x - this.pitch_left_buffer)/nodeInPixels);
		int y = (int)Math.floor((p.y - this.pitch_top_buffer)/nodeInPixels);
		Point grid_point = new Point(x,y);
		return grid_point;
	}
	
	//Compacts WorldState position point into "Node" center position
	public Point[] convertToNodes(Point[] p){
		
		Point[] grid_points = new Point[p.length];
		
		for(int i = 0; i < p.length ; i++){
			int x = (int)Math.floor((p[i].x - this.pitch_left_buffer)/nodeInPixels);
			int y = (int)Math.floor((p[i].y - this.pitch_top_buffer)/nodeInPixels);
			grid_points[i] = new Point(x,y);
		}
		
		return grid_points;
	}

}
