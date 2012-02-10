package uk.ac.ed.inf.sdp2012.group7.strategy.planning.astar;

import uk.ac.ed.inf.sdp2012.group7.strategy.planning.astar.heuristics.ClosestHeuristic;
import uk.ac.ed.inf.sdp2012.group7.strategy.planning.astar.utils.Logger;
import uk.ac.ed.inf.sdp2012.group7.strategy.planning.astar.utils.StopWatch;

public class TestAStar {
	
	private static int mapWidth = 29;
	private static int mapHeight = 58;
	
	private static int startX = 27;
	private static int startY = 10;
	private static int goalX = 5;
	private static int goalY = 56;
	
	
	public static void main(String[] args) {
		Logger log = new Logger();
		StopWatch s = new StopWatch();
		s.start();
		
		log.addToLog("Map initializing...");
		AreaMap map = new AreaMap(mapWidth, mapHeight);
		
		log.addToLog("Heuristic initializing...");
		AStarHeuristic heuristic = new ClosestHeuristic();
		
		log.addToLog("Pathfinder initializing...");
		AStar pathFinder = new AStar(map, heuristic);
		
		log.addToLog("Calculating shortest path...");
		pathFinder.calcShortestPath(startX, startY, goalX, goalY);
		
		s.stop();
		log.addToLog("Time to calculate path in milliseconds: " + s.getElapsedTime());
		
		log.addToLog("Printing map of shortest path...");
		pathFinder.printPath();
	}

}
