package uk.ac.ed.inf.sdp2012.group7.strategy.planning;


import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.strategy.PlanTypes;
import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;
import uk.ac.ed.inf.sdp2012.group7.vision.VisionTools;


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
	private int planType;
	private int action;
	
	//booleans for shots
	private boolean clearShot = false;
	private boolean angularShot = false;
	
	private boolean ballOnPitch;
	
	
	//navigation point
	private Point navPoint = new Point(0,0);
	private Point target;
	//navigation boolean
	private boolean openShotPossible;
	private boolean angularShotPossible;
	
	//penalty defence
	private double targetInCM;
	
	private boolean weHaveBall = false;
	private boolean theyHaveBall = false;
	private boolean ballIsTooCloseToWall = false;
	private WorldState worldState = WorldState.getInstance();

	private double bestAngle;
	
	private boolean goHome = false;

	
	//Constructor
	public TargetDecision(AllMovingObjects aMO, AllStaticObjects aSO) {
		
		//get all information required
		this.allMovingObjects = aMO;
		this.allStaticObjects = aSO;
		this.planType = this.allStaticObjects.getPlanType();
		
		//shot methods
		this.clearShot();
		this.angularShot();
		
		//conditions
		this.weHaveBall();
		this.theyHaveBall();
		this.ballTooCloseToWall();
		
		//setting nav and target
		try {
			this.setTargets();
		} catch (Exception ex) { 
			Strategy.logger.error("Could not setTargets: " + ex.getMessage());
		}
		

	}
	
	
	public void setTargets() {
		
		/*
		 * This whole section is experimental
		 * 
		 * set navPoint so we can start approaching the ball from the right angle
		 * set target so we sit next to the ball
		 * 
		 */
		
		Point ballPosition = allStaticObjects.convertToNode(worldState.getBall().getPosition().getCentre());
		
		//catch for when the ball is not on the pitch
		this.ballOnPitch = ((ballPosition.x >= 0) && (ballPosition.x <= allStaticObjects.getWidth()) && 
				   (ballPosition.y >= 0) && (ballPosition.y <= allStaticObjects.getHeight()));
		
		
		if(this.planType == PlanTypes.PlanType.FREE_PLAY.ordinal()){
		
			//Really need a better decision structure
			if(!this.ballOnPitch){
				
				//sit next to our goal
				this.action = PlanTypes.ActionType.DRIVE.ordinal();
				logger.debug("Ball is not found on pitch, driving to our goal");
				this.target = this.allStaticObjects.getInFrontOfOurGoal();
				this.navPoint = this.target;
				
			} else {
				
				if(this.ballIsTooCloseToWall){
					
					//Need ability to dribble by next milestone
					//go sit infront of our goal
					this.action = PlanTypes.ActionType.DRIVE.ordinal();
					logger.debug("Ball is too close to the wall, driving to our goal");
					this.target = this.allStaticObjects.getInFrontOfOurGoal();
					this.navPoint = this.target;
					
				} else if (this.theyHaveBall){
					
					//go sit infront of our goal
					this.action = PlanTypes.ActionType.DRIVE.ordinal();
					//would be great if we could go into penalty mode here
					logger.debug("They have the ball, driving to our goal");
					//quick hack...
					goHome = true;
					this.target = this.allStaticObjects.getInFrontOfOurGoal();
					this.navPoint = this.target;
					
				} else if (this.weHaveBall){
					
					logger.debug("We have the ball, kicking WOOT!");
					this.action = PlanTypes.ActionType.KICK.ordinal();
					this.target = this.allMovingObjects.getBallPosition();
					this.setNavPointOpenNoOption();
					
				} else {
					
					//create open shot nav and target
					this.setTargetPointOpen();
					this.setNavPointOpen();
					
					double b = 3;
					Point node1 = allStaticObjects.convertToNode(this.navPoint);
					
					//test to see if we can do an open shot...
					this.openShotPossible = (node1.x > b) && 
					node1.x < (this.allStaticObjects.getWidth() - b) &&
							(node1.y < (this.allStaticObjects.getHeight() - b)&& 
									node1.y > (b));
					
					logger.debug("Open shot possibility is : " + this.openShotPossible);
					
					//And if we can't switch the target to an angular shot
					if(!this.openShotPossible){
						
						this.setTargetPointAngular();
						this.setNavPointAngular();
						
						Point node2 = allStaticObjects.convertToNode(this.navPoint);
						
						//test to see if we can do an angular shot...
						this.angularShotPossible = (node2.x > b) && 
						node2.x < (this.allStaticObjects.getWidth() - b) &&
						(node2.y < (this.allStaticObjects.getHeight() - b)&& 
								node2.y > (b));
						
						logger.debug("navPoint from angular is : " + this.allStaticObjects.convertToNode(this.navPoint).toString());
						
						logger.debug("Angular shot possibility is : " + this.angularShotPossible);
						
						//Just go to the ball
						if(!this.angularShotPossible){
							
							
							this.setNavPointOpenNoOption();
							
							
							logger.debug("navPoint from no option is : " + this.allStaticObjects.convertToNode(this.navPoint).toString());
							
							logger.debug("Fuck it, just go to ball, with our goal behind");
							
						}
						
					}
				
					this.action = PlanTypes.ActionType.DRIVE.ordinal();
					this.target = this.allMovingObjects.getBallPosition();
					
					
				}
			}
		} 
		else if(this.planType == PlanTypes.PlanType.HALT.ordinal()){
			this.action = PlanTypes.ActionType.STOP.ordinal();
		}
		
		// Penalty offence
		else if(this.planType == PlanTypes.PlanType.PENALTY_OFFENCE.ordinal()) {				
			logger.debug("In penalty offence, will try to turn then kick");
			double randomDecision = Math.random();
			double angleToTurn;
			if (randomDecision >= 0.5) {
				angleToTurn = allMovingObjects.getOurAngle() + Math.PI/24;
			} else {
				angleToTurn = allMovingObjects.getOurAngle() - Math.PI/24;
			}
			logger.debug("Trying to turn to angle "+angleToTurn);
			this.bestAngle = angleToTurn;
			this.action = PlanTypes.ActionType.ANGLE_KICK.ordinal();
			this.target = allStaticObjects.convertToNode(this.allMovingObjects.getOurPosition());
			logger.debug("The target is "+target+" it should be our position");
		}
			
		// No other plan types so must be penalty defence
		else {
			double distanceBetween=Point.distance(this.allMovingObjects.getOurPosition().x, this.allMovingObjects.getOurPosition().y, this.allMovingObjects.getTheirPosition().x, this.allMovingObjects.getTheirPosition().y);
			double theirAngle = this.allMovingObjects.getTheirAngle();
			// they are facing right when taking penalty
			if(theirAngle < 110 && theirAngle > 70) {
				double diversionFrom90 = Math.abs(theirAngle-90);
				double pixelsToMove = Math.sin(diversionFrom90)*distanceBetween;
				this.targetInCM = VisionTools.pixelsToCM(pixelsToMove);
				// they are pointing towards the top of the goal
				if (90-theirAngle >= 0) {
					this.action = PlanTypes.ActionType.EUCLID_FORWARDS.ordinal();
				} else {
					this.action = PlanTypes.ActionType.EUCLID_BACKWARDS.ordinal();
				}
			// they are facing left
			} else {
				double diversionFrom270 = Math.abs(theirAngle-270);
				double pixelsToMove = Math.sin(diversionFrom270)*distanceBetween;
				this.targetInCM = VisionTools.pixelsToCM(pixelsToMove);
				// they are pointing towards top of the goal
				if (theirAngle-270 >= 0) {
					this.action = PlanTypes.ActionType.EUCLID_FORWARDS.ordinal();
				} else {
					this.action = PlanTypes.ActionType.EUCLID_BACKWARDS.ordinal();
				}
			}
		}
	}

	
	private void weHaveBall(){

		Point ourPosition = allMovingObjects.getOurPosition();
		Point ballPosition = allMovingObjects.getBallPosition();
		double ourAngle = allMovingObjects.getOurAngle();

		if(40 > (int)ourPosition.distance(ballPosition)){


			//double angleBetweenUsBall = allMovingObjects.angleBetween(ourPosition, ballPosition);

			//if(Math.abs(angleBetweenUsBall - ourAngle) < (Math.PI/6)){
				weHaveBall = true;
			//}
		}
		
		logger.debug("We have the ball : " + this.weHaveBall);


	}
	
	private void theyHaveBall(){

		Point theirPosition = allMovingObjects.getTheirPosition();
		Point ballPosition = allMovingObjects.getBallPosition();
		double theirAngle = allMovingObjects.getTheirAngle();

		if(30 > (int)theirPosition.distance(ballPosition)){


			//double angleBetweenThemBall = allMovingObjects.angleBetween(theirPosition, ballPosition);

			
			//if(Math.abs(angleBetweenThemBall - theirAngle) < (Math.PI/6)){
				theyHaveBall = true;
			//}
		}

		logger.debug("They have the ball : " + this.theyHaveBall);

	}
	
	//set navPoint 7 nodes behind the ball
	private void setNavPointOpenNoOption(){
		
		Point ballPosition = this.allStaticObjects.convertToNode(this.allMovingObjects.getBallPosition());
		Point ballReturnPosition = this.allMovingObjects.getBallPosition();
		Point centreGoal = this.allStaticObjects.getCentreOfOurGoal();
		
		double angleBetweenBallAndGoal = Math.atan2((centreGoal.y - ballPosition.y),(centreGoal.x - ballPosition.x));
		
		
		int navX = ballReturnPosition.x + (int)(Math.cos(angleBetweenBallAndGoal)*4*allStaticObjects.getNodeInPixels());
		int navY = ballReturnPosition.y + (int)(Math.sin(angleBetweenBallAndGoal)*4*allStaticObjects.getNodeInPixels());
		
		this.navPoint = new Point(navX,navY);
		
		
	}
	

	
	//set navPoint 7 nodes behind the ball
	private void setNavPointOpen(){
		
		Point ballPosition = this.allStaticObjects.convertToNode(this.allMovingObjects.getBallPosition());
		Point ballReturnPosition = this.allMovingObjects.getBallPosition();
		Point centreGoal = this.allStaticObjects.getCentreOfTheirGoal();
		
		double angleBetweenBallAndGoal = Math.atan2((centreGoal.y - ballPosition.y),(centreGoal.x - ballPosition.x));
		
		
		int navX = ballReturnPosition.x - (int)(Math.cos(angleBetweenBallAndGoal)*7*allStaticObjects.getNodeInPixels());
		int navY = ballReturnPosition.y - (int)(Math.sin(angleBetweenBallAndGoal)*7*allStaticObjects.getNodeInPixels());
		
		this.navPoint = new Point(navX,navY);
		
		
	}
	
	//set Target 3 nodes behind the ball (our robot width)
	private void setTargetPointOpen(){
		
		Point ballPosition = this.allStaticObjects.convertToNode(this.allMovingObjects.getBallPosition());
		Point ballReturnPosition = this.allMovingObjects.getBallPosition();
		Point centreGoal = this.allStaticObjects.getCentreOfTheirGoal();
		
		double angleBetweenBallAndGoal = Math.atan2((centreGoal.y - ballPosition.y),(centreGoal.x - ballPosition.x));
		
		
		int navX = ballReturnPosition.x - (int)(Math.cos(angleBetweenBallAndGoal)*3*allStaticObjects.getNodeInPixels());
		int navY = ballReturnPosition.y - (int)(Math.sin(angleBetweenBallAndGoal)*3*allStaticObjects.getNodeInPixels());
		
		this.target = new Point(navX,navY);
		
		
	}
	
	//set navPoint 7 nodes behind the ball
	private void setNavPointAngular(){
		
		Point ballPosition = this.allStaticObjects.convertToNode(this.allMovingObjects.getBallPosition());
		Point ballReturnPosition = this.allMovingObjects.getBallPosition();
		Point centreGoal = this.allStaticObjects.getCentreOfTheirGoal();
		
		double angleBetweenBallAndGoal = Math.atan2((centreGoal.y - ballPosition.y),(centreGoal.x - ballPosition.x));
		
		int navX = ballReturnPosition.x - (int)(Math.cos(angleBetweenBallAndGoal)*7*allStaticObjects.getNodeInPixels());
		int navY = ballReturnPosition.y + (int)(Math.sin(angleBetweenBallAndGoal)*7*allStaticObjects.getNodeInPixels());
		
		//int distanceToTop = this.allStaticObjects.getPitchTopBuffer() - navY;
		//int distanceToBottom = navY - this.allStaticObjects.getPitchBottomBuffer(); 
		
		//if(distanceToBottom < distanceToTop){
		//	navY = navY + distanceToBottom;
		//}
		//else {
		//	navY = navY - distanceToTop;
		//}
		
		this.navPoint = new Point(navX,navY);
		
	}
	
	//set Target 3 nodes behind the ball (our robot width)
	private void setTargetPointAngular(){
		
		Point ballPosition = this.allStaticObjects.convertToNode(this.allMovingObjects.getBallPosition());
		Point ballReturnPosition = this.allMovingObjects.getBallPosition();
		Point centreGoal = this.allStaticObjects.getCentreOfTheirGoal();
		
		double angleBetweenBallAndGoal = Math.atan2((centreGoal.y - ballPosition.y),(centreGoal.x - ballPosition.x));
		
		int navX = ballReturnPosition.x - (int)(Math.cos(angleBetweenBallAndGoal)*3*allStaticObjects.getNodeInPixels());
		int navY = ballReturnPosition.y - (int)(Math.sin(angleBetweenBallAndGoal)*3*allStaticObjects.getNodeInPixels());
		
//		int distanceToTop = this.allStaticObjects.getPitchTopBuffer() - navY;
//		int distanceToBottom = navY - this.allStaticObjects.getPitchBottomBuffer(); 
//		
//		if(distanceToBottom < distanceToTop){
//			navY = navY + distanceToBottom;
//		}
//		else {
//			navY = navY - distanceToTop;
//		}
		
		this.navPoint = new Point(navX,navY);
		
	}
	
	private void clearShot(){

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
	
	
	//Takes a point and an angle, returns true if there is a shot
	private boolean clearShot(Point position,double angle) {

		if(this.weHaveBall){

			//Position
			Point ourPosition = position;

			//Angles
			double our_angle = angle;
			double angleWithTopPost = allMovingObjects.angleBetween(ourPosition, allStaticObjects.getTheirTopGoalPost()); 
			double angleWithBottomPost = allMovingObjects.angleBetween(ourPosition, allStaticObjects.getTheirBottomGoalPost());
			
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
	
	public int getPlanType(){
		return this.planType;
	}
	
	public double getAngleWanted(){
		return this.bestAngle;
	}
	
	public Point getTarget(){
		return this.target;
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
	
	public Point getTargetAsNode(){
		if(goHome){
			return this.target;
		} else {
			return this.allStaticObjects.convertToNode(this.target);
		}
	}
	
	public Point getNavAsNode(){
		if(goHome){
			return this.navPoint;
		} else {
			return this.allStaticObjects.convertToNode(this.navPoint);
		}
	}
	
	public double getTargetCM(){
		return this.targetInCM;
	}

}
