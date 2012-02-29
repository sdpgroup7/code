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



public class FeedProcessor{
    
    //private ThresholdsState thresholdsState; //might not be needed any more
    //private OrientationFinder orientationFinder; //might not be needed anymore

    private InitialLocation initialLocation;
    private Thresholding doThresh; // Do Thresholding 
    private VisionFeed visionFeed;
    private OrientationFinder findAngle; // finds the angle
    private BufferedImage previousOverlay = null;
    private DistortionFix fix= new DistortionFix();
    
    private int height;
    private int width;

    
    double prevAngle = 0;

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

    public void processAndUpdateImage(BufferedImage image, long before, JLabel label) {
    	if(Vision.TESTING && visionFeed.paused){
    		Graphics frameGraphics = label.getGraphics();
            Graphics imageGraphics = image.getGraphics();
            calculateFPS(before,imageGraphics,frameGraphics, image, this.width, this.height);
    	} else {
    		//image = removeBackground(image,Vision.backgroundImage);
    		if (Vision.worldState.getBarrelFix()){
    		    image = fix.removeBarrelDistortion(
    		                                image,
							                Vision.worldState.getPitch().getLeftBuffer(),
							                Vision.worldState.getPitch().getRightBuffer(),
							                Vision.worldState.getPitch().getTopBuffer(),
							                Vision.worldState.getPitch().getBottomBuffer()
							                );
		    }
		    
    		image = initialLocation.markImage(image);
            Graphics frameGraphics = label.getGraphics();
            Graphics imageGraphics = doThresh.getThresh(
							                image,
							                Vision.worldState.getPitch().getLeftBuffer(),
							                Vision.worldState.getPitch().getRightBuffer(),
							                Vision.worldState.getPitch().getTopBuffer(),
							                Vision.worldState.getPitch().getBottomBuffer()
            							).getGraphics();
            //give strategy a timestamp of when we've finished updating worldstate
            Vision.worldState.setUpdatedTime();
            markObjects(imageGraphics);
            if(Vision.worldState.getGenerateOverlay()) drawOverlay(image);
    		calculateFPS(before,imageGraphics,frameGraphics, image, this.width, this.height);
            
            
        }

    }
    
