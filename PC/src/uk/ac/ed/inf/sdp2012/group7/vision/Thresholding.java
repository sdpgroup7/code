package uk.ac.ed.inf.sdp2012.group7.vision;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.util.ArrayList;
import uk.ac.ed.inf.sdp2012.group7.vision.ThresholdsState;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;
import uk.ac.ed.inf.sdp2012.group7.vision.EuclideanDistance;

/**
 * 
 * @author s0951580
 * 
 * TODO: clustering algorithm to get the T right 
 * TODO: do we give a shit about the grey circles anymore can we just remove them
 * 
 */
 


public class Thresholding {

	private ArrayList<Integer> yellowRobotX = new ArrayList<Integer>();
	private ArrayList<Integer> blueRobotX = new ArrayList<Integer>();
	private ArrayList<Integer> yellowRobotY = new ArrayList<Integer>();
	private ArrayList<Integer> blueRobotY = new ArrayList<Integer>();
	private ArrayList<Point> blueGreenPlate = new ArrayList<Point>();
	private ArrayList<Point> yellowGreenPlate = new ArrayList<Point>();
	private Point[] blueGreenPlate4Points = new Point[]{new Point(0,0),new Point(0,0),new Point(0,0),new Point(0,0)};
	private Point[] yellowGreenPlate4Points = new Point[]{new Point(0,0),new Point(0,0),new Point(0,0),new Point(0,0)};
	

    private Color c;
    /*The north, south, east and west immediate pixel's colors of c*/
    private Color cS;
    private Color cE;
    private Color cEN;
    private Color cEE;
    private Color cSS;
    private Color cSW;
    
	private int GB;// green - blue
	private int RG; // red - green
	private int RB; // red - blue
	private int RGthresh;
	
    private int[][] redBallThresh= new int[2][3];
    private int[][] yellowRobotThresh= new int[2][3];
    private int[][] blueRobotThresh= new int[2][3];
    private int[][] greenPlatesThresh= new int[2][1];
    
    private int pitch; 
    private int height;
    private int width;
    
    private Point pastBlueGreyCent = new Point();
    private Point pastYellowGreyCent = new Point(); 
    
    private Point ballCentroid = new Point();
    private Point blueCentroid = new Point();
    private Point yellowCentroid = new Point();
    private Point blueGreenPlateCentroid = new Point();
    private Point blueGreyCentroid = new Point();
    private Point yellowGreyCentroid = new Point();
    
    private int ballCount;
    private int yellowCount;
    private int blueCount;
    private int blueGreyCount;
    private int yellowGreyCount;
 //    private int robot; // 0 for Yellow, 1 for Blue(our robot)  We will use the world state
    
    private ThresholdsState ts;

    private EuclideanDistance ed = new EuclideanDistance();
    

    
    
