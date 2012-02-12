package uk.ac.ed.inf.sdp2012.group7.vision.worldstate;

import java.awt.Point;

public class TimePoint extends Point{

	long timestamp;
	
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
