package uk.ac.ed.inf.sdp2012.group7.strategy.astar;

import java.awt.Point;

public class Node extends Point implements Comparable<Node> {
	
	private static final long serialVersionUID = -1084579547399653262L;
	
	// properties of this node
	AreaMap map;
	boolean visited;
	float distanceFromStart;
	float heuristicDistanceFromGoal;
	Node previousNode;
	boolean isObstacle;
	boolean isStart;
	private boolean isGoal;
	
	public Node(int x, int y) {
		this(x,y,false,java.lang.Float.MAX_VALUE,false,false,false);
	}
	
	public Node (int x, int y, boolean visited, float distanceFromStart, boolean isObstical, boolean isStart, boolean isGoal) {
		super(x, y);
		this.visited = visited;
		this.distanceFromStart = distanceFromStart;
		this.isObstacle = isObstical;
		this.isStart = isStart;
		this.isGoal = isGoal;
	}


	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public float getDistanceFromStart() {
		return distanceFromStart;
	}

	public void setDistanceFromStart(float f) {
		this.distanceFromStart = f;
	}

	public Node getPreviousNode() {
		return previousNode;
	}

	public void setPreviousNode(Node previousNode) {
		this.previousNode = previousNode;
	}
	
	public float getHeuristicDistanceFromGoal() {
		return heuristicDistanceFromGoal;
	}

	public void setHeuristicDistanceFromGoal(float heuristicDistanceFromGoal) {
		this.heuristicDistanceFromGoal = heuristicDistanceFromGoal;
	}

	public boolean isObstical() {
		return isObstacle;
	}

	public void setObstical(boolean isObstical) {
		this.isObstacle = isObstical;
	}

	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	public boolean isGoal() {
		return isGoal;
	}

	public void setGoal(boolean isGoal) {
		this.isGoal = isGoal;
	}

	public boolean equals(Node node) {
		return (node.x == x) && (node.y == y);
	}

	@Override
	public int compareTo(Node otherNode) {
		float thisTotalDistanceFromGoal = heuristicDistanceFromGoal + distanceFromStart;
		float otherTotalDistanceFromGoal = otherNode.getHeuristicDistanceFromGoal() + otherNode.getDistanceFromStart();
		
		// if positive, otherNode is chosen as it has a smaller total distance from goal
		return (int) (thisTotalDistanceFromGoal - otherTotalDistanceFromGoal);
	}
	
	public Point nodeToPoint() {
		Point point = new Point(this.x,this.y);
		return point;
	}
}