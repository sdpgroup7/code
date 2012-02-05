package uk.ac.ed.inf.sdp2012.group7.vision;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.*;
import javax.swing.JFrame;

import uk.ac.ed.inf.sdp2012.group7.vision.ui.ControlGUI;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.ObjectPosition;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.Pitch;

public class InitialLocation implements MouseListener, MouseMotionListener {

    private Point coords = new Point();
    private boolean mouseClick = false;
    private PitchConstants pitchConstants;
    private ControlGUI thresholdGUI;
    private VisionFeed visionFeed;
    private boolean buffersSet = false;
    //private JFrame windowFrame;

    public InitialLocation(ControlGUI thresholdsGUI, VisionFeed visionFeed, PitchConstants pitchConstants, JFrame windowFrame) {
    	this.thresholdGUI = thresholdsGUI;
        this.visionFeed = visionFeed;
        this.pitchConstants = pitchConstants;
        //this.windowFrame = windowFrame;
        windowFrame.addMouseListener(this);
        windowFrame.addMouseMotionListener(this);
        Vision.logger.info("InitialLocation Initialised");
    }
    
    public void mouseExited(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseMoved(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    //When the mouse has been clicked get the location.
    public void mouseClicked(MouseEvent e){
    	Vision.logger.debug(e.getPoint().toString());
        coords = correctPoint(e.getPoint());
        mouseClick = true;
    }
    
	public void getPoints(){
	
		Vision.worldState.setPitch(new Pitch(
				getClickPoint("Click the top left corner"),
				getClickPoint("Click the top right corner"),
				getClickPoint("Click the bottom right corner"),
				getClickPoint("Click the bottom left corner")));
		
		Vision.worldState.setPitchBuffers(
				getClickPoint("Click the top bulge").y,
				getClickPoint("Click the right bulge").x,
				getClickPoint("Click the bottom bulge").y,
				getClickPoint("Click the left bulge").x);

		buffersSet = true;
		
	}
    /*
    just register the mouse click after being asked to by getClickPoint
    */
	public Point getClickPoint(String message){
		System.out.println(message);

        while (!mouseClick) {
            try{
                Thread.sleep(100);
            } catch (Exception e) {}
        }
        mouseClick = false;
        Vision.logger.debug(coords.toString());
        return coords;
    }
    
    //Set the sliders on the GUI, the messages are used to tell the user what to click
    public void getColors(){
        thresholdGUI.setBallValues(getClickColor("Click the ball"));
        thresholdGUI.setYellowValues(getClickColor("Click the yellow robot"));
        thresholdGUI.setBlueValues(getClickColor("Click the blue robot"));
        thresholdGUI.setGreenValues(getClickColor("Click a green plate"));
        thresholdGUI.setGreyValues(getClickColor("Click a grey circle"));
    }
    /*
    Get the threshold values for the objects in the match i.e. ball.
    Registers the mouse clicks after being asked to by getColors
    */
    public Color getClickColor(String message){
        System.out.println(message);

        while (!mouseClick) {
            try{
                Thread.sleep(100);
            } catch (Exception e) {}
        }
        mouseClick = false;
        return getColor(coords, this.visionFeed.getFrameImage());
    }

    public Point correctPoint(Point p){
        return new Point(correctX(p.x),correctY(p.y));
    }
    
    public int correctX(int x){
    	return x-4;
    }
    
    public int correctY(int y){
    	return y-24;
    }

    /*
    Get the color where the mouse was clicked.  Takes an average of the adjacent
    pixels, but you should try and click centrally in the object still.
    */
    public Color getColor(Point p, BufferedImage image){
        //writeImage(image,"test.png");

        /*Color[] temp = new Color[9];
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
        return avgColor;*/
    	Color c = new Color(image.getRGB(p.x,p.y));
    	Vision.logger.debug(c.toString());
    	return c;
    }
    
    public BufferedImage markImage(BufferedImage image) {
        int width = 640;
        int height = 480;
        Graphics2D graphics = image.createGraphics();
        if(buffersSet){
        	graphics.drawLine(Vision.worldState.getPitch().getLeftBuffer(),0,Vision.worldState.getPitch().getLeftBuffer(),height);
        	graphics.drawLine(Vision.worldState.getPitch().getRightBuffer(),0,Vision.worldState.getPitch().getRightBuffer(),height);
        	graphics.drawLine(0,Vision.worldState.getPitch().getTopBuffer(),width,Vision.worldState.getPitch().getTopBuffer());
        	graphics.drawLine(0,Vision.worldState.getPitch().getBottomBuffer(),width,Vision.worldState.getPitch().getBottomBuffer());
            return image;
        } else {
            return image;
        }
    }
}
