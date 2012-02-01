package uk.ac.ed.inf.sdp2012.group7.vision;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Point;

/**
 * 
 * @author s0951580
 */
public class Thresholding {

    private final Color black= new Color(0,0,0);
    private final Color white = new Color(255,255,255);
    private final Color red = new Color(255,0,0);
    private final Color yellow = new Color(255,255,0);
    private final Color blue = new Color(0,0,255);
    private Color c;
	private int GB;// green - blue
	private int RG; // red - green
	private int RGthresh;
    private int[][] redBallThresh= new int[2][3];
    private int[][] yellowRobotThresh= new int[2][3];
    private int[][] blueRobotThresh= new int[2][3];
    private int pitch;
    private int height;
    private int width;
    private Point ballCentroid = new Point();
    private Point blueCentroid = new Point();
    private Point yellowCentroid = new Point();
    private int ballCount;
    private int yellowCount;
    private int blueCount;
    
    
    public Thresholding(int pitch) {
    	redBallThresh[0][0] = 130;
    	redBallThresh[0][1] = 110;
    	redBallThresh[0][2] = 110;
    	redBallThresh[1][0] = 150;
    	redBallThresh[1][1] = 110;
    	redBallThresh[1][2] = 110;
	yellowRobotThresh[0][0] = 140;
	yellowRobotThresh[0][1] = 140;
	yellowRobotThresh[0][2] = 150;
	yellowRobotThresh[1][0] = 150;
	yellowRobotThresh[1][1] = 190;
	yellowRobotThresh[1][2] = 140;
	blueRobotThresh[0][0] = 150;
	blueRobotThresh[0][1] = 150;
	blueRobotThresh[0][2] = 100;
	blueRobotThresh[1][0] = 150;
	blueRobotThresh[1][1] = 150;
	blueRobotThresh[1][2] = 100;

    	this.pitch=pitch;
    }
    public BufferedImage getThresh(BufferedImage img, Point TL, Point BR) { // Method to get thresholded image 
		   	
		   width = BR.x;
		   height = BR.y;
		 //  BufferedImage threshed = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
	    	
           ballCount = 0;
           ballCentroid.setLocation(0,0);
            
           blueCount = 0;
           blueCentroid.setLocation(0,0);
            
           yellowCount = 0;
           yellowCentroid.setLocation(0,0);

	    	
	    	for (int i = TL.x; i < width; i++) {
				for (int j = TL.y; j < height; j++) {
					c = new Color(img.getRGB(i,j));
					GB = Math.abs((c.getBlue() - c.getGreen()));
					RG = Math.abs((c.getRed() - c.getGreen()));
					if( (c.getRed() > redBallThresh[pitch][0]) &&  (c.getBlue() <= redBallThresh[pitch][1]) &&  (c.getGreen() <= redBallThresh[pitch][2]) && GB < 40){
						img.setRGB(i, j, black.getRGB()); //Red Ball
						ballCount++;
						ballCentroid.setLocation(ballCentroid.getX() + i, ballCentroid.getY() + j);
						
					}
					else if( RG < 35 &&  (c.getBlue() <= yellowRobotThresh[pitch][2]) && (c.getRed() > yellowRobotThresh[pitch][0])  && (c.getGreen() > yellowRobotThresh[pitch][1])   ){
						img.setRGB(i, j, yellow.getRGB()); // Yellow robot
						yellowCount++;
						yellowCentroid.setLocation(yellowCentroid.getX() + i, yellowCentroid.getY() + j);
					}
					else if( (c.getRed() <= 130) && (c.getBlue()>90)  && (c.getGreen()>130) && (c.getGreen() <= 170)){
						img.setRGB(i, j, blue.getRGB()); // Blue robot 
						blueCount++;
						blueCentroid.setLocation(blueCentroid.getX() + i, blueCentroid.getY() + j);
					}
				}
			}
			
			ballCentroid.setLocation(ballCentroid.getX()/ballCount, ballCentroid.getY()/ballCount);
			yellowCentroid.setLocation(yellowCentroid.getX()/yellowCount, yellowCentroid.getY()/yellowCount);
			blueCentroid.setLocation(blueCentroid.getX()/blueCount, blueCentroid.getY()/blueCount);
			
			
	    	return img;
    }
    
    public Point getBallCentroid() {
        return ballCentroid;
    }
    
    public Point getBlueCentroid() {
        return blueCentroid;
    }
    
    public Point getYellowCentroid() {
        return yellowCentroid;
    }
	    
	    
}