    public void drawOverlay(BufferedImage im){
    	if(Vision.worldState.getOverlay() == null) return;
    	BufferedImage overlay = Vision.worldState.getOverlay();
    	int lb = Vision.worldState.getPitch().getLeftBuffer();
    	int rb = Vision.worldState.getPitch().getRightBuffer();
    	int bb = Vision.worldState.getPitch().getBottomBuffer();
    	int tb = Vision.worldState.getPitch().getTopBuffer();
    	
    	
    	for(int x = lb; x < rb; x++){
    		for(int y = tb; y < bb; y++){
    			int rgb = im.getRGB(x, y);
    			rgb = rgb | overlay.getRGB(x-lb, y-tb);
    			im.setRGB(x, y, rgb);
    		}
    	}
    	
    	previousOverlay = overlay;
    	
    }
    

    
    public void markObjects(Graphics imageGraphics){
            Point ball = Vision.worldState.getBall().getPosition().getCentre();
            Point blue = Vision.worldState.getBlueRobot().getPosition().getCentre();
            Point yellow = Vision.worldState.getYellowRobot().getPosition().getCentre();

            imageGraphics.setColor(Color.red);
            imageGraphics.drawLine(0, ball.y, 640, ball.y);
            imageGraphics.drawLine(ball.x, 0, ball.x, 480);
            imageGraphics.setColor(Color.blue);
            imageGraphics.drawOval(blue.x-15, blue.y-15, 30,30);
            imageGraphics.setColor(Color.yellow);
            imageGraphics.drawOval(yellow.x-15, yellow.y-15, 30,30);
            imageGraphics.setColor(Color.white);
            Vision.worldState.getBlueRobot().addAngle(
            	findAngle.findOrientation(Vision.worldState.getBluePixels(),Vision.worldState.getBlueRobot().getPosition().getCentre())
            );
            //System.err.
            //Vision.logger.info("Blue robot: " + Vision.worldState.getBlueRobot().getAngle());
            Point p = Vision.worldState.getBlueRobot().tip;
            imageGraphics.drawLine(
            		Vision.worldState.getBlueRobot().getPosition().getCentre().x,
            		Vision.worldState.getBlueRobot().getPosition().getCentre().y,
            		p.x,
            		p.y);
            imageGraphics.setColor(Color.red);
            Vision.worldState.getYellowRobot().addAngle(
            	findAngle.findOrientation(Vision.worldState.getYellowPixels(), Vision.worldState.getYellowRobot().getPosition().getCentre())
            );
            //Vision.logger.info("Yellow Robot: " + Vision.worldState.getYellowRobot().getAngle());
            p = Vision.worldState.getYellowRobot().tip;
            imageGraphics.drawLine(
            		Vision.worldState.getYellowRobot().getPosition().getCentre().x,
            		Vision.worldState.getYellowRobot().getPosition().getCentre().y,
            		p.x,
            		p.y);
            
            imageGraphics.drawLine(doThresh.getBlueGreenPlate4Points()[0].x,doThresh.getBlueGreenPlate4Points()[0].y, doThresh.getBlueGreenPlate4Points()[3].x, doThresh.getBlueGreenPlate4Points()[3].y);
            imageGraphics.drawLine(doThresh.getBlueGreenPlate4Points()[0].x,doThresh.getBlueGreenPlate4Points()[0].y, doThresh.getBlueGreenPlate4Points()[2].x, doThresh.getBlueGreenPlate4Points()[2].y);
            imageGraphics.drawLine(doThresh.getBlueGreenPlate4Points()[1].x,doThresh.getBlueGreenPlate4Points()[1].y, doThresh.getBlueGreenPlate4Points()[3].x, doThresh.getBlueGreenPlate4Points()[3].y);
            imageGraphics.drawLine(doThresh.getBlueGreenPlate4Points()[1].x,doThresh.getBlueGreenPlate4Points()[1].y, doThresh.getBlueGreenPlate4Points()[2].x, doThresh.getBlueGreenPlate4Points()[2].y);
            
            imageGraphics.drawLine(doThresh.getYellowGreenPlate4Points()[0].x,doThresh.getYellowGreenPlate4Points()[0].y, doThresh.getYellowGreenPlate4Points()[3].x, doThresh.getYellowGreenPlate4Points()[3].y);
            imageGraphics.drawLine(doThresh.getYellowGreenPlate4Points()[0].x,doThresh.getYellowGreenPlate4Points()[0].y, doThresh.getYellowGreenPlate4Points()[2].x, doThresh.getYellowGreenPlate4Points()[2].y);
            imageGraphics.drawLine(doThresh.getYellowGreenPlate4Points()[1].x,doThresh.getYellowGreenPlate4Points()[1].y, doThresh.getYellowGreenPlate4Points()[3].x, doThresh.getYellowGreenPlate4Points()[3].y);
            imageGraphics.drawLine(doThresh.getYellowGreenPlate4Points()[1].x,doThresh.getYellowGreenPlate4Points()[1].y, doThresh.getYellowGreenPlate4Points()[2].x, doThresh.getYellowGreenPlate4Points()[2].y);
    }
    

