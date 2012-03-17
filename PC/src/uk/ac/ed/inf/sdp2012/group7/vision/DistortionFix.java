package uk.ac.ed.inf.sdp2012.group7.vision;

import java.awt.image.BufferedImage;
import java.awt.Point;

/**
 * Class to remove barrel distortion from bufferedimages
 * 
 * @author Rado
 * @author James Hulme
 * 
 */


public class DistortionFix {
	
	private static int width = 640;
	private static int height = 480;
	private static final double barrelCorrectionX = -0.01;
	private static final double barrelCorrectionY = -0.055;

	private Point p = new Point();
	
	/**
	 * Remove barrel distortion on whole image
	 * 
	 * Buffers used so we only correct the pitch area not the useless background area
	 * 
	 * @param image Frame to correct
	 * @param left Left buffer
	 * @param right Right buffer
	 * @param top Top buffer
	 * @param bottom Bottom buffer
	 * @return A new image with no barrel distortion
	 */
    public BufferedImage removeBarrelDistortion(BufferedImage image, int left, int right, int top, int bottom){

    	BufferedImage newImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
    	
    	for (int i = left; i < right; i++) {
    		for (int j = top; j < bottom; j++) {
    			p = barrelCorrected(new Point(i,j));
    			
    			if(left <= p.x && p.x < right && top <=  p.y&& p.y < bottom ){
    			newImage.setRGB(p.x,p.y, image.getRGB(i,j));
    		    }
			}
		}
    	
        return newImage;
    }
    
    /**
     * Correct for single points
     * 
     * Called by the above function, but also called when overlay is turned off
     * 
     * @param p1 Point to fix
     * @return Fixed Point
     */
    public static Point barrelCorrected(Point p1) {
    	// System.out.println("Pixel: (" + x + ", " + y + ")");
    	// first normalise pixel
    	double px = (2 * p1.x - width) / (double) width;
    	double py = (2 * p1.y - height) / (double) height;

    	// System.out.println("Norm Pixel: (" + px + ", " + py + ")");
    	// then compute the radius of the pixel you are working with
    	double rad = px * px + py * py;

    	// then compute new pixel'
    	double px1 = px * (1 - barrelCorrectionX * rad);
    	double py1 = py * (1 - barrelCorrectionY * rad);

    	// then convert back
    	int pixi = (int) ((px1 + 1) * width / 2);
    	int pixj = (int) ((py1 + 1) * height / 2);
    	// System.out.println("New Pixel: (" + pixi + ", " + pixj + ")");
    	return new Point(pixi, pixj);
    	}
}
