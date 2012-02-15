package uk.ac.ed.inf.sdp2012.group7.vision.worldstate;

import java.awt.Point;

public class TimePoint extends Point{

	private static final long serialVersionUID = -3693845226820198796L;

	volatile long timestamp;
	
	public TimePoint(int x, int y, long t){
		super(x,y);
		timestamp = t;
	}
	
	public TimePoint(Point p, long t){
		this(p.x,p.y,t);
	}
	
	
	public long getTimestamp(){
		return this.timestamp;
	}
	
}
