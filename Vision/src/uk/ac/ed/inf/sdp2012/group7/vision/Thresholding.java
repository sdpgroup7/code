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
    private Color c;
	private int GB;// green - blue
	private int RG; // red - green
	private int RGthresh;
    private int[][] redBallThresh= new int[2][3];
    private int pitch;
    private int height;
    private int width;
    
    
    public Thresholding(int pitch) {
    	redBallThresh[0][0] = 130;
    	redBallThresh[0][1] = 110;
    	redBallThresh[0][2] = 110;
    	redBallThresh[1][0] = 160;
    	redBallThresh[1][1] = 110;
    	redBallThresh[1][2] = 110;
    	this.pitch=pitch;
    }
	   public BufferedImage getThresh(BufferedImage img, Point TL, Point BR) { // Method to get thresholded image 
		   	
		   width = BR.x;
		   height = BR.y;
		 //  BufferedImage threshed = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
	    	


	    	
	    	for (int i = TL.x; i < width; i++) {
				for (int j = TL.y; j < height; j++) {
					c = new Color(img.getRGB(i,j));
					GB = Math.abs((c.getBlue() - c.getGreen()));
					RG = Math.abs((c.getRed() - c.getGreen()));
					if( (c.getRed() > redBallThresh[pitch][0]) &&  (c.getBlue() <= redBallThresh[pitch][1]) &&  (c.getGreen() <= redBallThresh[pitch][2]) && GB < 40){
						img.setRGB(i, j, black.getRGB()); //Red Ball
					}
				/*	else if( RG < 35 &&  (c.getBlue() <=150) && (c.getRed() > 140)  && (c.getRed() > 140)   ){
						threshed.setRGB(i, j, black.getRGB()); // Yellow robot
					}
					else if( (c.getRed() <= 120) && (c.getBlue()>100)  && (c.getGreen()>140) && (c.getGreen() <= 165)){
						threshed.setRGB(i, j, black.getRGB()); // Blue robot 
					}*/
					else{
						//img.setRGB(i, j, red.getRGB());
					}
				}
			}
	    	return img;
	    }
}
