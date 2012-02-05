package uk.ac.ed.inf.sdp2012.group7.vision.worldstate;
import java.lang.Math;
import java.awt.Point;

import uk.ac.ed.inf.sdp2012.group7.vision.Vision;

/**
 * Represents the position of an object, for example the ball or a robot.
 * 
 * @author Dale Myers - 0942590
 */
public class ObjectPosition{

	private static final long serialVersionUID = -31952448440473456L;
	private Point topLeft = new Point(0,0);
    private Point topRight = new Point(0,0);
    private Point bottomLeft = new Point(0,0);
    private Point bottomRight = new Point(0,0);
    private Point centre = new Point(0,0);
    
    public ObjectPosition(){
        
    }

    public ObjectPosition(int x, int y){
        this.centre = new Point(x,y);
    }
    
    public ObjectPosition(Point p){
    	this.centre = p;
    }
    
    public ObjectPosition(Point topLeft, Point topRight, Point bottomLeft, Point bottomRight){
    	this(topLeft,topRight,bottomLeft,bottomRight,new Point(((topLeft.x + bottomRight.x) / 2),((topLeft.y + bottomRight.y) / 2)));
    }
    
    public ObjectPosition(Point topLeft, Point topRight, Point bottomLeft, Point bottomRight, Point centre){
    	this.topLeft = topLeft;
    	this.topRight = topRight;
    	this.bottomLeft = bottomLeft;
    	this.bottomRight = bottomRight;
    	this.centre = centre;
    }
    
    public Point[] getCorners(){
        Point[] ret = new Point[4];
        ret[0] = topLeft;
        ret[1] = topRight;
        ret[2] = bottomLeft;
        ret[3] = bottomRight;
        return ret;
    }

    public void setCorners(Point[] corners){
        this.topLeft = corners[0];
        this.topRight = corners[1];
        this.bottomLeft = corners[2];
        this.bottomRight = corners[3];
    }

    public Point getTopLeft(){
        return this.topLeft;
    }

    public Point getTopRight(){
        return this.topRight;
    }

    public Point getBottomLeft(){
        return this.bottomLeft;
    }

    public Point getBottomRight(){
        return this.bottomRight;
    }
    
    public Point getCentre(){
    	//Vision.logger.debug(Integer.toString(this.centre.x) + "," + Integer.toString(this.centre.y));
    	return this.centre;
    }

    public void setTopLeft(Point v){
        this.topLeft = v;
    }

    public void setTopRight(Point v){
        this.topRight = v;
    }

    public void setBottomLeft(Point v){
        this.bottomLeft = v;
    }

    public void setBottomRight(Point v){
        this.bottomRight = v;
    }
    
    public void setCentre(Point p){
    	this.centre = p;
    }
    
    public void setCentre(int x, int y){
    	this.centre = new Point(x,y);
    }

    public int getHeight(){
        return Math.max(topLeft.y,topRight.y) - Math.min(bottomLeft.y,bottomRight.y);
    }

    public int getWidth(){
        return Math.max(topRight.x,bottomRight.x) - Math.min(topLeft.x,bottomLeft.x);
    }

    public String toString(){
        return "(TopLeft,TopRight,BottomLeft,BottomRight,Centre) = (" + topLeft.toString() + "," + topRight.toString() + "," + bottomLeft.toString() + "," + bottomRight.toString() + "," + centre.toString() + ")";
    }
}
