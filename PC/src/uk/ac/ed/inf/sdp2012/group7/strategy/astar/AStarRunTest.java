package uk.ac.ed.inf.sdp2012.group7.strategy.planning.astar;

import java.awt.Point;

public class AStarRunTest {

		public static void main(String[] args) {
			Point start = new Point(27,10);
			Point end = new Point(5,56);
			AStarRun run = new AStarRun(58,29, end, start, null);
			Path path = run.getPath();
			path.printPath();
		}
}
