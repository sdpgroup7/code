package uk.ac.ed.inf.sdp2012.group7.strategy.planning;


import java.awt.Point;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.strategy.PlanTypes;
import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;
import uk.ac.ed.inf.sdp2012.group7.vision.VisionTools;


/**
 * @author s0955088
 * 
 * EVERYTHING IN HERE SHOULD BE IN THE NODE SYSTEM
 *
 */
public class TargetDecision {

	public static final Logger logger = Logger.getLogger(PlanningThread.class);
	
	/**
	 * 
	 */
	
	//Setup
	private AllMovingObjects allMovingObjects;
	private AllStaticObjects allStaticObjects;
	private int planType;
	private int action;
	private WorldState worldState = WorldState.getInstance();
	
	//booleans for taking the shot
	//lets include in this test whether the opposition is in the way
	private boolean clearShot = false;
	
	//boolean to test if the ball is on the pitch
	private boolean ballOnPitch;
	
	
	//navigation point
	private Point navPoint = new Point(0,0);
	private Point target = new Point(0,0);
	//navigation boolean
	//possibly surplus
	private boolean openShotPossible;
	private boolean angularShotPossible;

	
	//penalty defence
	private double targetInCM;
	
	//condition tests
	private boolean weHaveBall = false;
	private boolean theyHaveBall = false;
	private boolean ballIsTooCloseToWall = false;


