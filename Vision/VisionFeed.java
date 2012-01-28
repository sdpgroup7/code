import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.io.*;
import javax.imageio.*;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;

import au.edu.jcu.v4l4j.CaptureCallback;
import au.edu.jcu.v4l4j.DeviceInfo;
import au.edu.jcu.v4l4j.FrameGrabber;
import au.edu.jcu.v4l4j.ImageFormat;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.VideoFrame;
import au.edu.jcu.v4l4j.exceptions.ImageFormatException;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;

/**
 * The main class for showing the video feed and processing the video
 * data. Identifies ball and robot locations, and robot orientations.
 *
 * @author s0840449
 */



//TODO: The points returned when we click are out somehow. Click on the ball for example and you can see that it returns the wrong colour
//        I verified this when I drew lines on the image as they were not in the place I clicked. 

public class VisionFeed extends WindowAdapter implements MouseListener, MouseMotionListener {
    private VideoDevice videoDev;
    private JLabel label;
    private JFrame windowFrame;
    private FrameGrabber frameGrabber;
    private Thread captureThread;
    private boolean stop;
    private int width, height;
    private WorldState worldState;
    private ThresholdsState thresholdsState;
    private static PitchConstants pitchConstants;
    private Point coords = new Point();
    private boolean mouseClick = false;
    private Color[] objects = new Color[5];
    private int objectIndex = 0;
    private BufferedImage frameImage;
    private ControlGUI thresholdGUI;
    private static Point[] corners = new Point[4];
    private static boolean buffersSet = false;
    private static Point mouseCo = new Point(0,0);
    private int[] hsbLowBall = new int[3];
    private int[] hsbHighBall = new int[3];
    private int[] hsbLowBlue = new int[3];
    private int[] hsbHighBlue = new int[3];
    private int[] hsbLowYellow = new int[3];
    private int[] hsbHighYellow = new int[3];
    private int[] hsbLowGreen = new int[3];
    private int[] hsbHighGreen = new int[3];
    private int[] hsbLowGrey = new int[3];
    private int[] hsbHighGrey = new int[3];

    /**
     * Default constructor.
     *
     * @param videoDevice           The video device file to capture from.
     * @param width                 The desired capture width.
     * @param height                The desired capture height.
     * @param videoStandard         The capture standard.
     * @param channel               The capture channel.
     * @param compressionQuality    The JPEG compression quality.
     * @param worldState
     * @param thresholdsState
     * @param pitchConstants
     *
     * @throws V4L4JException   If any parameter if invalid.
     */
    public VisionFeed(String videoDevice, int width, int height, int channel, int videoStandard,
            int compressionQuality, WorldState worldState, ThresholdsState thresholdsState,
            PitchConstants pitchConstants, ControlGUI thresholdsGUI) throws V4L4JException {

        /* Set the state fields. */
        this.worldState = worldState;
        this.thresholdsState = thresholdsState;
        this.pitchConstants = pitchConstants;

        /* Initialise the GUI that displays the video feed. */
        initFrameGrabber(videoDevice, width, height, channel, videoStandard, compressionQuality);
        initGUI();
        this.thresholdGUI = thresholdsGUI;
        getColors();
        getPoints();
    }
    
