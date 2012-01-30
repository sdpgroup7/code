package uk.ac.ed.inf.sdp2012.group7.vision;
import java.lang.Math;
import java.awt.Point;

/**
 * Represents the position of an object, for example the ball or a robot.
 * 
 * @author Dale Myers - 0942590
 */
public class ObjectPosition extends Point{

    private int topLeft = 0;
    private int topRight = 0;
    private int bottomLeft = 0;
    private int bottomRight = 0;
    
    public ObjectPosition(){
        super(0,0);
    }

    public ObjectPosition(int x, int y){
        super(x,y);
    }

    public ObjectPosition(int topLeft, int topRight, int bottomLeft, int bottomRight){
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }


    public int[] getCorners(){
        int[] ret = new int[4];
        ret[0] = topLeft;
        ret[1] = topRight;
        ret[2] = bottomLeft;
        ret[3] = bottomRight;
        return ret;
    }

    public void setCorners(int[] corners){
        this.topLeft = corners[0];
        this.topRight = corners[1];
        this.bottomLeft = corners[2];
        this.bottomRight = corners[3];
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

    public int getHeight(){
        return Math.max(topLeft,topRight) - Math.min(bottomLeft,bottomRight);
    }

    public int getWidth(){
        return Math.max(topRight,bottomRight) - Math.min(topLeft,bottomLeft);
    }

    public String toString(){
        return "Center: " + super.toString() + "\n" + "(TopLeft,TopRight,BottomLeft,BottomRight) = (" + Integer.toString(topLeft) + "," + Integer.toString(topRight) + "," + Integer.toString(bottomLeft) + "," + Integer.toString(bottomRight) + ")";
    }
}
