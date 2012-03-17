package uk.ac.ed.inf.sdp2012.group7.vision;
import java.util.ArrayList;
import java.awt.Point;

/**
 * Get the robot's orientation
 * 
 * @author Dale Myers
 * @author Rado
 */

public class OrientationFinder{
	

    ThresholdsState thresholdsState;
    
    /**
     * A constructor
     * 
     * @param thresholdsState
     */
    public OrientationFinder(ThresholdsState thresholdsState){
        this.thresholdsState = thresholdsState;
    }
    
    /**
     * A constructor
     */
    public OrientationFinder() {
    	super();
    }

    /**
     * Fins the orientation
     * 
     * Finds the orientation of a robot, given a list of the points contained within it's
     * T-shape (in terms of a list of x coordinates and y coordinates), the mean x and
     * y coordinates, and the image from which it was taken.
     *
     * @param xpoints           The x-coordinates of the points contained within the T-shape.
     * @param ypoints           The y-coordinates of the points contained within the T-shape.
     * @param meanX             The mean x-point of the T.
     * @param meanY             The mean y-point of the T.
     * @param image             The image from which the points were taken.
     * @param showImage         A boolean flag - if true a line will be drawn showing
     *                          the direction of orientation found.
     *
     * @return                  An orientation from -Pi to Pi degrees.
     * @throws NoAngleException
     */
  
    public double findOrientation(int robotX, int robotY, int greyX, int greyY){
    	
    	double ans = Math.atan2(robotY - greyY, robotX - greyX);
    	//ans = (( (ans * (180/Math.PI)) + 90) + 360) % 360;
    	ans = Math.toDegrees(ans);
    /*	if(ans < 0){
    		ans = -ans;
    	} else {
    		ans = (2.0*Math.PI) - ans;
    	}
    	ans = (ans + (3*Math.PI/2.0)) % (2*Math.PI); */
    	return ans; 
    }
    
    /**
     * Terribly named
     * 
     * Finds the farthest point on T away from centroid to use when calculating orientation
     * 
     * @param pixels The points on the T
     * @param centroid The robot centroid
     * @return The farthest point
     */
    public Point findOrientation(ArrayList<Point> pixels, Point centroid){    	
    	if(pixels.size() > 0){
    		
	    	Point furthest = new Point(0,0);
	    	double dist = 0;
	    	for(Point p : pixels){
	    		if	(Point.distance(p.x, p.y, centroid.x, centroid.y) > dist){
	    			furthest = p;
	    			dist = Point.distance(p.x, p.y, centroid.x, centroid.y);
	    		}
	    	}
	    	return furthest;
    	}
    	return new Point(0,0);
    }

}
