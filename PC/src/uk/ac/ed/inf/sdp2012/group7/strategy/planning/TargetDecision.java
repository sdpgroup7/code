package uk.ac.ed.inf.sdp2012.group7.strategy.planning;


import java.awt.Point;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.strategy.PlanTypes;
import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.strategy.newastar.Node;
import uk.ac.ed.inf.sdp2012.group7.vision.VisionTools;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;


/**
 * @author s0955088
 * 
 * EVERYTHING IN HERE SHOULD BE IN THE NODE SYSTEM
 *
 */
public class TargetDecision {

	public static final Logger logger = Logger.getLogger(PlanningThread.class);
	
	/**
	 * Please keep this section as tidy and commented as possible, there are
	 * three people working in this area now, and its tricky to know who is using what :)
	 */
	
	//General Setup
	private AllMovingObjects allMovingObjects;
	private AllStaticObjects allStaticObjects;
	private int planType;
	private int action;
	private WorldState worldState = WorldState.getInstance();
	
	//navigation/target point details
	private Node navPoint = new Node (new Point(0,0));
	private Node target = new Node (new Point(0,0));
	private boolean openShotPossible;
	private boolean angularShotPossible;
	private Node shotOnGoal;
	private Node predictedBallPosition;
	
	//penalty defence
	private double targetInCM;
	private double bestAngle;
	
	
	//condition tests
	private boolean weHaveBall = false;
	private boolean theyHaveBall = false;
	private boolean ballIsTooCloseToWall = false;
	private boolean clearShot = false;
	private boolean ballOnPitch;

	
	//Constructor
	public TargetDecision(AllMovingObjects aMO, AllStaticObjects aSO) {
		
		//get all information required
		this.allMovingObjects = aMO;
		this.allStaticObjects = aSO;
		this.planType = this.allStaticObjects.getPlanType();
		
		//Set all conditions
		this.weHaveBall();
		this.theyHaveBall();
		this.ballTooCloseToWall();
		this.ballOnPitch();
		
		//setting nav and target
		try {
			this.setTargets();
		} catch (Exception ex) { 
			Strategy.logger.error("Could not setTargets: " + ex.getMessage());
		}
		

	}


