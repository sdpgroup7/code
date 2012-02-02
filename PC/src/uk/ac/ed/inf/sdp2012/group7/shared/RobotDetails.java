package uk.ac.ed.inf.sdp2012.group7.shared;

import java.awt.Point;

public abstract class RobotDetails
{
	protected Point coors;
    protected double angle;
    
    public RobotDetails() {
    }
    
    public RobotDetails(Point coors, float angle) {
        super();
        this.coors = coors;
        this.angle = angle;
     }
    
    public Point getCoors() {
        return coors;
    }

    public double getAngle() {
        return angle;
    }

    
    
}