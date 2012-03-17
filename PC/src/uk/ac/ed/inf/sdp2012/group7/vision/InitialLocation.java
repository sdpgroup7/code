package uk.ac.ed.inf.sdp2012.group7.vision;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.*;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.Pitch;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;
/**
 * Get the colour value of yellow and do the manual testing stuff
 * 
 * @author James Hulme
 * @author Dale Myers
 */
public class InitialLocation implements MouseListener, MouseMotionListener {
	

    
    private int count = 0;
    private Point coords = new Point();
    private boolean mouseClick = false;
    
    private ThresholdsState thresholdsState;
    private WorldState worldState = WorldState.getInstance();
    
    private VisionFeed visionFeed;
    //private JFrame windowFrame;
    //The below variables are for the testing system
    private ArrayList<Point> points = new ArrayList<Point>();
    private ArrayList<Point> autoPoints = new ArrayList<Point>();
    private ArrayList<Double> angles = new ArrayList<Double>();
    private ArrayList<Integer> pitch = new ArrayList<Integer>();
    public boolean testMouseClick = false;
    public Point testCoords = new Point(0,0);
    
    /**
     * The constructor
     * @param visionFeed 
     * @param windowFrame
     * @param ts
     */
    public InitialLocation(VisionFeed visionFeed, JFrame windowFrame, ThresholdsState ts) {
        this.visionFeed = visionFeed;
        this.thresholdsState = ts;
        //this.windowFrame = windowFrame;
        windowFrame.addMouseListener(this);
        windowFrame.addMouseMotionListener(this);
        Vision.logger.info("InitialLocation Initialised");
    }
    
    /**
     * Constructor
     */
    public InitialLocation(){
    }

