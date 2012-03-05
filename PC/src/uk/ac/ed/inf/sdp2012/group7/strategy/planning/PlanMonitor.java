package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.AStarRun;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.AreaMap;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.Node;
import uk.ac.ed.inf.sdp2012.group7.vision.VisionTools;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;
//import uk.ac.ed.inf.sdp2012.group7.strategy.Arc;
//import uk.ac.ed.inf.sdp2012.group7.strategy.ControlInterface;


public class PlanMonitor {
	
	private Plan currentPlan;
	private WorldState worldState = WorldState.getInstance();
	private double nodeInPixels;
	private double nodeWidthInPixels;
	private double nodeHeightInPixels;
	
	public PlanMonitor(Plan plan){
		setPlan(plan);
	}
	
	public PlanMonitor(){
		
	}

	public void setPlan(Plan plan){
		currentPlan = plan;
		nodeInPixels = currentPlan.getAllStaticObjects().getNodeInPixels();
		nodeWidthInPixels = currentPlan.getAllStaticObjects().getNodeWidthInPixels();
		nodeHeightInPixels = currentPlan.getAllStaticObjects().getNodeHeightInPixels();
	}
	
	public void savePlan(){
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter("plan.txt",true));
			out.write(generateASCIIPlan().toString());
		} catch (IOException ex) { 
			Strategy.logger.error("Could not write to file: " + ex.getMessage());
		} finally {
			if(out != null){
				try{
					out.close();
				} catch (Exception ex){
					Strategy.logger.error("Error in closing buffered writer: " + ex.getMessage());
				}
			}
		}
	}
	
	public void outputPlan(){
		long start = System.currentTimeMillis();
		String[][] plan = generateASCIIPlan();
		System.out.println(array2DToString(plan));
		generateImage(plan);
		long timed = System.currentTimeMillis() - start;
		Strategy.logger.info("Time to generate plan render: " + timed + "ms");
	}
	
	public String array2DToString(String[][] a){
		String output = "";
		for(int i = 0; i < a.length; i++){
			for(int j = 0; j < a[i].length; j++){
				output += a[i][j];
			}
			output += "\n";
		}
		return output;
	}
	
	public String[][] generateASCIIPlan(){
		AreaMap map = currentPlan.getAStarRun().getAreaMap();
		ArrayList<Node> waypoints = null;
		try{
			waypoints = currentPlan.getAStarRun().getPath().getWayPoints();
		} catch (Exception ex){
			Strategy.logger.error("Waypoins in generateASCIIPlan is null");
			return new String[0][0];
		}
		if(map.getNodes().length <= 0) return new String[0][0];
		String[][] ascii = new String[map.getMapHeight()][map.getMapWidth()];
		for(int y = 0; y < ascii.length; y++){
			for(int x = 0; x < ascii[y].length;x++){
				ascii[y][x] = " ";
			}
		}
		for(int x = 0; x < ascii[0].length; x++){
			ascii[0][x] = ".";
		}
		for(int x = 0; x < ascii[0].length; x++){
			ascii[ascii.length-1][x] = ".";
		}
		for(int y = 0; y < ascii.length; y++){
			ascii[y][0] = ".";
		}
		for(int y = 0; y < ascii.length; y++){
			ascii[y][ascii[y].length - 1] = ".";
		}
				
		for(int y = 0; y < ascii.length; y++){
			for(int x = 0; x < ascii[y].length; x++){
				for(Node n : waypoints){
					if(n.nodeToPoint().equals(new Point(x,y))){
						ascii[y][x] = "#";
						continue;
					}
				}
				Node n = map.getNode(x,y);
				if(n.isObstical()) ascii[y][x] = "X";
				if(currentPlan.getAllStaticObjects().getCentreOfTheirGoal().equals(new Point(x,y))) ascii[y][x] = "C";
				if(n.isStart()) ascii[y][x] = "S";
				//if(currentPlan.getAStarRun().getPath().getWayPoint(currentPlan.getAStarRun().getPath().getLength()-1).equals(new Point(x,y))) ascii[y][x] = "N";
				if(currentPlan.getAllMovingObjects().getBallPosition().equals(new Point(x,y))) ascii[y][x] = "B";
			}
		}
		return ascii;
	}
	
	public void saveImage(String[][] text){
		try {
		    BufferedImage bi = generateImage(text);
		    File outputfile = new File("planoutput.png");
		    ImageIO.write(bi, "png", outputfile);
		} catch (IOException ex) {
			Strategy.logger.error("Error saving image: " + ex.getMessage());
		}
	}
	
	public BufferedImage generateImage(String[][] text){
		int width = worldState.getPitch().getWidthInPixels();
		int height = worldState.getPitch().getHeightInPixels();
        Strategy.logger.info("Buffer dimensions: " + Integer.toString(width) + "," + Integer.toString(height));
		BufferedImage im = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		im = generateOverlay(text,im);
		worldState.setOverlay(im);
		return im;
	}
	
		
    public BufferedImage generateOverlay(String[][] ascii, BufferedImage image){
    	Graphics graphics = image.getGraphics();
        graphics.setColor(Color.white);
        for(int y = 0; y < ascii.length; y++){
        	for(int x = 0; x < ascii[y].length; x++){
        		if(ascii[y][x] != " "){
                    graphics.setColor(Color.white);
        			int xp = (int)((double)x*nodeWidthInPixels + (nodeWidthInPixels / 4) + 0.5);
        			int yp = (int)((double)y*nodeHeightInPixels + (nodeHeightInPixels / 4) + 0.5);
        			int width = (int)((nodeWidthInPixels/2) + 0.5);
        			int height = (int)((nodeHeightInPixels/2) + 0.5);
        			if(ascii[y][x] == "X"){
        				graphics.setColor(new Color(100,100,100));
        			} else if(ascii[y][x] == "S"){
        				graphics.setColor(Color.blue);
        			} else if(ascii[y][x] == "C"){
                        graphics.setColor(Color.red);
                    } else if(ascii[y][x] == "B"){
                        graphics.setColor(Color.red);
                    }
        				
        				
        			graphics.fillRect(xp, yp, width, height);
        		}
        	}
        }
        //graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        return image;
    }
	
	
}
