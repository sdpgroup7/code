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
			if (obstacles.size() > 0){
				for (Point obstacle : obstacles) {
					if(!(obstacle.x < 0 || obstacle.y < 0)){
						try{
							map.setObstical(obstacle.x, obstacle.y, true);
						} catch (Exception ex){
							//Do Nothing
						}
					}
				}
			}
			
			// set heuristic and run the path finder
			AStarHeuristic heuristic = new ClosestHeuristic();
			AStar pathFinder = new AStar(map, heuristic);
			try{
//				Strategy.logger.error("start x: " + some_robot.x);
//				Strategy.logger.error("start y: " + some_robot.y);
//				Strategy.logger.error("ball x: " + ball.x);
//				Strategy.logger.error("ball y: " + ball.y);
				shortestPath = pathFinder.calcShortestPath(some_robot.x, some_robot.y, ball.x, ball.y);
			} catch (Exception ex) {
				Strategy.logger.error("Shortest path calculation failed: " + ex.getMessage());
				ex.printStackTrace();
				shortestPath = new Path();
			}
			//pathFinder.printPath();
			}
		
		public Path getPath() {
			try{
				return shortestPath;
			} catch (Exception ex) {
				Strategy.logger.error("getPath return failed: " + ex.getMessage());
				return null;
			}
		}
		
}
