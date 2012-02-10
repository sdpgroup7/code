package uk.ac.ed.inf.sdp2012.group7.strategy.planning.astar;

import java.awt.Point;
import java.util.ArrayList;

import uk.ac.ed.inf.sdp2012.group7.strategy.planning.astar.heuristics.ClosestHeuristic;

public class AStarRun {
		
		private Path shortestPath;
		
		AStarRun(int golf_balls_height, int golf_balls_width, Point ball, Point some_robot, ArrayList<Point> obstacles) {
			AreaMap map = new AreaMap(golf_balls_width, golf_balls_height);
			AStarHeuristic heuristic = new ClosestHeuristic();
			AStar pathFinder = new AStar(map, heuristic);
			shortestPath = pathFinder.calcShortestPath(some_robot.x, some_robot.y, ball.x, ball.y);
		}

		public Path getPath() {
			return shortestPath;
		}
}
