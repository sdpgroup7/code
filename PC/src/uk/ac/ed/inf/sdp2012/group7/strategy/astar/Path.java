package uk.ac.ed.inf.sdp2012.group7.strategy.planning.astar;

import java.util.ArrayList;

public class Path {
	// The waypoints in the path (list of coordinates making up the path)
	private ArrayList<Node> waypoints = new ArrayList<Node>();
	
	public Path() {
	}
	
	public int getLength() {
		return waypoints.size();
	}

	public Node getWayPoint(int index) {
		return waypoints.get(index);
	}

	/**
	 * Get the x-coordinate for the waypoiny at the given index.
	 * 
	 * @param index The index of the waypoint to get the x-coordinate of.
	 * @return The x coordinate at the waypoint.
	 */
	public int getX(int index) {
		return getWayPoint(index).getX();
	}

	/**
	 * Get the y-coordinate for the waypoint at the given index.
	 * 
	 * @param index The index of the waypoint to get the y-coordinate of.
	 * @return The y coordinate at the waypoint.
	 */
	public int getY(int index) {
		return getWayPoint(index).getY();
	}

	/**
	 * Append a waypoint to the path.  
	 * 
	 * @param x The x coordinate of the waypoint.
	 * @param y The y coordinate of the waypoint.
	 */
	public void appendWayPoint(Node n) {
		waypoints.add(n);
	}

	/**
	 * Add a waypoint to the beginning of the path.  
	 * 
	 * @param x The x coordinate of the waypoint.
	 * @param y The y coordinate of the waypoint.
	 */
	public void prependWayPoint(Node n) {
		waypoints.add(0, n);
	}

	/**
	 * Check if this path contains the WayPoint
	 * 
	 * @param x The x coordinate of the waypoint.
	 * @param y The y coordinate of the waypoint.
	 * @return True if the path contains the waypoint.
	 */
	public boolean contains(int x, int y) {
		for(Node node : waypoints) {
			if (node.getX() == x && node.getY() == y)
				return true;
		}
		return false;
	}
	
	// only needed if path is to be given as an arraylist of points
	/**
	public ArrayList<Point> pathToPoints() {
		ArrayList<Point> points = new ArrayList<Point>();
		for (Node node : waypoints) {
			Point point = node.nodeToPoint(node);
			points.add(point);
		}
		return points;
	} */
	
	public void printPath() {
		for(Node node : waypoints) {
			System.out.print("("+node.getX()+","+node.getY()+") ");
		}
	}

}
