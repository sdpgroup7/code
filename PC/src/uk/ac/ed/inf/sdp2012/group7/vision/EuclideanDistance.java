package uk.ac.ed.inf.sdp2012.group7.vision;

import java.awt.Point;
import java.lang.Math;

public class EuclideanDistance {
    
    public double getDistance(Point a, Point b){
        return Math.sqrt((((a.getX()-b.getX())*(a.getX()-b.getX())) + ((a.getY()-b.getY())*(a.getY()-b.getY()))));
    }

}
