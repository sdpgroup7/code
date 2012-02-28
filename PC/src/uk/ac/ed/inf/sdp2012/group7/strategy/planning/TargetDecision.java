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
	
	//booleans for shots
	private boolean clearShot = false;
	private boolean angularShot = false;
	
	private boolean ballOnPitch;
	
	
	//navigation point
	private Point navPoint;
	
	//target point
	private Point target;
	
	private boolean weHaveBall = false;
	private boolean theyHaveBall = false;
	private boolean ballIsTooCloseToWall = false;
	private WorldState worldState = WorldState.getInstance();
	private Point bestPosition;
	private double bestAngle;

	
	//Constructor
	public TargetDecision(AllMovingObjects aMO, AllStaticObjects aSO, ArrayList<Point> obstacles) {
		this.allMovingObjects = aMO;
		this.allStaticObjects = aSO;
		this.obstacles = obstacles;
		this.planType = this.allStaticObjects.getPlanType();
		
		//initialise target
		this.target = new Point(0,0);
		
		//shot methods
		this.clearShot();
		this.angularShot();
		
		
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
		
		
		this.target = allStaticObjects.convertToNode(this.allMovingObjects.getBallPosition());
		//boolean for knowing if the ball is on the pitch
		//otherwise we crash..
		this.ballOnPitch = ((target.x >= 0) && (target.x <= allStaticObjects.getWidth()) && 
							   (target.y >= 0) && (target.y <= allStaticObjects.getHeight()));
		
		if(this.planType == PlanTypes.PlanType.FREE_PLAY.ordinal()){
		
			//Really need a better decision structure
			if(!this.ballOnPitch){
				//sit next to our goal
				this.action = PlanTypes.ActionType.DRIVE.ordinal();
				logger.debug("Ball is not found on pitch, driving to our goal");
				this.target = this.allStaticObjects.getInFrontOfOurGoal();
				return this.target;

				
			} else {
				if(this.ballIsTooCloseToWall){
					//Need ability to dribble by next milestone
					//go sit infront of our goal
					this.action = PlanTypes.ActionType.DRIVE.ordinal();
					logger.debug("Ball is too close to the wall, driving to our goal");
					this.target = this.allStaticObjects.getInFrontOfOurGoal();
					return this.target;
					
				} else if (this.theyHaveBall){
					//go sit infront of our goal
					this.action = PlanTypes.ActionType.DRIVE.ordinal();
					//would be great if we could go into penalty mode here
					logger.debug("They have the ball, driving to our goal");
					this.target = this.allStaticObjects.getInFrontOfOurGoal();
					return this.target;
					
				} else {
					//weHaveBall && not @ bestAngle && not @ bestPosition
					if(!this.clearShot && this.weHaveBall){
						//drive towards their goal
						this.action = PlanTypes.ActionType.DRIVE.ordinal();
						logger.debug("We have the ball, we don't have a good angle to shoot or position; driving to their goal");
						this.target = this.allStaticObjects.getInFrontOfTheirGoal();
						return this.target;
						
					//weHaveBall && not @ bestAngle && atBestPosition
					} else if (this.weHaveBall) {
						//turn to best angle
						this.action = PlanTypes.ActionType.DRIVE.ordinal();
						logger.debug("We have the ball, we don't have a good angle to shoot; drive to their goal");
						this.target = this.allStaticObjects.getInFrontOfTheirGoal();
						return this.target;
						
					//weHaveBall && bestAngle && bestPosition
					} else if (this.weHaveBall) {
						this.action = PlanTypes.ActionType.KICK.ordinal();
						logger.debug("We have the ball, we're on; kicking");
						return target;
						
					} else {
						return target;
					}
				}
			
			}
		} 
		else if(this.planType == PlanTypes.PlanType.HALT.ordinal()){
			this.action = PlanTypes.ActionType.STOP.ordinal();
			return target;
		}
		// Penalty offence
		else if(this.planType == PlanTypes.PlanType.PENALTY_OFFENCE.ordinal()) {
				
			if(this.allStaticObjects.getCounter() % 2 == 0) {
				logger.debug("In penalty offence, try to turn 20 degrees");
				double angle = allMovingObjects.getOurAngle() + Math.PI/18;
				logger.debug("Trying to turn to angle "+angle);
				this.bestAngle = angle;
				target = allStaticObjects.convertToNode(this.allMovingObjects.getOurPosition());
				logger.debug("The target is "+target+" it should be our position");
				this.action = PlanTypes.ActionType.ANGLE.ordinal();
				this.allStaticObjects.setCounter();
				logger.debug("Counter incremented, next plan should try to kick");
				return target;
			} else {
				logger.debug("In penalty offence, trying to kick");
				this.action = PlanTypes.ActionType.KICK.ordinal();
				this.allStaticObjects.setCounter();
				target = allStaticObjects.convertToNode(this.allMovingObjects.getOurPosition());
				logger.debug("The target is "+target+" it should be our position");
				return target;
			}
		}
		// No other plan types so must be penalty defence
		else {
			
			this.action = PlanTypes.ActionType.STOP.ordinal();
			return target;
		}
	}

	
	private void weHaveBall(){

		Point ourPosition = allMovingObjects.getOurPosition();
		Point ballPosition = allMovingObjects.getBallPosition();
		double ourAngle = allMovingObjects.getOurAngle();

		if(40 > (int)ourPosition.distance(ballPosition)){


			double angleBetweenUsBall = allMovingObjects.angleBetween(ourPosition, ballPosition);

			if(Math.abs(angleBetweenUsBall - ourAngle) < (Math.PI/6)){
				weHaveBall = true;
			}
		}
		
		logger.debug("We have the ball : " + this.weHaveBall);


	}
	
	private void theyHaveBall(){

		Point theirPosition = allMovingObjects.getTheirPosition();
		Point ballPosition = allMovingObjects.getBallPosition();
		double theirAngle = allMovingObjects.getTheirAngle();

		if(40 > (int)theirPosition.distance(ballPosition)){


			double angleBetweenThemBall = allMovingObjects.angleBetween(theirPosition, ballPosition);

			
			if(Math.abs(angleBetweenThemBall - theirAngle) < (Math.PI/6)){
				theyHaveBall = true;
			}
		}

		logger.debug("They have the ball : " + this.theyHaveBall);

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
			double angleWithTopPost = allMovingObjects.angleBetween(ourPosition, allStaticObjects.getTheirTopGoalPost()); 
			double angleWithBottomPost = allMovingObjects.angleBetween(ourPosition, allStaticObjects.getTheirBottomGoalPost());

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
	
	//Takes a point and an angle, returns true if there is a shot
	private boolean clearShot(Point position,double angle) {

		if(this.weHaveBall){

			//Position
			Point ourPosition = position;

			//Angles
			double our_angle = angle;
			double angleWithTopPost = allMovingObjects.angleBetween(ourPosition, allStaticObjects.getTheirTopGoalPost()); 
			double angleWithBottomPost = allMovingObjects.angleBetween(ourPosition, allStaticObjects.getTheirBottomGoalPost());
			
			int navX = ourPosition.x - (int)(Math.cos(our_angle)*3*allStaticObjects.getNodeInPixels());
			int navY = ourPosition.y - (int)(Math.sin(our_angle)*3*allStaticObjects.getNodeInPixels());
			
			this.navPoint = new Point(navX,navY);
			
			//Set clear shot boolean
			if(worldState.getShootingDirection() == 1){
				if(our_angle > angleWithTopPost && our_angle < angleWithBottomPost){
					return true;
				}
			}
			else{
				if(our_angle < angleWithTopPost && our_angle > angleWithBottomPost){
					return true;
				}
			}				
		}
		return false;
	}
	
	
	private void angularShot(){
		int pitchHeight = this.allStaticObjects.getPitchHeight();
		
		//if angle of our robot is 90 or 270 , there cannot be angular shot 
		double angle = allMovingObjects.getOurAngle();
		if (angle == Math.PI/2 || angle == Math.PI*3/2) {
			return;
		}
		// y = slope * x + constant. Plug current position of robot to find the constant
		double slope = Math.tan(angle);
		double constant = allMovingObjects.getOurPosition().y - allMovingObjects.getOurPosition().x * slope;
		
		// no angular shot if robot is facing up or down
		if (slope == 0) {
			return;
		}
		
		// Find intercepts by plugging in y = 0 and y = pitch_height, and find the x ;
		
		double xTop = constant / slope;
		double xBot = (pitchHeight - constant) / slope;
		Point interceptTop = new Point ((int)xTop,0);
		Point interceptBot = new Point ((int)xBot,pitchHeight);
		//compute the bounce angle
		double bounceAngle = Math.PI - angle;
		while (bounceAngle < 0) {
			bounceAngle = bounceAngle + (2*Math.PI);
		}
		if  (clearShot(interceptTop,bounceAngle) || clearShot(interceptBot,bounceAngle)) {
			this.angularShot = true;
		}
	}
	
	
	// returns best angle at a point for an open shot
	
	private double bestAngleOpen(Point p) {
		// Find the coordinates of the middle of their goal
		int x = (allStaticObjects.getTheirTopGoalPost().x - allStaticObjects.getTheirBottomGoalPost().x)/2;
		int y = (allStaticObjects.getTheirTopGoalPost().y - allStaticObjects.getTheirBottomGoalPost().y)/2;
		Point midGoal = new Point(x,y);
		//The sin of the best shot angle is
	
		double angle = allMovingObjects.angleBetween(p, midGoal);
		return allMovingObjects.convertAngle(angle);
	}
	
	// returns best angle at a point for an angular shot
	//does not work yet
	private double bestAngleAngular(Point P) {
		// Find the coordinates of the middle of their goal
		int x = (allStaticObjects.getTheirTopGoalPost().x - allStaticObjects.getTheirBottomGoalPost().x)/2;
		int y = (allStaticObjects.getTheirTopGoalPost().y - allStaticObjects.getTheirBottomGoalPost().y)/2;
		Point midGoal = new Point(x,y);
		
		return 0;
	}
	
	private void ballTooCloseToWall() {
		
		Point ballPosition = this.allStaticObjects.convertToNode(this.allMovingObjects.getBallPosition());
		int rightWall = this.allStaticObjects.getWidth();
		int bottomWall = this.allStaticObjects.getHeight();
		int b = this.allStaticObjects.getBoundary();
		
		boolean insideLeftBoundary = ballPosition.x < b;
		if(insideLeftBoundary){
			logger.debug("inside left boundary... " + ballPosition.x);
		}
		boolean insideRightBoundary = ballPosition.x > ((rightWall - 1) -b);
		if(insideRightBoundary){
			logger.debug("inside right boundary... " + ballPosition.x + "boundary condition : " + ((rightWall - 1) -b));
		}
		boolean insideTopBoundary = ballPosition.y < b;
		if(insideTopBoundary){
			logger.debug("inside top boundary... " + ballPosition.y + "bounary is : " + b);
		}
		boolean insideBottomBoundary = ballPosition.y > ((bottomWall -1) -b);
		if(insideBottomBoundary){
			logger.debug("inside bottom boundary... " + ballPosition.y + "boundary condition : " + ((bottomWall -1) -b ));
		}
		
		this.ballIsTooCloseToWall = (insideLeftBoundary) || (insideRightBoundary) || (insideTopBoundary ) || (insideBottomBoundary);
	}
	
	public Point ballPrediction(double time){
		//formula used  d = d0 + v0*t + 1/2*a*t^2
		//              x = d cos(theta)
		//			    y = d sin(theta) 
		Point ball = allMovingObjects.getBallPosition();
		double angle = allMovingObjects.getBallAngle();
		double velocity = allMovingObjects.getBallVelocity();
		double acceleration = allStaticObjects.getDeceleration();
		//width and height of pitch in pixels
		double pitchWidth = allStaticObjects.getPitchWidth();
		double pitchHeight = allStaticObjects.getPitchHeight();
		
		int direction;
		//-1 if ball goes to right, 1 if ball goes to left
		if (angle < Math.PI) {
			direction = 1;
		} else {
			direction = -1;
		}
		
		double d = ball.distance(new Point(0,0)) + velocity * time + 1/2 * acceleration * time * time; 
		double x = d * Math.cos(angle);
		double y = d * Math.cos(angle);
		
		int number_bounces_x = 0;
		int number_bounces_y = 0;
		//dealing with bounces of the walls
		while (x > pitchWidth) {
			number_bounces_x++;
			x = x - pitchWidth;			
		}
		x = x * direction * Math.pow(-1, number_bounces_x);
		
		if (x < 0) {
			x = x + pitchWidth;
		}
		
		while (y > pitchHeight) {
			number_bounces_y++;
			y = y - pitchHeight;						
		}
		y = y * direction * Math.pow(-1, number_bounces_y);
		
		if (y < 0) {
			y = y + pitchHeight;
		}
		
		
		Point predictedPoint = new Point ((int)x,(int) y);
		
		return predictedPoint;
	}
	
	public boolean getClearShot(){
		return clearShot;
	}
	
	public int getAction(){
		return this.action;
	}
	
	public double getAngleWanted(){
		return this.bestAngle;
	}
	
	public Point getTarget(){
		return this.target;
	}
	
	public Point getNav(){
		return this.navPoint;
	}	
	
	public boolean getWeHaveTheBall(){
		return this.weHaveBall;
	}	
	
	public boolean getTheyHaveTheBall(){
		return this.theyHaveBall;
	}	
	
	public boolean getBallOnThePitch(){
		return this.ballOnPitch;
	}
}
