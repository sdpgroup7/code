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
		
		
		Point target = new Point();
		target = allStaticObjects.convertToNode(this.allMovingObjects.getBallPosition());
		//boolean for knowing if the ball is on the pitch
		//otherwise we crash..
		boolean ballOnPitch = ((target.x >= 0) && (target.x <= allStaticObjects.getWidth()) && 
							   (target.y >= 0) && (target.y <= allStaticObjects.getHeight()));
		
		if(this.planType == PlanTypes.PlanType.FREE_PLAY.ordinal()){
		
			//Really need a better decision structure
			if(!ballOnPitch){
				//sit next to our goal
				this.action = PlanTypes.ActionType.DRIVE.ordinal();
				logger.debug("Ball is not found on pitch, driving to our goal");
				return this.allStaticObjects.getInFrontOfOurGoal();

				
			} else {
				if(this.ballIsTooCloseToWall){
					//Need ability to dribble by next milestone
					//go sit infront of our goal
					this.action = PlanTypes.ActionType.DRIVE.ordinal();
					logger.debug("Ball is too close to the wall, driving to our goal");
					return this.allStaticObjects.getInFrontOfOurGoal();
					
				} else if (this.theyHaveBall){
					//go sit infront of our goal
					this.action = PlanTypes.ActionType.DRIVE.ordinal();
					//would be great if we could go into penalty mode here
					logger.debug("They have the ball, driving to our goal");
					return this.allStaticObjects.getInFrontOfOurGoal();
					
				} else {
					//weHaveBall && not @ bestAngle && not @ bestPosition
					if(this.weHaveBall){
						//drive towards their goal
						this.action = PlanTypes.ActionType.DRIVE.ordinal();
						logger.debug("We have the ball, we don't have a good angle to shoot or position; driving to their goal");
						return this.allStaticObjects.getInFrontOfTheirGoal();
						
					//weHaveBall && not @ bestAngle && atBestPosition
					} else if (this.weHaveBall) {
						//turn to best angle
						this.action = PlanTypes.ActionType.DRIVE.ordinal();
						logger.debug("We have the ball, we don't have a good angle to shoot; turning to their goal");
						return target;
						
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
		//Penalty modes continue from here...
		else if(this.planType == PlanTypes.PlanType.PENALTY_OFFENCE.ordinal()) {
			this.action = PlanTypes.ActionType.ANGLE.ordinal();
			return target;		
		}
		//Penalty Defence
		else {
			
			this.action = PlanTypes.ActionType.STOP.ordinal();
			return target;
		}
	}


	private void weHaveBall(){

		Point ourPosition = allMovingObjects.getOurPosition();
		Point ball_position = allMovingObjects.getBallPosition();
		double ourAngle = allMovingObjects.getOurAngle();

		if(40 > (int)ourPosition.distance(ball_position)){


			double angle_between_us_ball = Math.asin((ball_position.x - ourPosition.x)/ourPosition.distance(ball_position));

			if (angle_between_us_ball < 0){ 
				angle_between_us_ball = angle_between_us_ball + 360;
			}

			if(Math.abs(angle_between_us_ball - ourAngle) < (30)){
				weHaveBall = true;
			}
		}
		
		logger.debug("We have the ball : " + this.weHaveBall);


	}
	
	private void theyHaveBall(){

		Point their_position = allMovingObjects.getTheirPosition();
		Point ball_position = allMovingObjects.getBallPosition();
		double their_angle = allMovingObjects.getTheirAngle();

		if(40 > (int)their_position.distance(ball_position)){


			double angle_between_them_ball = Math.asin((ball_position.x - their_position.x)/their_position.distance(ball_position));

			if (angle_between_them_ball < 0){ 
				angle_between_them_ball = angle_between_them_ball + 360;
			}

			if(Math.abs(angle_between_them_ball - their_angle) < (30)){
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
	
	//Takes a point and an angle, returns true if there is a shot
	private boolean clearShot(Point position,double angle) {

		if(this.weHaveBall){

			//Position
			Point our_position = position;

			//Angles
			double our_angle = angle;
			double angle_with_top_post = Math.asin((this.allStaticObjects.getTheirTopGoalPost().x - our_position.x)/
					(our_position.distance(this.allStaticObjects.getTheirTopGoalPost())));
			double angle_with_bottom_post = Math.asin((this.allStaticObjects.getTheirBottomGoalPost().x - our_position.x)/
					(our_position.distance(this.allStaticObjects.getTheirBottomGoalPost())));



			angle_with_bottom_post = angle_with_bottom_post % (2*Math.PI);
			angle_with_top_post = angle_with_top_post % (2*Math.PI);
			

			//Set clear shot boolean
			if(worldState.getShootingDirection() == 1){
				if(our_angle > angle_with_top_post && our_angle < angle_with_bottom_post){
					return true;
				}
			}
			else{
				if(our_angle < angle_with_top_post && our_angle > angle_with_bottom_post){
					return true;
				}
			}				
		}
		return false;
	}
	
	
	private void angularShot(){
		int pitch_height = this.allStaticObjects.getPitchHeight();
		
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
		double xBot = (pitch_height - constant) / slope;
		Point intercept_top = new Point ((int)xTop,0);
		Point intercept_bot = new Point ((int)xBot,pitch_height);
		//compute the bounce angle
		double bounce_angle = Math.PI - angle;
		bounce_angle = bounce_angle % (2*Math.PI);
		
		if  (clearShot(intercept_top,bounce_angle) || clearShot(intercept_bot,bounce_angle)) {
			this.angularShot = true;
		}
	}
	
	
	// returns best angle at a point for an open shot
	//does not work yet 
	private double bestAngleOpen(Point p) {
		// Find the coordinates of the middle of their goal
		int x = (allStaticObjects.getTheirTopGoalPost().x - allStaticObjects.getTheirBottomGoalPost().x)/2;
		int y = (allStaticObjects.getTheirTopGoalPost().y - allStaticObjects.getTheirBottomGoalPost().y)/2;
		Point midGoal = new Point(x,y);
		//The sin of the best shot angle is
		double sinAngle = (midGoal.x - p.x)/midGoal.distance(p); 
		double angle = Math.asin(sinAngle) % (Math.PI *2);
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
	
	private void findBestShot(){
		
		//1 Choose best position for openShot
		//2 return this angle
		//3 test if the line to the goal from above position is blocked
		//4 if not, set bestPosition & bestAngle to above
		//5 if above blocked choose best position for angularShot
		//6 return this angle
		//7 test is the line to goal from above is blocked
		//if not, set bestPosition & bestAngle to above
		//if above blocked, we need to decide what dribbling is at this point
		//or sit on the line which is their best attack
		
		//Positions
		this.bestPosition = allMovingObjects.getBallPosition();
		
		//Angles
		
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
		double pitch_width = allStaticObjects.getPitchWidth();
		double pitch_height = allStaticObjects.getPitchHeight();
		
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
		while (x > pitch_width) {
			number_bounces_x++;
			x = x - pitch_width;			
		}
		x = x * direction * Math.pow(-1, number_bounces_x);
		
		if (x < 0) {
			x = x + pitch_width;
		}
		
		while (y > pitch_height) {
			number_bounces_y++;
			y = y - pitch_height;						
		}
		y = y * direction * Math.pow(-1, number_bounces_y);
		
		if (y < 0) {
			y = y + pitch_height;
		}
		
		
		Point predicted_point = new Point ((int)x,(int) y);
		
		return predicted_point;
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
	
}
