package uk.ac.ed.inf.sdp2012.group7.vision;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import uk.ac.ed.inf.sdp2012.group7.vision.Thresholding;
import uk.ac.ed.inf.sdp2012.group7.vision.VisionFeed;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

/**
 * Do our work on the frames, call the thresholding,
 * do the marking of frames etc
 * 
 * @author ?
 */

public class FeedProcessor{
	

    
    //private ThresholdsState thresholdsState; //might not be needed any more
    //private OrientationFinder orientationFinder; //might not be needed anymore

    private InitialLocation initialLocation;
    private Thresholding doThresh; // Do Thresholding 
    private VisionFeed visionFeed;
    private OrientationFinder findAngle; // finds the angle
    private BufferedImage previousOverlay = null;
    private DistortionFix fix= new DistortionFix();
    private WorldState worldState = WorldState.getInstance();
    
    private int height;
    private int width;

    
    double prevAngle = 0;
    
    /**
     * Constructor
     * 
     * @param il
     * @param height
     * @param width
     * @param visionFeed
     * @param ts
     */
    public FeedProcessor(InitialLocation il, int height, int width, VisionFeed visionFeed, ThresholdsState ts){
        
    	//this.thresholdsState = controlGUI.getThresholdsState();
        this.initialLocation = il;
        this.height = height;
        this.width = width;
        this.visionFeed = visionFeed;
        this.doThresh = new Thresholding(ts);
        this.findAngle = new OrientationFinder();
        //this.orientationFinder = new OrientationFinder(this.thresholdsState);
        Vision.logger.info("Feed Processor Initialised");
    }
    
    /**
     * Where everything is called from
     * 
     * @param image Current frame
     * @param before ?
     * @param label ?
     */
    public void processAndUpdateImage(BufferedImage image, long before, JLabel label) {
    	if(Vision.TESTING && visionFeed.paused){
    		Graphics frameGraphics = label.getGraphics();
            Graphics imageGraphics = image.getGraphics();
            calculateFPS(before,imageGraphics,frameGraphics, image, this.width, this.height);
    	} else {
    		//image = removeBackground(image,Vision.backgroundImage);
    		if (worldState.getBarrelFix()){
    		    image = fix.removeBarrelDistortion(
    		                                image,
							                worldState.getPitch().getLeftBuffer(),
							                worldState.getPitch().getRightBuffer(),
							                worldState.getPitch().getTopBuffer(),
							                worldState.getPitch().getBottomBuffer()
							                );
		    }
		    
    		image = initialLocation.markImage(image);
            Graphics frameGraphics = label.getGraphics();
            Graphics imageGraphics = doThresh.getThresh(
							                image,
							                worldState.getPitch().getLeftBuffer(),
							                worldState.getPitch().getRightBuffer(),
							                worldState.getPitch().getTopBuffer(),
							                worldState.getPitch().getBottomBuffer()
            							).getGraphics();
            //give strategy a timestamp of when we've finished updating worldstate
            worldState.setUpdatedTime();
            markObjects(imageGraphics);
            if(worldState.getGenerateOverlay()) drawOverlay(image);
    		calculateFPS(before,imageGraphics,frameGraphics, image, this.width, this.height);
            
            
        }

    }
    
    /**
     * Draw pitch onto image
     * @param im Current frame
     */
    public void drawOverlay(BufferedImage im){
    	if(worldState.getOverlay() == null) return;
    	BufferedImage overlay = worldState.getOverlay();
    	int lb = worldState.getPitch().getLeftBuffer();
    	int rb = worldState.getPitch().getRightBuffer();
    	int bb = worldState.getPitch().getBottomBuffer();
    	int tb = worldState.getPitch().getTopBuffer();
    	
    	
    	for(int x = lb; x < rb; x++){
    		for(int y = tb; y < bb; y++){
    			int rgb = im.getRGB(x, y);
    			rgb = rgb | overlay.getRGB(x-lb, y-tb);
    			im.setRGB(x, y, rgb);
    		}
    	}
    	
    	previousOverlay = overlay;
    	
    }
    

