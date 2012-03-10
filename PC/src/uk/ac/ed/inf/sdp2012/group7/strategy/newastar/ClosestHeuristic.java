package uk.ac.ed.inf.sdp2012.group7.strategy.newastar;

import java.awt.Point;

public class ClosestHeuristic implements AStarHeuristic {

	@Override
	public double getEstimatedDistanceToGoal(Node a, Node b) {
		return a.getHere().distance(b.getHere());
	}


}
