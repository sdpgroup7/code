package uk.ac.ed.inf.sdp2012.group7.vision;
import uk.ac.ed.inf.sdp2012.group7.vision.ui.*;
import java.util.ArrayList;
import java.awt.Color;

public class FeedProcessor{
    
    private WorldState worldState = new WorldState();
    private ThesholdsState thesholdsState = new ThresholdsState();

    Position ball;
    Position blue;
    Position yellow;

    public FeedProcessor(){
        
    }

    public void processAndUpdateImage(BufferedImage image, long before) {

        image = markImage(image);

        int ballX = 0;
        int ballY = 0;
        int numBallPixels = 0;

        int blueX = 0;
        int blueY = 0;
        int numBluePixels = 0;

        int yellowX = 0;
        int yellowY = 0;
        int numYellowPixels = 0;

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
                if (thresholdsState.isGrey_debug() && isGrey(c)) {
                    image.setRGB(column, row, 0xFFFF0099);
                }

                if (thresholdsState.isGreen_debug() && isGreen(c)) {
                    image.setRGB(column, row, 0xFFFF0099);
                }

                /* Is this pixel part of the Blue T? */
                if (isBlue(c) ){

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
                if (isYellow(c)) {

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
                if (isBall(c)) {

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
            float blueOrientation = findOrientation(blueXPoints, blueYPoints, blue.getX(), blue.getY(), image, true);
            /*float diff = Math.abs(blueOrientation - worldState.getBlueOrientation());
            if (diff > 0.1) {
                float angle = (float) Math.round(((blueOrientation / Math.PI) * 180) / 5) * 5;
                //TODO: Have a look at this. Divide by 5 then times by 5? It seems to only be in 5 degree increments.
                worldState.setBlueOrientation((float) (angle / 180 * Math.PI));
                //TODO: WTF!?!
            }*/
            worldState.setBlueOrientation(blueOrientation);
        } catch (NoAngleException e) {
            //worldState.setBlueOrientation(worldState.getBlueOrientation());
        }


        /* Attempt to find the yellow robot's orientation. */
        try {
            float yellowOrientation = findOrientation(yellowXPoints, yellowYPoints, yellow.getX(), yellow.getY(), image, true);
            /*float diff = Math.abs(yellowOrientation - worldState.getYellowOrientation());
            if (yellowOrientation != 0 && diff > 0.1) {
                float angle = (float) Math.round(((yellowOrientation / Math.PI) * 180) / 5) * 5;
                worldState.setYellowOrientation((float) (angle / 180 * Math.PI));
            }*/
            worldState.setYellowOrientation(yellowOrientation);
        } catch (NoAngleException e) {
            //worldState.setYellowOrientation(worldState.getYellowOrientation());
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

        
        markObjects(imageGraphics);

        calculateFPS(before,imageGraphics,frameGraphics);
    }

    public static void markObjects(Graphics imageGraphics){
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

    public static void calculateFPS(long before, Graphics imageGraphics, Graphics frameGraphics){
        /* Used to calculate the FPS. */
        long after = System.currentTimeMillis();

        /* Display the FPS that the vision system is running at. */
        float fps = (1.0f)/((after - before) / 1000.0f);
        imageGraphics.setColor(Color.white);
        imageGraphics.drawString("FPS: " + fps, 15, 15);
        frameGraphics.drawImage(image, 0, 0, width, height, null);
    }

    public BufferedImage getThresh(BufferedImage img, int redL, int redH, int greenL, int greenH, int blueL, int blueH) { // Method to get thresholded image 

    	BufferedImage threshed = new BufferedImage(width,height, 0);
    	Color c;
    	
    	for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
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
