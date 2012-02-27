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

	private TargetDecision target_decision;
	private OppositionPrediction opposition;
	private ArrayList<Point> obstacles;
	private ArrayList<Point> path;
	private AStarRun astar;
	private AllStaticObjects all_static_objects;
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
	public Plan(AllStaticObjects all_static_objects, AllMovingObjects all_moving_objects) {
		
		this.all_static_objects = all_static_objects;
		this.all_moving_objects = all_moving_objects;
		all_moving_objects.update();
		
		//Set up obstacles created by opposition
		opposition = new OppositionPrediction(all_moving_objects, this.all_static_objects);
		
		//Add the opposition obstacles to the overall obstacles
		//IE add in the boundary...
		this.obstacles = all_static_objects.convertToNodes(opposition.getDefaultObstacles());
		
		//Setup target for A*
		target_decision = new TargetDecision(this.all_moving_objects, this.all_static_objects, this.obstacles);
		
		logger.debug("Target Decision Position: " + target_decision.getTargetAsNode().toString());
		logger.debug("Ball Position: " + this.all_static_objects.convertToNode(Vision.worldState.getBall().getPosition().getCentre()));
		logger.debug("Robot Position: " + this.all_static_objects.convertToNode(all_moving_objects.getOurPosition()).toString());
		logger.debug("lb,tb: " + this.all_static_objects.convertToNode(new Point(Vision.worldState.getPitch().getLeftBuffer(),Vision.worldState.getPitch().getTopBuffer())));
		logger.debug("rb,bb: " + this.all_static_objects.convertToNode(new Point(Vision.worldState.getPitch().getRightBuffer() - 9,Vision.worldState.getPitch().getBottomBuffer() - 9)));
		logger.debug("pitch height: " + this.all_static_objects.getHeight());
		logger.debug("pitch width: " + this.all_static_objects.getWidth());
		
		//Now create an A* object from which we create a path
		astar = new AStarRun(	this.all_static_objects.getHeight(),
								this.all_static_objects.getWidth(),
								this.target_decision.getTargetAsNode(),
								this.all_static_objects.convertToNode(all_moving_objects.getOurPosition()),
								this.obstacles
							);

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
	
	//Unused
	public double getOurRobotAngle(){
		return all_moving_objects.getOurAngle();
	}
	
	//Unused
	public Point getOurRobotPosition() {
		return all_static_objects.convertToNode(all_moving_objects.getOurPosition());
	}
	
	//For Control Interface
	public Point getOurRobotPositionVisual() {
		return all_static_objects.convertToNode(worldState.getOurRobot().getPosition().getCentre());
	}
	
	//For testing
	public Path getNodePath(){
		return this.node_path;
	}
	
	//For testing
	public AStarRun getAStar(){
		return this.astar;
	}
	
	//For Control Interface
	public double getNodeInPixels(){
		return this.all_static_objects.getNodeInPixels();
	}
	
	//For Control Interface
	public int getHeightInNodes(){
		return this.all_static_objects.getHeight();
	}

}
