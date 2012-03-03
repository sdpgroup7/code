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

public class TestSaver {
	public void writePoints(ArrayList<Point> points, BufferedImage image,String filename){
		Vision.logger.info("Starting XML generation");
        try{
        	//TODO: verify angles
        	Point[] pts = new Point[points.size()];
        	points.toArray(pts);
        	double blueO = 0;
        	double yellowO = 0;
        	
        	//blueO = Vision.worldState.getBlueRobot().getAngle();
        	//yellowO = Vision.worldState.getYellowRobot().getAngle();

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
}
