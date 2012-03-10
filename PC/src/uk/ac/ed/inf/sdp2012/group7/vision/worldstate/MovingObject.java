package uk.ac.ed.inf.sdp2012.group7.vision.worldstate;

import java.awt.Point;
import java.util.ArrayList;

import uk.ac.ed.inf.sdp2012.group7.vision.VisionTools;


public class MovingObject {
    
	volatile ObjectPosition position = new ObjectPosition();
	volatile double velocity;
	volatile double angle; 
	volatile float height = 0.2f;
	volatile public ArrayList<TimePoint> positions = new ArrayList<TimePoint>();
	volatile public ArrayList<Point> angles = new ArrayList<Point>();
	volatile public ArrayList<Point> movedAngles = new ArrayList<Point>();
	volatile public ArrayList<Point> centroids = new ArrayList<Point>();
	volatile public ArrayList<Point> movedCentroids = new ArrayList<Point>();
	volatile public Point tip = new Point();
	volatile private int kickerDistance = VisionTools.cmToPixels(16);
	
	
	
	/**
	 * @return the kickerDistance
	 */
	public int getKickerDistance() {
		return kickerDistance;
	}

	/**
	 * @param kickerDistance the kickerDistance to set
	 */
	public void setKickerDistance(int kickerDistance) {
		this.kickerDistance = kickerDistance;
	}

	public float getHeight(){
		return this.height;
	}
	
	public void setHeight(float height){
		this.height = height;
	}
	
    public void addPosition(Point p){
    	positions.add(new TimePoint(p,System.currentTimeMillis()));
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
	
	public void setAngle(double angle){
		this.angle = angle; 
	}
	
	public void addAngle(Point p){
		if(angles.size() > 1){
			if(Point.distance(p.x, p.y, angles.get(1).x, angles.get(1).y) < 10){
				angles.add(p);
				movedAngles.clear();
			} else {
				movedAngles.add(p);
			}
		} else {
			angles.add(p);
		}
		if(movedAngles.size() > 3){
			angles.clear();
			Point c = new Point(0,0);
			for(Point m : movedAngles){
				c = new Point(c.x + m.x , c.y + m.y);
			}
			c = new Point(c.x / movedAngles.size() , c.y / movedAngles.size());
			angles.add(c);
			movedAngles.clear();
		}
		if(angles.size() > 2) angles.remove(0);
		calculateAngle();
	}
	
	public void calculateAngle(){
		VisionTools vt = new VisionTools();
		if(angles.size() > 0){
			Point a = new Point(0,0);
			for(Point p : angles){
				a = new Point(a.x + p.x, a.y + p.y);
			}
			a = new Point(a.x / angles.size(), a.y / angles.size());
			tip = a;
			this.angle = vt.convertAngle(Math.atan2(a.y - getPosition().getCentre().y, a.x - getPosition().getCentre().x));
		} else {
			this.angle = 0;
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