	public void getPoints(){
	
	    /*
	    Get the corners of the pitch by choosing the widest parts of the pitch in the horizontal
	    and the vertical.  Gets bits not in the pitch because of distortion
	    */
		System.err.println("By bulge we mean the part of the pitch (in green) which sticks out the most in the specified direction");
		pitchConstants.setTopBuffer(getClickPoint("Click the top bulge").y);
		pitchConstants.setRightBuffer(getClickPoint("Click the right bulge").x);
		pitchConstants.setBottomBuffer(getClickPoint("Click the bottom bulge").y);
		pitchConstants.setLeftBuffer(getClickPoint("Click the left bulge").x);

		corners[0] = getClickPoint("Click the top left corner");
		corners[1] = getClickPoint("Click the top right corner");
		corners[2] = getClickPoint("Click the bottom right corner");
		corners[3] = getClickPoint("Click the bottom left corner");

		System.err.println("Corners:");
		System.err.println(corners[0]);
		System.err.println(corners[1]);
		System.err.println(corners[2]);
		System.err.println(corners[3]);

		buffersSet = true;
		
	}
    /*
    just register the mouse click after being asked to by getClickPoint
    */
	public Point getClickPoint(String message){
		System.err.println(message);

        while (!mouseClick) {
            try{
                Thread.sleep(100);
            } catch (Exception e) {}
        }
        mouseClick = false;
        System.err.println(coords);
        return coords;
    }
    /*
    Get the threshold values for the objects in the match i.e. ball.
    Registers the mouse clicks after being asked to by getColors
    */
    public Color getClickColor(String message){
        System.err.println(message);

        while (!mouseClick) {
            try{
                Thread.sleep(100);
            } catch (Exception e) {}
        }
        mouseClick = false;
        return getColor(coords, frameImage);
    }

    
    //Set the sliders on the GUI, the messages are used to tell the user what to click
    public void getColors(){
        thresholdGUI.setBallValues(getClickColor("Click the ball"));
        hsbLowBall = hsbValues(thresholdsState.getBall_r_low(),thresholdsState.getBall_g_low(),thresholdsState.getBall_b_low(), -25);
        hsbHighBall = hsbValues(thresholdsState.getBall_r_high(),thresholdsState.getBall_g_high(),thresholdsState.getBall_b_high(), 25);
        
        thresholdGUI.setYellowValues(getClickColor("Click the yellow robot"));
        hsbLowYellow = hsbValues(thresholdsState.getYellow_r_low(),thresholdsState.getYellow_g_low(),thresholdsState.getYellow_b_low(), -25);
        hsbHighYellow = hsbValues(thresholdsState.getYellow_r_high(),thresholdsState.getYellow_g_high(),thresholdsState.getYellow_b_high(), 25);
        
        thresholdGUI.setBlueValues(getClickColor("Click the blue robot"));
        hsbLowBlue = hsbValues(thresholdsState.getBlue_r_low(),thresholdsState.getBlue_g_low(),thresholdsState.getBlue_b_low(), -25);
        hsbHighBlue = hsbValues(thresholdsState.getBlue_r_high(),thresholdsState.getBlue_g_high(),thresholdsState.getBlue_b_high(), 25);
        
        thresholdGUI.setGreenValues(getClickColor("Click a green plate"));
        hsbLowGreen = hsbValues(thresholdsState.getGreen_r_low(),thresholdsState.getGreen_g_low(),thresholdsState.getGreen_b_low(), -25);
        hsbHighGreen = hsbValues(thresholdsState.getGreen_r_high(),thresholdsState.getGreen_g_high(),thresholdsState.getGreen_b_high(), 25);

        
        thresholdGUI.setGreyValues(getClickColor("Click a grey circle"));
        hsbLowGrey = hsbValues(thresholdsState.getGrey_r_low(),thresholdsState.getGrey_g_low(),thresholdsState.getGrey_b_low(), -25);
        hsbHighGrey = hsbValues(thresholdsState.getGrey_r_high(),thresholdsState.getGrey_g_high(),thresholdsState.getGrey_b_high(), 25);
        
    }
    
