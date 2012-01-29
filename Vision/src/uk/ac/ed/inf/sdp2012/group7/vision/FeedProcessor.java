package uk.ac.ed.inf.sdp2012.group7.vision;
import uk.ac.ed.inf.sdp2012.group7.vision.ui.*;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;

public class FeedProcessor{
    
    private WorldState worldState = new WorldState();
    private ThresholdsState thresholdsState;
    private PitchConstants pitchConstants;
    private OrientationFinder orientationFinder;

    private ColorDetection colorDetection;
    private InitialLocation initialLocation;
    
    private int height;
    private int width;

    public FeedProcessor(InitialLocation il, int height, int width, PitchConstants pitchConstants, ControlGUI controlGUI){
        this.thresholdsState = controlGUI.getThresholdsState();
        this.initialLocation = il;
        this.height = height;
        this.width = width;
        this.pitchConstants = pitchConstants;
        this.colorDetection = new ColorDetection(thresholdsState);
        this.orientationFinder = new OrientationFinder(this.thresholdsState);
    }

    public void processAndUpdateImage(BufferedImage image, long before, JLabel label) {

        image = initialLocation.markImage(image);

        int ballX = 0;
        int ballY = 0;
        int numBallPixels = 0;

        int blueX = 0;
        int blueY = 0;
        int numBluePixels = 0;

        int yellowX = 0;
        int yellowY = 0;
        int numYellowPixels = 0;

        Position ball;
        Position blue;
        Position yellow;

        ArrayList<Integer> ballXPoints = new ArrayList<Integer>();
        ArrayList<Integer> ballYPoints = new ArrayList<Integer>();
        ArrayList<Integer> blueXPoints = new ArrayList<Integer>();
        ArrayList<Integer> blueYPoints = new ArrayList<Integer>();
        ArrayList<Integer> yellowXPoints = new ArrayList<Integer>();
        ArrayList<Integer> yellowYPoints = new ArrayList<Integer>();

        int topBuffer = pitchConstants.getTopBuffer();
        int bottomBuffer = pitchConstants.getBottomBuffer();
        int leftBuffer = pitchConstants.getLeftBuffer();
        int rightBuffer = pitchConstants.getRightBuffer();

        /* For every pixel within the pitch, test to see if it belongs to the ball,
         * the yellow T, the blue T, either green plate or a grey circle. */
        Color c;
        for (int row = topBuffer; row < bottomBuffer; row++) {

            for (int column = leftBuffer; column < rightBuffer; column++) {
                //System.err.println("column,row = " + column + "," + row);
                /* The RGB colors and hsv values for the current pixel. */
                c = new Color(image.getRGB(column, row));

                /* Debug graphics for the grey circles and green plates.
                 * TODO: Move these into the actual detection. */
                if (thresholdsState.isGrey_debug() && colorDetection.isGrey(c)) {
                    image.setRGB(column, row, 0xFFFF0099);
                }

                if (thresholdsState.isGreen_debug() && colorDetection.isGreen(c)) {
                    image.setRGB(column, row, 0xFFFF0099);
                }

                /* Is this pixel part of the Blue T? */
                if (colorDetection.isBlue(c) ){
                    blueX += column;
                    blueY += row;
                    numBluePixels++;

                    blueXPoints.add(column);
                    blueYPoints.add(row);

                    /* If we're in the "Blue" tab, we show what pixels we're looking at,
                     * for debugging and to help with threshold setting. */
                    if (thresholdsState.isBlue_debug()) {
                        image.setRGB(column, row, 0xFFFF0099);
                    }

                }

                /* Is this pixel part of the Yellow T? */
                if (colorDetection.isYellow(c)) {

                    yellowX += column;
                    yellowY += row;
                    numYellowPixels++;

                    yellowXPoints.add(column);
                    yellowYPoints.add(row);

                    /* If we're in the "Yellow" tab, we show what pixels we're looking at,
                     * for debugging and to help with threshold setting. */
                    if (thresholdsState.isYellow_debug()) {
                        image.setRGB(column, row, 0xFFFF0099);
                    }
                }

                /* Is this pixel part of the Ball? */
                if (colorDetection.isBall(c)) {

                    ballX += column;
                    ballY += row;
                    numBallPixels++;

                    ballXPoints.add(column);
                    ballYPoints.add(row);

                    /* If we're in the "Ball" tab, we show what pixels we're looking at,
                     * for debugging and to help with threshold setting. */
                    if (thresholdsState.isBall_debug()) {
                        image.setRGB(column, row, 0xFF000000);
                    }
                }
            }
        }

        /* Position objects to hold the centre point of the ball and both robots. */


        /* If we have only found a few 'Ball' pixels, chances are that the ball has not
         * actually been detected. */
        if (numBallPixels > 5) {
            ballX = ballX / numBallPixels;
            ballY = ballY /numBallPixels;

            ball = new Position(ballX, ballY);
            ball.fixValues(worldState.getBallX(), worldState.getBallY());
            ball.filterPoints(ballXPoints, ballYPoints);
        } else {
            ball = new Position(worldState.getBallX(), worldState.getBallY());
        }

        /* If we have only found a few 'Blue' pixels, chances are that the blue bot has not
         * actually been detected. */
        if (numBluePixels > 0) {
            blueX = blueX / numBluePixels;
            blueY = blueY / numBluePixels;

            blue = new Position(blueX, blueY);
            blue.fixValues(worldState.getBlueX(), worldState.getBlueY());
            blue.filterPoints(blueXPoints, blueYPoints);
        } else {
            blue = new Position(worldState.getBlueX(), worldState.getBlueY());
        }

        /* If we have only found a few 'Yellow' pixels, chances are that the yellow bot has not
         * actually been detected. */
        if (numYellowPixels > 0) {
            yellowX = yellowX / numYellowPixels;
            yellowY = yellowX / numYellowPixels;

            yellow = new Position(yellowX, yellowY);
            yellow.fixValues(worldState.getYellowX(), worldState.getYellowY());
            yellow.filterPoints(yellowXPoints, yellowYPoints);
        } else {
            yellow = new Position(worldState.getYellowX(), worldState.getYellowY());
        }



        /* Attempt to find the blue robot's orientation. */
        try {
            float blueOrientation = orientationFinder.findOrientation(blueXPoints, blueYPoints, blue.getX(), blue.getY(), image, true);
            worldState.setBlueOrientation(blueOrientation);
        } catch (NoAngleException e) {

        }


        /* Attempt to find the yellow robot's orientation. */
        try {
            float yellowOrientation = orientationFinder.findOrientation(yellowXPoints, yellowYPoints, yellow.getX(), yellow.getY(), image, true);
            worldState.setYellowOrientation(yellowOrientation);
        } catch (NoAngleException e) {

        }


        worldState.setBallX(ball.getX());
        worldState.setBallY(ball.getY());

        worldState.setBlueX(blue.getX());
        worldState.setBlueY(blue.getY());
        worldState.setYellowX(yellow.getX());
        worldState.setYellowY(yellow.getY());
        worldState.updateCounter();

        /* Draw the image onto the vision frame. */
        Graphics frameGraphics = label.getGraphics();
        Graphics imageGraphics = image.getGraphics();
        
        markObjects(imageGraphics,ball,blue,yellow);

        calculateFPS(before,imageGraphics,frameGraphics, image, this.width, this.height);
    }

