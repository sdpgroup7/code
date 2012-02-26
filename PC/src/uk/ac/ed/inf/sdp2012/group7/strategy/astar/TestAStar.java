package uk.ac.ed.inf.sdp2012.group7.strategy.astar;

import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.heuristics.ClosestHeuristic;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.utils.StopWatch;

public class TestAStar {
	
	private static int mapWidth = 50;
	private static int mapHeight = 25;
	
	private static int startX = 39;
	private static int startY = 13;
	private static int goalX = 27;
	private static int goalY = 14;
	
	
	public static void main(String[] args) {
		StopWatch s = new StopWatch();
		s.start();
		
		Strategy.logger.info("Map initializing...");
		AreaMap map = new AreaMap(mapWidth, mapHeight);
		
		Strategy.logger.info("Heuristic initializing...");
		AStarHeuristic heuristic = new ClosestHeuristic();
		
		Strategy.logger.info("Pathfinder initializing...");
		AStar pathFinder = new AStar(map, heuristic);
		
		Strategy.logger.info("Calculating shortest path...");
		pathFinder.calcShortestPath(startX, startY, goalX, goalY);
		
		s.stop();
		Strategy.logger.info("Time to calculate path in milliseconds: " + s.getElapsedTime());
		
		Strategy.logger.info("Printing map of shortest path...");
		pathFinder.printPath();
	}

}
