package uk.ac.ed.inf.sdp2012.group7.strategy.astar;

public interface AStarHeuristic {

	/**
	 * 
	 * The heuristic tries to guess how far a given Node is from the goal Node.
	 * The lower the cost, the more likely a Node will be searched next. 
	 * 
	 * @param map The map on which the path is being found
	 * @param x The x coordinate of the tile being evaluated
	 * @param y The y coordinate of the tile being evaluated
	 * @param tx The x coordinate of the target location
	 * @param ty The y coordinate of the target location
	 * @return The cost associated with the given tile
	 */
	public float getEstimatedDistanceToGoal(int startX, int startY, int goalX, int goalY);
}