    public void markObjects(Graphics imageGraphics, Position ball, Position blue, Position yellow){
        /* Only display these markers in non-debug mode. */
        if (!(thresholdsState.isBall_debug() || thresholdsState.isBlue_debug()
                || thresholdsState.isYellow_debug() || thresholdsState.isGreen_debug()
                || thresholdsState.isGrey_debug())) {
            imageGraphics.setColor(Color.red);
            imageGraphics.drawLine(0, ball.getY(), 640, ball.getY());
            imageGraphics.drawLine(ball.getX(), 0, ball.getX(), 480);
            imageGraphics.setColor(Color.blue);
            imageGraphics.drawOval(blue.getX()-15, blue.getY()-15, 30,30);
            imageGraphics.setColor(Color.yellow);
            imageGraphics.drawOval(yellow.getX()-15, yellow.getY()-15, 30,30);
            imageGraphics.setColor(Color.white);
        }
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

    public BufferedImage getThresh(BufferedImage img, int redL, int redH, int greenL, int greenH, int blueL, int blueH) { // Method to get thresholded image 

    	BufferedImage threshed = new BufferedImage(this.width,this.height, 0);
    	Color c;
    	
    	for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				c = new Color(img.getRGB(i,j));
				if( (c.getRed()>redL) && (c.getRed() <= redH) && (c.getBlue()>blueL) && (c.getBlue() <=blueH) && (c.getGreen()>greenL) && (c.getGreen() <= greenH)){
					threshed.setRGB(i, j, Color.black.getRGB());
				}
				else{
					threshed.setRGB(i, j, Color.white.getRGB());
				}
			}
		}
    	return threshed;
    }


}