	public void mouseExited(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseMoved(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    //When the mouse has been clicked get the location.
    /**
     * Checks when mouse is clicked
     */
    public void mouseClicked(MouseEvent e){
    	Vision.logger.debug(e.getPoint().toString());
        coords = correctPoint(e.getPoint());
        mouseClick = true;
    }
    
    public ArrayList<Point> getTestPoints(){
    	return this.points;
    }
    
    public ArrayList<Point> getTestPointsAuto(){
    	return this.autoPoints;
    }
    
    public ArrayList<Double> getOrientationPoints(){
    	return this.angles;
    }
    
    public ArrayList<Integer> getPitchPoints(){
    	return this.pitch;
    }
    
    /**
     * Get the data for the test framework
     * 
     * @param image Frame to test
     * @param filename What to call saved file
     */
    public void getTestData(BufferedImage image, String filename){
    	visionFeed.paused = true;
    	Vision.logger.info("Feed paused.");
    	Vision.logger.info("Saving image.");
    	writeImage(image,filename + ".png");
    	Vision.logger.info("Image saved.");
    	
    	getClickColor("Click yellow centroid");
    	points.add(coords);
    	getClickColor("Click bottom of yellow T");
    	points.add(coords);
    	
    	getClickColor("Click blue centroid");
    	points.add(coords);
    	getClickColor("Click bottom of blue T");
    	points.add(coords);
    	
    	getClickColor("Click ball centroid");
    	points.add(coords);
    	
    	//for checking that pixelstoCM and cmtoPixels is working, and that o
    	getClickColor("Click left goal line");
    	points.add(coords);
    	
    	getClickColor("Click right goal line");
    	points.add(coords);
    	
    	
    	
    	autoPoints.add(worldState.getBall().getPosition().getCentre());
        autoPoints.add(worldState.getOurRobot().getPosition().getCentre());
        angles.add(worldState.getOurRobot().getAngle());
        autoPoints.add(worldState.getOpponentsRobot().getPosition().getCentre());
        angles.add(worldState.getOpponentsRobot().getAngle());
        pitch.add(worldState.getPitch().getLeftBuffer());
        pitch.add(worldState.getPitch().getRightBuffer());
    	
        visionFeed.paused = false;
        Vision.logger.info("Vision System unpaused.");
    }
    
    
    //Set the sliders on the GUI, the messages are used to tell the user what to click
    public void getColors(){
        setYellowValues(getClickColor("Click the yellow robot"));
    }
    
    public void setYellowValues(Color c){
    	setYellowValues(c.getRed(),c.getGreen(),c.getBlue());
    }
    
    /**
     * Set the yellow values
     * 
     * r,g,b gained from setYellowValues with just a c
     * 
     * @param r red channel
     * @param g green channel
     * @param b blue channel
     */
	public void setYellowValues(int r, int g, int b){
		int YELLOW_THRESHOLD = 25;
		int rLower = r-YELLOW_THRESHOLD;
		int rUpper = r+YELLOW_THRESHOLD;
		int gLower = g-YELLOW_THRESHOLD;
		int gUpper = g+YELLOW_THRESHOLD;
		int bLower = b-YELLOW_THRESHOLD;
		int bUpper = b+YELLOW_THRESHOLD;

		if(rLower < 0) rLower = 0;
		if(gLower < 0) gLower = 0;
		if(bLower < 0) bLower = 0;
		if(rUpper > 255) rUpper = 255;
		if(rUpper > 255) rUpper = 255;
		if(rUpper > 255) rUpper = 255;
		
		
		thresholdsState.setYellow_r_low(rLower);
		thresholdsState.setYellow_r_high(rUpper);
		thresholdsState.setYellow_g_low(gLower);
		thresholdsState.setYellow_g_high(gUpper);
		thresholdsState.setYellow_b_low(bLower);
		thresholdsState.setYellow_b_high(bUpper);
	}
	
    /**
     * Get the color where we click
     * 
     * @param message Asks what to click on
     * @return The colour of pixel where we clicked
     */
    public Color getClickColor(String message){
        System.out.println(message);

        while (!mouseClick) {
            try{
                Thread.sleep(100);
            } catch (Exception e) {}
        }
        mouseClick = false;
        count++;
        
        return getColor(coords, this.visionFeed.getFrameImage());
    }
    
    /**
     * Necessary for some reason
     * 
     * @param p Point to correct
     * @return Corrected Point
     */
    public Point correctPoint(Point p){
        return new Point(correctX(p.x),correctY(p.y));
    }
    
    public int correctX(int x){
    	return x-4;
    }
    
    public int correctY(int y){
    	return y-24;
    }

    /**
     * Gets the colour where clicked
     * 
     * @param p Point where we clicked
     * @param image Frame to get colour from
     * @return The colour
     */
    public Color getColor(Point p, BufferedImage image){
    	Color c = new Color(image.getRGB(p.x,p.y));
    	System.out.println(c);
    	//Vision.logger.debug(c.toString());
    	return c;
    }
    
    /**
     * Draw the pitch boundaries on
     * 
     * @param image Frame to draw on
     * @return The frame after being drawn on
     */
    public BufferedImage markImage(BufferedImage image) {
        int width = 640;
        int height = 480;
        Graphics2D graphics = image.createGraphics();
        if(worldState.getPitch().getBuffersSet()){
        	graphics.drawLine(
        	    worldState.getPitch().getLeftBuffer(),
        	    0,
        	    worldState.getPitch().getLeftBuffer(),
        	    height
        	);
        	graphics.drawLine(
        	    worldState.getPitch().getRightBuffer(),
        	    0,
        	    worldState.getPitch().getRightBuffer(),
        	    height
        	);
        	graphics.drawLine(
        	    0,
        	    worldState.getPitch().getTopBuffer(),
        	    width,worldState.getPitch().getTopBuffer()
        	);
        	graphics.drawLine(
        	    0,
        	    worldState.getPitch().getBottomBuffer(),
        	    width,worldState.getPitch().getBottomBuffer()
        	);
            return image;
        } else {
            return image;
        }
    }
    
    /**
     * Save an image
     * 
     * @param image What to save
     * @param fn Where to save
     */
    public void writeImage(BufferedImage image, String fn){
        try {
            File outputFile = new File(fn);
            ImageIO.write(image, "png", outputFile);
        } catch (Exception e) {
        	Vision.logger.error("Failed to write image: " + e.getMessage());
        }
    }
    
}