    //useless, had to be included because of the MouseEvent interface
    public void mouseExited(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseMoved(MouseEvent e) {
        mouseCo = correctPoint(e.getPoint());
    }
    public void mouseDragged(MouseEvent e) {
        mouseCo = correctPoint(e.getPoint());
    }

     /**
     * Initialises a FrameGrabber object with the given parameters.
     *
     * @param videoDevice           The video device file to capture from.
     * @param inWidth               The desired capture width.
     * @param inHeight              The desired capture height.
     * @param channel               The capture channel.
     * @param videoStandard         The capture standard.
     * @param compressionQuality    The JPEG compression quality.
     *
     * @throws V4L4JException   If any parameter is invalid.
     */
    private void initFrameGrabber(String videoDevice, int inWidth, int inHeight, int channel, int videoStandard, int compressionQuality) throws V4L4JException {
        videoDev = new VideoDevice(videoDevice);

        DeviceInfo deviceInfo = videoDev.getDeviceInfo();

        if (deviceInfo.getFormatList().getNativeFormats().isEmpty()) {
          throw new ImageFormatException("Unable to detect any native formats for the device!");
        }
        ImageFormat imageFormat = deviceInfo.getFormatList().getNativeFormat(0);

        frameGrabber = videoDev.getJPEGFrameGrabber(inWidth, inHeight, channel, videoStandard, compressionQuality, imageFormat);

        frameGrabber.setCaptureCallback(new CaptureCallback() {
            public void exceptionReceived(V4L4JException e) {
                System.err.println("Unable to capture frame:");
                e.printStackTrace();
            }

            public void nextFrame(VideoFrame frame) {
                long before = System.currentTimeMillis();
                frameImage = frame.getBufferedImage();
                frame.recycle();
                processAndUpdateImage(frameImage, before);
            }
        });

        frameGrabber.startCapture();
        //System.err.println("Video Frame width,height: " + frameGrabber.getWidth() + "," + frameGrabber.getHeight());
        width = frameGrabber.getWidth();
        height = frameGrabber.getHeight();
    }

    /**
     * Creates the graphical interface components and initialises them
     */
    private void initGUI() {
        windowFrame = new JFrame("Vision Window");
        label = new JLabel();
        windowFrame.getContentPane().add(label);
        windowFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        windowFrame.addWindowListener(this);
        windowFrame.setVisible(true);
        //windowFrame.setSize(width, height);
        windowFrame.setSize(width+5, height+25);
        //System.err.println("JFrame width,height: " + windowFrame.getSize().width + "," + windowFrame.getSize().height);
        windowFrame.addMouseListener(this);
        windowFrame.addMouseMotionListener(this);
    }
    
    //When the mouse has been clicked get the location.
    public void mouseClicked(MouseEvent e){
        coords = correctPoint(e.getPoint());
        mouseClick = true;
    }
    
    public Point correctPoint(Point p){
        return new Point(p.x-4,p.y-24);
    }

    /*
    Get the colour where the mouse was clicked.  Takes an average of the adjacent
    pixels, but you should try and click centrally in the object still.
    */
    public Color getColor(Point p, BufferedImage image){
        //writeImage(image,"test.png");

        Color[] temp = new Color[9];
        temp[0] = new Color(image.getRGB(p.x-1,p.y-1));
        temp[1] = new Color(image.getRGB(p.x-1,p.y));
        temp[2] = new Color(image.getRGB(p.x-1,p.y+1));
        temp[3] = new Color(image.getRGB(p.x,p.y-1));
        temp[4] = new Color(image.getRGB(p.x,p.y));
        temp[5] = new Color(image.getRGB(p.x,p.y+1));
        temp[6] = new Color(image.getRGB(p.x+1,p.y-1));
        temp[7] = new Color(image.getRGB(p.x+1,p.y));
        temp[8] = new Color(image.getRGB(p.x+1,p.y+1));
        
        int avgr = 0;
        int avgg = 0;
        int avgb = 0;

        for(int i = 0;i<9;i++){
            avgr += temp[i].getRed();
            avgg += temp[i].getGreen();
            avgb += temp[i].getBlue();
        }
        avgr = avgr/9;
        avgg = avgg/9;
        avgb = avgb/9;

        Color avgColor = new Color(avgr,avgg,avgb);
        System.err.println(avgColor.toString());
        return avgColor;
    }
    
    //can output the buffered image to disk, can normalise if neccessary
    public void writeImage(BufferedImage image, String fn){
        //NormaliseRGB norm = new NormaliseRGB();
        //image = norm.normalise(image);
        try {
            File outputFile = new File(fn);
            ImageIO.write(image, "png", outputFile);
        } catch (Exception e) {}
    }

    //crops the image based on the corner values and then stretches that back up to 640x480
    private static BufferedImage markImage(BufferedImage image) {
        int width = 640;
        int height = 480;
        
        if(buffersSet){
            //currently instead of cropping and stretching the image it simply draws on the borders of where it would crop to in blue
            for(int x = 0;x<width;x++){
                for(int y = 0;y<height;y++){
                    if((y == pitchConstants.getTopBuffer()) || (x == pitchConstants.getRightBuffer()) || (y == pitchConstants.getBottomBuffer()) || (x == pitchConstants.getLeftBuffer())){
                        image.setRGB(x,y,(255 << 24) + 255);
                    }
                    /*if((x == mouseCo.x) || (y == mouseCo.y)){
                        image.setRGB(x,y,(255 << 24) + 255);
                    }*/
                }
            }
            return image;

        } else {
            return image;
        }
    }


    /**
     * Catches the window closing event, so that we can free up resources
     * before exiting.
     *
     * @param e         The window closing event.
     */
    public void windowClosing(WindowEvent e) {
        /* Dispose of the various swing and v4l4j components. */
        frameGrabber.stopCapture();
        videoDev.releaseFrameGrabber();

        windowFrame.dispose();

        System.exit(0);
    }

    /**
     * Processes an input image, extracting the ball and robot positions and robot
     * orientations from it, and then displays the image (with some additional graphics
     * layered on top for debugging) in the vision frame.
     *
     * @param image     The image to process and then show.
     */
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
                /* The RGB colours and hsv values for the current pixel. */
                Color c = new Color(image.getRGB(column, row));
                float hsbvals[] = new float[3];
                Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), hsbvals);

                /* Debug graphics for the grey circles and green plates.
                 * TODO: Move these into the actual detection. */
                if (thresholdsState.isGrey_debug() && isGrey(c, hsbvals)) {
                    image.setRGB(column, row, 0xFFFF0099);
                }

                if (thresholdsState.isGreen_debug() && isGreen(c, hsbvals)) {
                    image.setRGB(column, row, 0xFFFF0099);
                }

                /* Is this pixel part of the Blue T? */
                if (isBlue(c, hsbvals) ){

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
                if (isYellow(c, hsbvals)) {

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
                if (isBall(c, hsbvals)) {

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
     * Determines if a pixel is part of the blue T, based on input RGB colours
     * and hsv values.
     *
     * @param color         The RGB colours for the pixel.
     * @param hsbvals       The HSV values for the pixel.
     *
     * @return              True if the RGB and HSV values are within the defined
     *                      thresholds (and thus the pixel is part of the blue T),
     *                      false otherwise.
     */
    
    private int[] hsbValues(int r, int g, int b, int thresh){
        float[] hsb = new float[3];
        int[] hsbInt = new int[3];
        Color.RGBtoHSB(r,g,b,hsb);
        for(int i = 0; i < 3; i++){
            hsbInt[i] = (int)hsb[i] + thresh;
        }
        return hsbInt;
    }
     
    private boolean isBlue(Color color, float[] hsbvals) {
        return hsbvals[0] <= hsbHighBlue[0] && hsbvals[0] >= hsbLowBlue[0] &&
        hsbvals[1] <= hsbHighBlue[1] && hsbvals[1] >= hsbLowBlue[1] &&
        hsbvals[2] <= hsbHighBlue[2] && hsbvals[2] >= hsbLowBlue[2] &&
        color.getRed() <= thresholdsState.getBlue_r_high() && color.getRed() >= thresholdsState.getBlue_r_low() &&
        color.getGreen() <= thresholdsState.getBlue_g_high() && color.getGreen() >= thresholdsState.getBlue_g_low() &&
        color.getBlue() <= thresholdsState.getBlue_b_high() && color.getBlue() >= thresholdsState.getBlue_b_low();
    }

    /**
     * Determines if a pixel is part of the yellow T, based on input RGB colours
     * and hsv values.
     *
     * @param color         The RGB colours for the pixel.
     * @param hsbvals       The HSV values for the pixel.
     *
     * @return              True if the RGB and HSV values are within the defined
     *                      thresholds (and thus the pixel is part of the yellow T),
     *                      false otherwise.
     */
    private boolean isYellow(Color colour, float[] hsbvals) {
        return hsbvals[0] <= hsbHighYellow[0] && hsbvals[0] >= hsbLowYellow[0] &&
        hsbvals[1] <= hsbHighYellow[1] && hsbvals[1] >= hsbLowYellow[1] &&
        hsbvals[2] <= hsbHighYellow[2] && hsbvals[2] >= hsbLowYellow[2] &&
        colour.getRed() <= thresholdsState.getYellow_r_high() &&  colour.getRed() >= thresholdsState.getYellow_r_low() &&
        colour.getGreen() <= thresholdsState.getYellow_g_high() && colour.getGreen() >= thresholdsState.getYellow_g_low() &&
        colour.getBlue() <= thresholdsState.getYellow_b_high() && colour.getBlue() >= thresholdsState.getYellow_b_low();
    }

    /**
     * Determines if a pixel is part of the ball, based on input RGB colours
     * and hsv values.
     *
     * @param color         The RGB colours for the pixel.
     * @param hsbvals       The HSV values for the pixel.
     *
     * @return              True if the RGB and HSV values are within the defined
     *                      thresholds (and thus the pixel is part of the ball),
     *                      false otherwise.
     */
    private boolean isBall(Color colour, float[] hsbvals) {
        return hsbvals[0] <= hsbHighBall[0] && hsbvals[0] >= hsbLowBall[0] &&
        hsbvals[1] <= hsbHighBall[1] && hsbvals[1] >= hsbLowBall[1] &&
        hsbvals[2] <= hsbHighBall[2] && hsbvals[2] >= hsbLowBall[2] &&
        colour.getRed() <= thresholdsState.getBall_r_high() &&  colour.getRed() >= thresholdsState.getBall_r_low() &&
        colour.getGreen() <= thresholdsState.getBall_g_high() && colour.getGreen() >= thresholdsState.getBall_g_low() &&
        colour.getBlue() <= thresholdsState.getBall_b_high() && colour.getBlue() >= thresholdsState.getBall_b_low();
    }

    /**
     * Determines if a pixel is part of either grey circle, based on input RGB colours
     * and hsv values.
     *
     * @param color         The RGB colours for the pixel.
     * @param hsbvals       The HSV values for the pixel.
     *
     * @return              True if the RGB and HSV values are within the defined
     *                      thresholds (and thus the pixel is part of a grey circle),
     *                      false otherwise.
     */
    private boolean isGrey(Color colour, float[] hsbvals) {
        return hsbvals[0] <= hsbHighGrey[0] && hsbvals[0] >= hsbLowGrey[0] &&
        hsbvals[1] <= hsbHighGrey[1] && hsbvals[1] >= hsbLowGrey[1] &&
        hsbvals[2] <= hsbHighGrey[2] && hsbvals[2] >= hsbLowGrey[2] &&
        colour.getRed() <= thresholdsState.getGrey_r_high() &&  colour.getRed() >= thresholdsState.getGrey_r_low() &&
        colour.getGreen() <= thresholdsState.getGrey_g_high() && colour.getGreen() >= thresholdsState.getGrey_g_low() &&
        colour.getBlue() <= thresholdsState.getGrey_b_high() && colour.getBlue() >= thresholdsState.getGrey_b_low();
    }

    /**
     * Determines if a pixel is part of either green plate, based on input RGB colours
     * and hsv values.
     *
     * @param color         The RGB colours for the pixel.
     * @param hsbvals       The HSV values for the pixel.
     *
     * @return              True if the RGB and HSV values are within the defined
     *                      thresholds (and thus the pixel is part of a green plate),
     *                      false otherwise.
     */
    private boolean isGreen(Color colour, float[] hsbvals) {
        return hsbvals[0] <= hsbHighGreen[0] && hsbvals[0] >= hsbLowGreen[0] &&
        hsbvals[1] <= hsbHighGreen[1] && hsbvals[1] >= hsbLowGreen[1] &&
        hsbvals[2] <= hsbHighGreen[2] && hsbvals[2] >= hsbLowGreen[2] &&
        colour.getRed() <= thresholdsState.getGreen_r_high() &&  colour.getRed() >= thresholdsState.getGreen_r_low() &&
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
                    float hsbvals[] = new float[3];
                    Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), hsbvals);
                    if (isGrey(c, hsbvals)) {
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
                    float hsbvals[] = new float[3];
                    Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), hsbvals);
                    if (isGreen(c, hsbvals)) {
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
                    float hsbvals[] = new float[3];
                    Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), hsbvals);
                    if (isGreen(c, hsbvals)) {
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
                    float hsbvals[] = new float[3];
                    Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), hsbvals);
                    if (isGreen(c, hsbvals)) {
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
                    float hsbvals[] = new float[3];
                    Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), hsbvals);
                    if (isGreen(c, hsbvals)) {
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
