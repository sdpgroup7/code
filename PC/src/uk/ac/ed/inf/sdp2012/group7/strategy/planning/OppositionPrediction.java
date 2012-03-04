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
		
		
		//shit naive method to make the robot avoid the opposition
		//notice the huge square, because we have to take into account our our width
		Point position = this.allMovingObjects.getTheirPosition();

		//Everything is in nodes...
		for(int x = (position.x - 5); x <= position.x + 5; x++){
			for(int y = (position.y - 5); y <= position.y + 5; y++){
				Point p = new Point(x,y);
				obstacles.add(p);
			}
		}
		
		
		//Required to prevent the robot from driving into the ball when we don't need it to
		Point positionBall = this.allMovingObjects.getBallPosition();
		//Possible problem with conversion back to Integers here..?
		for(int x = (position.x - 2); x <= position.x + 2; x++){
			for(int y = (position.y - 2); y <= position.y + 2; y++){
				Point p = new Point(x,y);
				obstacles.add(p);
			}
		}
		
		return obstacles;
	}

}
