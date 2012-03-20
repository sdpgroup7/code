package uk.ac.ed.inf.sdp2012.group7.strategy.astar;



import java.awt.Point;
import java.util.ArrayList;

import org.apache.log4j.Logger;



public class AStar {
	
	private int height, width;
	private Node start, target;
	private ArrayList<Node> balls, oppositions, closedList;
	private SortedNodeList openList;
	private Node[][] map;
	private ClosestHeuristic heuristic;
	private Node currentNode;
	public static final Logger logger = Logger.getLogger(AStar.class);
	
	public AStar(int height, int width, Node start, Node target, ArrayList<Node> balls, ArrayList<Node> oppositions) {
		
		this.height = height;// + 1;
		this.width = width;// + 1;
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
		for(Node n : this.balls){
                    if(!((n.x < 0) || (n.x >= this.width) || (n.y < 0) || (n.y >= this.height))){
                    	this.map[n.x][n.y] = n;
                    }
		}
		
		//put in opposition
		for(Node n : this.oppositions){
                    if(!((n.x < 0) || (n.x >= this.width) || (n.y < 0) || (n.y >= this.height))){
                    	this.map[n.x][n.y] = n;
                    }
		}
                
                //printMap(null);
		
		if(this.target.x >= this.width) this.target.setLocation(this.width - 1, this.target.y);
		if(this.target.y >= this.height) this.target.setLocation(this.target.x, this.height - 1);
		if(this.target.x < 0) this.target.setLocation(0, this.target.y);
		if(this.target.y < 0) this.target.setLocation(this.target.x, 0);
		
		
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
						nearestNeighbours.add(this.map[i][j]);
					}
				}
			}
		}
		
		return nearestNeighbours; 
		
	}
	
	public ArrayList<Node> returnPath(){
		
		this.start.setStart(true);
		this.start.setParent(this.start);
		this.start.sethCost(heuristic.getEstimatedDistanceToGoal(this.start, this.target));
		this.start.setgCost(0);
		this.start.setfCost();
		
		if(this.target.x > this.width) this.target.setLocation(this.width - 1, this.target.y);
		if(this.target.y > this.height) this.target.setLocation(this.target.x, this.height - 1);
		if(this.target.x < 0) this.target.setLocation(0, this.target.y);
		if(this.target.y < 0) this.target.setLocation(this.target.x, 0);
		
		this.map[this.start.x][this.start.y] = this.start;
		this.openList.add(this.map[start.x][start.y]);
		currentNode = this.map[start.x][start.y];
		
		boolean hasBeenFound = false;
		
		int count = 0;
		
		while(!hasBeenFound){
            
			//should not need to search through the sorted list...
			currentNode = openList.get(0);
			
			ArrayList<Node> nearestNeighbours = nearestNeighbours(currentNode);
			
			this.closedList.add(currentNode);
			this.openList.remove(currentNode);
			
			for(Node n : nearestNeighbours){
				if(!(this.closedList.contains(n))){
					if(!(this.openList.contains(n))){
						n.setParent(currentNode);
						//add up distance travelled so far
						n.setgCost((heuristic.getEstimatedDistanceToGoal(n, currentNode) + currentNode.getgCost())*0.5);
						//distance "left"
						n.sethCost(heuristic.getEstimatedDistanceToGoal(n, this.target));
						//add together (with obstacle cost)
						n.setfCost();
						this.openList.add(n);
					} else {
						double oldCost = n.getgCost();
						double newCost = (heuristic.getEstimatedDistanceToGoal(n, currentNode) + currentNode.getgCost());
						if(newCost < oldCost){
							n.setParent(currentNode);
							n.setgCost(newCost);
							n.setfCost();
						} else {
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
			
			count++;
		}
		
                ArrayList<Node> returnPath = getPath(closedList);
                //printMap(closedList);
                //printMap(returnPath);
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
        	String mapStr = "\n";
            if(path == null) path = new ArrayList<Node>();
            for(int y = 0; y < this.height; y++){
                for(int x = 0; x < this.width; x++){
                    Node n = map[x][y];
                    if(n != null){
                        if(path.contains(new Node(new Point(x,y), 0))){
                            mapStr += "X ";
                        }else if(n.isTarget()){
                        	mapStr += "T ";
                        }else if(n.isBall()){
                        	mapStr += "B ";
                        } else if (n.isOpposition()){
                        	mapStr += "O ";
                        } else {
                        	mapStr += "  ";
                        }
                    } else {
                    	mapStr += "  ";
                    }
                }
                mapStr += "\n";
            }
            logger.info(mapStr);
        }
	
        public Node[][] getMap(){
        	return this.map;
        }
		
}
