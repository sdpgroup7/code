package uk.ac.ed.inf.sdp2012.group7.strategy.newastar;


import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;


public class AStar {
	
	private int height, width;
	private Node start, target;
	private ArrayList<Node> balls, oppositions, closedList;
	private SortedNodeList openList;
	private Node[][] map;
	private ClosestHeuristic heuristic;
	private Node[] currentNode = new Node[1];
	
	public AStar(int height, int width, Node start, Node target, ArrayList<Node> balls, ArrayList<Node> oppositions) {
		
		this.height = height;
		this.width = width;
		this.start = start;
	    this.target = target;
		this.balls = balls;
		this.oppositions = oppositions;
		this.map = new Node[this.width][this.height];
		this.heuristic = new ClosestHeuristic();
		
		this.closedList = new ArrayList<Node>();
		this.openList = new SortedNodeList();
		
		this.closedList.clear();
		this.openList.clear();
		
		//put in ball
		for(Node n : balls){
			
			this.map[n.x][n.y] = n;
			
		}
		
		//put in opposition
		for(Node n : oppositions){
			
			this.map[n.x][n.y] = n;
			
		}
		
		this.map[this.target.x][this.target.y] = this.target;
		this.map[this.target.x][this.target.y].setTarget(true);
		this.map[this.target.x][this.target.y].sethCost(0);
	}
	
	public ArrayList<Node> nearestNeighbours(Node current){
		
		ArrayList<Node> nearestNeighbours = new ArrayList<Node>();
		
		for(int i = current.x -2; i < current.x + 2; i++){
			for(int j = current.y -2; j < current.y +2; j++){
				if(!((i < 0) || (i > this.width) || (j < 0) || (j > this.height))){
					if(!(current == this.map[i][j])){

						this.map[i][j] = new Node ( new Point(i,j), 0);
						this.map[i][j].sethCost(heuristic.getEstimatedDistanceToGoal(this.map[i][j], this.target));
						this.map[i][j].setgCost(heuristic.getEstimatedDistanceToGoal(this.map[i][j], current));
						this.map[i][j].setfCost();
						nearestNeighbours.add(this.map[i][j]);
					}
				}
			}
		}
		
		return nearestNeighbours; 
		
	}
	
	public ArrayList<Node> returnPath(){
		
		
		
		this.map[this.start.x][this.start.y] = this.start;
		this.map[this.start.x][this.start.y].sethCost(
				heuristic.getEstimatedDistanceToGoal
				(this.map[this.start.x][this.start.y], this.target) * 100);
		
		this.map[this.start.x][this.start.y].setgCost(900);
		this.map[this.start.x][this.start.y].setfCost();
		
		this.openList.add(this.map[start.x][start.y]);
		this.map[start.x][start.y].setStart(true);
		
		currentNode[0] = this.map[start.x][start.y];
		
		boolean hasBeenFound = false;
		
		while(!hasBeenFound){
			
			for(Node n : this.openList.getList()){
				System.out.println("fCost evaluation : " + n.getfCost() + " to " + currentNode[0].getfCost());
				if(currentNode[0].getfCost() > n.getfCost()){
					
					currentNode[0] = n;
				}
			}
			
			System.out.println("Next currentNode is " + currentNode[0].x() + " " + currentNode[0].y() + " " + currentNode[0].getfCost());
			
			ArrayList<Node> nearestNeighbours = nearestNeighbours(currentNode[0]);
			
			System.out.println("Number of neighbours : " + nearestNeighbours.size());
			System.out.println("Number in openList @ start : " + openList.size());
			
			this.closedList.add(currentNode[0]);
			this.openList.remove(currentNode[0]);
			
			for(Node n : nearestNeighbours){
				if(!(this.closedList.contains(n))){
					System.out.println("Not in closedList : " + n.x() + " " + n.y());
					if(!(this.openList.contains(n))){
						System.out.println("Not in openList : " + n.x() + " " + n.y());
						n.setParent(currentNode[0]);
						n.setgCost(heuristic.getEstimatedDistanceToGoal(n, currentNode[0])*100 + n.getParent().getgCost());
						n.sethCost(heuristic.getEstimatedDistanceToGoal(n, this.target));
						n.setfCost();
						this.openList.add(n);
					}else {
						if(n.getParent() == null){
							n.setParent(currentNode[0]);
						}
						System.out.println("Is in openList : " + n.x() + " " + n.y());
						double old = n.getfCost();
						n.setgCost(heuristic.getEstimatedDistanceToGoal(n, currentNode[0])*100 + n.getParent().getgCost());
						double test = n.getgCost();
						if(old > test){
							n.setParent(currentNode[0]);
							n.setfCost();
						} else {
							n.setgCost(heuristic.getEstimatedDistanceToGoal(n, n.getParent()));
						}
					}
				}
			}
			
			System.out.println("Number in openList @ finish : " + openList.size());
			
			if(closedList.contains(this.target)){
				System.out.println("Found");
				hasBeenFound = true;
			}
			
			if(openList.getList().size() < 1){
				System.out.println("Too small");
				hasBeenFound = true;
			}
			
		}
		
		return closedList;
	}
	
	
	
}
