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
	
	public Arc(double r, boolean d) {
		this.radius = r;
		this.direction = d;
	}
	
	public double getRadius() {
		return radius;
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
