package uk.ac.ed.inf.sdp2012.group7.vision.worldstate;

import uk.ac.ed.inf.sdp2012.group7.vision.Vision;
import java.awt.Point;

public class Pitch extends ObjectPosition{
	
	
	private volatile int topBuffer = 0;
	private volatile int bottomBuffer = 480;
	private volatile int leftBuffer = 0;
	private volatile int rightBuffer = 640;
	private volatile boolean buffersSet = false;
	private volatile float cameraHeight = 2.45f;
	private volatile float width = 2.44f;
	private volatile float height = 1.22f;

	public float getPitchWidth() {
		return width;
	}

	public float getPitchHeight() {
		return height;
	}

	public float getCameraHeight() {
		return cameraHeight;
	}

	public int getWidthInPixels(){
		return rightBuffer - leftBuffer;
	}
	
	public int getHeightInPixels(){
		return bottomBuffer - topBuffer;
	}

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
		this.buffersSet = true;
	}
	
	public boolean getBuffersSet(){
		return this.buffersSet;
	}
	
	public void setBuffersSet(boolean buffersSet){
		this.buffersSet = buffersSet;
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
