/**
 * 
 */
package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;
import java.util.ArrayList;

import uk.ac.ed.inf.sdp2012.group7.vision.Vision;

/**
 * @author s0955088
 *
 */
public class OppositionPrediction {

	private AllMovingObjects all_moving_objects ;
	private int nodeInPixels;
	
	/**
	 * 
	 */
	public OppositionPrediction(AllMovingObjects aMO, int nodeInPixels) {
		
		this.nodeInPixels = nodeInPixels;
		this.all_moving_objects = aMO;
	}
	
	public ArrayList<Point> getDefaultObstacles(){
		
		ArrayList<Point> obstacles = new ArrayList<Point>();
		
		Point position = this.all_moving_objects.getTheirPosition();
		//Possible problem with conversion back to Int here..
		for(int x = (position.x - (2*nodeInPixels)); x <= position.x + 2*nodeInPixels; x = x + nodeInPixels){
			for(int y = (position.y - (2*nodeInPixels)); y <= position.y + 2*nodeInPixels; y = y + nodeInPixels){
				Point p = new Point(x,y);
				obstacles.add(p);
			}
		}
		
		return obstacles;
	}

}
