package uk.ac.ed.inf.sdp2012.group7.vision;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

import uk.ac.ed.inf.sdp2012.group7.vision.ui.ControlGUI;

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
    private FrameGrabber frameGrabber;
    private int width, height;
    private BufferedImage frameImage;
    private ControlGUI thresholdGUI;
    private FeedProcessor processor;
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
            int compressionQuality, ControlGUI thresholdsGUI, PitchConstants pitchConstants) throws V4L4JException {
            
       /*
       Removed pitch constants, threshold constants and worldstate from the constructor
       */

        /* Initialise the GUI that displays the video feed. */
        initFrameGrabber(videoDevice, width, height, channel, videoStandard, compressionQuality);
        initGUI();
        this.thresholdGUI = thresholdsGUI;
        InitialLocation il = new InitialLocation(this.thresholdGUI,this,pitchConstants);
        processor = new FeedProcessor(il,height,width,pitchConstants);

        il.getColors();
        il.getPoints();
        
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
                processor.processAndUpdateImage(frameImage, before, label);
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
        windowFrame.setSize(width+5, height+25);
        //windowFrame.addMouseListener(this);
        //windowFrame.addMouseMotionListener(this);
    }
    
    //useless, had to be included because of the MouseEvent interface
    
    
    //can output the buffered image to disk, can normalise if neccessary
    public void writeImage(BufferedImage image, String fn){
        try {
            File outputFile = new File(fn);
            ImageIO.write(image, "png", outputFile);
        } catch (Exception e) {}
    }

    //crops the image based on the corner values and then stretches that back up to 640x480


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

    
}
