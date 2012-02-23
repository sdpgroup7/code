package uk.ac.ed.inf.sdp2012.group7.strategy;


/**
 * Defines an arc to be used for a robot to drive on
 * 
 * True for turning left, false for turning right 
 * @author David Fraser - s0912336
 *
 */
public class Arc {
	
	private double radius;
	private boolean direction;
	//This is the number of mm in a golf ball
	private double conversion = 43.67;
	
	public Arc(double r, boolean d) {
		this.radius = r;
		this.direction = d;
	}
	
	public int getRadius() {
		int convertedRadius = (int) Math.round(radius * conversion); 
		return convertedRadius;
	}
	
	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	public boolean isDirection() {
		return direction;
	}
	
	public void setDirection(boolean direction) {
		this.direction = direction;
	}
	

}