	public void setTargets() {
		
		/*
		 * This whole section is experimental and UNTESTED
		 * 
		 * set navPoint so we can start approaching the ball from the right angle
		 * set target so we sit next to the ball
		 * 
		 */ 
		
		
		if(this.planType == PlanTypes.PlanType.FREE_PLAY.ordinal()){
			
			//these are controls for the navPoint / Target setting ball and goal centre
			//variables - keep in here as Goal Def/Off doesn't care much for them :(
			
			//this.shotOnGoal = whereToShoot(allMovingObjects.getBallPosition());
			//this.predictedBallPosition = ballPrediction(3);
			
			this.shotOnGoal = allStaticObjects.getCentreOfTheirGoal();
			this.predictedBallPosition = allMovingObjects.getBallPosition();
		
			//REQUIRED
			if(!this.ballOnPitch){
				
				//sit next to our goal
				this.action = PlanTypes.ActionType.DRIVE.ordinal();
				logger.debug("DECISION MADE : NO BALL ON PITCH - DRIVE TO OUR GOAL");
				this.target = this.allStaticObjects.getInFrontOfOurGoal();
				this.navPoint = this.target;
				
			} else {
				
				//Don't kill yourself
				if(this.ballIsTooCloseToWall){
					
					//to construct dribble function
					//go sit infront of our goal
					this.action = PlanTypes.ActionType.DRIVE.ordinal();
					logger.debug("DECISION MADE : BALL INSIDE OUR BOUNDARY - DRIVE TO OUR GOAL - coming soon : Dribbling!");
					this.target = this.allStaticObjects.getInFrontOfOurGoal();
					this.navPoint = this.target;
				
				//Don't let them score
				} else if (this.theyHaveBall){
					
					//go sit infront of our goal
					this.action = PlanTypes.ActionType.DRIVE.ordinal();
					//would be great if we could go into penalty mode here
					logger.debug("DECISION MADE : THEY HAVE THE BALL - DRIVE TO OUR GOAL");
					this.target = this.allStaticObjects.getInFrontOfOurGoal();
					this.navPoint = this.target;
					
				//Try to score
				} else if (this.weHaveBall){
					
					logger.debug("DECISION MADE : WE HAVE THE BALL - KICK - assumption; we are on goal line");
					this.action = PlanTypes.ActionType.KICK.ordinal();
					this.target = this.allMovingObjects.getBallPosition();
					this.setNavPointOpenNoOption();
				
				//Fetch
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
				angleToTurn = allMovingObjects.getOurAngle() + Math.PI/18;
			} else {
				angleToTurn = allMovingObjects.getOurAngle() - Math.PI/18;
			}
			logger.debug("Setting angle to turn to as "+angleToTurn);
			this.bestAngle = angleToTurn;
		}
			
		// No other plan types so must be penalty defence
		else {
			// First method - using trig with their angle
			/*double distanceBetween=Point.distance(	this.allMovingObjects.getOurPosition().x, 
													this.allMovingObjects.getOurPosition().y, 
													this.allMovingObjects.getTheirPosition().x, 
													this.allMovingObjects.getTheirPosition().y);
			double theirAngle = this.allMovingObjects.getTheirAngle();
			// they are facing right when taking penalty
			if(worldState.getShootingDirection()==-1) {
				double diversionFrom0 = Math.abs(theirAngle);
				double pixelsToMove = Math.sin(diversionFrom0)*distanceBetween;
				this.targetInCM = VisionTools.pixelsToCM(pixelsToMove);
				// they are pointing towards the top of the goal
				if (theirAngle<=Math.PI/2) {
					// facing towards bottom of goal
					this.action = PlanTypes.ActionType.EUCLID_BACKWARDS.ordinal();
				} else {
					// facing towards top
					this.action = PlanTypes.ActionType.EUCLID_FORWARDS.ordinal();
				}
			// they are facing left
			} else {
				double diversionFromPi = Math.abs(theirAngle-Math.PI);
				double pixelsToMove = Math.sin(diversionFromPi)*distanceBetween;
				this.targetInCM = VisionTools.pixelsToCM(pixelsTballoMove);
				if (theirAngle-Math.PI <= 0) {
					// they are pointing towards bottom of goal
					this.action = PlanTypes.ActionType.EUCLID_BACKWARDS.ordinal();
				} else {
					// facing towards top
					this.action = PlanTypes.ActionType.EUCLID_FORWARDS.ordinal();
				}*/
			// Second method - project a line from their robot and look for intersection on goal line
			Point theirPosition = allMovingObjects.getTheirPosition();
			logger.debug("Their position is "+theirPosition);
			double theirAngle = this.allMovingObjects.getTheirAngle();
			logger.debug("Their angle is "+theirAngle);
			if (theirAngle==(Math.PI/2)||theirAngle==(3*Math.PI/2))
				this.action = PlanTypes.ActionType.STOP.ordinal();
			// they are facing right when taking a penalty
			if(worldState.getShootingDirection()==-1) {
				logger.debug("They are shooting to the right");
				Point inFrontOfOurGoal = allStaticObjects.getInFrontOfOurGoal();
				logger.debug("Point in front of our goal is"+inFrontOfOurGoal);
				// creating an equation for the line they project onto our line
				double c = theirPosition.getY()-(Math.tan(theirAngle)*theirPosition.getX());
				long y = Math.round(Math.tan(theirAngle)*inFrontOfOurGoal.x + c);
				logger.debug("Lines intersect at y node "+y);
				Point toDriveTo = new Point(inFrontOfOurGoal.x,(int)y);
				logger.debug("Will drive to "+toDriveTo);
				int nodesUpOrDown = toDriveTo.y-inFrontOfOurGoal.y;
				logger.debug("Number of nodes to drive is "+nodesUpOrDown);
				this.targetInCM = VisionTools.pixelsToCM(allStaticObjects.getNodeInPixels()*Math.abs(nodesUpOrDown));
				logger.debug("Number of cm to drive is "+targetInCM);
				if (nodesUpOrDown <=0) {
					logger.debug("Need to drive upwards");
					this.action = PlanTypes.ActionType.FORWARD_WITH_DISTANCE.ordinal();
				} else {
					this.action = PlanTypes.ActionType.BACKWARD_WITH_DISTANCE.ordinal();
				}
			} else {
				logger.debug("They are shooting to the left");
				Point inFrontOfOurGoal = allStaticObjects.getInFrontOfOurGoal();
				logger.debug("Point in front of our goal is"+inFrontOfOurGoal);
				// creating an equation for the line they project onto our line
				double c = theirPosition.getY()-(Math.tan(theirAngle)*theirPosition.getX());
				long y = Math.round(Math.tan(theirAngle)*inFrontOfOurGoal.x + c);
				logger.debug("Lines intersect at y node "+y);
				Point toDriveTo = new Point(inFrontOfOurGoal.x,(int)y);
				logger.debug("Will drive to "+toDriveTo);
				int nodesUpOrDown = toDriveTo.y-inFrontOfOurGoal.y;
				logger.debug("Number of nodes to drive is "+nodesUpOrDown);
				this.targetInCM = VisionTools.pixelsToCM(allStaticObjects.getNodeInPixels()*Math.abs(nodesUpOrDown));
				logger.debug("Number of cm to drive is "+targetInCM);
				if (nodesUpOrDown <=0) {
					logger.debug("Need to drive upwards");
					this.action = PlanTypes.ActionType.FORWARD_WITH_DISTANCE.ordinal();
				} else {
					this.action = PlanTypes.ActionType.BACKWARD_WITH_DISTANCE.ordinal();
				}
			}
		}
	}
	
	//BOOLEANS___________________________________________________________________________________________

	//REQUIRED
	//Chris Williams & Darie Picu
	private void weHaveBall(){

		Node ourPosition = allMovingObjects.getOurPosition();
		Node ballPosition = allMovingObjects.getBallPosition();
		double ourAngle = allMovingObjects.getOurAngle();

		if(ourPosition.distance(ballPosition) < allStaticObjects.getCentreToEndOfKicker()){


			double angleBetweenUsBall = allMovingObjects.angleBetween(ourPosition, ballPosition);

			if(Math.abs(angleBetweenUsBall - ourAngle) < (Math.PI/6)){
				weHaveBall = true;
			}
		}
		
		logger.debug("We have the ball : " + this.weHaveBall);


	}
	
	//REQUIRED
	//Chris Williams & Darie Picu
	private void theyHaveBall(){

		Node theirPosition = allMovingObjects.getTheirPosition();
		Node ballPosition = allMovingObjects.getBallPosition();
		double theirAngle = allMovingObjects.getTheirAngle();

		if(theirPosition.distance(ballPosition) < allStaticObjects.getCentreToEndOfKicker()){


			double angleBetweenThemBall = allMovingObjects.angleBetween(theirPosition, ballPosition);

			
			if(Math.abs(angleBetweenThemBall - theirAngle) < (Math.PI/6)){
				theyHaveBall = true;
			}
		}

		logger.debug("They have the ball : " + this.theyHaveBall);

	}
	
	//REDUNDANT
	//but keep for possible future plans
	//Chris Williams & Darie Picu
	private void clearShot(){

		//Positions
		Node ourPosition = allMovingObjects.getOurPosition();


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
	
	//REQUIRED
	//Chris Williams
	private void ballTooCloseToWall() {
		
		Point ballPosition = this.allMovingObjects.getBallPosition();
		int rightWall = this.allStaticObjects.getWidth();
		int bottomWall = this.allStaticObjects.getHeight();
		int b = (int)this.allStaticObjects.getBoundary();
		
		boolean insideLeftBoundary = ballPosition.x < b;
		if(insideLeftBoundary){
			logger.debug("inside left boundary... " + ballPosition.x + " boundary condition : " + b);
		}
		boolean insideRightBoundary = ballPosition.x > ((rightWall - 1) -b);
		if(insideRightBoundary){
			logger.debug("inside right boundary... " + ballPosition.x + " boundary condition : " + ((rightWall - 1) -b));
		}
		boolean insideTopBoundary = ballPosition.y < b;
		if(insideTopBoundary){
			logger.debug("inside top boundary... " + ballPosition.y + " bounary is : " + b);
		}
		boolean insideBottomBoundary = ballPosition.y > ((bottomWall -1) -b);
		if(insideBottomBoundary){
			logger.debug("inside bottom boundary... " + ballPosition.y + " boundary condition : " + ((bottomWall -1) -b ));
		}
		
		this.ballIsTooCloseToWall = (insideLeftBoundary) || (insideRightBoundary) || (insideTopBoundary ) || (insideBottomBoundary);
	}
	
	//REQUIRED
	//Chris Williams
	private void ballOnPitch() {
		
		Node ballPosition = allMovingObjects.getBallPosition();
		
		//catch for when the ball is not on the pitch
		this.ballOnPitch = ((ballPosition.x >= 0) && (ballPosition.x <= allStaticObjects.getWidth()) && 
				   (ballPosition.y >= 0) && (ballPosition.y <= allStaticObjects.getHeight()));
		
	}
	
	//POINTS/NODES____________________________________________________________________________________
	
	
	//-----------------------------------------------------------------------------------------
	//REQUIRED
	//set navPoint 4 nodes behind the ball
	//Chris Williams & Darie Picu
	private void setNavPointOpenNoOption(){
		
		Node ballPosition = this.predictedBallPosition;
		Node centreGoal = this.shotOnGoal;
		
		//double angleBetweenBallAndGoal = this.allMovingObjects.angleBetween(ballPosition,centreGoal);
		double angleBetweenBallAndGoal = Math.atan2((centreGoal.y - ballPosition.y),(centreGoal.x - ballPosition.x));
		
		
		int navX = ballPosition.x + (int)(Math.cos(angleBetweenBallAndGoal)*4);
		int navY = ballPosition.y + (int)(Math.sin(angleBetweenBallAndGoal)*4);
		
		this.navPoint =  new Node (new Point(navX,navY));
		
		
	}
	

	//-----------------------------------------------------------------------------------------
	//REQUIRED
	//set navPoint 7 nodes behind the ball
	//Chris Williams & Darie Picu
	private void setNavPointOpen(){
		
		Node ballPosition = this.predictedBallPosition;
		Node centreGoal = this.shotOnGoal;
		
		//double angleBetweenBallAndGoal = this.allMovingObjects.angleBetween(ballPosition,centreGoal);
		double angleBetweenBallAndGoal = Math.atan2((centreGoal.y - ballPosition.y),(centreGoal.x - ballPosition.x));
		
		//Seven Nodes behind the ball
		int navX = ballPosition.x - (int)(Math.cos(angleBetweenBallAndGoal)*7);
		int navY = ballPosition.y - (int)(Math.sin(angleBetweenBallAndGoal)*7);
		
		this.navPoint = new Node (new Point(navX,navY));
		
		
	}

	//-----------------------------------------------------------------------------------------
	//REQUIRED
	//set Target 3 nodes behind the ball (our robot width)
	//Chris Williams & Darie Picu
	private void setTargetPointOpen(){
		
		Node ballPosition = this.predictedBallPosition;
		Node centreGoal = this.shotOnGoal;
		
		//double angleBetweenBallAndGoal = this.allMovingObjects.angleBetween(ballPosition,centreGoal);
		double angleBetweenBallAndGoal = Math.atan2((centreGoal.y - ballPosition.y),(centreGoal.x - ballPosition.x));
		
		
		int navX = ballPosition.x - (int)(Math.cos(angleBetweenBallAndGoal)*3);
		int navY = ballPosition.y - (int)(Math.sin(angleBetweenBallAndGoal)*3);
		
		this.target = new Node (new Point(navX,navY));
		
		
	}
	
	//-----------------------------------------------------------------------------------------
	//REQUIRED
	//set navPoint 7 nodes behind the ball
	//Chris Williams & Darie Picu
	private void setNavPointAngular(){
		
		Node ballPosition = this.predictedBallPosition;
		Node centreGoal = this.shotOnGoal;
		
		//double angleBetweenBallAndGoal = this.allMovingObjects.angleBetween(ballPosition,centreGoal);
		double angleBetweenBallAndGoal = Math.atan2((centreGoal.y - ballPosition.y),(centreGoal.x - ballPosition.x));
		
		int navX = ballPosition.x - (int)(Math.cos(angleBetweenBallAndGoal)*7);
		int navY = ballPosition.y + (int)(Math.sin(angleBetweenBallAndGoal)*7);
		
		//How do we know which wall to bounce off?--------------------------------------------
		
		//int distanceToTop = this.allStaticObjects.getPitchTopBuffer() - navY;
		//int distanceToBottom = navY - this.allStaticObjects.getPitchBottomBuffer(); 
		
		//if(distanceToBottom < distanceToTop){
		//	navY = navY + distanceToBottom;
		//}
		//else {
		//	navY = navY - distanceToTop;
		//}
		
		this.navPoint = new Node (new Point(navX,navY));
		
	}
	
	//-----------------------------------------------------------------------------------------
	//REQUIRED
	//set Target 3 nodes behind the ball (half our robot length)
	//Chris Williams & Darie Picu
	private void setTargetPointAngular(){
		
		Node ballPosition = this.predictedBallPosition;
		Node centreGoal = this.shotOnGoal;
		
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
		
		this.navPoint = new Node (new Point(navX,navY));
		
	}
	
	//Method to find the Best Node in their Goal Line
	//REQUIRED
	//Darie Picu
	private Node whereToShoot(Node ball) {
		ArrayList<Node> theirGoalNodes = allStaticObjects.getTheirGoalNodes();
		//it is faster if i check their centre first
		Node centreGoal = allStaticObjects.getCentreOfTheirGoal();
		if (!obstacleOnLine(ball,centreGoal)) {
			return centreGoal;
		}
		//for now set the target node as the one closest to the centreGoal
		double distanceToCentreGoal=0;
		double minDistance = theirGoalNodes.size();
		int indexMinDistance = theirGoalNodes.indexOf(centreGoal);
		/*int goodNodesUp = 0;
		int goodNodesDown = 0;
		*/
		
		for (int i=0; i<theirGoalNodes.size(); i++) {
			
			distanceToCentreGoal =centreGoal.distance(theirGoalNodes.get(i));
			if ((!obstacleOnLine(ball,theirGoalNodes.get(i)) && (minDistance > distanceToCentreGoal))) {
				minDistance = distanceToCentreGoal;
				indexMinDistance = i;
			/*if (i>theirGoalNodes.indexOf(centreGoal)) {
				goodNodesUp++;
			} else {
				goodNodesDown++;
			}
			*/	
			}
		}
		return theirGoalNodes.get(indexMinDistance);
	
		/*if (goodNodesUp >= goodNodesDown) {
			return theirGoalNodes.get(theirGoalNodes.size() -1 - (int) goodNodesUp/2);
		} else {
			return theirGoalNodes.get((int) goodNodesDown/2);
		}*/

	}
	
	
	//DOES THIS WORK?
	//REQUIRED
	//Darie Picu
	public Node ballPrediction(double time){
		//formula used  d = d0 + v0*t + 1/2*a*t^2
		//              x = d cos(theta)
		//			    y = d sin(theta) 
		Node ball = allMovingObjects.getBallPosition();
		double angle = allMovingObjects.getBallAngle();
		double velocity = allMovingObjects.getBallVelocity();
		double acceleration = allStaticObjects.getDeceleration();
		//width and height of pitch in pixels
		double pitchWidthinNodes = allStaticObjects.getWidth();
		double pitchHeightinNodes = allStaticObjects.getHeight();
		
		int direction = worldState.getShootingDirection();
		
		double d = velocity * time;// + 1/2 * acceleration * time * time; 
		double x = ball.x + d * Math.cos(angle);
		double y = ball.y + d * Math.sin(angle);
		
		int numberBouncesX = 0;
		int numberBouncesY = 0;
		//dealing with bounces of the walls
		while (x > pitchWidthinNodes) {
			numberBouncesX++;
			x = x - pitchWidthinNodes;			
		}
		x = x * direction * Math.pow(-1, numberBouncesX);
		
		if (x < 0) {
			x = x + pitchWidthinNodes;
		}
		
		while (y > pitchHeightinNodes) {
			numberBouncesY++;
			y = y - pitchHeightinNodes;						
		}
		y = y * direction * Math.pow(-1, numberBouncesY);
		
		if (y < 0) {
			y = y + pitchHeightinNodes;
		}
		
		
		Point predictedPoint = (new Point ((int)x,(int) y));		
		return allStaticObjects.convertToNode(predictedPoint);
	}
	
	//Method returns navPoint for ball interception
	//REQUIRED
	//Darie Picu
	private Node ballIntercept(){
		Node ourPosition = allMovingObjects.getOurPosition();
		boolean canGetThere = false;
		double time = 0; 
		double dt = allStaticObjects.getDt();
		Node target = ballPrediction(time);

		/*At this moment no equations are solved
		still need to know whether we use deceleration or not.
		If we do, equations are much harder to solve.
		The while loop checks at each small time interval 
		whether the robot can get to the predicted point or not
		*/
		while (!canGetThere && time < 5) {
			time = time + dt;
			target = ballPrediction(time);
			//check if we can get to the target in time using Manhattan distance
			double timeToGetThere = dt * (Math.abs(target.x - ourPosition.x) + (Math.abs(target.y - ourPosition.y)));
			if (timeToGetThere <= time) {
				canGetThere = true;
			}
		}		
		return target;		
	}
	
	//method that checks whether there are any obstacles on the line between 2 points
	//REQUIRED
	//Darie Picu
	private boolean obstacleOnLine(Node n1, Node n2) {

		ArrayList<Node> obstacleNodes = allMovingObjects.getBinaryObstacles();
		double angle = allMovingObjects.angleBetween(n1, n2);
		for (int i=0 ; i<obstacleNodes.size(); i++) {
			//compare 'angle' with the angle between n1 and each of the obstacles
			//if it is between 5 degrees then there is an obstacle on the line
			double obstacleAngle = allMovingObjects.angleBetween(n1, obstacleNodes.get(i));
			if (Math.abs(Math.toDegrees(angle-obstacleAngle))<5) {
				return true;
			}
			
		}

		return false;
	}
	
	
	
	//GETTERS/SETTERS____________________________________________________________________________________
	
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
