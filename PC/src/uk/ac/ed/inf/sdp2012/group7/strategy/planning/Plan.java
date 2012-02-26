/**
 * 
 */
package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.strategy.astar.*;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

/**
 * @author s0955088
 *
 */
public class Plan {

	private TargetDecision target_decision;
	private OppositionPrediction opposition;
	private ArrayList<Point> obstacles;
	private ArrayList<Point> path;
	private AStarRun astar;
	private AllStaticObjects all_static_objects;
	private int plan_type;
	public static final Logger logger = Logger.getLogger(Plan.class);

	//World state info
	private AllMovingObjects all_moving_objects;
	private WorldState worldState = WorldState.getInstance();
	
	//For testing
	private Path node_path;

	/**
	 * 
	 */
	//Constructor
	public Plan(AllStaticObjects all_static_objects, int plan_type) {
		
		this.all_static_objects = all_static_objects;
		
		//grab plan type
		this.plan_type = plan_type;
		
		//This is here to make an attempt on ensuring all the moving
		//data is read at the same time. Is this the best way though?
		this.all_moving_objects = new AllMovingObjects();
		
		//Set up obstacles created by opposition
		opposition = new OppositionPrediction(this.all_moving_objects, this.all_static_objects);
		
		//Add the opposition obstacles to the overall obstacles
		//IE add in the boundary...
		this.obstacles = all_static_objects.convertToNodes(opposition.getDefaultObstacles());
		
		//Setup target for A*
		target_decision = new TargetDecision(this.all_moving_objects, this.all_static_objects, this.obstacles, this.plan_type);
		
		//Now add in the obstacles created by AllStaticObjects
		this.obstacles = all_static_objects.addBoundary(this.obstacles);
		
		logger.debug("pitch hieght" + this.all_static_objects.getHeight());
		logger.debug("pitch hieght" + this.all_static_objects.getWidth());
		
		//Now create an A* object from which we create a path
		astar = new AStarRun(this.all_static_objects.getHeight(), this.all_static_objects.getWidth(), this.all_static_objects.convertToNode(target_decision.getTarget()), this.all_static_objects.convertToNode(all_moving_objects.getOurPosition()), this.obstacles );

		//Requires method to convert from path to ArrayList<Point>
		//Now grab path through A* method
		this.path = astar.getPathInPoints();
		
		//Grab path in Node
		this.node_path = astar.getPath();
		
		

	}	
	
	public ArrayList<Point> getPath(){
		return this.path;
	}
	//action = 0; nothing 
	//action = 1; kick
	public int getAction(){
		return target_decision.getAction();
	}
	
	public double getOurRobotAngle(){
		return all_moving_objects.getOurAngle();
	}
	
	public Point getOurRobotPosition() {
		return all_static_objects.convertToNode(all_moving_objects.getOurPosition());
	}
	
	//err.. 
	public Point getOurRobotPositionVisual() {
		return all_static_objects.convertToNode(worldState.getOurRobot().getPosition().getCentre());
	}
	
	public Path getNodePath(){
		return this.node_path;
	}

}
