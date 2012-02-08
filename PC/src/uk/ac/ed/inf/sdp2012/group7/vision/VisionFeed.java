package uk.ac.ed.inf.sdp2012.group7.vision;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.awt.Color;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;

import uk.ac.ed.inf.sdp2012.group7.vision.ui.ControlGUI;
import uk.ac.ed.inf.sdp2012.group7.testing.vision.TestSaver;
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
 * TODO: THIS CLASS IS A TOTAL MESS!
 *
 * @author s0840449
 */


public class VisionFeed extends WindowAdapter {
    private VideoDevice videoDev;
    private JLabel label;
    private JFrame windowFrame;
    //private JLabel labelThresh;
    //private JFrame windowFrameThresh;
    private FrameGrabber frameGrabber;
    private int width, height;
    private BufferedImage frameImage;
    //private ControlGUI thresholdGUI;
    private FeedProcessor processor;
    public boolean paused = false;
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
            int compressionQuality, ControlGUI thresholdsGUI) throws V4L4JException {

        /* Initialise the GUI that displays the video feed. */
        initFrameGrabber(videoDevice, width, height, channel, videoStandard, compressionQuality);
        initGUI();
        //this.thresholdGUI = thresholdsGUI;
        InitialLocation il = new InitialLocation(thresholdsGUI, this, this.windowFrame);
        processor = new FeedProcessor(il, height, width, thresholdsGUI, this);
        Vision.logger.info("VisionFeed Initialised");
        il.getPoints();
        il.getColors();
		if (Vision.worldState.getColor() == Color.blue) {
		    Vision.worldState.setOurRobotPosition(il.getInitCentroids()[2]);
		    Vision.worldState.setOpponentsRobotPosition(il.getInitCentroids()[1]);
		} else {
		    Vision.worldState.setOpponentsRobotPosition(il.getInitCentroids()[2]);
		    Vision.worldState.setOurRobotPosition(il.getInitCentroids()[1]);
		}
        Vision.logger.info("Vision System Calibrated");
        if(Vision.TESTING){
        	Vision.logger.info("Vision testing starting.");
        	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	Date date = new Date();
        	String filename = "testData/" + dateFormat.format(date);
        	il.getTestData(frameImage,filename);
        	TestSaver ts = new TestSaver();
        	ts.writePoints(il.getTestPoints(), frameImage, filename);
        	Vision.logger.info("Vision testing complete.");
        }
        Vision.worldState.setClickingDone(true);
    }


    public BufferedImage getFrameImage(){
        return this.frameImage;
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
        	Vision.logger.fatal("Couldn't detect native format for the device.");
            throw new ImageFormatException("Unable to detect any native formats for the device!");
        }
        ImageFormat imageFormat = deviceInfo.getFormatList().getNativeFormat(0);

        frameGrabber = videoDev.getJPEGFrameGrabber(inWidth, inHeight, channel, videoStandard, compressionQuality, imageFormat);

        frameGrabber.setCaptureCallback(new CaptureCallback() {
            public void exceptionReceived(V4L4JException e) {
                Vision.logger.error("Unable to capture frame: " + e.getMessage());
            }

            public void nextFrame(VideoFrame frame) {
                long before = System.currentTimeMillis();
                if(Vision.TESTING){
                	if(!paused) frameImage = frame.getBufferedImage();
                } else {
                	frameImage = frame.getBufferedImage();
                }
                frame.recycle();
                //processor.processAndUpdateImage(frameImage, before, label, labelThresh);
                processor.processAndUpdateImage(frameImage, before, label);
            }
        });

        frameGrabber.startCapture();
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
        windowFrame.setSize(width+5, height+25);
       
      /*  windowFrameThresh = new JFrame("Vision Window Threshed");
        labelThresh = new JLabel();
        windowFrameThresh.getContentPane().add(labelThresh);
	windowFrameThresh.addWindowListener(this);
        windowFrameThresh.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        windowFrameThresh.setVisible(true);
        windowFrameThresh.setSize(width+5, height+25);  */
    }
    
    //useless, had to be included because of the MouseEvent interface
    
    
    //can output the buffered image to disk, can normalise if necessary
    public void writeImage(BufferedImage image, String fn){
        try {
            File outputFile = new File(fn);
            ImageIO.write(image, "png", outputFile);
        } catch (Exception e) {
        	Vision.logger.error("Failed to write image: " + e.getMessage());
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
        Vision.logger.info("Vision System Ending...");
        System.exit(0);
    }

    
}
