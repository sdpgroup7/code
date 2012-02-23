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
	private AllMovingObjects all_moving_objects ;
	
	public BallPrediction(AllMovingObjects aMO) {
		this.all_moving_objects = aMO;
		
	}
	public Point getTarget () {
		return this.all_moving_objects.getOurPosition();
	}
}