    /**
     * Draw positions of objects
     * 
     * @param imageGraphics ?
     */
    public void markObjects(Graphics imageGraphics){
            Point ball = worldState.getBall().getPosition().getCentre();
            Point blue = worldState.getBlueRobot().getPosition().getCentre();
            Point yellow = worldState.getYellowRobot().getPosition().getCentre();

            imageGraphics.setColor(Color.red);
            imageGraphics.drawLine(0, ball.y, 640, ball.y);
            imageGraphics.drawLine(ball.x, 0, ball.x, 480);
            imageGraphics.setColor(Color.blue);
            imageGraphics.drawOval(blue.x-15, blue.y-15, 30,30);
            imageGraphics.setColor(Color.yellow);
            imageGraphics.drawOval(yellow.x-15, yellow.y-15, 30,30);
            imageGraphics.setColor(Color.white);
            worldState.getBlueRobot().addAngle(
            	findAngle.findOrientation(worldState.getBluePixels(),worldState.getBlueRobot().getPosition().getCentre())
            );
            //System.err.
            //Vision.logger.info("Blue robot: " + worldState.getBlueRobot().getAngle());
            Point p = worldState.getBlueRobot().tip;
            imageGraphics.drawLine(
            		worldState.getBlueRobot().getPosition().getCentre().x,
            		worldState.getBlueRobot().getPosition().getCentre().y,
            		p.x,
            		p.y);
            imageGraphics.setColor(Color.red);
            worldState.getYellowRobot().addAngle(
            	findAngle.findOrientation(worldState.getYellowPixels(), worldState.getYellowRobot().getPosition().getCentre())
            );
            //Vision.logger.info("Yellow Robot: " + worldState.getYellowRobot().getAngle());
            p = worldState.getYellowRobot().tip;
            imageGraphics.drawLine(
            		worldState.getYellowRobot().getPosition().getCentre().x,
            		worldState.getYellowRobot().getPosition().getCentre().y,
            		p.x,
            		p.y);
            
            p = worldState.getBall().getPosition().getCentre();
            worldState.getBall().addBallsAngle(p);
            worldState.getBall().calculateBallAngle();
            
            
            
            imageGraphics.drawLine(doThresh.getBlueGreenPlate4Points()[0].x,doThresh.getBlueGreenPlate4Points()[0].y, doThresh.getBlueGreenPlate4Points()[3].x, doThresh.getBlueGreenPlate4Points()[3].y);
            imageGraphics.drawLine(doThresh.getBlueGreenPlate4Points()[0].x,doThresh.getBlueGreenPlate4Points()[0].y, doThresh.getBlueGreenPlate4Points()[2].x, doThresh.getBlueGreenPlate4Points()[2].y);
            imageGraphics.drawLine(doThresh.getBlueGreenPlate4Points()[1].x,doThresh.getBlueGreenPlate4Points()[1].y, doThresh.getBlueGreenPlate4Points()[3].x, doThresh.getBlueGreenPlate4Points()[3].y);
            imageGraphics.drawLine(doThresh.getBlueGreenPlate4Points()[1].x,doThresh.getBlueGreenPlate4Points()[1].y, doThresh.getBlueGreenPlate4Points()[2].x, doThresh.getBlueGreenPlate4Points()[2].y);
            
            imageGraphics.drawLine(doThresh.getYellowGreenPlate4Points()[0].x,doThresh.getYellowGreenPlate4Points()[0].y, doThresh.getYellowGreenPlate4Points()[3].x, doThresh.getYellowGreenPlate4Points()[3].y);
            imageGraphics.drawLine(doThresh.getYellowGreenPlate4Points()[0].x,doThresh.getYellowGreenPlate4Points()[0].y, doThresh.getYellowGreenPlate4Points()[2].x, doThresh.getYellowGreenPlate4Points()[2].y);
            imageGraphics.drawLine(doThresh.getYellowGreenPlate4Points()[1].x,doThresh.getYellowGreenPlate4Points()[1].y, doThresh.getYellowGreenPlate4Points()[3].x, doThresh.getYellowGreenPlate4Points()[3].y);
            imageGraphics.drawLine(doThresh.getYellowGreenPlate4Points()[1].x,doThresh.getYellowGreenPlate4Points()[1].y, doThresh.getYellowGreenPlate4Points()[2].x, doThresh.getYellowGreenPlate4Points()[2].y);
    }
    
