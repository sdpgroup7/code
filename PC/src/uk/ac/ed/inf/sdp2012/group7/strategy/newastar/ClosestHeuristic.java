package astar;

import java.awt.Point;

public class ClosestHeuristic implements AStarHeuristic {

	@Override
	public double getEstimatedDistanceToGoal(Node a, Node b) {
		return a.getHere().distance(b.getHere());
	}


}
