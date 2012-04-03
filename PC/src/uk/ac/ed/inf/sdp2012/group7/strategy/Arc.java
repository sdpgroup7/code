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
	private boolean left;
	private double distance;
	//This is the number of cm in a golf ball
	

	public Arc(double r, boolean d, double dist) {
		Strategy.logger.info("New arc created: (radius,left):(" + r + "," + Boolean.toString(d) + ")");
		this.radius = r;
		this.left = d;
		this.distance = dist;
	}
	public double getDistance(){
		return this.distance;
	}
	public void setDistance(double dist){
		this.distance = dist;
	}
	public double getRadius() {
		return this.radius;
	}
	
	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	public boolean isLeft() {
		return left;
	}
	
	public void setLeft(boolean direction) {
		this.left = direction;
	}

	
	

}
