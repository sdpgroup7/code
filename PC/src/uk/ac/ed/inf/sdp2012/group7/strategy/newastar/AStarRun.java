package astar;


import java.util.ArrayList;



public class AStarRun {
	
	private int height, width;
	private Node start, target;
	private ArrayList<Node> balls, oppositions;

	public AStarRun(int height, int width, Node start, Node target, ArrayList<Node> balls, ArrayList<Node> oppositions) {
		this.height = height;
		this.width = width;
		this.start = start;
	    this.target = target;
		this.balls = balls;
		this.oppositions = oppositions;
	
	
		AStar astar = new AStar(this.height, this.width, this.start, this.target, this.balls, this.oppositions);
	
		ArrayList<Node> closedList = astar.returnPath();
	
		for(Node n : closedList){
			System.out.println("x value : " + n.x() + " y value : " + n.y() );
		}

	}
}
