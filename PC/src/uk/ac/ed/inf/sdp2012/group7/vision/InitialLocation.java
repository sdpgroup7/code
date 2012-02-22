package uk.ac.ed.inf.sdp2012.group7.vision;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.*;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import uk.ac.ed.inf.sdp2012.group7.vision.ui.ControlGUI;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.Pitch;

public class InitialLocation implements MouseListener, MouseMotionListener {
    
    private int count = 0;
    private Point coords = new Point();
    private boolean mouseClick = false;
    private ControlGUI thresholdGUI;
    private VisionFeed visionFeed;
    //private JFrame windowFrame;
    //The below variables are for the testing system
    private ArrayList<Point> points = new ArrayList<Point>();
    public boolean testMouseClick = false;
    public Point testCoords = new Point(0,0);

    public InitialLocation(ControlGUI thresholdsGUI, VisionFeed visionFeed, JFrame windowFrame) {
    	this.thresholdGUI = thresholdsGUI;
        this.visionFeed = visionFeed;
        //this.windowFrame = windowFrame;
        windowFrame.addMouseListener(this);
        windowFrame.addMouseMotionListener(this);
        Vision.logger.info("InitialLocation Initialised");
    }
    
    public InitialLocation(){
    }

	public void mouseExited(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseMoved(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    //When the mouse has been clicked get the location.
    public void mouseClicked(MouseEvent e){
    	Vision.logger.debug(e.getPoint().toString());
        coords = correctPoint(e.getPoint());
        mouseClick = true;
    }
    
    public ArrayList<Point> getTestPoints(){
    	return this.points;
    }
    
    public void getTestData(BufferedImage image, String filename){
    	visionFeed.paused = true;
    	Vision.logger.info("Feed paused.");
    	Vision.logger.info("Saving image.");
    	writeImage(image,filename + ".png");
    	Vision.logger.info("Image saved.");

    	points.add(Vision.worldState.getBall().getPosition().getCentre());
    	Point[] corners = Vision.worldState.getBlueRobot().getPosition().getCorners();
    	points.add(corners[0]);
    	points.add(corners[1]);
    	points.add(corners[2]);
    	points.add(corners[3]);
    	corners = Vision.worldState.getYellowRobot().getPosition().getCorners();
    	points.add(corners[0]);
    	points.add(corners[1]);
    	points.add(corners[2]);
    	points.add(corners[3]);
        visionFeed.paused = false;
        Vision.logger.info("Vision System unpaused.");
    }

	public void writeImage(BufferedImage image, String fn){
        try {
            File outputFile = new File(fn);
            ImageIO.write(image, "png", outputFile);
        } catch (Exception e) {
        	Vision.logger.error("Failed to write image: " + e.getMessage());
        }
    }

    
	public void getPoints(){
	
		Vision.worldState.setPitch(new Pitch(
				    getClickPoint("Click the top left corner"),
				    getClickPoint("Click the top right corner"),
				    getClickPoint("Click the bottom right corner"),
				    getClickPoint("Click the bottom left corner")
				));
		
		Vision.worldState.setPitchBuffers(
				    getClickPoint("Click the top bulge").y,
				    getClickPoint("Click the right bulge").x,
				    getClickPoint("Click the bottom bulge").y,
				    getClickPoint("Click the left bulge").x
				);

		
	}
    /*
    just register the mouse click after being asked to by getClickPoint
    */
	public Point getClickPoint(String message){
		System.out.println(message);
        while (!mouseClick) {
            try{
                Thread.sleep(100);
            } catch (Exception e) {}
        }
        mouseClick = false;
        Vision.logger.debug(coords.toString());
        return coords;
    }
    
    /*
    TODO Get rid of prompts for what we dont need i.e. blue robot
    */
    //Set the sliders on the GUI, the messages are used to tell the user what to click
    public void getColors(){
        //thresholdGUI.setBallValues(getClickColor("Click the ball"));
        thresholdGUI.setYellowValues(getClickColor("Click the yellow robot"));
        //thresholdGUI.setBlueValues(getClickColor("Click the blue robot"));
        //thresholdGUI.setGreenValues(getClickColor("Click a green plate"));
        //thresholdGUI.setGreyValues(getClickColor("Click blue's grey circle"));
        //thresholdGUI.setGreyValues(getClickColor("Click yellow's grey circle"));
    }
    /*
    Get the threshold values for the objects in the match i.e. ball.
    Registers the mouse clicks after being asked to by getColors
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

    public Point correctPoint(Point p){
        return new Point(correctX(p.x),correctY(p.y));
    }
    
    public int correctX(int x){
    	return x-4;
    }
    
    public int correctY(int y){
    	return y-24;
    }

    /*
    Get the color where the mouse was clicked.  Takes an average of the adjacent
    pixels, but you should try and click centrally in the object still.
    */
    public Color getColor(Point p, BufferedImage image){
    	Color c = new Color(image.getRGB(p.x,p.y));
    	System.out.println(c);
    	//Vision.logger.debug(c.toString());
    	return c;
    }
    
    public BufferedImage markImage(BufferedImage image) {
        int width = 640;
        int height = 480;
        Graphics2D graphics = image.createGraphics();
        if(Vision.worldState.getPitch().getBuffersSet()){
        	graphics.drawLine(
        	    Vision.worldState.getPitch().getLeftBuffer(),
        	    0,
        	    Vision.worldState.getPitch().getLeftBuffer(),
        	    height
        	);
        	graphics.drawLine(
        	    Vision.worldState.getPitch().getRightBuffer(),
        	    0,
        	    Vision.worldState.getPitch().getRightBuffer(),
        	    height
        	);
        	graphics.drawLine(
        	    0,
        	    Vision.worldState.getPitch().getTopBuffer(),
        	    width,Vision.worldState.getPitch().getTopBuffer()
        	);
        	graphics.drawLine(
        	    0,
        	    Vision.worldState.getPitch().getBottomBuffer(),
        	    width,Vision.worldState.getPitch().getBottomBuffer()
        	);
            return image;
        } else {
            return image;
        }
    }
    
}
