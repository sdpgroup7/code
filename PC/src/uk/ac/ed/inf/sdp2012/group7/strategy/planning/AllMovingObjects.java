package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;

import uk.ac.ed.inf.sdp2012.group7.vision.Vision;

public class AllMovingObjects {
	
	private Point ourPosition ;
	private Point theirPosition;
	private Point ballPosition;
	private double ourVelocity;
	private double theirVelocity;
	private double ballVelocity;
	private double ourAngle;
	private double theirAngle;
	private double ballAngle;
	public AllMovingObjects() {
		this.ourPosition = Vision.worldState.getOurRobot().getPosition().getCentre();
		this.theirPosition = Vision.worldState.getOpponentsRobot().getPosition().getCentre();
		this.ballPosition = Vision.worldState.getBall().getPosition().getCentre();
		this.ourVelocity = Vision.worldState.getOurRobot().getVelocity();
		this.theirVelocity = Vision.worldState.getOpponentsRobot().getVelocity();
		this.ballVelocity = Vision.worldState.getBall().getVelocity();
	    this.ourAngle = Vision.worldState.getOurRobot().getAngle();
	    this.theirAngle = Vision.worldState.getOpponentsRobot().getAngle();
	    this.ballAngle = Vision.worldState.getBall().getAngle();
		
	}
	public Point getOurPosition() {
		return ourPosition;
	}
	public Point getTheirPosition() {
		return theirPosition;
	}
	public Point getBallPosition() {
		return ballPosition;
	}
	public double getOurVelocity() {
		return ourVelocity;
	}
	public double getTheirVelocity() {
		return theirVelocity;
	}
	public double getBallVelocity() {
		return ballVelocity;
	}
	public double getOurAngle() {
		return ourAngle;
	}
	public double getTheirAngle() {
		return theirAngle;
	}
	public double getBallAngle() {
		return ballAngle;
	}
	
	

}
