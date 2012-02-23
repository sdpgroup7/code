/**
 * 
 */
package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import uk.ac.ed.inf.sdp2012.group7.strategy.astar.*;
import uk.ac.ed.inf.sdp2012.group7.vision.Vision;

/**
 * @author s0955088
 *
 */
public class Plan {
	
	private BallPrediction ball_prediction;
	private OppositionPrediction opposition;
	private ArrayList<Node> path;
	private AStarRun astar;
	private int height = 25;
	private int width = 50;
	private int nodeInPixels = Vision.worldState.getPitch().getWidthInPixels()/50;//width in pixels!
	
	//Worldstate info
	private int pitch_top_buffer;
	private int pitch_right_buffer;
	private int pitch_bottom_buffer;
	private int pitch_left_buffer;
	private AllMovingObjects all_moving_objects;
	
	
	

	//Vision.worldState.getBlueRobot().getPosition().getCentre().clone();
	
	/**
	 * 
	 */
	public Plan() {
		// TODO Auto-generated constructor stub
		
		this.all_moving_objects = new AllMovingObjects();
		
		ball_prediction = new BallPrediction(this.all_moving_objects);
		opposition = new OppositionPrediction(this.all_moving_objects, nodeInPixels);
		
		//Set up world
		this.pitch_top_buffer = Vision.worldState.getPitch().getTopBuffer();
		this.pitch_right_buffer = Vision.worldState.getPitch().getLeftBuffer();
		this.pitch_bottom_buffer = Vision.worldState.getPitch().getTopBuffer();
		this.pitch_left_buffer = Vision.worldState.getPitch().getLeftBuffer();
		
		//get our robot.... We need to make sure we get the right method
		//not sure if we got the latest file?
		//this.us = Vision.worldState.getOurRobot().getPosition().getCentre();
		
		astar = new AStarRun(height, width, convertToNode(ball_prediction.getTarget()), convertToNode(all_moving_objects.getOurPosition()), convertToNodes(opposition.getDefaultObstacles()));
		
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
	
	public ArrayList<Point> convertToNodes(ArrayList<Point> p){
		
		ArrayList<Point> node_points = new ArrayList<Point>();
		Iterator itr = p.iterator();
		
		while(itr.hasNext()){
			Point temp = (Point)itr.next();
			int x = (int)Math.floor((temp.x - this.pitch_left_buffer)/nodeInPixels);
			int y = (int)Math.floor((temp.y - this.pitch_top_buffer)/nodeInPixels);
			Point grid_point = new Point(x,y);
			node_points.add(grid_point);
		}
		
		return node_points;
	}

}
