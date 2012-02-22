package uk.ac.ed.inf.sdp2012.group7.strategy.planning;


import java.awt.Point;
import uk.ac.ed.inf.sdp2012.group7.vision.Vision;


/**
 * @author s0955088
 *
 */
public class BallPrediction {

	/**
	 * 
	 */
	private Point position;
	private Point velocity;
	public BallPrediction() {
		this.position = Vision.worldState.getBall().getPosition().getCentre();
	}
	
	
	public Point getPosition() {
		return position;
	}
	
	public Point getVelocity() {
		return velocity;
	}
}
