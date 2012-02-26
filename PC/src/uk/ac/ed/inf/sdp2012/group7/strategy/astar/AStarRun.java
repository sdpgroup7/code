package uk.ac.ed.inf.sdp2012.group7.strategy.astar;

import java.awt.Point;
import java.util.ArrayList;

import uk.ac.ed.inf.sdp2012.group7.strategy.astar.heuristics.ClosestHeuristic;

public class AStarRun {
		
		private Path shortestPath;
		
		public AStarRun(int golf_balls_height, int golf_balls_width, Point ball, Point some_robot, ArrayList<Point> obstacles) {
			AreaMap map = new AreaMap(golf_balls_width, golf_balls_height);
			
			// set obstacles
			for (Point obstacle : obstacles) {
				map.setObstical(obstacle.x, obstacle.y, true);
			}
			
			// set heuristic and run the path finder
			AStarHeuristic heuristic = new ClosestHeuristic();
			AStar pathFinder = new AStar(map, heuristic);
			shortestPath = pathFinder.calcShortestPath(some_robot.x, some_robot.y, ball.x, ball.y);
			
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
			return shortestPath;
		}
		
		public ArrayList<Point> getPathInPoints() {
			return this.shortestPath.pathToPoints();
		}
}
