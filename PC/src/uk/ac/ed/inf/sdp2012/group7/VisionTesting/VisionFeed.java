package uk.ac.ed.inf.sdp2012.group7.VisionTesting;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
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
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class VisionFeed extends WindowAdapter implements MouseListener {
    private VideoDevice videoDev;
    private JLabel label = new JLabel();
    private JFrame windowFrame;
    private FrameGrabber frameGrabber;
    private int width, height;
    private BufferedImage frameImage;
    private boolean mouseClick = false;
    private Point coords = new Point(0,0);
    private ArrayList<Point> points = new ArrayList<Point>();

    public VisionFeed(String videoDevice, int width, int height, int channel, int videoStandard, int compressionQuality) throws V4L4JException {
        initFrameGrabber(videoDevice, width, height, channel, videoStandard, compressionQuality);  
        initGUI();
        getPoints();
        writePoints(); //http://www.roseindia.net/xml/dom/createblankdomdocument.shtml
    }

    public void writePoints(){
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();
            Document doc = parser.newDocument();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void mouseExited(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseClicked(MouseEvent e){
        coords = correctPoint(e.getPoint());
        mouseClick = true;
    }


    public Point correctPoint(Point p){
        return new Point(p.x-4,p.y-24);
    }


    public void getPoints(){
        points.add(getClickPoint("Click the ball"));
        points.add(getClickPoint("Click a corner on the blue robot"));
        points.add(getClickPoint("Click another corner on the blue robot"));
        points.add(getClickPoint("Click another corner on the blue robot"));
        points.add(getClickPoint("Click another corner on the blue robot"));
        points.add(getClickPoint("Click a corner on the yellow robot"));
        points.add(getClickPoint("Click another corner on the yellow robot"));
        points.add(getClickPoint("Click another corner on the yellow robot"));
        points.add(getClickPoint("Click another corner on the yellow robot"));
        points.add(getClickPoint("Click the grey circle on the blue robot"));
        points.add(getClickPoint("Click the grey circle on the yellow robot"));
        points.add(getClickPoint("Click the very bottom of the T on the blue robot"));
        points.add(getClickPoint("Click the very bottom of the T on the yellow robot"));
    }

    public Point getClickPoint(String message){
        System.out.println(message);

        while (!mouseClick) {
            try{
                Thread.sleep(100);
            } catch (Exception e) {}
        }
        mouseClick = false;
        System.out.println(coords);
        return coords;
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
        windowFrame.addMouseListener(this);
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
