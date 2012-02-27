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
import java.util.ArrayList;

import javax.imageio.ImageIO;

import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.AreaMap;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.Node;

public class PlanMonitor {
	
	private Plan currentPlan;
	
	public PlanMonitor(Plan plan){
		currentPlan = plan;
	}
	
	public PlanMonitor(){
		this(null);
	}

	public void setPlan(Plan plan){
		currentPlan = plan;
	}
	
	public void savePlan(){
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter("plan.txt",true));
			out.write(generateASCIIPlan());
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
		String plan = generateASCIIPlan();
		System.out.println(plan);
		saveImage(plan);
	}
	
	public String generateASCIIPlan(){
		String output = "";
		AreaMap map = currentPlan.getAStar().getAreaMap();
		ArrayList<Node> waypoints = currentPlan.getAStar().getPath().getWayPoints();
		if(map.getNodes().length <= 0) return "";
		
		String[][] ascii = new String[map.getMapHeight()][map.getMapWidth()];
		for(int y = 0; y < ascii.length; y++){
			for(int x = 0; x < ascii[0].length; x++){
				ascii[y][x] = ".";
			}
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
				if(n.isGoal()) ascii[y][x] = "T";
				if(n.isObstical()) ascii[y][x] = "X";
				if(n.isObstical()) ascii[y][x] = "X";
				if(n.isStart()) ascii[y][x] = "S";
				if(n.isVisited()) ascii[y][x] = " ";
			}
		}
		for(int y = 0; y < ascii.length; y++){
			for(int x = 0; x < ascii[y].length; x++){
				output += ascii[y][x] + " ";
			}
			output += "\n";
		}
		return output;
	}
	
	public void saveImage(String text){
		try {
		    BufferedImage bi = generateImage(text);
		    File outputfile = new File("planoutput.png");
		    ImageIO.write(bi, "png", outputfile);
		} catch (IOException ex) {
			Strategy.logger.error("Error saving image: " + ex.getMessage());
		}
	}
	
	public BufferedImage generateImage(String text){
		BufferedImage im = new BufferedImage(900,620,BufferedImage.TYPE_INT_ARGB);
		textOverlay(text,im);
		BufferedImage returnImage = resize(im);
		return returnImage;
	}
	
	public BufferedImage resize(BufferedImage im){
		int width = 0;
		int height = 0;
		int left = Integer.MAX_VALUE;
		int right = 0;
		int top = Integer.MAX_VALUE;
		int bottom = 0;
		for(int i = 0; i < im.getWidth(); i++){
			for(int j = 0; j < im.getHeight(); j++){
				if(im.getRGB(i, j) == Color.white.getRGB()){
					if(i < left) left = i;
					if(i > right) right = i;
					if(j < top) top = j;
					if(j > bottom) bottom = j;
				}
			}
		}
		width = right - left;
		height = bottom - top;
		
		BufferedImage returnImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		for(int i = left; i < right; i++){
			for(int j = top; j < bottom; j++){
				if(im.getRGB(i,j) == Color.white.getRGB()){
					returnImage.setRGB(i-left, j-top, Color.white.getRGB());
				}
			}
		}
		
		return returnImage;
	}
	
	
    public void textOverlay(String text, BufferedImage image){
    	Graphics frameGraphics = image.getGraphics();
        frameGraphics.setColor(Color.white);
        frameGraphics.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));
        int vShift = 20;
        int lineCount = 0;
        for(String s : text.split("\n")){
        	lineCount++;
        	frameGraphics.drawString(s, 15, vShift*lineCount);
        }
        frameGraphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
    }
	
	
}
