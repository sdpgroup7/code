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

	private AllMovingObjects allMovingObjects;
	private AllStaticObjects allStaticObjects;
	
	/**
	 * 
	 */
	public OppositionPrediction(AllMovingObjects aMO, AllStaticObjects aSO) {
		
		this.allMovingObjects = aMO;
		this.allStaticObjects = aSO;
	}
	
	public ArrayList<Point> getDefaultObstacles(){
		
		ArrayList<Point> obstacles = new ArrayList<Point>();
		double nodeInPixels = this.allStaticObjects.getNodeInPixels();
		
		Point position = this.allMovingObjects.getTheirPosition();
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
