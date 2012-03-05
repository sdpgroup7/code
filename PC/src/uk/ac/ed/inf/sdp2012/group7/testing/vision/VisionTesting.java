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
import uk.ac.ed.inf.sdp2012.group7.vision.VisionTools;

public class VisionTesting extends Panel implements MouseListener, MouseMotionListener  {


	private static final long serialVersionUID = 987449760414360904L;
	BufferedImage image;
	static String imageName = "";
    private static Point coords = new Point();
    private static boolean mouseClick = false;
	
    public static ArrayList<Point> blueC = new ArrayList<Point>();
    public static ArrayList<Point> yellowC = new ArrayList<Point>();
    public static double blueOrientation = 0;
    public static double yellowOrientation = 0;
    
    public static Point ball;
    public static Point ballAuto;
    public static Point blueCentroid;
    public static Point yellowCentroid;
    public static Point blueCentroidAuto;
    public static Point yellowCentroidAuto;
    public static Point blueBottom;
    public static Point yellowBottom;
    public static int left;
    public static int right;
    public static int leftAuto;
    public static int rightAuto;
    public static double blueAngle;
    public static double yellowAngle;
    
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
    	
    	File fileA = null;
    	File fileB = null;
    	
    	System.out.println("Please select the clicked XML file of the image you wish to test.");
    	
    	fc.setFileFilter(new XMLFilter());
    	int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileA = fc.getSelectedFile();
            System.out.println("Opening file:");
            System.out.println(fileA.getAbsolutePath());
        } else {
            System.out.println("Open command cancelled by user.");
            System.out.println("Quiting...");
            System.exit(0);
        }
        
    	System.out.println("Please select the auto XML file of the image you wish to test.");
    	
    	fc.setFileFilter(new XMLFilter());
    	int returnValB = fc.showOpenDialog(null);

        if (returnValB == JFileChooser.APPROVE_OPTION) {
            fileB = fc.getSelectedFile();
            System.out.println("Opening file:");
            System.out.println(fileB.getAbsolutePath());
        } else {
            System.out.println("Open command cancelled by user.");
            System.out.println("Quiting...");
            System.exit(0);
        }
        
        readXMLClick(fileA);
        readXMLAuto(fileB);
        
        JFrame window = new JFrame("Vision Testing System");
    	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	VisionPanel panel = new VisionPanel(imageName);
    	window.getContentPane().add(panel);
    	window.setSize(650,500);
    	window.setVisible(true);
    	
	    VisionTesting v = new VisionTesting();
 	

    	
    	int total = 0;
    	/*
    	if (Point.distanceSq(blueCentroid.x, blueCentroid.y, blueCentroidAuto.x, blueCentroidAuto.y) < 25){
    		System.out.println("Blue Centroid Passed");
    		total++;
    	}else{
    		System.out.println("Blue Centroid Failed");
    	}
    	
    	if (Point.distanceSq(yellowCentroid.x, yellowCentroid.y, yellowCentroidAuto.x, yellowCentroidAuto.y) < 25){
    		System.out.println("Yellow Centroid Passed");
    		total++;
    	}else{
    		System.out.println("Yellow Centroid Failed");
    	}
    	
    	if (Point.distanceSq(ball.x, ball.y, ballAuto.x, ballAuto.y) < 25){
    		System.out.println("Ball Centroid Passed");
    		total++;
    	}else{
    		System.out.println("Ball Centroid Failed");
    	}
    	
    	if ((VisionTools.pixelsToCM((double)right-left) >= 240) && (VisionTools.pixelsToCM((double)right-left) <= 250)){
    		System.out.println("pixelsToCM works");
    	}else{
    		System.out.println("pixelsToCM fails");
    	}
    	
    	if ((VisionTools.cmToPixels(243) <= (right-left+5)) && (VisionTools.cmToPixels(243) >= (right-left-5))){
    		System.out.println("cmToPixels works");
    	}else{
    		System.out.println("cmToPixels fails");
    	}
    	*/
    	System.out.println(Integer.toString(total));
    	

    	
    
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
    
    
    public static void readXMLClick(File xmlFile){
    	
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
            Element goalLeft = (Element)data.item(3);
            Element goalRight = (Element)data.item(4);
            
            ball = new Point(	Integer.parseInt(ballE.getAttribute("x")),
            						Integer.parseInt(ballE.getAttribute("y")));
            
            left = Integer.parseInt(goalLeft.getAttribute("x"));
            right = Integer.parseInt(goalRight.getAttribute("x"));
            
            Node blueCent = blue.getFirstChild();
            Node blueBum = blue.getLastChild();
            
            Node yellowCent = yellow.getFirstChild();
            Node yellowBum = yellow.getLastChild();
            
            Element blueE = (Element)blueCent;
            Element blueET = (Element)blueBum;
            Element yellowE = (Element)yellowCent;
            Element yellowET = (Element)yellowBum;
            
            blueCentroidAuto = new Point(Integer.parseInt(blueE.getAttribute("x")), Integer.parseInt(blueE.getAttribute("y")));
            yellowCentroidAuto = new Point(Integer.parseInt(yellowE.getAttribute("x")), Integer.parseInt(yellowE.getAttribute("y")));
            
            blueBottom = new Point(Integer.parseInt(blueET.getAttribute("x")), Integer.parseInt(blueET.getAttribute("y")));
            yellowBottom = new Point(Integer.parseInt(yellowET.getAttribute("x")), Integer.parseInt(yellowET.getAttribute("y")));


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
    
    public static void readXMLAuto(File xmlFile){
    	try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(xmlFile);

            // normalize text representation
            doc.getDocumentElement().normalize();
            System.out.println("Root element of the doc is " + doc.getDocumentElement().getNodeName());
            
            Node root = doc.getDocumentElement(); 
            NodeList data = root.getChildNodes();
            Node blue = data.item(0);
            Node yellow = data.item(1);
            Element ballE = (Element)data.item(2);
            Element goalLeft = (Element)data.item(3);
            Element goalRight = (Element)data.item(4);
            
            ballAuto = new Point(	Integer.parseInt(ballE.getAttribute("x")),
            						Integer.parseInt(ballE.getAttribute("y")));
            
            leftAuto = Integer.parseInt(goalLeft.getAttribute("x"));
            rightAuto = Integer.parseInt(goalRight.getAttribute("x"));
            
            Node blueCent = blue.getFirstChild();
            Node blueBum = blue.getLastChild();
            
            Node yellowCent = yellow.getFirstChild();
            Node yellowBum = yellow.getLastChild();
            
            Element blueE = (Element)blueCent;
            Element blueET = (Element)blueBum;
            Element yellowE = (Element)yellowCent;
            Element yellowET = (Element)yellowBum;
            
            blueCentroidAuto = new Point(Integer.parseInt(blueE.getAttribute("x")), Integer.parseInt(blueE.getAttribute("y")));
            yellowCentroidAuto = new Point(Integer.parseInt(yellowE.getAttribute("x")), Integer.parseInt(yellowE.getAttribute("y")));
            
            blueAngle = Double.parseDouble(blueET.getAttribute("rads"));
            yellowAngle = Double.parseDouble(yellowET.getAttribute("rads"));


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
