import java.lang.Math;

/**
 * Represents the position of an object, for example the ball or a robot.
 * 
 * @author Dale Myers - 0942590
 */
public class ObjectPosition{

	private int topLeft;
	private int topRight;
	private int bottomLeft;
	private int bottomRight;
	
	public ObjectPosition(){
		this(0,0,0,0);
	}

	public ObjectPosition(int topLeft, int topRight, int bottomLeft, int bottomRight){
		this.topLeft = topLeft;
		this.topRight = topRight;
		this.bottomLeft = bottomLeft;
		this.bottomRight = bottomRight;
	}

	public int[] getPosition(){
		int[] ret = new int[4];
		ret[0] = topLeft;
		ret[1] = topRight;
		ret[2] = bottomLeft;
		ret[3] = bottomRight;
		return ret;
	}

	public void setPosition(int[] positions){
		this.topLeft = positions[0];
		this.topRight = positions[1];
		this.bottomLeft = positions[2];
		this.bottomRight = positions[3];
	}

	public int getTopLeft(){
		return this.topLeft;
	}

	public int getTopRight(){
		return this.topRight;
	}

	public int getBottomLeft(){
		return this.bottomLeft;
	}

	public int getBottomRight(){
		return this.bottomRight;
	}

	public void setTopLeft(int v){
		this.topLeft = v;
	}

	public void setTopRight(int v){
		this.topRight = v;
	}

	public void setBottomLeft(int v){
		this.bottomLeft = v;
	}

	public void setBottomRight(int v){
		this.bottomRight = v;
	}

	public Position getCentre(){
		int top = (int)((this.topLeft + this.topRight)/2);
		int bottom = (int)((this.bottomLeft + this.bottomRight)/2);
		int left = (int)((this.topLeft + this.bottomLeft)/2);
		int right = (int)((this.topRight + this.topRight)/2);
		int tbCentre = Math.max(top,bottom);
		int lrCentre = Math.max(left,right);
		return new Position(lrCentre,tbCentre);
	}

	public int getHeight(){
		return Math.max(topLeft,topRight) - Math.min(bottomLeft,bottomRight);
	}

	public int getWidth(){
		return Math.max(topRight,bottomRight) - Math.min(topLeft,bottomLeft);
	}

}
