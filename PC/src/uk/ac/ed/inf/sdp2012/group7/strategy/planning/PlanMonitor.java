package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
		System.out.println(generateASCIIPlan());
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
				if(n.getCost() == 1) ascii[y][x] = "O";
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
	
}
