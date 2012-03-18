package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.strategy.astar.Node;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;


public class PlanMonitor {
	
	private Plan currentPlan;
	private WorldState worldState = WorldState.getInstance();
	private double nodeWidthInPixels;
	private double nodeHeightInPixels;
	public static final Logger logger = Logger.getLogger(PlanMonitor.class);
	
	public PlanMonitor(Plan plan){
		currentPlan = plan;
		nodeWidthInPixels = plan.getNodeWidthInPixels();
		nodeHeightInPixels = plan.getNodeHeightInPixels();
	}
	
	public PlanMonitor(){
		
	}

	public void setPlan(Plan plan){
		currentPlan = plan;
		nodeWidthInPixels = plan.getNodeWidthInPixels();
		nodeHeightInPixels = plan.getNodeHeightInPixels();
	}
	
	public void savePlan(){
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter("plan.txt",true));
			out.write(generateASCIIPlan().toString());
		} catch (IOException ex) { 
			logger.error("Could not write to file: " + ex.getMessage());
		} finally {
			if(out != null){
				try{
					out.close();
				} catch (Exception ex){
					logger.error("Error in closing buffered writer: " + ex.getMessage());
				}
			}
		}
	}
	
	public void outputPlan(){
		long start = System.currentTimeMillis();
		String[][] plan = generateASCIIPlan();
		logger.info(plan.toString());
		generateImage(plan);
		long timed = System.currentTimeMillis() - start;
		logger.info("Time to generate plan render: " + timed + "ms");
	}
	
	public String[][] generateASCIIPlan(){
		//OldAreaMap map = currentPlan.getAStar().getAreaMap();
		int height = currentPlan.getHeightInNodes();
		int width = currentPlan.getMapWidth();

		
		if(height <= 0) return new String[0][0];
		String[][] ascii = new String[height][width];
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
				Node n = currentPlan.getMap()[x][y];
				if(n != null){
					if(n.isBall()) ascii[y][x] = "B";
					if(n.isOpposition()) ascii[y][x] = "O";
					if(n.isStart()) ascii[y][x] = "S";
					if(n.isPath()) ascii[y][x] = "#";
					if(currentPlan.getNavPoint().equals(new Point(x,y))) ascii[y][x] = "N";
					if(currentPlan.getTarget().equals(new Point(x,y))) ascii[y][x] = "G";		
					if(currentPlan.getBallPosition().equals(new Point(x,y))) ascii[y][x] = "B";
				}
				if(currentPlan.getAllStaticObjects().getCentreOfTheirGoal().equals(new Point(x,y))) ascii[y][x] = "C";
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
			logger.error("Error saving image: " + ex.getMessage());
		}
	}
	
	public BufferedImage generateImage(String[][] text){
		int width = worldState.getPitch().getWidthInPixels();
		int height = worldState.getPitch().getHeightInPixels();
		BufferedImage im = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		im = generateOverlay(text,im);
		worldState.setOverlay(im);
		return im;
	}
	
		
    public BufferedImage generateOverlay(String[][] ascii, BufferedImage image){
    	Graphics graphics = image.getGraphics();
        graphics.setColor(Color.white);
        graphics.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));
        for(int y = 0; y < ascii.length; y++){
        	for(int x = 0; x < ascii[y].length; x++){
        		if(ascii[y][x] != " "){
        			int xp = (int)(x*nodeWidthInPixels + (nodeWidthInPixels / 4) + 0.5);
        			int yp = (int)(y*nodeHeightInPixels + (nodeHeightInPixels / 4) + 0.5);
        			int width = (int)((nodeWidthInPixels/2)+0.5);
        			int height = (int)((nodeHeightInPixels/2)+0.5);
        			if(ascii[y][x] == "B"){
        				graphics.setColor(Color.red);
        			} else if(ascii[y][x] == "O"){
        				if(worldState.getColor().equals(Color.blue)){
        					graphics.setColor(Color.yellow);
        				} else {
        					graphics.setColor(Color.blue);
        				}
        			} else if(ascii[y][x] == "S"){
        				graphics.setColor(Color.black);
        			} else if(ascii[y][x] == "#"){
        				graphics.setColor(Color.white);
        			} else if(ascii[y][x] == "G"){
        				graphics.setColor(Color.green);
        			} else {
        				graphics.setColor(Color.white);
        			}
        			graphics.fillRect(xp, yp, width, height);
        		}
        	}
        }
        return image;
    }
	
	
}
