package uk.ac.ed.inf.sdp2012.group7.vision;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

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
    
    private int height;
    private int width;

    public FeedProcessor(InitialLocation il, int height, int width, ControlGUI controlGUI, VisionFeed visionFeed){
        
    	//this.thresholdsState = controlGUI.getThresholdsState();
        this.initialLocation = il;
        this.height = height;
        this.width = width;
        this.visionFeed = visionFeed;
        this.doThresh = new Thresholding(controlGUI.getThresholdsState());
        //this.orientationFinder = new OrientationFinder(this.thresholdsState);
        Vision.logger.info("Feed Processor Initialised");
    }

    public void processAndUpdateImage(BufferedImage image, long before, JLabel label) {
    	if(Vision.TESTING && visionFeed.paused){
    		Graphics frameGraphics = label.getGraphics();
            Graphics imageGraphics = image.getGraphics();
            calculateFPS(before,imageGraphics,frameGraphics, image, this.width, this.height);
    	} else {
    		image = initialLocation.markImage(image);
            Graphics frameGraphics = label.getGraphics();
            Graphics imageGraphics = doThresh.getThresh(image, Vision.worldState.getPitch().getLeftBuffer(),Vision.worldState.getPitch().getRightBuffer(), Vision.worldState.getPitch().getTopBuffer(),Vision.worldState.getPitch().getBottomBuffer()).getGraphics(); 
            markObjects(imageGraphics);
            calculateFPS(before,imageGraphics,frameGraphics, image, this.width, this.height);
        }
    }

    public void markObjects(Graphics imageGraphics){
            Point ball = Vision.worldState.getBall().getPosition().getCentre();
            Point blue;
            Point yellow;
            
            if (Vision.worldState.getColor() == Color.blue){
                blue = Vision.worldState.getOurRobot().getPosition().getCentre();
                yellow = Vision.worldState.getOpponentsRobot().getPosition().getCentre();
            } else {
                yellow = Vision.worldState.getOurRobot().getPosition().getCentre();
                blue = Vision.worldState.getOpponentsRobot().getPosition().getCentre();
            }
            imageGraphics.setColor(Color.red);
            imageGraphics.drawLine(0, ball.y, 640, ball.y);
            imageGraphics.drawLine(ball.x, 0, ball.x, 480);
            imageGraphics.setColor(Color.blue);
            imageGraphics.drawOval(blue.x-15, blue.y-15, 30,30);
            imageGraphics.setColor(Color.yellow);
            imageGraphics.drawOval(yellow.x-15, yellow.y-15, 30,30);
            imageGraphics.setColor(Color.white);
            imageGraphics.setColor(Color.red);
            imageGraphics.drawLine(Vision.worldState.getBall().getPosition().getCentre().x,Vision.worldState.getBall().getPosition().getCentre().y,Vision.worldState.getOurRobot().getPosition().getCentre().x,Vision.worldState.getOurRobot().getPosition().getCentre().y);
            //could the above line be shorter with the current worldState state?
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

}