	private double bestAngle;
	
	
	//Constructor
	public TargetDecision(AllMovingObjects aMO, AllStaticObjects aSO) {
		
		//get all information required
		this.allMovingObjects = aMO;
		this.allStaticObjects = aSO;
		this.planType = this.allStaticObjects.getPlanType();
		
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
		
		Point ballPosition = allMovingObjects.getBallPosition();
		
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
					
					double b = allStaticObjects.getBoundary();
					Point node1 = this.navPoint;
					
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
						
						Point node2 = this.navPoint;
						
						//test to see if we can do an angular shot...
						this.angularShotPossible = (node2.x > b) && 
						node2.x < (this.allStaticObjects.getWidth() - b) &&
						(node2.y < (this.allStaticObjects.getHeight() - b)&& 
								node2.y > (b));
						
						logger.debug("navPoint from angular is : " + this.navPoint.toString());
						
						logger.debug("Angular shot possibility is : " + this.angularShotPossible);
						
						//Just go to the ball
						if(!this.angularShotPossible){
							
							
							this.setNavPointOpenNoOption();
							
							
							logger.debug("navPoint from no option is : " + this.navPoint.toString());
							
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
			this.target = this.allMovingObjects.getOurPosition();
			logger.debug("The target is "+target+" it should be our position");
		}
			
		// No other plan types so must be penalty defence
		else {
			double distanceBetween=Point.distance(	this.allMovingObjects.getOurPosition().x, 
													this.allMovingObjects.getOurPosition().y, 
													this.allMovingObjects.getTheirPosition().x, 
													this.allMovingObjects.getTheirPosition().y);
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

	//DOES THIS WORK
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
	
	//DOES THIS WORK
	private void theyHaveBall(){

		Point theirPosition = allMovingObjects.getTheirPosition();
		Point ballPosition = allMovingObjects.getBallPosition();
		double theirAngle = allMovingObjects.getTheirAngle();

		if(30 > (int)theirPosition.distance(ballPosition)){


			double angleBetweenThemBall = allMovingObjects.angleBetween(theirPosition, ballPosition);

			
			if(Math.abs(angleBetweenThemBall - theirAngle) < (Math.PI/6)){
				theyHaveBall = true;
			}
		}

		logger.debug("They have the ball : " + this.theyHaveBall);

	}
	
	
	//-----------------------------------------------------------------------------------------ALL WRITTEN IN NODES WOKRING CODE
	//set navPoint 4 nodes behind the ball
	private void setNavPointOpenNoOption(){
		
		Point ballPosition = this.allMovingObjects.getBallPosition();
		Point centreGoal = this.allStaticObjects.getCentreOfOurGoal();
		
		//double angleBetweenBallAndGoal = this.allMovingObjects.angleBetween(ballPosition,centreGoal);
		double angleBetweenBallAndGoal = Math.atan2((centreGoal.y - ballPosition.y),(centreGoal.x - ballPosition.x));
		
		
		int navX = ballPosition.x + (int)(Math.cos(angleBetweenBallAndGoal)*4);
		int navY = ballPosition.y + (int)(Math.sin(angleBetweenBallAndGoal)*4);
		
		this.navPoint = new Point(navX,navY);
		
		
	}
	

	//-----------------------------------------------------------------------------------------ALL WRITTEN IN NODES WOKRING CODE	
	//set navPoint 7 nodes behind the ball
	private void setNavPointOpen(){
		
		Point ballPosition = this.allMovingObjects.getBallPosition();
		Point centreGoal = this.allStaticObjects.getCentreOfTheirGoal();
		
		//double angleBetweenBallAndGoal = this.allMovingObjects.angleBetween(ballPosition,centreGoal);
		double angleBetweenBallAndGoal = Math.atan2((centreGoal.y - ballPosition.y),(centreGoal.x - ballPosition.x));
		
		//Seven Nodes behind the ball
		int navX = ballPosition.x - (int)(Math.cos(angleBetweenBallAndGoal)*7);
		int navY = ballPosition.y - (int)(Math.sin(angleBetweenBallAndGoal)*7);
		
		this.navPoint = new Point(navX,navY);
		
		
	}

	//-----------------------------------------------------------------------------------------ALL WRITTEN IN NODES WOKRING CODE
	//set Target 3 nodes behind the ball (our robot width)
	private void setTargetPointOpen(){
		
		Point ballPosition = this.allMovingObjects.getBallPosition();
		Point centreGoal = this.allStaticObjects.getCentreOfTheirGoal();
		
		//double angleBetweenBallAndGoal = this.allMovingObjects.angleBetween(ballPosition,centreGoal);
		double angleBetweenBallAndGoal = Math.atan2((centreGoal.y - ballPosition.y),(centreGoal.x - ballPosition.x));
		
		
		int navX = ballPosition.x - (int)(Math.cos(angleBetweenBallAndGoal)*3);
		int navY = ballPosition.y - (int)(Math.sin(angleBetweenBallAndGoal)*3);
		
		this.target = new Point(navX,navY);
		
		
	}
	
	//-----------------------------------------------------------------------------------------ALL WRITTEN IN NODES WOKRING CODE
	//set navPoint 7 nodes behind the ball
	private void setNavPointAngular(){
		
		Point ballPosition = this.allMovingObjects.getBallPosition();
		Point centreGoal = this.allStaticObjects.getCentreOfTheirGoal();
		
		//double angleBetweenBallAndGoal = this.allMovingObjects.angleBetween(ballPosition,centreGoal);
		double angleBetweenBallAndGoal = Math.atan2((centreGoal.y - ballPosition.y),(centreGoal.x - ballPosition.x));
		
		int navX = ballPosition.x - (int)(Math.cos(angleBetweenBallAndGoal)*7);
		int navY = ballPosition.y + (int)(Math.sin(angleBetweenBallAndGoal)*7);
		
		//How do we know which wall to bounce off?--------------------------------------------NEED FIX
		
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
	
	//-----------------------------------------------------------------------------------------ALL WRITTEN IN NODES WOKRING CODE
	//set Target 3 nodes behind the ball (half our robot length)
	private void setTargetPointAngular(){
		
		Point ballPosition = this.allMovingObjects.getBallPosition();
		Point centreGoal = this.allStaticObjects.getCentreOfTheirGoal();
		
		//double angleBetweenBallAndGoal = this.allMovingObjects.angleBetween(ballPosition, centreGoal);
		double angleBetweenBallAndGoal = Math.atan2((centreGoal.y - ballPosition.y),(centreGoal.x - ballPosition.x));
		
		int navX = ballPosition.x - (int)(Math.cos(angleBetweenBallAndGoal)*3);
		int navY = ballPosition.y - (int)(Math.sin(angleBetweenBallAndGoal)*3);
		
		//How do we know which wall to bounce off?--------------------------------------------NEED FIX
		
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
	
	
	//DOES THIS WORK
	//edit this to check if the opposition is in the way of the shot - angular or not
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

	
	private void ballTooCloseToWall() {
		
		Point ballPosition = this.allMovingObjects.getBallPosition();
		int rightWall = this.allStaticObjects.getWidth();
		int bottomWall = this.allStaticObjects.getHeight();
		int b = this.allStaticObjects.getBoundary();
		
		boolean insideLeftBoundary = ballPosition.x < b;
		if(insideLeftBoundary){
			logger.debug("inside left boundary... " + ballPosition.x + "boundary condition : " + b);
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
	
	
	//DOES THIS WORK?
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
		
		int numberBouncesX = 0;
		int numberBouncesY = 0;
		//dealing with bounces of the walls
		while (x > pitchWidth) {
			numberBouncesX++;
			x = x - pitchWidth;			
		}
		x = x * direction * Math.pow(-1, numberBouncesX);
		
		if (x < 0) {
			x = x + pitchWidth;
		}
		
		while (y > pitchHeight) {
			numberBouncesY++;
			y = y - pitchHeight;						
		}
		y = y * direction * Math.pow(-1, numberBouncesY);
		
		if (y < 0) {
			y = y + pitchHeight;
		}
		
		
		Point predictedPoint = new Point ((int)x,(int) y);		
		return predictedPoint;
	}
	
	//method that checks whether there are any obstacles on the line between 2 points
	private boolean obstacleOnLine(Node n1, Node n2) {
		ArrayList<Node> obstacleNodes = AllStaticObjects.getObstacles();
		ArrayList<Point> d = new ArrayList<Point>();
		double angle = allMovingObjects.angleBetween(Node n1, Node n2);
		for ()
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
	
	public Point getNavPoint(){
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
	
	public double getTargetCM(){
		return this.targetInCM;
	}

}
