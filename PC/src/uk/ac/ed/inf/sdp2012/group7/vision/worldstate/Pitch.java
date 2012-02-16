package uk.ac.ed.inf.sdp2012.group7.vision.worldstate;

import uk.ac.ed.inf.sdp2012.group7.vision.Vision;
import java.awt.Point;

public class Pitch extends ObjectPosition{
	
	
	private int topBuffer = 0;
	private int bottomBuffer = 480;
	private int leftBuffer = 0;
	private int rightBuffer = 640;

	public Pitch(){
		super();
	}
	
	public Pitch(Point topLeft, Point topRight, Point bottomRight, Point bottomLeft){
		super(topLeft,topRight,bottomRight,bottomLeft);
	}
	
	public void setPosition(ObjectPosition position){
		super.setTopRight(position.getTopRight());
		super.setTopLeft(position.getTopLeft());
		super.setBottomRight(position.getBottomRight());
		super.setBottomLeft(position.getBottomLeft());
	}
	
	public void setBuffers(int top, int right, int bottom, int left){
		this.topBuffer = top;
		this.rightBuffer = right;
		this.bottomBuffer = bottom;
		this.leftBuffer = left;
	}
	
    public int getTopBuffer(){
        return this.topBuffer;
    }

    public int getRightBuffer(){
        return this.rightBuffer;
    }

    public int getBottomBuffer(){
        return this.bottomBuffer;
    }

    public int getLeftBuffer(){
        return this.leftBuffer;
    }

    public void setTopBuffer(int value){
        this.topBuffer = value;
        Vision.logger.debug(Integer.toString(value));
    }

    public void setRightBuffer(int value){
        this.rightBuffer = value;
        Vision.logger.debug(Integer.toString(value));
    }

    public void setBottomBuffer(int value){
        this.bottomBuffer = value;
        Vision.logger.debug(Integer.toString(value));
    }

    public void setLeftBuffer(int value){
        this.leftBuffer = value;
        Vision.logger.debug(Integer.toString(value));
    }

	
}
