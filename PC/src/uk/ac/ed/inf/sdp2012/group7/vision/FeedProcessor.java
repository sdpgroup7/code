package uk.ac.ed.inf.sdp2012.group7.vision;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;

import uk.ac.ed.inf.sdp2012.group7.vision.ui.ControlGUI;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

public class FeedProcessor{
    
    private WorldState worldState;
    //private ThresholdsState thresholdsState; //might not be needed any more
    //private OrientationFinder orientationFinder; //might not be needed anymore

    private InitialLocation initialLocation;
    private Thresholding doThresh = new Thresholding(0); // Do Thresholding 
    
    private int height;
    private int width;

    public FeedProcessor(InitialLocation il, int height, int width, ControlGUI controlGUI){
        
    	//this.thresholdsState = controlGUI.getThresholdsState();
        this.worldState = controlGUI.getWorldState();
        this.initialLocation = il;
        this.height = height;
        this.width = width;
        //this.orientationFinder = new OrientationFinder(this.thresholdsState);
        Vision.logger.info("Feed Processor Initialised");
    }

    public void processAndUpdateImage(BufferedImage image, long before, JLabel label) {
    	
        image = initialLocation.markImage(image);
        
        //int topBuffer = Vision.worldState.getPitch().getTopBuffer();
        //int bottomBuffer = Vision.worldState.getPitch().getBottomBuffer();
        //int leftBuffer = Vision.worldState.getPitch().getLeftBuffer();
        //int rightBuffer = Vision.worldState.getPitch().getRightBuffer();

        /* For every pixel within the pitch, test to see if it belongs to the ball,
         * the yellow T, the blue T, either green plate or a grey circle. */
        //Color c;
        /* Position objects to hold the centre point of the ball and both robots. */
        

        /* If we have only found a few 'Ball' pixels, chances are that the ball has not
         * actually been detected. */
        /* Attempt to find the blue robot's orientation. */
        /*
        try {
            float blueOrientation = orientationFinder.findOrientation(blueXPoints, blueYPoints, blue.getX(), blue.getY(), image, true);
            worldState.setBlueOrientation(blueOrientation);
        } catch (NoAngleException e) {

        }
        */

        /* Attempt to find the yellow robot's orientation. */
        /*
        try {
            float yellowOrientation = orientationFinder.findOrientation(yellowXPoints, yellowYPoints, yellow.getX(), yellow.getY(), image, true);
            worldState.setYellowOrientation(yellowOrientation);
        } catch (NoAngleException e) {

        }
        */
                /* Draw the image onto the vision frame. As well as the threshed image*/
        Graphics frameGraphics = label.getGraphics();
        
       // Graphics frameGraphicsThresh = labelThresh.getGraphics();
       // Graphics imageGraphics = image.getGraphics();
        Graphics imageGraphics = doThresh.getThresh(image, Vision.worldState.getPitch().getLeftBuffer(),Vision.worldState.getPitch().getRightBuffer(), Vision.worldState.getPitch().getTopBuffer(),Vision.worldState.getPitch().getBottomBuffer()).getGraphics();
        
        Point ballCent = doThresh.getBallCentroid();
        Point blueCent = doThresh.getBlueCentroid();
        Point yellowCent = doThresh.getYellowCentroid();
        //Point blueGreenPlate = doThresh.getBlueGreenPlateCentori();
        
        worldState.setBallPosition(ballCent);
        if(true){ //TODO: make this check if we are blue
        	worldState.setOurRobotPosition(blueCent);
        	worldState.setOpponentsRobotPosition(yellowCent);
        } /*else {
        	worldState.setOurRobotPosition(yellowCent);
        	worldState.setOpponentsRobotPosition(blueCent);
        }*/     
        markObjects(imageGraphics,ballCent,blueCent,yellowCent);
        calculateFPS(before,imageGraphics,frameGraphics, image, this.width, this.height);
    }

    public void markObjects(Graphics imageGraphics, Point ball, Point blue, Point yellow){
            imageGraphics.setColor(Color.red);
            imageGraphics.drawLine(0, (int)ball.getY(), 640, (int)ball.getY());
            imageGraphics.drawLine((int)ball.getX(), 0, (int)ball.getX(), 480);
            imageGraphics.setColor(Color.blue);
            imageGraphics.drawOval((int)blue.getX()-15, (int)blue.getY()-15, 30,30);
            imageGraphics.setColor(Color.yellow);
            imageGraphics.drawOval((int)yellow.getX()-15, (int)yellow.getY()-15, 30,30);
            imageGraphics.setColor(Color.white);
            imageGraphics.setColor(Color.red);
            imageGraphics.drawLine(worldState.getBall().getPosition().getCentre().x,worldState.getBall().getPosition().getCentre().y,worldState.getOurRobot().getPosition().getCentre().x,worldState.getOurRobot().getPosition().getCentre().y);
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