    public Thresholding(ThresholdsState ts) {  // Sets the constants for thresholding for each pitch 
    	redBallThresh[0][0] = 130;
    	redBallThresh[0][1] = 90;
    	redBallThresh[0][2] = 90;
    	redBallThresh[1][0] = 150;
    	redBallThresh[1][1] = 100;
    	redBallThresh[1][2] = 100;
    	yellowRobotThresh[0][0] = 140;
    	yellowRobotThresh[0][1] = 140;
    	yellowRobotThresh[0][2] = 170;
		yellowRobotThresh[1][0] = 150;
		yellowRobotThresh[1][1] = 190;
		yellowRobotThresh[1][2] = 140;
		blueRobotThresh[0][0] = 130;
		blueRobotThresh[0][1] = 180;
		blueRobotThresh[0][2] = 100;
		blueRobotThresh[1][0] = 130;
		blueRobotThresh[1][1] = 140;
		blueRobotThresh[1][2] = 90;

	
		greenPlatesThresh[0][0] = 120;
		greenPlatesThresh[1][0] = 140;

    	
    	this.ts = ts;

    }
    public BufferedImage getThresh(BufferedImage img, int left, int right, int top, int bottom) { // Method to get thresholded image 
    		//Vision.logger.debug("Starting thresholding");
    		
    	//stops it fucking up the locations before we've given it the thresholds
    	if (Vision.worldState.isClickingDone()){
    		ArrayList<Point> bluePixels = new ArrayList<Point>();
    		ArrayList<Point> yellowPixels = new ArrayList<Point>();
    		pitch = Vision.worldState.getRoom();
    		width = right-left;
    		height = top-bottom;
    	  
    	  	pastBlueGreyCent = Vision.worldState.getBlueGrey().getPosition().getCentre();
    	  	pastYellowGreyCent = Vision.worldState.getYellowGrey().getPosition().getCentre();
		 //  BufferedImage threshed = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
            
           /*
           Initialising to one to stop java dividing by 0 when it shouldn't
           */
           ballCount = 0;
           ballCentroid.setLocation(0,0);
            
           blueCount = 0;
           blueCentroid.setLocation(0,0);
            
           yellowCount = 0;
           yellowCentroid.setLocation(0,0);
           
           blueGreyCount = 0;
           blueGreyCentroid.setLocation(0,0);
           
           yellowGreyCount = 0;
           blueGreyCentroid.setLocation(0,0);

           //Vision.logger.debug("Iterating image");
	    	for (int i = left; i < right; i++) {
				for (int j = top; j < bottom; j++) {
					//Vision.logger.debug("Oh dear (i,j) = " + Integer.toString(i) + "," + Integer.toString(j) + ")");
					c = new Color(img.getRGB(i,j));

					GB = Math.abs((c.getBlue() - c.getGreen()));
					RG = Math.abs((c.getRed() - c.getGreen()));
					RB = Math.abs((c.getRed() - c.getBlue()));
					if(isRed(c, GB)){ //  was inside  RB > 50 && RG > 50
						img.setRGB(i, j, Color.red.getRGB()); //Red Ball
						ballCount++;
						ballCentroid.setLocation(ballCentroid.getX() + i, ballCentroid.getY() + j);

					}
					else if (isYellow(c)) {
					    setCs(i,j,right,left,top,bottom, img);
					    if (isYellow(cS) && isYellow(cE) && isYellow(cEE) && isYellow(cEN) && isYellow(cSS) && isYellow(cSW)){
						    img.setRGB(i, j, Color.yellow.getRGB()); // Yellow robot
						    yellowRobotX.add(i);
						    yellowRobotY.add(j);
						    yellowCount++;
						    yellowCentroid.setLocation(yellowCentroid.getX() + i, yellowCentroid.getY() + j);
						    yellowPixels.add(new Point(i,j));
					    }
					}
					else if (isBlue(c)){
					    setCs(i,j,right,left,top,bottom, img);
					    if (isBlue(cS) && isBlue(cE) && isBlue(cEE) && isBlue(cEN) && isBlue(cSS) && isBlue(cSW)){
						    img.setRGB(i, j, Color.blue.getRGB()); // Blue robot 
						    blueRobotX.add(i);
						    blueRobotY.add(j);
						    blueCount++;
						    blueCentroid.setLocation(blueCentroid.getX() + i, blueCentroid.getY() + j);
						    bluePixels.add(new Point(i,j));
					    }
						//make blue thresholds for the different pitches in that [pitch][x] style
					}
					else if (isGreen(c,GB,RG))  {
						img.setRGB(i,j, Color.green.getRGB()); // GreenPlates 
						if (ed.getDistance(Vision.worldState.getBlueRobot().getPosition().getCentre(), new Point(i,j)) < 34) {
							blueGreenPlate.add(new Point(i,j));
						} 
						if (ed.getDistance(Vision.worldState.getYellowRobot().getPosition().getCentre(), new Point(i,j)) < 34){
							yellowGreenPlate.add(new Point(i,j));
						}

					}
					else if (isGrey(c) && (ed.getDistance(pastBlueGreyCent, new Point(i,j)) < 15) && (ed.getDistance(Vision.worldState.getBlueRobot().getPosition().getCentre(), new Point(i,j)) < 22.5) )  {
						
					    img.setRGB(i,j, Color.orange.getRGB());
					    blueGreyCount++;
					    blueGreyCentroid.setLocation(blueGreyCentroid.getX() + i, blueGreyCentroid.getY() + j);
					} else if (isGrey(c) && (ed.getDistance(pastYellowGreyCent, new Point(i,j)) < 15) && (ed.getDistance(Vision.worldState.getYellowRobot().getPosition().getCentre(), new Point(i,j)) < 22.5)) {
						
					    img.setRGB(i,j, Color.pink.getRGB());
					    yellowGreyCount++;
					    yellowGreyCentroid.setLocation(yellowGreyCentroid.getX() + i, yellowGreyCentroid.getY() + j);

					}
				}
			}
			
			if (ballCount == 0) ballCount++;
			if (blueCount == 0) blueCount++;
			if (yellowCount == 0) yellowCount++;
			if (blueGreyCount == 0) blueGreyCount++;
			if (yellowGreyCount == 0) yellowGreyCount++;
			
	    	//Vision.logger.debug("End Iteration");
			ballCentroid.setLocation(ballCentroid.getX()/ballCount, ballCentroid.getY()/ballCount);
			yellowCentroid.setLocation(yellowCentroid.getX()/yellowCount, yellowCentroid.getY()/yellowCount);
			blueCentroid.setLocation(blueCentroid.getX()/blueCount, blueCentroid.getY()/blueCount);
			blueGreyCentroid.setLocation(blueGreyCentroid.getX()/blueGreyCount, blueGreyCentroid.getY()/blueGreyCount);
			yellowGreyCentroid.setLocation(yellowGreyCentroid.getX()/yellowGreyCount, yellowGreyCentroid.getY()/yellowGreyCount);
			
			Plate plate = new Plate();
			
			blueGreenPlate4Points = plate.getCorners(blueGreenPlate);
			yellowGreenPlate4Points = plate.getCorners(yellowGreenPlate);
			
			/*blueGreenPlate4Points = findTheFourPoints(blueGreenPlate);
			yellowGreenPlate4Points = findTheFourPoints(yellowGreenPlate);*/
			
			Vision.worldState.setBlueRobotPosition((int)blueCentroid.getX(),(int)blueCentroid.getY());
			Vision.worldState.setYellowRobotPosition((int)yellowCentroid.getX(),(int)yellowCentroid.getY());
			
			Vision.worldState.setBallPosition((int)ballCentroid.getX(),(int)ballCentroid.getY());
			if(ed.getDistance(Vision.worldState.getBlueRobot().getPosition().getCentre(),blueGreyCentroid) < 20 ){
				Vision.worldState.setBlueGreyPosition((int)blueGreyCentroid.getX() ,(int)blueGreyCentroid.getY());
			}
			if(ed.getDistance(Vision.worldState.getYellowRobot().getPosition().getCentre(),yellowGreyCentroid) < 20 ){
				Vision.worldState.setYellowGreyPosition((int)yellowGreyCentroid.getX(), (int)yellowGreyCentroid.getY());
			}
			
			Vision.worldState.setBluePixels(bluePixels);
			Vision.worldState.setYellowPixels(yellowPixels);
			
			blueGreenPlate.clear();
			yellowGreenPlate.clear();
    	}
    		
    	return img;
	    	
    }
    
