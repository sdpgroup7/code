package uk.ac.ed.inf.sdp2012.group7.vision.worldstate;

import java.awt.Point;
import java.util.ArrayList;


public class MovingObject {
    
	volatile ObjectPosition position = new ObjectPosition();
	volatile double velocity;
	volatile double angle; 
	volatile public ArrayList<TimePoint> positions = new ArrayList<TimePoint>();
	
    public void addPosition(Point p){
    	positions.add(new TimePoint(p,System.currentTimeMillis()));
    	if(positions.size() > 5){
    		positions.remove(0);
    	}
    	if(positions.size() == 5){ 
    	    updateVelocity();
    	}
    }
    
    public void updateVelocity(){
    	TimePoint a = positions.get(0);
    	TimePoint b = positions.get(4);
    	Point c = new Point(a.x - b.x, a.y - b.y);
    	double v = Math.sqrt(c.x*c.x + c.y*c.y);
    	v = v/(b.getTimestamp()-a.getTimestamp());
    	this.velocity = v;
    }
	
	public void setAngle(double angle){
		this.angle = angle; 
	}
	
	public double getAngle(){
		return angle;
	}
	
    public ObjectPosition getPosition(){
        return this.position;
    }

    public double getVelocity(){
        return this.velocity;
    }
    
    public void setPosition(ObjectPosition p){
    	addPosition(p.getCentre());
    	this.position = p;
    }
    
    public void setPosition(Point p){
    	addPosition(p);
    	this.position.setCentre(p.x,p.y);
    }
    
    public void setPosition(int x, int y){
    	addPosition(new Point(x,y));
    	this.position.setCentre(x,y);
    }
    
    public void setVelocity(double v){
    	this.velocity = v;
    }
    
    public void set(ObjectPosition p, double v){
    	addPosition(p.getCentre());
    	this.setPosition(p);
    	this.setVelocity(v);
    }

}
