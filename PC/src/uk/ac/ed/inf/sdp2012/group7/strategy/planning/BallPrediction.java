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
	private AllMovingObjects all_moving_objects;
	private AllStaticObjects all_static_objects;
	private ArrayList<Point> obstacles;
	private boolean clear_shot = false;
	private boolean we_have_ball = false;
	private boolean ball_is_too_close_to_wall = false;
	private WorldState worldState = WorldState.getInstance();

	
	//Constructor
	public BallPrediction(AllMovingObjects aMO, AllStaticObjects aSO, ArrayList<Point> obstacles) {
		this.all_moving_objects = aMO;
		this.all_static_objects = aSO;
		this.obstacles = obstacles;
		this.clearShot();
		this.weHaveBall();
		this.ballTooCloseToWall();

	}
	
	
	public Point getTarget () {
		
		if (this.ball_is_too_close_to_wall){
			//boundary handling...
			Point position = this.all_moving_objects.getBallPosition();
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
		return this.all_moving_objects.getBallPosition();
		}
	}


	private void weHaveBall(){

		Point our_position = all_moving_objects.getOurPosition();
		Point ball_position = all_moving_objects.getBallPosition();
		double our_angle = all_moving_objects.getOurAngle();

		if(40 < (int)our_position.distance(ball_position)){


			double angle_between_us_ball = Math.asin((ball_position.x - our_position.x)/our_position.distance(ball_position));

			if (angle_between_us_ball < 0){ 
				angle_between_us_ball = angle_between_us_ball + 360;
			}

			if(Math.abs(angle_between_us_ball - our_angle) < (30)){
				we_have_ball = true;
			}
		}


	}

	private void clearShot(){

		if(we_have_ball){

			//Positions
			Point our_position = all_moving_objects.getOurPosition();


			//Angles
			double our_angle = all_moving_objects.getOurAngle();
			double angle_with_top_post = Math.asin((all_static_objects.getTheir_top_goal_post().x - our_position.x)/(our_position.distance(all_static_objects.getTheir_top_goal_post())));
			double angle_with_bottom_post = Math.asin((all_static_objects.getTheir_bottom_goal_post().x - our_position.x)/(our_position.distance(all_static_objects.getTheir_bottom_goal_post())));

			//fix for normal angles into weird bearings.... :D
			if(angle_with_top_post < 0){
				angle_with_bottom_post = angle_with_bottom_post + 360;
				angle_with_top_post = angle_with_top_post + 360;
			}

			//Set clear shot boolean
			if(worldState.getShootingDirection() == 1){
				if(our_angle > angle_with_top_post && our_angle < angle_with_bottom_post){
					this.clear_shot = true;
				}
			}
			else{
				if(our_angle < angle_with_top_post && our_angle > angle_with_bottom_post){
					this.clear_shot = true;
				}
			}				
		}
	}
	
	private void ballTooCloseToWall() {
		this.ball_is_too_close_to_wall = obstacles.contains(all_static_objects.convertToNode(this.all_moving_objects.getBallPosition()));
	}
	
	public boolean getClearShot(){
		return clear_shot;
	}
}
