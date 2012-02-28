/**
 * 
 */
package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.strategy.astar.*;
import uk.ac.ed.inf.sdp2012.group7.vision.Vision;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

/**
 * @author s0955088
 *
 */
public class Plan {

	private TargetDecision targetDecision;
	private OppositionPrediction opposition;
	private ArrayList<Point> obstacles;
	private ArrayList<Point> path;
	private AStarRun astar;
	private AllStaticObjects allStaticObjects;
	public static final Logger logger = Logger.getLogger(Plan.class);

	//World state info
	private AllMovingObjects allMovingObjects;
	private WorldState worldState = WorldState.getInstance();
	
	//For testing
	private Path nodePath;

	/**
	 * 
	 */
	//Constructor
	public Plan(AllStaticObjects allStaticObjects, AllMovingObjects allMovingObjects) {
		
		logger.debug("Plan being generated");
		
		this.allStaticObjects = allStaticObjects;
		this.allMovingObjects = allMovingObjects;
		allMovingObjects.update();
		
		//Set up obstacles created by opposition
		opposition = new OppositionPrediction(allMovingObjects, this.allStaticObjects);
		
		//Add the opposition obstacles to the overall obstacles
		//IE add in the boundary...
		this.obstacles = allStaticObjects.convertToNodes(opposition.getDefaultObstacles());
		
		//Setup target for A*
		targetDecision = new TargetDecision(this.allMovingObjects, this.allStaticObjects, this.obstacles);
		
		logger.debug("Target Decision Position: " + targetDecision.getTargetAsNode().toString());
		logger.debug("Ball Position: " + this.allStaticObjects.convertToNode(Vision.worldState.getBall().getPosition().getCentre()));
		logger.debug("Robot Position: " + this.allStaticObjects.convertToNode(allMovingObjects.getOurPosition()).toString());
		logger.debug("Their Robot Position: " + this.allStaticObjects.convertToNode(worldState.getOpponentsRobot().getPosition().getCentre()));
		
		//Now create an A* object from which we create a path
		astar = new AStarRun(	this.allStaticObjects.getHeight(),
								this.allStaticObjects.getWidth(),
								this.targetDecision.getTargetAsNode(),
								this.allStaticObjects.convertToNode(allMovingObjects.getOurPosition()),
								this.obstacles
							);

		//Requires method to convert from path to ArrayList<Point>
		//Now grab path through A* method
		this.path = astar.getPathInPoints();
		
		logger.debug("Path length: " + this.path.size());
		
		//Grab path in Node
		this.nodePath = astar.getPath();
		
		

	}	
	
	public ArrayList<Point> getPath(){
		return this.path;
	}
	
	public void setPath(ArrayList<Point> path){
		this.path = path;
	}
	
	//action = 0; nothing 
	//action = 1; kick
	public int getAction(){
		return targetDecision.getAction();
	}
	
	//Plan Monitor
	public double getOurRobotAngle(){
		return allMovingObjects.getOurAngle();
	}
	
	public Point getBallPosition(){
		return allStaticObjects.convertToNode(allMovingObjects.getBallPosition());
	}
	
	//Unused
	public Point getOurRobotPosition() {
		return allStaticObjects.convertToNode(allMovingObjects.getOurPosition());
	}
	
	//For Control Interface
	public Point getOurRobotPositionVisual() {
		return allStaticObjects.convertToNode(worldState.getOurRobot().getPosition().getCentre());
	}
	
	//For testing
	public Path getNodePath(){
		return this.nodePath;
	}
	
	//For testing
	public AStarRun getAStar(){
		return this.astar;
	}
	
	//For Control Interface
	public double getNodeInPixels(){
		return this.allStaticObjects.getNodeInPixels();
	}
	
	//For Control Interface
	public int getHeightInNodes(){
		return this.allStaticObjects.getHeight();
	}

	public void setOurPosition(Point point) {
		// TODO Auto-generated method stub
		
	}

}
