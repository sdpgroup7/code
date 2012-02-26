package uk.ac.ed.inf.sdp2012.group7.strategy.astar;

import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.heuristics.ClosestHeuristic;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.utils.StopWatch;

public class TestAStar {
	
	private static int mapWidth = 29;
	private static int mapHeight = 58;
	
	private static int startX = 27;
	private static int startY = 10;
	private static int goalX = 5;
	private static int goalY = 56;
	
	
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
