package uk.ac.ed.inf.sdp2012.group7.vision;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class DistortionFix {
	
	private static int width;
	private static int height;
	private static final double barrelCorrectionX = -0.01;
	private static final double barrelCorrectionY = -0.055;
	ArrayList<Point> points ;
	ArrayList<Point> correctedPoints;
	ArrayList<Color> colors ;

    public BufferedImage removeBarrelDistortion(BufferedImage image, int left, int right, int top, int bottom){
    	
    	ArrayList<Point> points = new ArrayList<Point>();
    	ArrayList<Point> correctedPoints = new ArrayList<Point>();
    	ArrayList<Color> colors = new ArrayList<Color>();
    	Vision.logger.info("This method has not yet been implemented.");
    	width = image.getWidth();
    	height = image.getHeight();
    	
    	for (int i = left; i < right; i++) {
    		for (int j = top; j < bottom; j++) {
    			points.add(new Point(i,j));
    			colors.add(new Color(image.getRGB(i, j)));
			}
		}
    	
    	for(Point p : points){
			correctedPoints.add(barrelCorrected(p));
		}
    	
    	
    	assert correctedPoints.size() == colors.size();
    	
    	for (int i = 0; i < correctedPoints.size(); i++) {
    		if(0 <= correctedPoints.get(i).x && correctedPoints.get(i).x < width && 0 <=  correctedPoints.get(i).y&& correctedPoints.get(i).y < height ){
    			image.setRGB(correctedPoints.get(i).x,correctedPoints.get(i).y, colors.get(i).getRGB());
    		}
			//image.setRGB(correctedPoints.get(i).x,correctedPoints.get(i).y, colors.get(i).getRGB());
		}
    	
        return image;
    }
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
