package uk.ac.ed.inf.sdp2012.group7.vision.worldstate;
/**
 * Represents the distance and direction of one object from another
 * 
 * @author Dale Myers - 0942590
 */
public class Vector2{

	private volatile int magnitude; //Magnitude 9.  It's super effective.  Pikachu fainted
	private volatile double direction;
	
	public Vector2(){
		this(0,0); // Lol owl.
	}

	public Vector2(int magnitude, double direction){
		this.magnitude = magnitude;
		this.direction = direction;
	}

	public int getMagnitude(){
		return this.magnitude;
	}

	public void setMagnitude(int magnitude){
		this.magnitude = magnitude;
	}

	public double getDirection(){
		return this.direction;
	}

	public void setDirection(double direction){
		this.direction = direction;
	}

}