    public static void calculateFPS(long before, Graphics imageGraphics, Graphics frameGraphics, BufferedImage image, int width, int height){
        /* Used to calculate the FPS. */
        long after = System.currentTimeMillis();

        /* Display the FPS that the vision system is running at. */
        float fps = (1.0f)/((after - before) / 1000.0f);
        imageGraphics.setColor(Color.white);
        imageGraphics.drawString("FPS: " + fps, 30, 420);
        if (Vision.worldState.getColor() == Color.blue){
            imageGraphics.drawString("Our Colour: Blue", 30, 435);
        }else{
            imageGraphics.drawString("Our Colour: Yellow", 30, 435);
        }
        if (Vision.worldState.getRoom() == 0){
            imageGraphics.drawString("Pitch: Main", 30, 450);
        }else{
            imageGraphics.drawString("Pitch: Secondary", 30, 450);
        }
        if (Vision.worldState.getShootingDirection() == 1){
            imageGraphics.drawString("Shooting: Right", 30, 465);
        }else{
            imageGraphics.drawString("Shooting: Left", 30, 465);
        }
        
        imageGraphics.drawString("Our Position: (" + Vision.worldState.getOurRobot().getPosition().getCentre().x + "," + Vision.worldState.getOurRobot().getPosition().getCentre().y + ")", 30, 20);
        imageGraphics.drawString("Our Velocity: " + String.format("%.4g%n", Vision.worldState.getOurRobot().getVelocity()) + "px/s", 30, 35);
        imageGraphics.drawString("Our Bearing: " + String.format("%.4g%n", Vision.worldState.getOurRobot().getAngle()) + "rads", 30, 50);
        imageGraphics.drawString("Dist to Ball: " + String.format("%.4g%n",Point.distance(Vision.worldState.getOurRobot().getPosition().getCentre().x, Vision.worldState.getOurRobot().getPosition().getCentre().y, Vision.worldState.getBall().getPosition().getCentre().x, Vision.worldState.getBall().getPosition().getCentre().y)) + "px", 30, 65);
        imageGraphics.drawString("Dist to Opp: " + String.format("%.4g%n",Point.distance(Vision.worldState.getOurRobot().getPosition().getCentre().x, Vision.worldState.getOurRobot().getPosition().getCentre().y, Vision.worldState.getOpponentsRobot().getPosition().getCentre().x, Vision.worldState.getOpponentsRobot().getPosition().getCentre().y)) + "px", 30, 80);
        
        imageGraphics.drawString("Opp Position: (" + Vision.worldState.getOpponentsRobot().getPosition().getCentre().x + "," + Vision.worldState.getOpponentsRobot().getPosition().getCentre().y + ")", 220, 20);
        imageGraphics.drawString("Opp Velocity: " + String.format("%.4g%n", Vision.worldState.getOpponentsRobot().getVelocity()) + "px/s", 220, 35);
        imageGraphics.drawString("Opp Bearing: " + String.format("%.4g%n", Vision.worldState.getOpponentsRobot().getAngle()) + "rads", 220, 50);
        imageGraphics.drawString("Dist to Ball: " + String.format("%.4g%n",Point.distance(Vision.worldState.getOpponentsRobot().getPosition().getCentre().x, Vision.worldState.getOpponentsRobot().getPosition().getCentre().y, Vision.worldState.getBall().getPosition().getCentre().x, Vision.worldState.getBall().getPosition().getCentre().y)) + "px", 220, 65);
        imageGraphics.drawString("Dist to Us: " + String.format("%.4g%n",Point.distance(Vision.worldState.getOurRobot().getPosition().getCentre().x, Vision.worldState.getOurRobot().getPosition().getCentre().y, Vision.worldState.getOpponentsRobot().getPosition().getCentre().x, Vision.worldState.getOpponentsRobot().getPosition().getCentre().y)) + "px", 220, 80);
        
        imageGraphics.drawString("Ball Position: (" + Vision.worldState.getBall().getPosition().getCentre().x + "," + Vision.worldState.getBall().getPosition().getCentre().y + ")", 410, 20);
        imageGraphics.drawString("Ball Velocity: " + String.format("%.4g%n", Vision.worldState.getBall().getVelocity()) + "px/s", 410, 35);
        
        
        frameGraphics.drawImage(image, 0, 0, width, height, null);
    }
    
    public BufferedImage removeBackground(BufferedImage image, BufferedImage background){
    	int black = -16777216;
    	int pink = -60269;
    	if(Vision.worldState.isClickingDone()){
    		for(int x = Vision.worldState.getBlueRobot().getPosition().getCentre().x - 40;
    		x < Vision.worldState.getBlueRobot().getPosition().getCentre().x + 40;x++)
    		{
    			for(int y = Vision.worldState.getBlueRobot().getPosition().getCentre().y - 40;
    			y < Vision.worldState.getBlueRobot().getPosition().getCentre().y + 40; y++)
    			{
    				try{
    					Color imageRGB = new Color(image.getRGB(x,y));
    					Color backgroundRGB = new Color(background.getRGB(x,y));
    					if(similarColor(imageRGB,backgroundRGB)){
    						image.setRGB(x,y,pink); //pure black
    					}
    				} catch (Exception e) {
    					//This is probably just an index out of bounds exception so we can ignore it for now.
    				}
    			}
    		}
    		for(int x = Vision.worldState.getYellowRobot().getPosition().getCentre().x - 40;
    		x < Vision.worldState.getYellowRobot().getPosition().getCentre().x + 40;x++)
    		{
    			for(int y = Vision.worldState.getYellowRobot().getPosition().getCentre().y - 40;
    			y < Vision.worldState.getYellowRobot().getPosition().getCentre().y + 40; y++)
    			{
    				try{
    					Color imageRGB = new Color(image.getRGB(x,y));
    					Color backgroundRGB = new Color(background.getRGB(x,y));
    					if(similarColor(imageRGB,backgroundRGB)){
    						image.setRGB(x,y,pink); //pure black
    					}
    				} catch (Exception e) {
    					//Again, it is just an out of bounds exception
    				}
    			}
    		}
    	}
    	return image;
    }
    
    

    public static boolean similarColor(Color a, Color b){
    	int rDiff = 33;
    	int gDiff = 33;
    	int bDiff = 33;
    	if(
    			(Math.abs(a.getRed() - b.getRed()) < rDiff) &&
    			(Math.abs(a.getBlue() - b.getBlue()) < gDiff) &&
    			(Math.abs(a.getGreen() - b.getGreen()) < bDiff)
    	) return true;
    	return false;
    }



}
