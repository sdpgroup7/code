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
        	double blueO;
        	double yellowO;
        	
        	if(Vision.worldState.getColor() == Color.blue){
        		blueO = Vision.worldState.getOurRobot().getVelocity().getDirection();
        		yellowO = Vision.worldState.getOpponentsRobot().getVelocity().getDirection();
        	} else {
        		yellowO = Vision.worldState.getOurRobot().getVelocity().getDirection();
         		blueO = Vision.worldState.getOpponentsRobot().getVelocity().getDirection();
        	}
        	
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        	DocumentBuilder docBuilder = factory.newDocumentBuilder();
        	Document doc = docBuilder.newDocument();
        	Element root = doc.createElement("data");
        	root.setAttribute("location", filename + ".png");
        	doc.appendChild(root);
        	Element childElement = doc.createElement("blue");
        	childElement.setAttribute("orientation","" + blueO );
        	root.appendChild(childElement);
        	for(int i = 1;i<5;i++){
        		Element corner = doc.createElement("corner");
        		corner.setAttribute("vertex", Integer.toString(i-1));
        		corner.setAttribute("x", Integer.toString(pts[i].x));
        		corner.setAttribute("y", Integer.toString(pts[i].y));
        		childElement.appendChild(corner);
        	}
        	childElement = doc.createElement("yellow");
        	childElement.setAttribute("orientation","" + yellowO);
        	root.appendChild(childElement);
        	for(int i = 5;i<9;i++){
        		Element corner = doc.createElement("corner");
        		corner.setAttribute("vertex", Integer.toString(i-5));
        		corner.setAttribute("x", Integer.toString(pts[i].x));
        		corner.setAttribute("y", Integer.toString(pts[i].y));
        		childElement.appendChild(corner);
        	}
        	childElement = doc.createElement("ball");
        	childElement.setAttribute("x",Integer.toString(pts[0].x));
        	childElement.setAttribute("y",Integer.toString(pts[0].y));
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
