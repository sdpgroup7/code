package uk.ac.ed.inf.sdp2012.group7.vision.worldstate;

import java.awt.Point;
import java.util.ArrayList;

import net.phys2d.raw.shapes.AABox;

import uk.ac.ed.inf.sdp2012.group7.vision.VisionTools;


public class MovingObject {
    
	private volatile ObjectPosition position = new ObjectPosition();
	private volatile double velocity;
	private volatile double angularVelocity;
	private volatile double angle; 
	private volatile float height = 0.2f;
	private volatile ArrayList<TimePoint> positions = new ArrayList<TimePoint>();
	private volatile ArrayList<TimePoint> angles = new ArrayList<TimePoint>();
	private volatile ArrayList<TimePoint> ballAngles = new ArrayList<TimePoint>();
	private volatile ArrayList<TimePoint> movedAngles = new ArrayList<TimePoint>();
	private volatile ArrayList<TimePoint> centroids = new ArrayList<TimePoint>();
	private volatile ArrayList<TimePoint> movedCentroids = new ArrayList<TimePoint>();
	private volatile Point tip = new Point();
	private volatile int kickerDistance = 16;
	private volatile double predictionTime = 0.2;
	
	public Point getTipPoint(){
		return this.tip;
	}
	
	public int getKickerDistanceInCM() {
		return kickerDistance;
	}

	public int getKickerDistanceInPixels(){
		return VisionTools.cmToPixels(getKickerDistanceInCM());
	}
	
	public float getHeight(){
		return this.height;
	}
	
	public void setHeight(float height){
		this.height = height;
	}
	
    public void addPosition(Point p){
    	positions.add(new TimePoint(p));
    	if(positions.size() > 5){
    		positions.remove(0);
    	}
    	if(positions.size() == 5){ 
    	    updateVelocity();
    	}
    }
    
    public void addPosition(int x, int y){
    	addPosition(new Point(x,y));
    }
    
    public void updateVelocity(){
    	TimePoint a = positions.get(0);
    	TimePoint b = positions.get(4);
    	Point c = new Point(a.x - b.x, a.y - b.y);
    	double v = Math.sqrt(c.x*c.x + c.y*c.y);
    	v = v/(b.getTimestamp()-a.getTimestamp());
    	this.velocity = v*1000;
    }
	
    public void updateAngularVelocity(){
    	TimePoint a = angles.get(0);
    	TimePoint b = angles.get(4);
    	double angle = VisionTools.convertAngle(Math.atan2(a.y - b.y, a.x - b.x));
    	angle = angle/(b.getTimestamp()-a.getTimestamp());
    	this.angularVelocity = angle;
    }
    
	public void setAngle(double angle){
		this.angle = angle; 
	}
	
	public void addAngle(Point p){
		if(angles.size() > 1){
			if(Point.distance(p.x, p.y, angles.get(1).x, angles.get(1).y) < 20){
				angles.add(new TimePoint(p));
				movedAngles.clear();
			} else {
				movedAngles.add(new TimePoint(p));
			}
		} else {
			angles.add(new TimePoint(p));
		}
		if(movedAngles.size() > 3){
			angles.clear();
			Point c = new Point(0,0);
			for(Point m : movedAngles){
				c = new Point(c.x + m.x , c.y + m.y);
			}
			c = new Point(c.x / movedAngles.size() , c.y / movedAngles.size());
			angles.add(new TimePoint(c));
			movedAngles.clear();
		}
		if(angles.size() > 2) angles.remove(0);
		calculateAngle();
	}
	
	public void calculateAngle(){
		if(angles.size() > 0){
			Point a = new Point(0,0);
			for(TimePoint p : angles){
				a = new Point(a.x + p.x, a.y + p.y);
			}
			a = new Point(a.x / angles.size(), a.y / angles.size());
			tip = a;
			this.angle = VisionTools.convertAngle(Math.atan2(a.y - getPosition().getCentre().y, a.x - getPosition().getCentre().x));
		} else {
			this.angle = 0;
		}
	}
	
	
	/*
	 * fuck this shit 
	 */
	
	public void addBallsAngle(Point p) {
		if (ballAngles.size() > 5) {
			ballAngles.remove(0);
			ballAngles.add(new TimePoint(p));
		}
		else {
			ballAngles.add(new TimePoint(p));
		}
		
	}
	public void calculateBallAngle() {
		if(ballAngles.size()>1){
			angle = Math.atan2(ballAngles.get(ballAngles.size()-1).y -  ballAngles.get(0).y, ballAngles.get(ballAngles.size()-1).x -  ballAngles.get(0).x);
		}
	}
	
	public double getAngle(){
		return this.angle;
	}
	
	
    public ObjectPosition getPosition(){
        return this.position;
    }

    public double getVelocity(){
        return this.velocity;
    }

    public void setPosition(Point p){
    	setPosition(p.x, p.y);
    }
    
    public void setPosition(int x, int y){
    	addPosition(new Point(x,y));
    	TimePoint a = null;
		TimePoint b = null;
    	double vx = 0;
    	double vy = 0;
    	if(positions.size() == 5){
    		a = positions.get(0);
    		b = positions.get(4);
    		if(b.getTimestamp() - a.getTimestamp() != 0){
	    		vx = (b.x - a.x) / (b.getTimestamp() - a.getTimestamp());
	    		vy = (b.y - a.y) / (b.getTimestamp() - a.getTimestamp());
    		}
    		this.position.setCentre((int)Math.round(b.x + (vx * predictionTime)), (int)Math.round(b.y + (vy * predictionTime)));
    	}
    }
    
    public void setVelocity(double v){
    	this.velocity = v;
    }
    

}
