package uk.ac.ed.inf.sdp2012.group7.testing.vision;

import java.io.File;
import javax.swing.*;
import java.awt.Point;
import java.util.ArrayList;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 

import org.w3c.dom.NodeList;

public class VisionTesting {

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
            NodeList data = root.getChildNodes();
            Node blue = data.item(0);
            Node yellow = data.item(1);
            Element ballE = (Element)data.item(2);
            
            ArrayList<Point> blueC = new ArrayList<Point>();
            ArrayList<Point> yellowC = new ArrayList<Point>();
            double yellowO = 0;
            double blueO = 0;
            Point ball = new Point(	Integer.parseInt(ballE.getAttribute("x")),
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
            

        }catch (SAXParseException err) {
        System.out.println ("** Parsing error" + ", line " 
             + err.getLineNumber () + ", uri " + err.getSystemId ());
        System.out.println(" " + err.getMessage ());

        }catch (SAXException e) {
        Exception x = e.getException ();
        ((x == null) ? e : x).printStackTrace ();

        }catch (Throwable t) {
        t.printStackTrace ();
        }
        //System.exit (0);

    }
    
}