    /**
     * Calculates and draws FPS and the HUD
     * 
     * @param before ?
     * @param imageGraphics ?
     * @param frameGraphics ?
     * @param image The frame to draw on
     * @param width Of image
     * @param height Of image
     */
    public void calculateFPS(long before, Graphics imageGraphics, Graphics frameGraphics, BufferedImage image, int width, int height){
        /* Used to calculate the FPS. */
        long after = System.currentTimeMillis();

        /* Display the FPS that the vision system is running at. */
        float fps = (1.0f)/((after - before) / 1000.0f);
        imageGraphics.setColor(Color.white);
        imageGraphics.drawString("FPS: " + fps, 30, 420);
        if (worldState.getColor() == Color.blue){
            imageGraphics.drawString("Our Colour: Blue", 30, 435);
        }else{
            imageGraphics.drawString("Our Colour: Yellow", 30, 435);
        }
        if (worldState.getRoom() == 0){
            imageGraphics.drawString("Pitch: Main", 30, 450);
        }else{
            imageGraphics.drawString("Pitch: Secondary", 30, 450);
        }
        if (worldState.getShootingDirection() == 1){
            imageGraphics.drawString("Shooting: Right", 30, 465);
        }else{
            imageGraphics.drawString("Shooting: Left", 30, 465);
        }
        
        imageGraphics.drawString("Our Position: (" + worldState.getOurRobot().getPosition().getCentre().x + "," + worldState.getOurRobot().getPosition().getCentre().y + ")", 30, 20);
        imageGraphics.drawString("Our Velocity: " + String.format("%.4g%n", worldState.getOurRobot().getVelocity()) + "px/s", 30, 35);
        imageGraphics.drawString("Our Bearing: " + String.format("%.4g%n", worldState.getOurRobot().getAngle()) + "rads", 30, 50);
        imageGraphics.drawString("Dist to Ball: " + String.format("%.4g%n",Point.distance(worldState.getOurRobot().getPosition().getCentre().x, worldState.getOurRobot().getPosition().getCentre().y, worldState.getBall().getPosition().getCentre().x, worldState.getBall().getPosition().getCentre().y)) + "px", 30, 65);
        imageGraphics.drawString("Dist to Opp: " + String.format("%.4g%n",Point.distance(worldState.getOurRobot().getPosition().getCentre().x, worldState.getOurRobot().getPosition().getCentre().y, worldState.getOpponentsRobot().getPosition().getCentre().x, worldState.getOpponentsRobot().getPosition().getCentre().y)) + "px", 30, 80);
        
        imageGraphics.drawString("Opp Position: (" + worldState.getOpponentsRobot().getPosition().getCentre().x + "," + worldState.getOpponentsRobot().getPosition().getCentre().y + ")", 220, 20);
        imageGraphics.drawString("Opp Velocity: " + String.format("%.4g%n", worldState.getOpponentsRobot().getVelocity()) + "px/s", 220, 35);
        imageGraphics.drawString("Opp Bearing: " + String.format("%.4g%n", worldState.getOpponentsRobot().getAngle()) + "rads", 220, 50);
        imageGraphics.drawString("Dist to Ball: " + String.format("%.4g%n",Point.distance(worldState.getOpponentsRobot().getPosition().getCentre().x, worldState.getOpponentsRobot().getPosition().getCentre().y, worldState.getBall().getPosition().getCentre().x, worldState.getBall().getPosition().getCentre().y)) + "px", 220, 65);
        imageGraphics.drawString("Dist to Us: " + String.format("%.4g%n",Point.distance(worldState.getOurRobot().getPosition().getCentre().x, worldState.getOurRobot().getPosition().getCentre().y, worldState.getOpponentsRobot().getPosition().getCentre().x, worldState.getOpponentsRobot().getPosition().getCentre().y)) + "px", 220, 80);
        
        imageGraphics.drawString("Ball Position: (" + worldState.getBall().getPosition().getCentre().x + "," + worldState.getBall().getPosition().getCentre().y + ")", 410, 20);
        imageGraphics.drawString("Ball Velocity: " + String.format("%.4g%n", worldState.getBall().getVelocity()) + "px/s", 410, 35);
        imageGraphics.drawString("Ball Bearing: " + String.format("%.4g%n", worldState.getBall().getAngle()) + "rads", 410, 50);
        
        imageGraphics.drawString("Strategy Update Time: " + worldState.getStrategyTime() + "ms", 220, 420);
        
        
        frameGraphics.drawImage(image, 0, 0, width, height, null);
    }
    

}