    /*
    for the blob extraction.  E,N,S,W are compass directions
    If it goes out of bounds then just set it to the original colour.
    Could probably be made far more efficient through recursion
    */

    public void setCs(int x, int y, int right, int left, int top, int bottom, BufferedImage img){
        if (x + 1 < right){
            cE = new Color(img.getRGB(x+1,y));
        }else {
            cE = c;
        }
        if (y + 1 < bottom){
            cS = new Color(img.getRGB(x,y+1));
        }else {
            cS = c;
        }
        if ((x + 1 < right) && (y - 1 > top)){
            cEN = new Color(img.getRGB(x+1,y-1));
        }else {
            cEN = c;
        }
        if ((x + 2 < right)){
            cEE = new Color(img.getRGB(x+2,y));
        }else {
            cEE = c;
        }
        if ((y + 2 < bottom)){
            cSS = new Color(img.getRGB(x,y+2));
        }else {
            cSS = c;
        }
        if ((x - 1 > left) && (y + 1 < bottom)){
            cSW = new Color(img.getRGB(x-1,y+1));
        }else {
            cSW = c;
        }
        
    }
    /**
     * 
     * @param listOfPoints
     * @return centroidPoint
     * Given an array of points return its centorid
     */
    public Point findCentroid(ArrayList<Point> listOfPoints){
    	int sumX = 0;
    	int sumY = 0;
    	for (int i = 0; i < listOfPoints.size(); i++) {
			sumX += listOfPoints.get(i).x;
			sumY += listOfPoints.get(i).y;
		}
    	
    	return new Point((int) (sumX/(double)listOfPoints.size() ), (int) (sumY/(double)listOfPoints.size()));
    }

    public Point getBlueGreenPlateCentorid(){ 
    	return blueGreenPlateCentroid;
    }
    
    public boolean isBlue(Color c){
        return ( (c.getRed() <= blueRobotThresh[pitch][0]) && (c.getBlue() > blueRobotThresh[pitch][2])   && (c.getGreen() <= blueRobotThresh[pitch][1]));
    }
    
    public boolean isRed(Color c, int GB){
        return ( (c.getRed() > redBallThresh[pitch][0]) &&  (c.getBlue() <= redBallThresh[pitch][1]) &&  (c.getGreen() <= redBallThresh[pitch][2]) && GB < 70 );
    }
    
    public boolean isGreen(Color c, int GB, int RG){
        return ( GB > 55 && RG > 55 && c.getGreen() > greenPlatesThresh[pitch][0]);
    }
    
