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
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.AreaMap;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.Node;
import uk.ac.ed.inf.sdp2012.group7.vision.VisionTools;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;
import uk.ac.ed.inf.sdp2012.group7.strategy.Arc;
import uk.ac.ed.inf.sdp2012.group7.strategy.ControlInterface;


public class PlanMonitor {
	
	private Plan currentPlan;
	private WorldState worldState = WorldState.getInstance();
	
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
		long start = System.currentTimeMillis();
		String plan = generateASCIIPlan();
		System.out.println(plan);
		generateImage(plan);
		long timed = System.currentTimeMillis() - start;
		Strategy.logger.info("Time to generate plan render: " + timed + "ms");
	}
	
	public String generateASCIIPlan(){
		String output = "";
		AreaMap map = currentPlan.getAStar().getAreaMap();
		ArrayList<Node> waypoints = null;
		try{
			waypoints = currentPlan.getAStar().getPath().getWayPoints();
		} catch (Exception ex){
			return "";
		}
		if(map.getNodes().length <= 0) return "";
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
				if(n.isGoal()) ascii[y][x] = "T";
				if(n.isObstical()) ascii[y][x] = "X";
				if(n.isObstical()) ascii[y][x] = "X";
				if(currentPlan.getAllStaticObjects().getCentreOfTheirGoal().equals(new Point(x,y))) ascii[y][x] = "C";
				if(n.isStart()) ascii[y][x] = "S";
				if(n.isVisited()) ascii[y][x] = " ";
				if(currentPlan.getTarget().equals(new Point(x,y))) ascii[y][x] = "G";
				if(currentPlan.getNavPoint().equals(new Point(x,y))) ascii[y][x] = "N";
				if(currentPlan.getBallPosition().equals(new Point(x,y))) ascii[y][x] = "B";
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
		returnImage = markData(returnImage);
		worldState.setOverlay(returnImage);
		return returnImage;
	}
	
	public BufferedImage markData(BufferedImage im){
		Graphics2D graphics = im.createGraphics();
		//ControlInterface ci = new ControlInterface(5);
		Arc arc = ControlInterface.chooseArc(currentPlan);
		double radius = arc.getRadius();
		double angle = currentPlan.getOurRobotAngle();
		if(arc.isLeft()) {
			angle = angle + (Math.PI * 2) - (Math.PI/2);
		} else {
			angle = angle + Math.PI/2;
		}
		angle = angle % (Math.PI*2);
		double retAngle = (Math.PI + angle) % (Math.PI * 2);
		int x = (int)(currentPlan.getNodeInPixels()*currentPlan.getOurRobotPosition().x + (currentPlan.getNodeInPixels()*radius*Math.cos(angle)));
		int y = (int)(currentPlan.getNodeInPixels()*currentPlan.getOurRobotPosition().y + (currentPlan.getNodeInPixels()*radius*Math.sin(angle)));
		
		Strategy.logger.info("Arc info: " + String.format("%d,%d,%d,%d,%d,%d",x, y, 
				(int)radius, (int)radius, 
				(int)Math.toDegrees(VisionTools.convertAngleBack(retAngle)), 
				(int)Math.toDegrees(VisionTools.convertAngleBack(Math.PI))));
		
		graphics.drawArc(	x, y, 
							(int)radius, (int)radius, 
							Math.abs((int)Math.toDegrees(VisionTools.convertAngleBack(retAngle))), 
							Math.abs((int)Math.toDegrees(VisionTools.convertAngleBack(Math.PI))));
		
		graphics.drawOval(	(int)(	currentPlan.getNodeInPixels() * 
									currentPlan.getOurRobotPositionVisual().x)-15, 
							(int)(	currentPlan.getNodeInPixels() *
									currentPlan.getOurRobotPositionVisual().y)-15,
							30, 30);
		return im;
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
		
		BufferedImage croppedImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		for(int i = left; i < right; i++){
			for(int j = top; j < bottom; j++){
				if(im.getRGB(i,j) == Color.white.getRGB()){
					croppedImage.setRGB(i-left, j-top, Color.white.getRGB());
				}
			}
		}
		
		int bufferWidth = worldState.getPitch().getWidthInPixels();
		int bufferHeight = worldState.getPitch().getHeightInPixels();
		
		BufferedImage scaledImage = new BufferedImage(bufferWidth, bufferHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = scaledImage.createGraphics();
		AffineTransform xform = AffineTransform.getScaleInstance(
									(double)bufferWidth/(double)croppedImage.getWidth(),
									(double)bufferHeight/(double)croppedImage.getHeight());
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		graphics2D.drawImage(croppedImage, xform, null);
		graphics2D.dispose();

		return scaledImage;
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
