package uk.ac.ed.inf.sdp2012.group7.VisionTesting;

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


public class VisionFeed extends WindowAdapter {
    private VideoDevice videoDev;
    private JLabel label = new JLabel();
    private JFrame windowFrame;
    private FrameGrabber frameGrabber;
    private int width, height;
    private BufferedImage frameImage;

    public VisionFeed(String videoDevice, int width, int height, int channel, int videoStandard, int compressionQuality) throws V4L4JException {
        initFrameGrabber(videoDevice, width, height, channel, videoStandard, compressionQuality);  
        initGUI();
        System.out.println("I've run");      
    }

    public BufferedImage getFrameImage(){
        return this.frameImage;
    }
    
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
        width = frameGrabber.getWidth();
        height = frameGrabber.getHeight();
    }

    private void initGUI() {
        windowFrame = new JFrame("Vision Window");
        windowFrame.getContentPane().add(label);
        windowFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        windowFrame.addWindowListener(this);
        windowFrame.setVisible(true);
        windowFrame.setSize(660,500);
    }

    public void writeImage(BufferedImage image, String fn){
        try {
            File outputFile = new File(fn);
            ImageIO.write(image, "png", outputFile);
        } catch (Exception e) {}
    }

    public void windowClosing(WindowEvent e) {
        /* Dispose of the various swing and v4l4j components. */
        frameGrabber.stopCapture();
        videoDev.releaseFrameGrabber();

        windowFrame.dispose();

        System.exit(0);
    }

    public void processAndUpdateImage(BufferedImage image, long before) {

        Graphics frameGraphics = label.getGraphics();
        Graphics imageGraphics = image.getGraphics();

        long after = System.currentTimeMillis();

        float fps = (1.0f)/((after - before) / 1000.0f);
        imageGraphics.setColor(Color.white);
        imageGraphics.drawString("FPS: " + fps, 15, 15);
        frameGraphics.drawImage(image, 0, 0, width, height, null);
    }

    
}
