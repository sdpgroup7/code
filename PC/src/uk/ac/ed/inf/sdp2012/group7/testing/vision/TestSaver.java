package uk.ac.ed.inf.sdp2012.group7.testing.vision;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import uk.ac.ed.inf.sdp2012.group7.vision.Vision;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

public class TestSaver {
	
	private WorldState worldState = WorldState.getInstance();
	
	public void writeClickPoints(ArrayList<Point> points, BufferedImage image,String filename){
		Vision.logger.info("Starting XML generation");
        try{
        	//TODO: verify angles
        	Point[] pts = new Point[points.size()];
        	points.toArray(pts);

        	
        	//blueO = worldState.getBlueRobot().getAngle();
        	//yellowO = worldState.getYellowRobot().getAngle();

        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        	DocumentBuilder docBuilder = factory.newDocumentBuilder();
        	Document doc = docBuilder.newDocument();
        	Element root = doc.createElement("data");
        	root.setAttribute("location", filename + ".png");
        	doc.appendChild(root);
        	Element childElement = doc.createElement("blue");
        	Element centroid = doc.createElement("centroid");
        	centroid.setAttribute("x", Integer.toString(pts[2].x));
        	centroid.setAttribute("y", Integer.toString(pts[2].y));
        	childElement.appendChild(centroid);
        	Element blueBottom = doc.createElement("blueBottom");
        	blueBottom.setAttribute("x", Integer.toString(pts[3].x));
        	blueBottom.setAttribute("y", Integer.toString(pts[3].y));
        	childElement.appendChild(blueBottom);
        	root.appendChild(childElement);
        	
        	childElement = doc.createElement("yellow");
        	Element centroidY = doc.createElement("centroid");
        	centroidY.setAttribute("x", Integer.toString(pts[0].x));
        	centroidY.setAttribute("y", Integer.toString(pts[0].y));
        	childElement.appendChild(centroidY);
        	Element yBottom = doc.createElement("yellowBottom");
        	yBottom.setAttribute("x", Integer.toString(pts[1].x));
        	yBottom.setAttribute("y", Integer.toString(pts[1].y));
        	childElement.appendChild(yBottom);
        	root.appendChild(childElement);
        	
        	childElement = doc.createElement("ball");
        	childElement.setAttribute("x",Integer.toString(pts[4].x));
        	childElement.setAttribute("y",Integer.toString(pts[4].y));
        	root.appendChild(childElement);
        	
        	childElement = doc.createElement("leftGoal");
        	childElement.setAttribute("x", Integer.toString(pts[5].x));
        	root.appendChild(childElement);
        	
        	childElement = doc.createElement("rightGoal");
        	childElement.setAttribute("x", Integer.toString(pts[6].x));
        	root.appendChild(childElement);
        	
        	TransformerFactory tranFactory = TransformerFactory.newInstance(); 
        	Transformer aTransformer = tranFactory.newTransformer(); 

        	Source src = new DOMSource(doc); 
        	Result dest = new StreamResult(new File(filename + ".xml")); 
        	aTransformer.transform(src, dest); 
        	Vision.logger.info("XML creation complete.");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
	
	public void writeAutoPoints(ArrayList<Point> autoPoints, ArrayList<Double> angles, ArrayList<Integer> pitch, BufferedImage image,String filename){
		Vision.logger.info("Starting XML generation");
        try{
        	//TODO: verify angles
        	Point[] pts = new Point[autoPoints.size()];
        	autoPoints.toArray(pts);
        	Double[] ang = new Double[angles.size()];
        	angles.toArray(ang);
        	Integer[] pch = new Integer[pitch.size()];
        	pitch.toArray(pch);
        	
        	Point blueCentroid;
        	Point yellCentroid;
        	double blueAngle;
        	double yellAngle;
        	
        	if (worldState.getColor() == Color.blue) {
        		blueCentroid = pts[1];
        		yellCentroid = pts[2];
        		blueAngle = ang[0];
        		yellAngle = ang[1];
        	} else {
        		blueCentroid = pts[2];
        		yellCentroid = pts[1];
        		blueAngle = ang[1];
        		yellAngle = ang[0];
        	}

        	
        	//blueO = worldState.getBlueRobot().getAngle();
        	//yellowO = worldState.getYellowRobot().getAngle();

        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        	DocumentBuilder docBuilder = factory.newDocumentBuilder();
        	Document doc = docBuilder.newDocument();
        	Element root = doc.createElement("data");
        	root.setAttribute("location", filename+"auto" + ".png");
        	doc.appendChild(root);
        	Element childElement = doc.createElement("blue");
        	Element centroid = doc.createElement("centroid");
        	centroid.setAttribute("x", Integer.toString(blueCentroid.x));
        	centroid.setAttribute("y", Integer.toString(blueCentroid.y));
        	childElement.appendChild(centroid);
        	Element blueBottom = doc.createElement("blueAngle");
        	blueBottom.setAttribute("rads", Double.toString(blueAngle));
        	childElement.appendChild(blueBottom);
        	root.appendChild(childElement);
        	
        	childElement = doc.createElement("yellow");
        	Element centroidY = doc.createElement("centroid");
        	centroidY.setAttribute("x", Integer.toString(yellCentroid.x));
        	centroidY.setAttribute("y", Integer.toString(yellCentroid.y));
        	childElement.appendChild(centroidY);
        	Element yBottom = doc.createElement("yellowAngle");
        	yBottom.setAttribute("rads", Double.toString(yellAngle));
        	childElement.appendChild(yBottom);
        	root.appendChild(childElement);
        	
        	childElement = doc.createElement("ball");
        	childElement.setAttribute("x",Integer.toString(pts[0].x));
        	childElement.setAttribute("y",Integer.toString(pts[0].y));
        	root.appendChild(childElement);
        	
        	childElement = doc.createElement("leftGoal");
        	childElement.setAttribute("x", Integer.toString(pch[0]));
        	root.appendChild(childElement);
        	
        	childElement = doc.createElement("rightGoal");
        	childElement.setAttribute("x", Integer.toString(pch[1]));
        	root.appendChild(childElement);
        	
        	TransformerFactory tranFactory = TransformerFactory.newInstance(); 
        	Transformer aTransformer = tranFactory.newTransformer(); 

        	Source src = new DOMSource(doc); 
        	Result dest = new StreamResult(new File(filename + "auto" + ".xml")); 
        	aTransformer.transform(src, dest); 
        	Vision.logger.info("XML creation complete.");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
