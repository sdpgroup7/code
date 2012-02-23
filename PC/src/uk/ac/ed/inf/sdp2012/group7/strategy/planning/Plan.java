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
	private ArrayList<Point> obstacles;
	private ArrayList<Node> path;
	private AStarRun astar;
	private int height = 25;
	private int width = 50;

	//Worldstate info
	private AllMovingObjects all_moving_objects;

	/**
	 * 
	 */
	public Plan() {
		// TODO Auto-generated constructor stub

		this.all_moving_objects = new AllMovingObjects();
		this.createBoundary();
		ball_prediction = new BallPrediction(this.all_moving_objects, this.obstacles);
		opposition = new OppositionPrediction(this.all_moving_objects, ConvertToNode.nodeInPixels);
		this.obstacles = ConvertToNode.convertToNodes(opposition.getDefaultObstacles());
		
		astar = new AStarRun(height, width, ConvertToNode.convertToNode(ball_prediction.getTarget()), ConvertToNode.convertToNode(all_moving_objects.getOurPosition()), this.obstacles );

		//Requires method to convert from path to ArrayList<Point>
		//path = astar.getPathInPoints();

	}

	//This method creates obstacles in front of both goals.
	private void createBoundary(){
		//boundary holds how thick is node obstacle at the edge of the pitch
		int boundary = 3;
		
		for(int y = 0; y < height; y++){
			for (int b=0; b < boundary; b++) {
				this.obstacles.add(new Point(b,y));
				this.obstacles.add(new Point(width - b,y));
			}
		}
		for(int x = boundary; x < width-boundary; x++){
			for (int b=0; b < boundary; b++) {
				this.obstacles.add(new Point(x,b));
				this.obstacles.add(new Point(x,height-b));
			}
		}

	}	

}