    public boolean isGrey(Color c){
        return ((c.getRed() >= ts.getGrey_r_low()) && (c.getRed() <= ts.getGrey_r_high()) && (c.getGreen() >= ts.getGrey_g_low()) && (c.getGreen() <= ts.getGrey_g_high()) && (c.getBlue() >= ts.getGrey_b_low()) && (c.getBlue() <= ts.getGrey_b_high()));
    }
    /*public boolean isGrey(Color c, int RG){
        return (RG < 20 && c.getBlue() < 50 && c.getGreen() > 80 && c.getGreen() < 110 && c.getRed() > 80 && c.getRed() < 110);
    }*/
    
    
    public boolean isYellow(Color c){
        return ((c.getRed() >= ts.getYellow_r_low()) && (c.getRed() <= ts.getYellow_r_high()) && (c.getGreen() >= ts.getYellow_g_low()) && (c.getGreen() <= ts.getYellow_g_high()) && (c.getBlue() >= ts.getYellow_b_low()) && (c.getBlue() <= ts.getYellow_b_high()));
    }
    
    public ArrayList<Integer> getBlueX(){
        return blueRobotX;
    }
    
    public ArrayList<Integer> getBlueY(){
        return blueRobotY;
    }
    
    public ArrayList<Integer> getYellowX(){
        return yellowRobotX;
    }
    
    public ArrayList<Integer> getYellowY(){
        return yellowRobotY;
    }
	
	
	//IS THIS FOR GREEN PLATE BOUNDING?
    public Point[] findTheFourPoints(ArrayList<Point> points){
    	Point[] ans = new Point[]{new Point(0,0),new Point(0,0),new Point(0,0),new Point(0,0)}; 
    	/*
    	 * ans[0] = xminP,
    	 * ans[1] = xmaxP,
    	 * ans[2] = yminP,
    	 * ans[3] = ymaxP. 
    	 */
    	int minX = Integer.MAX_VALUE;
    	int maxX = Integer.MIN_VALUE;
    	int minY = Integer.MAX_VALUE;
    	int maxY = Integer.MIN_VALUE;
    	
    	for (int i = 0; i < points.size(); i++) {
			if(points.get(i).x < minX){
				ans[0] = points.get(i);
				minX = points.get(i).x;
			}
			if(points.get(i).x >= maxX){
				ans[1] = points.get(i);
				maxX = points.get(i).x;
			}
			if(points.get(i).y < minY){
				ans[2] = points.get(i);
				minY = points.get(i).y;
			}
			if(points.get(i).y >= maxY){
				ans[3] = points.get(i);
				maxY = points.get(i).y;
			}
		}
    	/*for (int i = 0; i < ans.length; i++) {
			System.err.println(i+" "+ans[i]);
		}*/
    	
    	return ans;
    }
    
    //IS THIS FOR THE FURTHEST AWAY POINT?
	public Point findKeyPoint(Point[] points, Point cent){
		/*
		double one = ed.getDistance(points[0], cent);
		double two = ed.getDistance(points[1], cent);
		double three = ed.getDistance(points[2], cent);
		double four = ed.getDistance(points[3], cent);
		
		double min = Math.min(Math.min(Math.min(one, two), three), four);
		
		double nextMin = Double.MAX_VALUE;
		 */
		
		
		
		Point ans = new Point();
		double firstMin = Integer.MAX_VALUE;
		double secondMin = Integer.MAX_VALUE;
		Point firstMinP = new Point(0,0);
		Point secondMinP = new Point(0,0);

		for (int i = 0; i < points.length; i++) {
			if(ed.getDistance(points[i], cent) < firstMin){
				firstMinP = points[i];
				firstMin = ed.getDistance(points[i], cent);
			}
		}
		for (int i = 0; i < points.length; i++) {
			if( ed.getDistance(points[i], cent) > ed.getDistance(firstMinP, cent) && (secondMin > firstMin)){
				secondMinP = points[i];
				secondMin =  ed.getDistance(points[i], cent);
			}
		}
		//System.err.println("First point"+ firstMinP.x+","+firstMinP.y);
		//System.err.println("Second point"+ secondMinP.x+","+secondMinP.y);
		ans.setLocation( (firstMinP.x + secondMinP.x)/2, (firstMinP.y + secondMinP.y)/2);
		return ans;
	}
	public Point[] getBlueGreenPlate4Points(){
		return blueGreenPlate4Points;
	}
	public Point[] getYellowGreenPlate4Points(){
		return yellowGreenPlate4Points;
	}
}
