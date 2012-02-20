package uk.ac.ed.inf.sdp2012.group7.vision;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import uk.ac.ed.inf.sdp2012.group7.vision.ui.ControlGUI;
import uk.ac.ed.inf.sdp2012.group7.vision.Thresholding;
import uk.ac.ed.inf.sdp2012.group7.vision.VisionFeed;



public class FeedProcessor{
    
    //private ThresholdsState thresholdsState; //might not be needed any more
    //private OrientationFinder orientationFinder; //might not be needed anymore

    private InitialLocation initialLocation;
    private Thresholding doThresh; // Do Thresholding 
    private VisionFeed visionFeed;
    private OrientationFinder findAngle; // finds the angle
    
    private int height;
    private int width;

    
    double prevAngle = 0;

    public FeedProcessor(InitialLocation il, int height, int width, ControlGUI controlGUI, VisionFeed visionFeed){
        
    	//this.thresholdsState = controlGUI.getThresholdsState();
        this.initialLocation = il;
        this.height = height;
        this.width = width;
        this.visionFeed = visionFeed;
        this.doThresh = new Thresholding(controlGUI.getThresholdsState());
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
    		image = initialLocation.markImage(image);
            Graphics frameGraphics = label.getGraphics();
            Graphics imageGraphics = doThresh.getThresh(
							                image,
							                Vision.worldState.getPitch().getLeftBuffer(),
							                Vision.worldState.getPitch().getRightBuffer(), 
							                Vision.worldState.getPitch().getTopBuffer(),
							                Vision.worldState.getPitch().getBottomBuffer()
            							).getGraphics();
            markObjects(imageGraphics);
            calculateFPS(before,imageGraphics,frameGraphics, image, this.width, this.height);
            calculateAngle();
            //System.err.println(Vision.worldState.getOurRobot().getAngle());
        }

    }
    public void calculateAngle(){
    	double blueAngle = findAngle.findOrientation(
    		Vision.worldState.getBlueKeyPoint().x, 
    	    Vision.worldState.getBlueKeyPoint().y,
    	    Vision.worldState.getBlueRobot().getPosition().getCentre().x,
    	    Vision.worldState.getBlueRobot().getPosition().getCentre().y
    	   
    	);
    	double yellowAngle = findAngle.findOrientation(
    	    Vision.worldState.getYellowRobot().getPosition().getCentre().x,
    	    Vision.worldState.getYellowRobot().getPosition().getCentre().y, 
    	    Vision.worldState.getYellowGrey().getPosition().getCentre().x, 
    	    Vision.worldState.getYellowGrey().getPosition().getCentre().y
    	);
    	Vision.worldState.getBlueRobot().setAngle(blueAngle);
    	Vision.worldState.getYellowRobot().setAngle(yellowAngle);
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
            //imageGraphics.drawLine(Vision.worldState.getOurGrey().getPosition().getCentre().x,Vision.worldState.getOurGrey().getPosition().getCentre().y,Vision.worldState.getOurRobot().getPosition().getCentre().x,Vision.worldState.getOurRobot().getPosition().getCentre().y);
            //imageGraphics.drawLine(Vision.worldState.getOpponentsGrey().getPosition().getCentre().x,Vision.worldState.getOpponentsGrey().getPosition().getCentre().y,Vision.worldState.getOpponentsRobot().getPosition().getCentre().x,Vision.worldState.getOpponentsRobot().getPosition().getCentre().y);
            //imageGraphics.drawLine(Vision.worldState.getOurRobot().getPosition().getCentre().x,Vision.worldState.getOurRobot().getPosition().getCentre().y, Vision.worldState.getOurKeyPoint().x,Vision.worldState.getOurKeyPoint().y);
            //could the above line be shorter with the current worldState state?
            Point p = findAngle.findOrientation(Vision.worldState.getBluePixels(),Vision.worldState.getBlueRobot().getPosition().getCentre());
            Vision.worldState.getBlueRobot().addAngle(p);
            p = Vision.worldState.getBlueRobot().getAngle();
            imageGraphics.drawLine(
            		Vision.worldState.getBlueRobot().getPosition().getCentre().x,
            		Vision.worldState.getBlueRobot().getPosition().getCentre().y,
            		p.x,
            		p.y);
            imageGraphics.setColor(Color.red);
            p = findAngle.findOrientation(Vision.worldState.getYellowPixels(), Vision.worldState.getYellowRobot().getPosition().getCentre());
            Vision.worldState.getYellowRobot().addAngle(p);
            p = Vision.worldState.getYellowRobot().getAngle();
            imageGraphics.drawLine(
            		Vision.worldState.getYellowRobot().getPosition().getCentre().x,
            		Vision.worldState.getYellowRobot().getPosition().getCentre().y,
            		p.x,
            		p.y);
            
    
    }

    public static void calculateFPS(long before, Graphics imageGraphics, Graphics frameGraphics, BufferedImage image, int width, int height){
        /* Used to calculate the FPS. */
        long after = System.currentTimeMillis();

        /* Display the FPS that the vision system is running at. */
        float fps = (1.0f)/((after - before) / 1000.0f);
        imageGraphics.setColor(Color.white);
        imageGraphics.drawString("FPS: " + fps, 15, 15);
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
    
    public void writeImage(BufferedImage image, String fn){
        try {
            File outputFile = new File(fn);
            ImageIO.write(image, "png", outputFile);
        } catch (Exception e) {
        	Vision.logger.error("Failed to write image: " + e.getMessage());
        }
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
