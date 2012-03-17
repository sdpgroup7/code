package uk.ac.ed.inf.sdp2012.group7.strategy.oldastar;

import java.util.ArrayList;
import java.util.Collections;

import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;

public class OldAStar {
	private OldAreaMap map;
	private OldAStarHeuristic heuristic;

	// searched nodes
	private ArrayList<OldNode> closedList;
	
	// nodes not searched yet, ordered by our heuristic (manhattan distance)
	private SortedNodeList openList;
	private OldPath shortestPath;
	
	OldAStar(OldAreaMap map, OldAStarHeuristic heuristic) {
		this.map = map;
		this.heuristic = heuristic;
		
		closedList = new ArrayList<OldNode>();
		openList = new SortedNodeList();
	}
	
	// given a node, returns an ArrayList of its 8 (less if next to wall) neighbouring nodes
	private ArrayList<OldNode> getNeighbourList(OldNode current) {
		ArrayList<OldNode> result = new ArrayList<OldNode>();
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				if ((j!=0) || (i!=0)) {
					OldNode neighbour = map.getNode(current.getX()+i, current.getY()+j);
					if (neighbour != null) {
						result.add(neighbour);
					}
				}
			}
		}
		
		return result;
	}
	
	public OldPath calcShortestPath(int startX, int startY, int goalX, int goalY) {
		
		//mark start and goal node
		map.setStartLocation(startX, startY);
		map.setGoalLocation(goalX, goalY);
		
		//Check if the goal node is blocked (if it is, it is impossible to find a path there)
		if (map.getNode(goalX, goalY).isObstacle) {
			return null;
		}
		
		map.getStartNode().setDistanceFromStart(0);
		closedList.clear();
		openList.clear();
		openList.add(map.getStartNode());
		
		//while we haven't reached the goal yet
		while(openList.size() != 0) {
			if (openList.size()%100 == 0)
				Strategy.logger.info("\topenList.size() = "+openList.size());
			
			//get the first Node from non-searched Node list, sorted by lowest distance from our goal as guessed by our heuristic
			OldNode current = openList.getFirst();
			
			// check if our current Node location is the goal Node. If it is, we are done.
			if(current.getX() == map.getGoalLocationX() && current.getY() == map.getGoalLocationY()) {
				OldPath tempPath = reconstructPath(current);
				//printPath();
				return tempPath;
			}
			
			//move current Node to the closed (already searched) list
			openList.remove(current);
			closedList.add(current);
			
			//go through all the current Nodes neighbours and calculate if one should be our next step
			for(OldNode neighbour : getNeighbourList(current)) {
				boolean neighbourIsBetter;
				
				//if we have already searched this Node, don't bother and continue to the next one 
				if (closedList.contains(neighbour))
					continue;
				
				//also just continue if the neighbour is an obstacle
				if (!neighbour.isObstacle) {
					
					// calculate how long the path is if we choose this neighbour as the next step in the path 
					float neighbourDistanceFromStart = (current.getDistanceFromStart() + map.getDistanceBetween(current, neighbour));
					
					//add neighbour to the open list if it is not there
					if(!openList.contains(neighbour)) {
						openList.add(neighbour);
						neighbourIsBetter = true;
					//if neighbour is closer to start it could also be better
					} else if(neighbourDistanceFromStart < current.getDistanceFromStart()) {
						neighbourIsBetter = true;
					} else {
						neighbourIsBetter = false;
					}
					// set neighbours parameters if it is better
					if (neighbourIsBetter) {
						neighbour.setPreviousNode(current);
						neighbour.setDistanceFromStart(neighbourDistanceFromStart);
						neighbour.setHeuristicDistanceFromGoal(heuristic.getEstimatedDistanceToGoal(neighbour.getX(), neighbour.getY(), map.getGoalLocationX(), map.getGoalLocationY()));
					}
				}
				
			}
			
		}
		return new OldPath();
	}
	
	public void printPath() {
		OldNode node;
		
		// prints visual representation of the map
		for(int x=0; x<map.getMapWidth(); x++) {
			for(int y=0; y<map.getMapHeight(); y++) {
				node = map.getNode(x, y);
				boolean pathtest = false;
				try{
					pathtest = shortestPath.contains(node.getX(), node.getY());
				} catch (Exception ex){
					Strategy.logger.error("Shortest path error: (X,Y) = (" + Integer.toString(node.getX()) + "," + Integer.toString(node.getY()) + ")");
				}
				if (node.isObstacle) {
					System.out.print("O");
				} else if (node.isStart) {
					System.out.print("R");
				} else if (node.isGoal()) {
					System.out.print("B");
				} else if (pathtest) {
					System.out.print("X");
				} else {
					System.out.print("*");
				}
			}
			System.out.println();
		}
		
		// prints path information
		System.out.print("Going from ("+map.getStartLocationX()+","+map.getStartLocationY()+") to ("+map.getGoalLocationX()+","+map.getGoalLocationY()+") ");
		System.out.println("on a "+map.getMapWidth()+" x "+map.getMapHeight()+" pitch.");
		
		// prints path coordinates
		shortestPath.printWaypoints();
	}
	
	private OldPath reconstructPath(OldNode node) {
		OldPath path = new OldPath();
		while(!(node.getPreviousNode() == null)) {
			path.prependWayPoint(node);
			node = node.getPreviousNode();
		}
		this.shortestPath = path;
		return path;
	}

	private class SortedNodeList {
		
		private ArrayList<OldNode> list = new ArrayList<OldNode>();
		
		public OldNode getFirst() {
			return list.get(0);
		}
		
		public void clear() {
			list.clear();
		}
		
		public void add(OldNode node) {
			list.add(node);
			Collections.sort(list);
		}
		
		public void remove(OldNode n) {
			list.remove(n);
		}
		
		public int size() {
			return list.size();
		}
		
		public boolean contains(OldNode n) {
			return list.contains(n);
		}
	}

}
