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
    private ThresholdsState thresholdsState;
    private PitchConstants pitchConstants;
    private OrientationFinder orientationFinder;

    private ColorDetection colorDetection;
    private InitialLocation initialLocation;
    private Thresholding doThresh = new Thresholding(0); // Do Thresholding 
    
    private int height;
    private int width;

    public FeedProcessor(InitialLocation il, int height, int width, PitchConstants pitchConstants, ControlGUI controlGUI){
        this.thresholdsState = controlGUI.getThresholdsState();
        this.worldState = controlGUI.getWorldState();
        this.initialLocation = il;
        this.height = height;
        this.width = width;
        this.pitchConstants = pitchConstants;
        this.colorDetection = new ColorDetection(thresholdsState);
        this.orientationFinder = new OrientationFinder(this.thresholdsState);
	
    }

    public void processAndUpdateImage(BufferedImage image, long before, JLabel label) {

        image = initialLocation.markImage(image);

        int topBuffer = pitchConstants.getTopBuffer();
        int bottomBuffer = pitchConstants.getBottomBuffer();
        int leftBuffer = pitchConstants.getLeftBuffer();
        int rightBuffer = pitchConstants.getRightBuffer();

        /* For every pixel within the pitch, test to see if it belongs to the ball,
         * the yellow T, the blue T, either green plate or a grey circle. */
        Color c;
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
        Graphics imageGraphics = doThresh.getThresh(image, pitchConstants.getLeftBuffer(),pitchConstants.getRightBuffer(), pitchConstants.getTopBuffer(),pitchConstants.getBottomBuffer()).getGraphics();
        
        Point ballCent = doThresh.getBallCentroid();
        Point blueCent = doThresh.getBlueCentroid();
        Point yellowCent = doThresh.getYellowCentroid();

        worldState.setBallX((int)ballCent.getX());
        worldState.setBallY((int)ballCent.getY());
        
        /*
        worldState.setBlueX(blue.getX());
        worldState.setBlueY(blue.getY());
        worldState.setYellowX(yellow.getX());
        worldState.setYellowY(yellow.getY());
        worldState.updateCounter();
        */
       


        
        
        markObjects(imageGraphics,ballCent,blueCent,yellowCent);

        calculateFPS(before,imageGraphics,frameGraphics, image, this.width, this.height);
    }

    public void markObjects(Graphics imageGraphics, Point ball, Point blue, Point yellow){
        /* Only display these markers in non-debug mode. */
            imageGraphics.setColor(Color.red);
            imageGraphics.drawLine(0, (int)ball.getY(), 640, (int)ball.getY());
            imageGraphics.drawLine((int)ball.getX(), 0, (int)ball.getX(), 480);
            imageGraphics.setColor(Color.blue);
            imageGraphics.drawOval((int)blue.getX()-15, (int)blue.getY()-15, 30,30);
            imageGraphics.setColor(Color.yellow);
            imageGraphics.drawOval((int)yellow.getX()-15, (int)yellow.getY()-15, 30,30);
            imageGraphics.setColor(Color.white);
        
    }

    public static void calculateFPS(long before, Graphics imageGraphics, Graphics frameGraphics, BufferedImage image, int width, int height){
        /* Used to calculate the FPS. */
        long after = System.currentTimeMillis();

        /* Display the FPS that the vision system is running at. */
        float fps = (1.0f)/((after - before) / 1000.0f);
        imageGraphics.setColor(Color.white);
        imageGraphics.drawString("FPS: " + fps, 15, 15);
        frameGraphics.drawImage(image, 0, 0, width, height, null);
        //TODO: Check that the above isn't needed.
    }

}
