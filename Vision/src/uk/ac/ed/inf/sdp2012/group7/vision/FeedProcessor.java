package uk.ac.ed.inf.sdp2012.group7.vision;
import uk.ac.ed.inf.sdp2012.group7.vision.ui.*;
import java.util.ArrayList;
import java.awt.Color;

public class FeedProcessor{

    private final Color black= new Color(0,0,0);
    private final Color white = new Color(255,255,255);
    
    private WorldState worldState = new WorldState();
    private ThesholdsState thesholdsState = new ThresholdsState();

    public FeedProcessor(){

    }

    public void processAndUpdateImage(BufferedImage image, long before) {

        image = markImage(image);

        int ballX = 0;
        int ballY = 0;
        int numBallPos = 0;

        int blueX = 0;
        int blueY = 0;
        int numBluePos = 0;

        int yellowX = 0;
        int yellowY = 0;
        int numYellowPos = 0;

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
        for (int row = topBuffer; row < bottomBuffer; row++) {

            for (int column = leftBuffer; column < rightBuffer; column++) {
                //System.err.println("column,row = " + column + "," + row);
                /* The RGB colors and hsv values for the current pixel. */
                Color c = new Color(image.getRGB(column, row));

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
                    numBluePos++;

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
                    numYellowPos++;

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
                    numBallPos++;

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
        Position ball;
        Position blue;
        Position yellow;

        /* If we have only found a few 'Ball' pixels, chances are that the ball has not
         * actually been detected. */
        if (numBallPos > 5) {
            ballX /= numBallPos;
            ballY /= numBallPos;

            ball = new Position(ballX, ballY);
            ball.fixValues(worldState.getBallX(), worldState.getBallY());
            ball.filterPoints(ballXPoints, ballYPoints);
        } else {
            ball = new Position(worldState.getBallX(), worldState.getBallY());
        }

        /* If we have only found a few 'Blue' pixels, chances are that the blue bot has not
         * actually been detected. */
        if (numBluePos > 0) {
            blueX /= numBluePos;
            blueY /= numBluePos;

            blue = new Position(blueX, blueY);
            blue.fixValues(worldState.getBlueX(), worldState.getBlueY());
            blue.filterPoints(blueXPoints, blueYPoints);
        } else {
            blue = new Position(worldState.getBlueX(), worldState.getBlueY());
        }

        /* If we have only found a few 'Yellow' pixels, chances are that the yellow bot has not
         * actually been detected. */
        if (numYellowPos > 0) {
            yellowX /= numYellowPos;
            yellowY /= numYellowPos;

            yellow = new Position(yellowX, yellowY);
            yellow.fixValues(worldState.getYellowX(), worldState.getYellowY());
            yellow.filterPoints(yellowXPoints, yellowYPoints);
        } else {
            yellow = new Position(worldState.getYellowX(), worldState.getYellowY());
        }



        /* Attempt to find the blue robot's orientation. */
        try {
            float blueOrientation = findOrientation(blueXPoints, blueYPoints, blue.getX(), blue.getY(), image, true);
            float diff = Math.abs(blueOrientation - worldState.getBlueOrientation());
            if (diff > 0.1) {
                float angle = (float) Math.round(((blueOrientation / Math.PI) * 180) / 5) * 5;
                worldState.setBlueOrientation((float) (angle / 180 * Math.PI));
            }
        } catch (NoAngleException e) {
            worldState.setBlueOrientation(worldState.getBlueOrientation());
        }


        /* Attempt to find the yellow robot's orientation. */
        try {
            float yellowOrientation = findOrientation(yellowXPoints, yellowYPoints, yellow.getX(), yellow.getY(), image, true);
            float diff = Math.abs(yellowOrientation - worldState.getYellowOrientation());
            if (yellowOrientation != 0 && diff > 0.1) {
                float angle = (float) Math.round(((yellowOrientation / Math.PI) * 180) / 5) * 5;
                worldState.setYellowOrientation((float) (angle / 180 * Math.PI));
            }
        } catch (NoAngleException e) {
            worldState.setYellowOrientation(worldState.getYellowOrientation());
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

        /* Used to calculate the FPS. */
        long after = System.currentTimeMillis();

        /* Display the FPS that the vision system is running at. */
        float fps = (1.0f)/((after - before) / 1000.0f);
        imageGraphics.setColor(Color.white);
        imageGraphics.drawString("FPS: " + fps, 15, 15);
        frameGraphics.drawImage(image, 0, 0, width, height, null);
    }

    /**
     * Determines if a pixel is part of the blue T, based on input RGB colors
     * and hsv values.
     *
     * @param color         The RGB colors for the pixel.
     * @param hsbvals       The HSV values for the pixel.
     *
     * @return              True if the RGB and HSV values are within the defined
     *                      thresholds (and thus the pixel is part of the blue T),
     *                      false otherwise.
     */

     
    private boolean isBlue(Color color) {
        return color.getRed() <= thresholdsState.getBlue_r_high() && color.getRed() >= thresholdsState.getBlue_r_low() &&
        color.getGreen() <= thresholdsState.getBlue_g_high() && color.getGreen() >= thresholdsState.getBlue_g_low() &&
        color.getBlue() <= thresholdsState.getBlue_b_high() && color.getBlue() >= thresholdsState.getBlue_b_low();
    }

    /**
     * Determines if a pixel is part of the yellow T, based on input RGB colors
     * and hsv values.
     *
     * @param color         The RGB colors for the pixel.
     * @param hsbvals       The HSV values for the pixel.
     *
     * @return              True if the RGB and HSV values are within the defined
     *                      thresholds (and thus the pixel is part of the yellow T),
     *                      false otherwise.
     */

    private boolean isYellow(Color colour) {
        return colour.getRed() <= thresholdsState.getYellow_r_high() &&  colour.getRed() >= thresholdsState.getYellow_r_low() &&
        colour.getGreen() <= thresholdsState.getYellow_g_high() && colour.getGreen() >= thresholdsState.getYellow_g_low() &&
        colour.getBlue() <= thresholdsState.getYellow_b_high() && colour.getBlue() >= thresholdsState.getYellow_b_low();
    }

    /**
     * Determines if a pixel is part of the ball, based on input RGB colors
     * and hsv values.
     *
     * @param color         The RGB colors for the pixel.
     * @param hsbvals       The HSV values for the pixel.
     *
     * @return              True if the RGB and HSV values are within the defined
     *                      thresholds (and thus the pixel is part of the ball),
     *                      false otherwise.
     */

    private boolean isBall(Color colour) {
        return colour.getRed() <= thresholdsState.getBall_r_high() &&  colour.getRed() >= thresholdsState.getBall_r_low() &&
        colour.getGreen() <= thresholdsState.getBall_g_high() && colour.getGreen() >= thresholdsState.getBall_g_low() &&
        colour.getBlue() <= thresholdsState.getBall_b_high() && colour.getBlue() >= thresholdsState.getBall_b_low();
    }

    /**
     * Determines if a pixel is part of either grey circle, based on input RGB colors
     * and hsv values.
     *
     * @param color         The RGB colors for the pixel.
     * @param hsbvals       The HSV values for the pixel.
     *
     * @return              True if the RGB and HSV values are within the defined
     *                      thresholds (and thus the pixel is part of a grey circle),
     *                      false otherwise.
     */
    private boolean isGrey(Color colour) {
        return colour.getRed() <= thresholdsState.getGrey_r_high() &&  colour.getRed() >= thresholdsState.getGrey_r_low() &&
        colour.getGreen() <= thresholdsState.getGrey_g_high() && colour.getGreen() >= thresholdsState.getGrey_g_low() &&
        colour.getBlue() <= thresholdsState.getGrey_b_high() && colour.getBlue() >= thresholdsState.getGrey_b_low();

    }

    /**
     * Determines if a pixel is part of either green plate, based on input RGB colors
     * and hsv values.
     *
     * @param color         The RGB colors for the pixel.
     * @param hsbvals       The HSV values for the pixel.
     *
     * @return              True if the RGB and HSV values are within the defined
     *                      thresholds (and thus the pixel is part of a green plate),
     *                      false otherwise.
     */

    private boolean isGreen(Color colour) {
        return colour.getRed() <= thresholdsState.getGreen_r_high() &&  colour.getRed() >= thresholdsState.getGreen_r_low() &&
        colour.getGreen() <= thresholdsState.getGreen_g_high() && colour.getGreen() >= thresholdsState.getGreen_g_low() &&
        colour.getBlue() <= thresholdsState.getGreen_b_high() && colour.getBlue() >= thresholdsState.getGreen_b_low();

    }

    /**
     * Finds the orientation of a robot, given a list of the points contained within it's
     * T-shape (in terms of a list of x coordinates and y coordinates), the mean x and
     * y coordinates, and the image from which it was taken.
     *
     * @param xpoints           The x-coordinates of the points contained within the T-shape.
     * @param ypoints           The y-coordinates of the points contained within the T-shape.
     * @param meanX             The mean x-point of the T.
     * @param meanY             The mean y-point of the T.
     * @param image             The image from which the points were taken.
     * @param showImage         A boolean flag - if true a line will be drawn showing
     *                          the direction of orientation found.
     *
     * @return                  An orientation from -Pi to Pi degrees.
     * @throws NoAngleException
     */
    public float findOrientation(ArrayList<Integer> xpoints, ArrayList<Integer> ypoints,
            int meanX, int meanY, BufferedImage image, boolean showImage) throws NoAngleException {
        assert (xpoints.size() == ypoints.size()) :
            "Error: Must be equal number of x and y points!";

        if (xpoints.size() == 0) {
            throw new NoAngleException("No T pixels");
        }

        int stdev = 0;
        /* Standard deviation */
        for (int i = 0; i < xpoints.size(); i++) {
            int x = xpoints.get(i);
            int y = ypoints.get(i);

            stdev += Math.pow(Math.sqrt(Position.sqrdEuclidDist(x, y, meanX, meanY)), 2);
        }
        stdev  = (int) Math.sqrt(stdev / xpoints.size());

        /* Find the position of the front of the T. */
        int frontX = 0;
        int frontY = 0;
        int frontCount = 0;
        for (int i = 0; i < xpoints.size(); i++) {
            if (stdev > 15) {
                if (Math.abs(xpoints.get(i) - meanX) < stdev && Math.abs(ypoints.get(i) - meanY) < stdev &&
                        Position.sqrdEuclidDist(xpoints.get(i), ypoints.get(i), meanX, meanY) > Math.pow(15, 2)) {
                    frontCount++;
                    frontX += xpoints.get(i);
                    frontY += ypoints.get(i);
                }
            } else {
                if (Position.sqrdEuclidDist(xpoints.get(i), ypoints.get(i), meanX, meanY) > Math.pow(15, 2)) {
                    frontCount++;
                    frontX += xpoints.get(i);
                    frontY += ypoints.get(i);
                }
            }
        }

        /* If no points were found, we'd better bail. */
        if (frontCount == 0) {
            throw new NoAngleException("Front of T was not found");
        }

        /* Otherwise, get the frontX and Y. */
        frontX /= frontCount;
        frontY /= frontCount;

        /* In here, calculate the vector between meanX/frontX and
         * meanY/frontY, and then get the angle of that vector. */

        // Calculate the angle from center of the T to the front of the T
        float length = (float) Math.sqrt(Math.pow(frontX - meanX, 2)
                + Math.pow(frontY - meanY, 2));
        float ax = (frontX - meanX) / length;
        float ay = (frontY - meanY) / length;
        float angle = (float) Math.acos(ax);

        if (frontY < meanY) {
            angle = -angle;
        }

        //Look in a cone in the opposite direction to try to find the grey circle
        ArrayList<Integer> greyXPoints = new ArrayList<Integer>();
        ArrayList<Integer> greyYPoints = new ArrayList<Integer>();

        for (int a=-20; a < 21; a++) {
            ax = (float) Math.cos(angle+((a*Math.PI)/180));
            ay = (float) Math.sin(angle+((a*Math.PI)/180));
            for (int i = 15; i < 25; i++) {
                int greyX = meanX - (int) (ax * i);
                int greyY = meanY - (int) (ay * i);
                try {
                    Color c = new Color(image.getRGB(greyX, greyY));
                    if (isGrey(c)) {
                        greyXPoints.add(greyX);
                        greyYPoints.add(greyY);
                    }
                } catch (Exception e) {
                    //This happens if part of the search area goes outside the image
                    //This is okay, just ignore and continue
                }
            }
        }
        /* No grey circle found
         * The angle found is probably wrong, skip this value and return 0 */

        if (greyXPoints.size() < 30) {
            throw new NoAngleException("No grey circle found");
        }

        /* Calculate center of grey circle points */
        int totalX = 0;
        int totalY = 0;
        for (int i = 0; i < greyXPoints.size(); i++) {
            totalX += greyXPoints.get(i);
            totalY += greyYPoints.get(i);
        }

        /* Center of grey circle */
        float backX = totalX / greyXPoints.size();
        float backY = totalY / greyXPoints.size();

        /* Check that the circle is surrounded by the green plate
         * Currently checks above and below the circle */

        int foundGreen = 0;
        int greenSides = 0;
        /* Check if green points are above the grey circle */
        for (int x=(int) (backX-2); x < (int) (backX+3); x++) {
            for (int y = (int) (backY-9); y < backY; y++) {
                try {
                    Color c = new Color(image.getRGB(x, y));
                    if (isGreen(c)) {
                        foundGreen++;
                        break;
                    }
                } catch (Exception e) {
                    // Ignore.
                }
            }
        }

        if (foundGreen >= 3) {
            greenSides++;
        }


        /* Check if green points are below the grey circle */
        foundGreen = 0;
        for (int x=(int) (backX-2); x < (int) (backX+3); x++) {
            for (int y = (int) (backY); y < backY+10; y++) {
                try {
                    Color c = new Color(image.getRGB(x, y));
                    if (isGreen(c)) {
                        foundGreen++;
                        break;
                    }
                } catch (Exception e) {
                    // Ignore.
                }
            }
        }

        if (foundGreen >= 3) {
            greenSides++;
        }


        /* Check if green points are left of the grey circle */
        foundGreen = 0;
        for (int x=(int) (backX-9); x < backX; x++) {
            for (int y = (int) (backY-2); y < backY+3; y++) {
                try {
                    Color c = new Color(image.getRGB(x, y));
                    if (isGreen(c)) {
                        foundGreen++;
                        break;
                    }
                } catch (Exception e) {
                    // Ignore.
                }
            }
        }

        if (foundGreen >= 3) {
            greenSides++;
        }

        /* Check if green points are right of the grey circle */
        foundGreen = 0;
        for (int x=(int) (backX); x < (int) (backX+10); x++) {
            for (int y = (int) (backY-2); y < backY+3; y++) {
                try {
                    Color c = new Color(image.getRGB(x, y));
                    if (isGreen(c)) {
                        foundGreen++;
                        break;
                    }
                } catch (Exception e) {
                    // Ignore.
                }
            }
        }

        if (foundGreen >= 3) {
            greenSides++;
        }


        if (greenSides < 3) {
            throw new NoAngleException("Not enough green areas around the grey circle");
        }


        /*
         * At this point, the following is true:
         * Center of the T has been found
         * Front of the T has been found
         * Grey circle has been found
         * Grey circle is surrounded by green plate pixels on at least 3 sides
         * The grey circle, center of the T and front of the T line up roughly with the same angle
         */

        /* Calculate new angle using just the center of the T and the grey circle */
        length = (float) Math.sqrt(Math.pow(meanX - backX, 2)
                + Math.pow(meanY - backY, 2));
        ax = (meanX - backX) / length;
        ay = (meanY - backY) / length;
        angle = (float) Math.acos(ax);

        if (frontY < meanY) {
            angle = -angle;
        }

        if (showImage) {
            image.getGraphics().drawLine((int)backX, (int)backY, (int)(backX+ax*70), (int)(backY+ay*70));
            image.getGraphics().drawOval((int) backX-4, (int) backY-4, 8, 8);
        }

        if (angle == 0) {
            return (float) 0.001;
        }

        return angle;
    }
    public BufferedImage getThresh(BufferedImage img, int redL, int redH, int greenL, int greenH, int blueL, int blueH) { // Method to get thresholded image 

    	BufferedImage threshed = new BufferedImage(width,height, 0);
    	Color c;
    	
    	for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				c = new Color(img.getRGB(i,j));
				if( (c.getRed()>redL) && (c.getRed() <= redH) && (c.getBlue()>blueL) && (c.getBlue() <=blueH) && (c.getGreen()>greenL) && (c.getGreen() <= greenH)){
					threshed.setRGB(i, j, black.getRGB());
				}
				else{
					threshed.setRGB(i, j, white.getRGB());
				}
			}
		}
    	return threshed;
    }


}
