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
    
    private Point pastBlueCent = new Point();
    private Point pastYellCent = new Point();
    private Point pastOurGreyCent = new Point();
    private Point pastOpponentGreyCent = new Point(); 
    
    private Point ballCentroid = new Point();
    private Point blueCentroid = new Point();
    private Point yellowCentroid = new Point();
    private Point blueGreenPlateCentroid = new Point();
    private Point ourGreyCentroid = new Point();
    private Point opponentGreyCentroid = new Point();
    
    private int ballCount;
    private int yellowCount;
    private int blueCount;
    private int ourGreyCount;
    private int opponentGreyCount;
 //    private int robot; // 0 for Yellow, 1 for Blue(our robot)  We will use the world state
    
    private ThresholdsState ts;

    private EuclideanDistance ed = new EuclideanDistance();
    

    
    
    public Thresholding(ThresholdsState ts) {  // Sets the constants for thresholding for each pitch 
    	redBallThresh[0][0] = 170;
    	redBallThresh[0][1] = 120;
    	redBallThresh[0][2] = 120;
    	redBallThresh[1][0] = 150;
    	redBallThresh[1][1] = 100;
    	redBallThresh[1][2] = 100;
    	yellowRobotThresh[0][0] = 140;
    	yellowRobotThresh[0][1] = 140;
    	yellowRobotThresh[0][2] = 170;
		yellowRobotThresh[1][0] = 150;
		yellowRobotThresh[1][1] = 190;
		yellowRobotThresh[1][2] = 140;
		blueRobotThresh[0][0] = 100;
		blueRobotThresh[0][1] = 170;
		blueRobotThresh[0][2] = 100;
		blueRobotThresh[1][0] = 130;
		blueRobotThresh[1][1] = 140;
		blueRobotThresh[1][2] = 90;
		greenPlatesThresh[0][0] = 200;
		greenPlatesThresh[1][0] = 140;

    	
    	this.ts = ts;

    }
    public BufferedImage getThresh(BufferedImage img, int left, int right, int top, int bottom) { // Method to get thresholded image 
    		//Vision.logger.debug("Starting thresholding");
    		
    	if (Vision.worldState.isClickingDone()){

    		pitch = Vision.worldState.getRoom();
    		width = right-left;
    		height = top-bottom;
    		if (Vision.worldState.getColor() == Color.yellow) {
    			  pastBlueCent = Vision.worldState.getOpponentsRobot().getPosition().getCentre();
    	    	  pastYellCent = Vision.worldState.getOurRobot().getPosition().getCentre();
    			
    		}
    		else {
    			pastBlueCent = Vision.worldState.getOurRobot().getPosition().getCentre();
   	    	  	pastYellCent = Vision.worldState.getOpponentsRobot().getPosition().getCentre();
    		}
    	  
    	  	pastOurGreyCent = Vision.worldState.getOurGrey().getPosition().getCentre();
    	  	pastOpponentGreyCent = Vision.worldState.getOpponentsGrey().getPosition().getCentre();
		 //  BufferedImage threshed = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);

           ballCount = 0;
           ballCentroid.setLocation(0,0);
            
           blueCount = 0;
           blueCentroid.setLocation(0,0);
            
           yellowCount = 0;
           yellowCentroid.setLocation(0,0);
           
           ourGreyCount = 0;
           ourGreyCentroid.setLocation(0,0);
           
           opponentGreyCount = 0;
           ourGreyCentroid.setLocation(0,0);

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
					else if (isYellow(c) && (ed.getDistance(pastYellCent, new Point(i,j)) < 25)) {
						img.setRGB(i, j, Color.yellow.getRGB()); // Yellow robot
						yellowRobotX.add(i);
						yellowRobotY.add(j);
						yellowCount++;
						yellowCentroid.setLocation(yellowCentroid.getX() + i, yellowCentroid.getY() + j);
					}
					else if (isBlue(c) && (ed.getDistance(pastBlueCent, new Point(i,j)) < 25)){
						img.setRGB(i, j, Color.blue.getRGB()); // Blue robot 
						blueRobotX.add(i);
						blueRobotY.add(j);
						blueCount++;
						blueCentroid.setLocation(blueCentroid.getX() + i, blueCentroid.getY() + j);
						//make blue thresholds for the different pitches in that [pitch][x] style
					}
					/*else if (isGreen(c,GB,RG))  {
						img.setRGB(i,j, Color.green.getRGB()); // GreenPlates 
					}*/
					else if (isGrey(c) && (ed.getDistance(pastOpponentGreyCent, new Point(i,j)) < 10) && (ed.getDistance(Vision.worldState.getOpponentsRobot().getPosition().getCentre(), new Point(i,j)) < 23) ) {
						
					    img.setRGB(i,j, Color.pink.getRGB());
					    opponentGreyCount++;
					    opponentGreyCentroid.setLocation(opponentGreyCentroid.getX() + i, opponentGreyCentroid.getY() + j);

					}
					else if (isGrey(c) && (ed.getDistance(pastOurGreyCent, new Point(i,j)) < 10) && (ed.getDistance(Vision.worldState.getOurRobot().getPosition().getCentre(), new Point(i,j)) < 23) )  {
						
					    img.setRGB(i,j, Color.orange.getRGB());
					    ourGreyCount++;
					    ourGreyCentroid.setLocation(ourGreyCentroid.getX() + i, ourGreyCentroid.getY() + j);

					}
				}
			}
	    	//Vision.logger.debug("End Iteration");
			ballCentroid.setLocation(ballCentroid.getX()/ballCount, ballCentroid.getY()/ballCount);
			yellowCentroid.setLocation(yellowCentroid.getX()/yellowCount, yellowCentroid.getY()/yellowCount);
			blueCentroid.setLocation(blueCentroid.getX()/blueCount, blueCentroid.getY()/blueCount);
			ourGreyCentroid.setLocation(ourGreyCentroid.getX()/ourGreyCount, ourGreyCentroid.getY()/ourGreyCount);
			opponentGreyCentroid.setLocation(opponentGreyCentroid.getX()/opponentGreyCount, opponentGreyCentroid.getY()/opponentGreyCount);
			
			if (Vision.worldState.getColor() == Color.blue) {
			    Vision.worldState.setOurRobotPosition((int)blueCentroid.getX(),(int)blueCentroid.getY());
			    Vision.worldState.setOpponentsRobotPosition((int)yellowCentroid.getX(),(int)yellowCentroid.getY());
			} else {
			    Vision.worldState.setOpponentsRobotPosition((int)blueCentroid.getX(),(int)blueCentroid.getY());
			    Vision.worldState.setOurRobotPosition((int)yellowCentroid.getX(),(int)yellowCentroid.getY());
			}
			
			Vision.worldState.setBallPosition((int)ballCentroid.getX(),(int)ballCentroid.getY());
			Vision.worldState.setOurGreyPosition((int)ourGreyCentroid.getX() ,(int)ourGreyCentroid.getY());
			Vision.worldState.setOpponentsGreyPosition((int)opponentGreyCentroid.getX(), (int)opponentGreyCentroid.getY());
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
        return ( (c.getRed() <= blueRobotThresh[pitch][0]) && (c.getBlue() > blueRobotThresh[pitch][2])   && (c.getGreen() <= blueRobotThresh[pitch][1]));
    }
    
    public boolean isRed(Color c, int GB){
        return ( (c.getRed() > redBallThresh[pitch][0]) &&  (c.getBlue() <= redBallThresh[pitch][1]) &&  (c.getGreen() <= redBallThresh[pitch][2]) && GB < 70 );
    }
    
    public boolean isGreen(Color c, int GB, int RG){
        return ( GB > 100 && RG > 100 && c.getGreen() > greenPlatesThresh[pitch][0]);
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
	    
	    
}
