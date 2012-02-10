package uk.ac.ed.inf.sdp2012.group7.strategy.astar;

import java.awt.Point;
import java.util.ArrayList;

public class AStarRunTest {

		public static void main(String[] args) {
			
			// set start and end points of the path
			Point start = new Point(24,28);
			Point end = new Point(12,28);
			
			// set obstacles
			ArrayList<Point> obstacles = new ArrayList<Point>();
			obstacles.add(new Point(20,26));
			obstacles.add(new Point(20,27));
			obstacles.add(new Point(20,28));
			obstacles.add(new Point(20,29));
			obstacles.add(new Point(21,26));
			obstacles.add(new Point(21,27));
			obstacles.add(new Point(21,28));
			obstacles.add(new Point(21,29));
			obstacles.add(new Point(22,26));
			obstacles.add(new Point(22,27));
			obstacles.add(new Point(22,28));
			obstacles.add(new Point(22,29));
			obstacles.add(new Point(23,26));
			obstacles.add(new Point(23,27));
			obstacles.add(new Point(23,28));
			obstacles.add(new Point(23,29));
			
			// run the algorithm
			AStarRun run = new AStarRun(58,29, end, start, obstacles);
			Path path = run.getPath();
			path.printPath();
		}
}
