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
	private int command;
	//This is the number of cm in a golf ball
	
	public Arc(double r, boolean d, int c) {
		this.radius = r;
		this.direction = d;
		this.command = c;
	}
	
	public double getRadius() {
		return this.radius;
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

	public void setCommand(int command) {
		this.command = command;
	}

	public int getCommand() {
		return command;
	}
	

}
