package uk.ac.ed.inf.sdp2012.group7.strategy.oldastar;

import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.heuristics.ClosestHeuristic;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.utils.StopWatch;

public class OldTestAStar {
	
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
		OldAreaMap map = new OldAreaMap(mapWidth, mapHeight);
		
		Strategy.logger.info("Heuristic initializing...");
		OldAStarHeuristic heuristic = new ClosestHeuristic();
		
		Strategy.logger.info("Pathfinder initializing...");
		OldAStar pathFinder = new OldAStar(map, heuristic);
		
		Strategy.logger.info("Calculating shortest path...");
		pathFinder.calcShortestPath(startX, startY, goalX, goalY);
		
		s.stop();
		Strategy.logger.info("Time to calculate path in milliseconds: " + s.getElapsedTime());
		
		Strategy.logger.info("Printing map of shortest path...");
		pathFinder.printPath();
	}

}
