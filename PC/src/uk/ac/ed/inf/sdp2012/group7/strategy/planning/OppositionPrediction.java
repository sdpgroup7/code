/**
 * 
 */
package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;
import java.util.ArrayList;

/**
 * @author s0955088
 *
 */
public class OppositionPrediction {

	private AllMovingObjects all_moving_objects;
	private AllStaticObjects all_static_objects;
	
	/**
	 * 
	 */
	public OppositionPrediction(AllMovingObjects aMO, AllStaticObjects aSO) {
		
		this.all_moving_objects = aMO;
		this.all_static_objects = aSO;
	}
	
	public ArrayList<Point> getDefaultObstacles(){
		
		ArrayList<Point> obstacles = new ArrayList<Point>();
		double nodeInPixels = this.all_static_objects.getNodeInPixels();
		
		Point position = this.all_moving_objects.getTheirPosition();
		//Possible problem with conversion back to Integers here..?
		for(int x = (int)(position.x - (2*nodeInPixels)); x <= position.x + 2*nodeInPixels; x = x + (int)nodeInPixels){
			for(int y = (int)(position.y - (2*nodeInPixels)); y <= position.y + 2*nodeInPixels; y = y + (int)nodeInPixels){
				Point p = new Point(x,y);
				obstacles.add(p);
			}
		}
		
		return obstacles;
	}

}
