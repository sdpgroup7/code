package uk.ac.ed.inf.sdp2012.group7.vision;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.util.ArrayList;
import uk.ac.ed.inf.sdp2012.group7.vision.ThresholdsState;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

/**
 * 
 * @author s0951580
 * TODO: get WHICH ROBOT ARE WE? and find the centroid of the plate that is ours
 * TODO: clustering algorithm to get the T right 
 * TODO: BLUE ROBOT IS CURRENTLY HARD CODED, needs to be done for the other pitch as well 
 */


public class Thresholding {

	private ArrayList<Integer> yellowRobotX = new ArrayList<Integer>();
	private ArrayList<Integer> blueRobotX = new ArrayList<Integer>();
	private ArrayList<Integer> yellowRobotY = new ArrayList<Integer>();
	private ArrayList<Integer> blueRobotY = new ArrayList<Integer>();

    private Color c;
    
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
    
    private Point ballCentroid = new Point();
    private Point blueCentroid = new Point();
    private Point yellowCentroid = new Point();
    private Point blueGreenPlateCentroid = new Point();
    
    private int ballCount;
    private int yellowCount;
    private int blueCount;
    private int robot; // 0 for Yellow, 1 for Blue(our robot) 
    
    private ThresholdsState ts;
    

    
    
    public Thresholding(int pitch, ThresholdsState ts) {  // Sets the constants for thresholding for each pitch 
    	redBallThresh[0][0] = 130;
    	redBallThresh[0][1] = 100;
    	redBallThresh[0][2] = 100;
    	redBallThresh[1][0] = 150;
    	redBallThresh[1][1] = 110;
    	redBallThresh[1][2] = 110;
    	yellowRobotThresh[0][0] = 140;
    	yellowRobotThresh[0][1] = 140;
    	yellowRobotThresh[0][2] = 170;
		yellowRobotThresh[1][0] = 150;
		yellowRobotThresh[1][1] = 190;
		yellowRobotThresh[1][2] = 140;
		blueRobotThresh[0][0] = 150;
		blueRobotThresh[0][1] = 150;
		blueRobotThresh[0][2] = 100;
		blueRobotThresh[1][0] = 150;
		blueRobotThresh[1][1] = 150;
		blueRobotThresh[1][2] = 100;
		greenPlatesThresh[0][0] = 155;
		greenPlatesThresh[1][0] = 155;

    	this.pitch=pitch;
    	this.ts = ts;
    	//this.robot = robot;
    }
    public BufferedImage getThresh(BufferedImage img, int left, int right, int top, int bottom) { // Method to get thresholded image 
    		//Vision.logger.debug("Starting thresholding");
    		
    	if (Vision.worldState.isClickingDone()){
		   width = right-left;
		   height = top-bottom;
		 //  BufferedImage threshed = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);

           ballCount = 0;
           ballCentroid.setLocation(0,0);
            
           blueCount = 0;
           blueCentroid.setLocation(0,0);
            
           yellowCount = 0;
           yellowCentroid.setLocation(0,0);

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
						//img.setRGB(i, j, Color.yellow.getRGB()); // Yellow robot
						//p.setLocation(i, j);
						//yellowRobot.add(p);
						yellowCount++;
						yellowCentroid.setLocation(yellowCentroid.getX() + i, yellowCentroid.getY() + j);
					}
					else if (isBlue(c)){
						img.setRGB(i, j, Color.blue.getRGB()); // Blue robot 
						blueRobotX.add(i);
						blueRobotY.add(j);						
						blueCount++;
						blueCentroid.setLocation(blueCentroid.getX() + i, blueCentroid.getY() + j);
						//make blue thresholds for the different pitches in that [pitch][x] style
					}
					else if (isGreen(c,GB,RG))  {
						img.setRGB(i,j, Color.green.getRGB()); // GreenPlates 
					}
					else if (isGrey(c)) {
					    img.setRGB(i,j, Color.black.getRGB());
					}
				}
			}
	    	//Vision.logger.debug("End Iteration");
			ballCentroid.setLocation(ballCentroid.getX()/ballCount, ballCentroid.getY()/ballCount);
			yellowCentroid.setLocation(yellowCentroid.getX()/yellowCount, yellowCentroid.getY()/yellowCount);
			blueCentroid.setLocation(blueCentroid.getX()/blueCount, blueCentroid.getY()/blueCount);
			
			Vision.worldState.setOurRobotPosition((int)blueCentroid.getX(),(int)blueCentroid.getY());
			Vision.worldState.setOpponentsRobotPosition((int)yellowCentroid.getX(),(int)yellowCentroid.getY());
			Vision.worldState.setBallPosition((int)ballCentroid.getX(),(int)ballCentroid.getY());
		}

	    	return img;
    }
    /**
     * 
     * @param allGreenThings
     * @return ourGreen
     *  Given all green points return the green points around the blue robot
     */
     /*
    public ArrayList<Point> getGreenPlateBlue(ArrayList<Point> allGreenThings){
    	ArrayList<Point> ourGreen = new ArrayList<Point>();
    	for (int i = 0; i < greenPlates.size(); i++) {
			if( (greenPlates.get(i).x > blueCentroid.x - 40) && (greenPlates.get(i).x < blueCentroid.x + 40) &&(greenPlates.get(i).y > blueCentroid.y - 40) && (greenPlates.get(i).y < blueCentroid.y + 40 ) ){
				ourGreen.add(greenPlates.get(i));
			}
		}
    	return ourGreen;
    }
    */
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

    public Point getBlueGreenPlateCentori(){ 
    	return blueGreenPlateCentroid;
    }
    
    public boolean isBlue(Color c){
        return ( (c.getRed() <= 110) && (c.getBlue()>110)   && (c.getGreen() <= 165));
    }
    
    public boolean isRed(Color c, int GB){
        return ( (c.getRed() > redBallThresh[pitch][0]) &&  (c.getBlue() <= redBallThresh[pitch][1]) &&  (c.getGreen() <= redBallThresh[pitch][2]) && GB < 40 );
    }
    
    public boolean isGreen(Color c, int GB, int RG){
        return ( GB > 50 && RG > 50 && c.getGreen() > 140);
    }
    
    public boolean isGrey(Color c){
        return ((c.getRed() >= ts.getGrey_r_low()) && (c.getRed() <= ts.getGrey_r_high()) && (c.getGreen() >= ts.getGrey_g_low()) && (c.getGreen() <= ts.getGrey_g_high()) && (c.getBlue() >= ts.getGrey_b_low()) && (c.getBlue() <= ts.getGrey_b_high()));
    }
    
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
	    
	    
}
