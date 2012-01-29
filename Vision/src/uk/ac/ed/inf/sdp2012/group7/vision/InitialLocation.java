package uk.ac.ed.inf.sdp2012.group7.vision;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.Color;

import java.awt.Point;

public class InitialLocation implements MouseListener, MouseMotionListener {

    private Point coords = new Point();
    private static Point mouseCo = new Point(0,0);

    public InitialLocation() {
    }
    
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
    //When the mouse has been clicked get the location.
    public void mouseClicked(MouseEvent e){
        coords = correctPoint(e.getPoint());
        mouseClick = true;
    }
    
	public void getPoints(){
	
	    /*
	    Get the extremes of the pitch.
	    */
		System.err.println("By bulge we mean the part of the pitch (in green) which sticks out the most in the specified direction");
		pitchConstants.setTopBuffer(getClickPoint("Click the top bulge").y);
		pitchConstants.setRightBuffer(getClickPoint("Click the right bulge").x);
		pitchConstants.setBottomBuffer(getClickPoint("Click the bottom bulge").y);
		pitchConstants.setLeftBuffer(getClickPoint("Click the left bulge").x);

		pitchConstants.setTopLeft(getClickPoint("Click the top left corner"));
		pitchConstants.setTopRight(getClickPoint("Click the top right corner"));
		pitchConstants.setBottomRight(getClickPoint("Click the bottom right corner"));
		pitchConstants.setBottomLeft(getClickPoint("Click the bottom left corner"));

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
        System.err.println(message);

        while (!mouseClick) {
            try{
                Thread.sleep(100);
            } catch (Exception e) {}
        }
        mouseClick = false;
        return getColor(coords, frameImage);
    }

    public Point correctPoint(Point p){
        return new Point(p.x-4,p.y-24);
    }

    /*
    Get the color where the mouse was clicked.  Takes an average of the adjacent
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
}
