package uk.ac.ed.inf.sdp2012.group7.vision;

import java.awt.Point;
import java.lang.Math;

/*
I was a bit silly making this class.  Just use the built in points api one
*/

public class EuclideanDistance {
    
    public double getDistance(Point a, Point b){
    	a = (a == null) ? a = new Point(0,0) : a;
    	b = (b == null) ? b = new Point(0,0) : b;
    	
        return Math.sqrt((((a.getX()-b.getX())*(a.getX()-b.getX())) + ((a.getY()-b.getY())*(a.getY()-b.getY()))));
    }

}
