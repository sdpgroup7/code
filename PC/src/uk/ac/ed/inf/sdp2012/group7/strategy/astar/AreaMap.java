package uk.ac.ed.inf.sdp2012.group7.strategy.astar;

//import uk.ac.ed.inf.sdp2012.group7.strategy.astar.utils.Logger;
import org.apache.log4j.Logger;

public class AreaMap {

	private int mapWidth;
	private int mapHeight;
	//private ArrayList<ArrayList<Node>> map;
	private Node[][] map;
	private int startLocationX = 0;
	private int startLocationY = 0;
	private int goalLocationX = 0;
	private int goalLocationY = 0;

	public static final Logger logger = Logger.getLogger(AreaMap.class);
	
	//private Logger log = new Logger();
	
	AreaMap(int mapWidth, int mapHeight) {
		
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		
		createMap();
		logger.debug("\tMap Created");
		logger.debug("\tMap Node edges registered");
	}
	private void createMap() {
		
		map = new Node[mapWidth][mapHeight];
		//map = new ArrayList<ArrayList<Node>>();
		for (int x=0; x<mapWidth; x++) {
			//map.add(new ArrayList<Node>());
			for (int y=0; y<mapHeight; y++) {
				//map.get(x).add(new Node(x,y));
				map[x][y] = new Node(x,y,0);
			}
		}
	}
	

	public Node[][] getNodes() {
		return map;
	}
	public void setObstical(int x, int y, boolean isObstical) {
		if (x<0 || x>=mapWidth || y<0 || y>=mapHeight) {
			logger.debug("Something is fucked (setObstical): x: " + Integer.toString(x) + " , y: " + Integer.toString(y));
		} else {
			map[x][y].setObstical(isObstical);
		}
		//map.get(x).get(y).setObstical(isObstical);
	}

	public Node getNode(int x, int y) {
		if (x<0 || x>=mapWidth || y<0 || y>=mapHeight) {
			return null;
		}
		return map[x][y];
	}

	public void setStartLocation(int x, int y) {
		map[startLocationX][startLocationY].setStart(false);
		map[x][y].setStart(true);
		//map.get(startLocationX).get(startLocationY).setStart(false);
		//map.get(x).get(y).setStart(true);
		startLocationX = x;
		startLocationY = y;
	}

	public void setGoalLocation(int x, int y) {

		map[goalLocationX][goalLocationY].setGoal(false);
		map[x][y].setGoal(true);
		//map.get(goalLocationX).get(goalLocationY).setGoal(false);
		//map.get(x).get(y).setGoal(true);
		goalLocationX = x;
		goalLocationY = y;
	}

	public int getStartLocationX() {
		return startLocationX;
	}

	public int getStartLocationY() {
		return startLocationY;
	}
	
	public Node getStartNode() {
		return map[startLocationX][startLocationY];
	}

	public int getGoalLocationX() {
		return goalLocationX;
	}

	public int getGoalLocationY() {
		return goalLocationY;
	}
	
	public Node getGoalLocation() {
		return map[goalLocationX][goalLocationY];
	}
	
	public float getDistanceBetween(Node node1, Node node2) {
		//if the nodes are on top or next to each other, return 1
		if (node1.getX() == node2.getX() || node1.getY() == node2.getY()){
			return 1;
		} else { //if they are diagonal to each other return diagonal distance: sqrt(1^2+1^2)
			return (float) Math.sqrt(2);
		}
	}
	
	public int getMapWidth() {
		return mapWidth;
	}
	public int getMapHeight() {
		return mapHeight;
	}
	public void clear() {
		startLocationX = 0;
		startLocationY = 0;
		goalLocationX = 0;
		goalLocationY = 0;
		createMap();
	}
}
