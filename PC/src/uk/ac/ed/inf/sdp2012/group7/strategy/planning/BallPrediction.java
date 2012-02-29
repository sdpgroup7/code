package uk.ac.ed.inf.sdp2012.group7.strategy.planning;


import java.awt.Point;
import java.util.ArrayList;

import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;


/**
 * @author s0955088
 *
 */
public class BallPrediction {

	/**
	 * 
	 */
	private AllMovingObjects allMovingObjects;
	private AllStaticObjects allStaticObjects;
	private ArrayList<Point> obstacles;
	private boolean clearShot = false;
	private boolean weHaveBall = false;
	private boolean ballIsTooCloseToWall = false;
	private WorldState worldState = WorldState.getInstance();

	
	//Constructor
	public BallPrediction(AllMovingObjects aMO, AllStaticObjects aSO, ArrayList<Point> obstacles) {
		this.allMovingObjects = aMO;
		this.allStaticObjects = aSO;
		this.obstacles = obstacles;
		this.clearShot();
		this.weHaveBall();
		this.ballTooCloseToWall();

	}
	
	
	public Point getTarget () {
		
		if (this.ballIsTooCloseToWall){
			//boundary handling...
			Point position = this.allMovingObjects.getBallPosition();
			// 3 is the boundary variable	
			if (position.x < 3) {
				position.x = 3;
			}
			if (position.x > 47) {
				position.x = 47;
			}
			if (position.y < 3) {
				position.y = 3;
			}
			if (position.y > 22) {
				position.y = 22;
			}
			
			return position;
		}
		else {
		return this.allMovingObjects.getBallPosition();
		}
	}


	private void weHaveBall(){

		Point ourPosition = allMovingObjects.getOurPosition();
		Point ballPosition = allMovingObjects.getBallPosition();
		double ourAngle = allMovingObjects.getOurAngle();

		if(40 < (int)ourPosition.distance(ballPosition)){


			double angleBetweenUsBall = Math.asin((ballPosition.x - ourPosition.x)/ourPosition.distance(ballPosition));

			if (angleBetweenUsBall < 0){ 
				angleBetweenUsBall = angleBetweenUsBall + 360;
			}

			if(Math.abs(angleBetweenUsBall - ourAngle) < (30)){
				weHaveBall = true;
			}
		}


	}

	private void clearShot(){

		if(weHaveBall){

			//Positions
			Point ourPosition = allMovingObjects.getOurPosition();


			//Angles
			double ourAngle = allMovingObjects.getOurAngle();
			double angleWithTopPost = Math.asin((allStaticObjects.getTheirTopGoalPost().x - ourPosition.x)/(ourPosition.distance(allStaticObjects.getTheirTopGoalPost())));
			double angleWithBottomPost = Math.asin((allStaticObjects.getTheirBottomGoalPost().x - ourPosition.x)/(ourPosition.distance(allStaticObjects.getTheirBottomGoalPost())));

			//fix for normal angles into weird bearings.... :D
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
	
	private void ballTooCloseToWall() {
		this.ballIsTooCloseToWall = obstacles.contains(allStaticObjects.convertToNode(this.allMovingObjects.getBallPosition()));
	}
	
	public boolean getClearShot(){
		return clearShot;
	}
}
