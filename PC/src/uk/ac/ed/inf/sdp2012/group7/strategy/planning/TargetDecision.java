package uk.ac.ed.inf.sdp2012.group7.strategy.planning;


import java.awt.Point;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.strategy.PlanTypes;
import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;



/**
 * @author s0955088
 *
 */
public class TargetDecision {

	public static final Logger logger = Logger.getLogger(PlanningThread.class);
	
	/**
	 * 
	 */
	private AllMovingObjects allMovingObjects;
	private AllStaticObjects allStaticObjects;
	private ArrayList<Point> obstacles;
	private int planType;
	private int action;
	private boolean clearShot = false;
	private boolean weHaveBall = false;
	private boolean theyHaveBall = false;
	private boolean ballIsTooCloseToWall = false;
	private WorldState worldState = WorldState.getInstance();

	
	//Constructor
	public TargetDecision(AllMovingObjects aMO, AllStaticObjects aSO, ArrayList<Point> obstacles) {
		this.allMovingObjects = aMO;
		this.allStaticObjects = aSO;
		this.obstacles = obstacles;
		this.planType = this.allStaticObjects.getPlanType();
		this.clearShot();
		this.weHaveBall();
		this.theyHaveBall();
		this.ballTooCloseToWall();
		

	}
	
	
	public Point getTargetAsNode() {
		
		/*
		 * This whole section is experimental
		 * 
		 * This should be led by us trying to get to the best position and angle at all time
		 * then if we are at these conditions we react, see comments above open_shot for details
		 * 
		 */
		
		
		Point target = new Point();
		//put it into node for assessment
		//hack :o)
		target = allStaticObjects.convertToNode(this.allMovingObjects.getBallPosition());
		//boolean for knowing if the ball is on the pitch
		boolean ballOnPitch = ((target.x >= 0) && (target.x <= allStaticObjects.getWidth()) && 
							   (target.y >= 0) && (target.y <= allStaticObjects.getHeight()));
		
		if(this.planType == PlanTypes.PlanType.FREE_PLAY.ordinal()){
		
			//Lets get this shit in, and then go read about proper decision making structures later.
			if(!ballOnPitch){
				//fuck off and sit next to our goal
				this.action = PlanTypes.ActionType.DRIVE.ordinal();
				logger.debug("Ball is not found on pitch, driving to our goal");
				return this.allStaticObjects.getInFrontOfOurGoal();

				
			} else {
				if(this.ballIsTooCloseToWall){
					//go sit infront of our goal
					this.action = PlanTypes.ActionType.DRIVE.ordinal();
					logger.debug("Ball is too close to the wall, driving to our goal");
					return this.allStaticObjects.getInFrontOfOurGoal();
				} else if (this.theyHaveBall){
					//go sit infront of our goal
					this.action = PlanTypes.ActionType.DRIVE.ordinal();
					logger.debug("They have the ball, driving to our goal");
					return this.allStaticObjects.getInFrontOfOurGoal();
				} else {
					if(this.clearShot){
						//here we kick
						this.action = PlanTypes.ActionType.KICK.ordinal();
						return target;
					} else {
						//here we should be providing either 
						//a) best position for open shot -- so we need find position for open shot, and that angle
						//b) best position for angular shot -- so we need find position for angular shot, and that angle
						//c) dribble the ball out -- drive to ball, drive after ball <--
						this.action = PlanTypes.ActionType.DRIVE.ordinal();
						return target;
					}
				}
			
			}
		} 
		else if(this.planType == PlanTypes.PlanType.HALT.ordinal()){
			this.action = PlanTypes.ActionType.STOP.ordinal();
			return target;
		}
		//Penalty modes continue from here...
		else {
			this.action = PlanTypes.ActionType.STOP.ordinal();
			return target;		
		}				
	}


	private void weHaveBall(){

		Point ourPosition = allMovingObjects.getOurPosition();
		Point ball_position = allMovingObjects.getBallPosition();
		double ourAngle = allMovingObjects.getOurAngle();

		if(40 < (int)ourPosition.distance(ball_position)){


			double angle_between_us_ball = Math.asin((ball_position.x - ourPosition.x)/ourPosition.distance(ball_position));

			if (angle_between_us_ball < 0){ 
				angle_between_us_ball = angle_between_us_ball + 360;
			}

			if(Math.abs(angle_between_us_ball - ourAngle) < (30)){
				weHaveBall = true;
			}
		}


	}
	
	private void theyHaveBall(){

		Point their_position = allMovingObjects.getTheirPosition();
		Point ball_position = allMovingObjects.getBallPosition();
		double their_angle = allMovingObjects.getTheirAngle();

		if(40 < (int)their_position.distance(ball_position)){


			double angle_between_them_ball = Math.asin((ball_position.x - their_position.x)/their_position.distance(ball_position));

			if (angle_between_them_ball < 0){ 
				angle_between_them_ball = angle_between_them_ball + 360;
			}

			if(Math.abs(angle_between_them_ball - their_angle) < (30)){
				theyHaveBall = true;
			}
		}


	}

	//This function should not check weHaveBall
	//It should find the best position and angle for "a" shot
	//update whether that target type is of open or angular
	//and set a variable bestTargetAngle and bestTargetPosition which is where we want to be
	//in the decision making getTargetNode it should then check if the robot
	//is on that angle and position and respond, either by moving to the above
	//of if on the above it should react - TURN / KICK
	//Thus we need two functions - getBestAngular
	//                           - getBestOpen
	//these then set bestPosition and bestAngle
	//and we just check if our robot is at these conditions...
	
	private void clearShot(){

		if(weHaveBall){

			//Positions
			Point ourPosition = allMovingObjects.getOurPosition();


			//Angles
			double ourAngle = allMovingObjects.getOurAngle();
			double angleWithTopPost = Math.asin((allStaticObjects.getTheirTopGoalPost().x 
					- ourPosition.x)/(ourPosition.distance(allStaticObjects.getTheirTopGoalPost())));
			double angleWithBottomPost = Math.asin((allStaticObjects.getTheirBottomGoalPost().x 
					- ourPosition.x)/(ourPosition.distance(allStaticObjects.getTheirBottomGoalPost())));

			//fix for normal angles into bearings.... :D
			if(angleWithTopPost < 0){
				angleWithBottomPost = angleWithBottomPost + 360;
				angleWithTopPost = angleWithTopPost + 360;
			}

			//Set clear shot boolean
			if(worldState.getShootingDirection() == 1){
				if(ourAngle > angleWithTopPost && ourAngle < angleWithBottomPost){
					this.clearShot = true;
				}
			}
			else{
				if(ourAngle < angleWithTopPost && ourAngle > angleWithBottomPost){
					this.clearShot = true;
				}
			}				
		}
	}
	
	private void findShot(){
		
		//Positions
		Point ballPosition = allMovingObjects.getBallPosition();
		
		//Angles
		
	}
	
	private void ballTooCloseToWall() {
		
		Point ballPosition = this.allMovingObjects.getBallPosition();
		int rightWall = this.allStaticObjects.getWidth();
		int bottomWall = this.allStaticObjects.getHeight();
		int b = this.allStaticObjects.getBoundary();
		
		this.ballIsTooCloseToWall = (ballPosition.x < b) || 
				(ballPosition.x > ((rightWall -1) - b ) || 
				(ballPosition.y < b ) || 
				(ballPosition.x > ((bottomWall -1) - b )));
	}
	
	public boolean getClearShot(){
		return clearShot;
	}
	
	public int getAction(){
		return this.action;
	}
	
}
