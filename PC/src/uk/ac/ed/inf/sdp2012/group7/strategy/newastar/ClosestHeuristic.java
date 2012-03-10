package astar;

import java.awt.Point;

public class ClosestHeuristic implements AStarHeuristic {

	@Override
	public double getEstimatedDistanceToGoal(Node a, Node b) {
		return Point.distance(a.x, a.y, b.x, b.y);
	}


}
