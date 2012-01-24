/**
 * Represents the distance and direction of one object from another
 * 
 * @author Dale Myers - 0942590
 */
public class Vector2{

	private int magnitude;
	private int direction;
	
	public Vector2(){
		this(0,0);
	}

	public Vector2(int magnitude, int direction){
		this.magnitude = magnitude;
		this.direction = direction;
	}

	public int getMagnitude(){
		return this.magnitude;
	}

	public void setMagnitude(int magnitude){
		this.magnitude = magnitude;
	}

	public int getDirection(){
		return this.direction;
	}

	public void setDirection(int direction){
		this.direction = direction;
	}

}
