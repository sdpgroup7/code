package uk.ac.ed.inf.sdp2012.group7.strategy.astar;

import java.awt.Point;
import java.util.ArrayList;

import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.heuristics.ClosestHeuristic;

public class AStarRun {
		
		private Path shortestPath;
		private AreaMap map;
		
		public AreaMap getAreaMap() {
			return map;
		}

		public AStarRun(int pitch_height_in_nodes, int pitch_width_in_nodes, Point ball, Point some_robot, ArrayList<Point> obstacles) {
			map = new AreaMap(pitch_width_in_nodes, pitch_height_in_nodes);
			
			// set obstacles
			for (Point obstacle : obstacles) {
				map.setObstical(obstacle.x, obstacle.y, true);
			}
			
			// set heuristic and run the path finder
			AStarHeuristic heuristic = new ClosestHeuristic();
			AStar pathFinder = new AStar(map, heuristic);
			try{
				shortestPath = pathFinder.calcShortestPath(some_robot.x, some_robot.y, ball.x, ball.y);
			} catch (Exception ex) {
				Strategy.logger.error("Shortest path calculation failed: " + ex.getMessage());
				shortestPath = new Path();
			}
			// copied from A* for printing
//			Node node;
//			for(int x=0; x<map.getMapWidth(); x++) {
//				for(int y=0; y<map.getMapHeight(); y++) {
//					node = map.getNode(x, y);
//					//System.out.println(node.getX());
//					//System.out.println(node.getY());
//					if (node.isObstacle) {
//						System.out.print("O");
//					} else if (node.isStart) {
//						System.out.print("R");
//					} else if (node.isGoal()) {
//						System.out.print("B");
//					} else if (shortestPath.contains(node.getX(), node.getY())) {
//						System.out.print("X");
//					} else {
//						System.out.print("*");
//					}
//				}
//				System.out.println();
//			}
		}
		
		public Path getPath() {
			try{
				return shortestPath;
			} catch (Exception ex) {
				Strategy.logger.error("getPath return failed: " + ex.getMessage());
				return shortestPath;
			}
		}
		
		public ArrayList<Point> getPathInPoints() {
			try {
				return this.shortestPath.pathToPoints();
			} catch (Exception ex) {
				Strategy.logger.error("getPathInPoints return failed: " + ex.getMessage());
				return this.shortestPath.pathToPoints();
			}
		}
}
