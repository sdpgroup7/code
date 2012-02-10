package uk.ac.ed.inf.sdp2012.group7.testing.vision;

import java.io.File;
import javax.swing.*;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 

import org.w3c.dom.NodeList;

public class VisionTesting extends Panel implements MouseListener, MouseMotionListener  {


	private static final long serialVersionUID = 987449760414360904L;
	BufferedImage image;
	static String imageName = "";
    private static Point coords = new Point();
    private static boolean mouseClick = false;
	
    public static ArrayList<Point> blueC = new ArrayList<Point>();
    public static ArrayList<Point> yellowC = new ArrayList<Point>();
    public static double yellowO = 0;
    public static double blueO = 0;
    public static double blueOrientation = 0;
    public static double yellowOrientation = 0;
    public static Point ball;
    
    public void mouseExited(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseMoved(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseClicked(MouseEvent e){
    	System.out.println(e.getPoint().toString());
        coords = correctPoint(e.getPoint());
        mouseClick = true;
    }
    
	public static Point getClickPoint(String message){
		System.out.println(message);
        while (!mouseClick) {
            try{
                Thread.sleep(100);
            } catch (Exception e) {}
        }
        mouseClick = false;
        System.out.println(coords.toString());
        return coords;
    }
	
    public Point correctPoint(Point p){
        return new Point(p.x,p.y);
    }
	

	  
    public static void main(String[] args){

    	JFileChooser fc = new JFileChooser();
    	fc.setCurrentDirectory(new File("./testData/"));
    	
    	File file = null;
    	
    	System.out.println("Please select the XML file of the image you wish to test.");
    	
    	fc.setFileFilter(new XMLFilter());
    	int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            System.out.println("Opening file:");
            System.out.println(file.getAbsolutePath());
        } else {
            System.out.println("Open command cancelled by user.");
            System.out.println("Quiting...");
            System.exit(0);
        }
        
        readXML(file);
        
        JFrame window = new JFrame("Vision Testing System");
    	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	VisionPanel panel = new VisionPanel(imageName);
    	window.getContentPane().add(panel);
    	window.setSize(650,500);
    	window.setVisible(true);
    	
	    VisionTesting v = new VisionTesting();
	    v.addMouseListeners(window,panel);
    	
    	ArrayList<Point> clickedCorners = new ArrayList<Point>();
    	Point blueRobot = getClickPoint("Click the centre of the blue robot");
    	Point yellowRobot = getClickPoint("Click the centre of the yellow robot");
    	blueOrientation = getOrientation(	getClickPoint("Click the grey circle on the blue robot"),
    										getClickPoint("Click the bottom of the T on the blue robot"));
    	yellowOrientation = getOrientation(	getClickPoint("Click the grey circle on the yellow robot"),
    										getClickPoint("Click the bottom of the T on the yellow robot"));
    	
    	Point ballPos = getClickPoint("Click the ball");
    	clickedCorners.add(getCorner(blueRobot,1));
    	clickedCorners.add(getCorner(blueRobot,2));
    	clickedCorners.add(getCorner(blueRobot,3));
    	clickedCorners.add(getCorner(blueRobot,4));
    	clickedCorners.add(getCorner(yellowRobot,1));
    	clickedCorners.add(getCorner(yellowRobot,2));
    	clickedCorners.add(getCorner(yellowRobot,3));
    	clickedCorners.add(getCorner(yellowRobot,4));
    	
    	int total = 0;
    	
    	
    	if(	close(clickedCorners.get(0),blueC.get(0)) &&
    		close(clickedCorners.get(1),blueC.get(1)) &&
    		close(clickedCorners.get(2),blueC.get(2)) &&
    		close(clickedCorners.get(3),blueC.get(3))){
    			System.out.println("Blue passed");
    	} else {
    		System.out.println("Blue failed");
    	}
    	if(	close(clickedCorners.get(4),yellowC.get(0)) &&
        		close(clickedCorners.get(5),yellowC.get(1)) &&
        		close(clickedCorners.get(6),yellowC.get(2)) &&
        		close(clickedCorners.get(7),yellowC.get(3))){
        			System.out.println("Yellow passed");
        } else {
        	System.out.println("Yellow failed");
        }
    	if(close(ballPos,ball)){
    		System.out.println("Ball passed");
    	} else {
    		System.out.println("Ball failed");
    	}
    	if(Math.abs(blueO-blueOrientation) < 0.175){
    		System.out.println("Blue Orientation Passed");
    	} else {
    		System.out.println("Blue Orientation Failed");
    	}
    	if(Math.abs(yellowO-yellowOrientation) < 0.175){
    		System.out.println("Yellow Orientation Passed");
    	} else {
    		System.out.println("Yellow Orientation Failed");
    	}
    	
    	
    	System.out.println("Accuracy: " + (int)((100.0*((float)total)/11)+0.5) + "%");
    
    }
    
    public static double getOrientation(Point greyCircle,Point top){
    	double a = Math.atan2(top.y-greyCircle.y,top.x-greyCircle.y);
    	System.out.println("Debug: " + a);
    	if(a < 0){
    		a = -a;
    	} else {
    		a = (2.0*Math.PI) - a;
    	}
    	System.out.println("Debug: " + a);
    	a = (a + (Math.PI/2.0)) % (2*Math.PI); 
    	System.out.println("Debug: " + a);
    	return a;
    }
    
    public void addMouseListeners(JFrame window, VisionPanel panel){
    	window.addMouseListener(this);
    	panel.addMouseListener(this);
    }
    
    public static boolean close(Point a, Point b){
    	//Tests if two points are within the specified delta distances to each other
    	int deltax = 20;
    	int deltay = 20;
    	if((Math.abs(a.x - b.x) < deltax) && (Math.abs(a.y - b.y) < deltay)){
    		return true;
    	}
    	return false;
    }
    
    private static Point getCorner(Point p, int i) {
    	int deltax;
    	int deltay;
    	switch(i){
	    	case 1:
	    		deltax = 0;
	    		deltay = -15;
	    		break;
	    	case 2:
	    		deltax = 15;
	    		deltay = 0;
	    		break;
	    	case 3:
	    		deltax = 0;
	    		deltay = 15;
	    		break;
	    	case 4:
	    		deltax = -15;
	    		deltay = 0;
	    		break;
	    	default:
	    		deltax = 0;
	    		deltay = 0;
	    		break;
    	}
		return new Point(p.x + deltax, p.y + deltay);
	}
    
    
    public static void readXML(File xmlFile){
    	
    	try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(xmlFile);

            // normalize text representation
            doc.getDocumentElement().normalize();
            System.out.println("Root element of the doc is " + doc.getDocumentElement().getNodeName());

            Node root = doc.getDocumentElement();
            Element rootE = (Element)root;
            imageName = rootE.getAttribute("location");
            NodeList data = root.getChildNodes();
            Node blue = data.item(0);
            Node yellow = data.item(1);
            Element ballE = (Element)data.item(2);
            
            
            ball = new Point(	Integer.parseInt(ballE.getAttribute("x")),
            						Integer.parseInt(ballE.getAttribute("y")));
            
            Element blueE = (Element)blue;
            Element yellowE = (Element)yellow;
            
            blueO = Double.parseDouble(blueE.getAttribute("orientation"));
            yellowO = Double.parseDouble(yellowE.getAttribute("orientation"));
            
            for(int i = 0;i<blue.getChildNodes().getLength();i++){
            	Point p;
            	Node corner = blue.getChildNodes().item(i);
            	Element elementCorner = (Element)corner;
            	p = new Point(	Integer.parseInt(elementCorner.getAttribute("x")),
            					Integer.parseInt(elementCorner.getAttribute("y")));
            	blueC.add(p);
            }
            for(int i = 0;i<yellow.getChildNodes().getLength();i++){
            	Point p;
            	Node corner = yellow.getChildNodes().item(i);
            	Element elementCorner = (Element)corner;
            	p = new Point(	Integer.parseInt(elementCorner.getAttribute("x")),
            					Integer.parseInt(elementCorner.getAttribute("y")));
            	yellowC.add(p);
            }
            
            System.out.println("Blue Robot:");
            System.out.println("Orientation: " + blueO);
            for(Point p: blueC){
            	System.out.println(p.toString());
            }
            System.out.println("Yellow Robot:");
            System.out.println("Orientation: " + yellowO);
            for(Point p: yellowC){
            	System.out.println(p.toString());
            }

        } catch (SAXParseException err) {
        	System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
        	System.out.println(" " + err.getMessage ());
        } catch (SAXException e) {
        	Exception x = e.getException ();
        	((x == null) ? e : x).printStackTrace ();
        }catch (Throwable t) {
        	t.printStackTrace ();
        }
    }
    
}
