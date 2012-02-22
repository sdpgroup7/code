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

	private Point position;
	private Point velocity;
	private float nodeInPixels;
	
	/**
	 * 
	 */
	public OppositionPrediction(float nodeInPixels) {
		this.nodeInPixels = nodeInPixels;
		this.position = Vision.worldState.getOpponentsRobot().getPosition().getCentre();
	}
	
	public ArrayList<Point> getDefaultObstacles(){
		
		ArrayList<Point> obstacles = new ArrayList<Point>();
		
		
		//Possible problem with conversion back to Int here..
		for(float x = (this.position.x - (2*nodeInPixels)); x <= this.position.x + 2*nodeInPixels; x = x + nodeInPixels){
			for(float y = (this.position.y - (2*nodeInPixels)); y <= this.position.y + 2*nodeInPixels; y = y + nodeInPixels){
				Point p = new Point((int)x,(int)y);
				obstacles.add(p);
			}
		}
		
		return obstacles;
	}
	
	public Point getPosition() {
		return position;
	}
	
	public Point getVelocity() {
		return velocity;
	}

}
