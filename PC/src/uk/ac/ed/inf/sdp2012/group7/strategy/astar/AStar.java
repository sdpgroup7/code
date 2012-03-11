package uk.ac.ed.inf.sdp2012.group7.strategy.astar;



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
	private Node currentNode;
	
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
                    if(!((n.x < 0) || (n.x >= this.width) || (n.y < 0) || (n.y >= this.height))){
			this.map[n.x][n.y] = n;
                    }
		}
		
		//put in opposition
		for(Node n : oppositions){
                    if(!((n.x < 0) || (n.x >= this.width) || (n.y < 0) || (n.y >= this.height))){
			this.map[n.x][n.y] = n;
                    }
		}
                
                printMap(null);
		
		this.map[this.target.x][this.target.y] = this.target;
		this.map[this.target.x][this.target.y].setTarget(true);
		this.map[this.target.x][this.target.y].sethCost(0);
	}
	
	public ArrayList<Node> nearestNeighbours(Node current){
		
		ArrayList<Node> nearestNeighbours = new ArrayList<Node>();
		
		for(int i = current.x - 1; i <= current.x + 1; i++){
			for(int j = current.y - 1; j <= current.y + 1; j++){
				if(!((i < 0) || (i >= this.width) || (j < 0) || (j >= this.height))){
					if(!(current.equals(this.map[i][j]))){
						if(this.map[i][j] == null) this.map[i][j] = new Node(new Point(i,j), 0);
						this.map[i][j].sethCost(heuristic.getEstimatedDistanceToGoal(this.map[i][j], this.target));
						if(this.map[i][j].getgCost() == -1){
							this.map[i][j].setgCost(heuristic.getEstimatedDistanceToGoal(this.map[i][j], current) + current.getgCost());
						}
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
				(this.map[this.start.x][this.start.y], this.target));// * 100);
		
		this.map[this.start.x][this.start.y].setgCost(0);
		this.map[this.start.x][this.start.y].setfCost();
		
		this.openList.add(this.map[start.x][start.y]);
		this.map[start.x][start.y].setStart(true);
                this.start.setParent(start);
		
		currentNode = this.map[start.x][start.y];
		
		boolean hasBeenFound = false;
		
		while(!hasBeenFound){
                        if(openList.size() > 0) currentNode = openList.get(0);
			for(Node n : this.openList){
				if(n.getfCost() < currentNode.getfCost()){
					currentNode = n;
				}
			}
			
			
			ArrayList<Node> nearestNeighbours = nearestNeighbours(currentNode);
			
			this.closedList.add(currentNode);
			this.openList.remove(currentNode);
			
			for(Node n : nearestNeighbours){
				if(!(this.closedList.contains(n))){
					if(!(this.openList.contains(n))){
						n.setParent(currentNode);
						n.setgCost(heuristic.getEstimatedDistanceToGoal(n, currentNode) + n.getParent().getgCost());
						n.sethCost(heuristic.getEstimatedDistanceToGoal(n, this.target));
						n.setfCost();
						this.openList.add(n);
					} else {
						double oldCost = n.getgCost();
						n.setgCost(heuristic.getEstimatedDistanceToGoal(n, currentNode) + currentNode.getParent().getgCost());
						double newCost = n.getgCost();
						if(newCost < oldCost){
							n.setParent(currentNode);
							n.setfCost();
						} else {
							n.setgCost(oldCost);
							n.setfCost();
						}
					}
				}
			}
			
			
			if(closedList.contains(this.target)){
				hasBeenFound = true;
			}
			
			if(openList.size() < 1){
				hasBeenFound = true;
			}
                        printMap(closedList);
		}
		
                ArrayList<Node> returnPath = getPath(closedList);
                printMap(returnPath);
                return returnPath;
	}
	
        public ArrayList<Node> getPath(ArrayList<Node> closedList){
            if(closedList.size() > 0){
                ArrayList<Node> path = new ArrayList<Node>();
                Node currentNode = closedList.get(closedList.size() - 1);
                path.add(currentNode);
                while(true){
                    currentNode = currentNode.getParent();
                    this.map[currentNode.x][currentNode.y].setPath(true);
                    path.add(currentNode);
                    if(currentNode.isStart()) break;
                }
                return reversePath(path);
            } else {
                return new ArrayList<Node>();
            }
        }
        
        public ArrayList<Node> reversePath(ArrayList<Node> list){
            ArrayList<Node> path = new ArrayList<Node>();
            while(list.size() > 0){
                path.add(list.remove(list.size() - 1));
            }
            return path;
        }
	
        
        
        public void printMap(ArrayList<Node> path) {
            //Node node;
            if(path == null) path = new ArrayList<Node>();
            for(int y = 0; y < this.height; y++){
                for(int x = 0; x < this.width; x++){
                    Node n = map[x][y];
                    if(n != null){
                        if(path.contains(new Node(new Point(x,y), 0))){
                            System.out.print("X ");
                        }else if(n.isTarget()){
                            System.out.append("T ");
                        }else if(n.isBall()){
                            System.out.print("B ");
                        } else if (n.isOpposition()){
                            System.out.print("O ");
                        } else {
                            System.out.print("  ");
                        }
                    } else {
                        System.out.print("  ");
                    }
                }
                System.out.println();
            }
            
        }
	
        public Node[][] getMap(){
        	return this.map;
        }
		
}
