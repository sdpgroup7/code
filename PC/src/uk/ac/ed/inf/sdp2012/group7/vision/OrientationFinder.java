package uk.ac.ed.inf.sdp2012.group7.vision;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.awt.Color;

public class OrientationFinder{

    ThresholdsState thresholdsState;

    public OrientationFinder(ThresholdsState thresholdsState){
        this.thresholdsState = thresholdsState;
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
    public float findOrientation1(int robotX, int robotY, int greyX, int greyY){
    	float length = (float) Math.sqrt(Math.pow(robotX - greyX, 2)
                + Math.pow(robotY - greyY, 2));
        float ax = (robotX - greyX) / length;
        float ay = (robotY - greyY) / length;
        float angle = (float) Math.acos(ax);

        if (robotY < greyY) {
            angle = -angle;
        }
        if (angle == 0) {
            return (float) 0.001;
        }

        return angle;
    }
    public float findOrientation(int robotX, int robotY, int greyX, int greyY){
    	
    	double ans = Math.atan2(robotY - greyY, robotX - greyX);
    	ans = (( (ans * (180/Math.PI)) + 90) + 360) % 360;
    	return (float) ans; 
    }
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
        ColorDetection colorDetection = new ColorDetection(this.thresholdsState);

        for (int a=-20; a < 21; a++) {
            ax = (float) Math.cos(angle+((a*Math.PI)/180));
            ay = (float) Math.sin(angle+((a*Math.PI)/180));
            for (int i = 15; i < 25; i++) {
                int greyX = meanX - (int) (ax * i);
                int greyY = meanY - (int) (ay * i);
                try {
                    Color c = new Color(image.getRGB(greyX, greyY));
                    if (colorDetection.isGrey(c)) {
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
                    if (colorDetection.isGreen(c)) {
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
                    if (colorDetection.isGreen(c)) {
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
                    if (colorDetection.isGreen(c)) {
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
                    if (colorDetection.isGreen(c)) {
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

}
