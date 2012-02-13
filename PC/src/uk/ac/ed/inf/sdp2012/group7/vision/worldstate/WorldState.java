package uk.ac.ed.inf.sdp2012.group7.vision.worldstate;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class WorldState{

	Color ourColor;
	int room; //0 == main room, 1 == side room
	boolean clickingDone = false;

    Pitch pitch = new Pitch();
    ObjectPosition ourGoal = new ObjectPosition();
    ObjectPosition opponentsGoal = new ObjectPosition();
    
    MovingObject ourRobot = new MovingObject();
    MovingObject opponentsRobot = new MovingObject();
    MovingObject ball = new MovingObject();
    MovingObject ourGrey = new MovingObject(); 
    MovingObject opponentsGrey = new MovingObject();
    
    ArrayList<Point> bluePixels = new ArrayList<Point>();
	ArrayList<Point> yellowPixels = new ArrayList<Point>();
    
	public ArrayList<Point> getBluePixels() {
		return bluePixels;
	}

	public void setBluePixels(ArrayList<Point> bluePixels) {
		this.bluePixels = bluePixels;
	}

	public ArrayList<Point> getYellowPixels() {
		return yellowPixels;
	}

	public void setYellowPixels(ArrayList<Point> yellowPixels) {
		this.yellowPixels = yellowPixels;
	}    
    
 
    public WorldState(){
    	this(Color.blue,0);
    }
    
    public WorldState(Color c, int room) {
    	this.room = room;
    	if(room == 0){
    		pitch = new Pitch(	new Point(40,104),
    							new Point(600,98),
    							new Point(40,394),
    							new Point(607,384));
    		pitch.setBuffers(86,612,402,24);
    	} else {
    		//TODO: Add the constants for pitch 2.
    	}
    }
    
    public void setRoom(int room){

    }
    
    public int getRoom(){
    	return this.room;
    }
    
    public void setColor(Color c){
    	this.ourColor = c;
    }
    
    public Color getColor(){
    	return this.ourColor;
    }
    
    public MovingObject getOurRobot(){
        return this.ourRobot;
    }

    public MovingObject getOpponentsRobot(){
        return this.opponentsRobot;
    }
    public MovingObject getOpponentsGrey(){
        return this.opponentsGrey;
    }
    public MovingObject getOurGrey(){
        return this.ourGrey;
    }

    public MovingObject getBall(){
        return this.ball;
    }

    public Pitch getPitch(){
        return this.pitch;
    }

    public ObjectPosition getOurGoal(){
        return this.ourGoal;
    }

    public ObjectPosition getOpponentsGoal(){
        return this.opponentsGoal;
    }
    
    public void setOurRobot(ObjectPosition position, double velocity){
    	this.ourRobot.set(position,velocity);
    }
    
    public void setOurRobotPosition(int x, int y){
    	//Vision.logger.debug(Integer.toString(x) + "," + Integer.toString(y));
    	this.ourRobot.setPosition(x,y);
    }
    public void setOurGreyPosition(int x, int y){
    	this.ourGrey.setPosition(x, y);
    }
    public void setOurGreyPosition(Point p){
    	this.ourGrey.setPosition(p);
    }
    public void setOpponentsGreyPosition(int x, int y){
    	this.opponentsGrey.setPosition(x, y);
    }
    public void setOpponentsGreyPosition(Point p){
    	this.opponentsGrey.setPosition(p);
    }
    public void setOurRobotPosition(Point p){
    	this.ourRobot.setPosition(p);
    }
    
    public void setOurRobotPosition(ObjectPosition position){
    	this.ourRobot.setPosition(position);
    }
    
    public void setOpponentsRobot(ObjectPosition position, double velocity){
    	this.opponentsRobot.set(position,velocity);
    }
    
    public void setOpponentsRobotPosition(int x, int y){
    	this.opponentsRobot.setPosition(x,y);
    }
    
    public void setOpponentsRobotPosition(Point p){
    	this.opponentsRobot.setPosition(p);
    }
    
    public void setOpponentsRobotPosition(ObjectPosition position){
    	this.opponentsRobot.setPosition(position);
    }
    
    public void setBall(ObjectPosition position, double velocity){
    	this.ball.set(position,velocity);
    }
    
    public void setBallPosition(int x, int y){
    	this.ball.setPosition(x,y);
    }
    
    public void setBallPosition(Point p){
    	this.ball.setPosition(p);
    }
    
    public void setBallPosition(ObjectPosition position){
    	this.ball.setPosition(position);
    }
    
    public void setBallVelocity(double velocity){
    	this.ball.setVelocity(velocity);
    }
    
    public void setPitch(Pitch pitch){
    	System.out.println("PITCH: \n" + 
				pitch.getTopLeft() + "\n" + 
				pitch.getTopRight() + "\n" + 
				pitch.getBottomRight() + "\n" + 
				pitch.getBottomLeft());
    	this.pitch = pitch;
    }
    
    public void setPitchBuffers(int top, int right, int bottom, int left){
    	System.out.println("BUFFERS: \n" + 
    							Integer.toString(top) + "\n" + 
    							Integer.toString(right) + "\n" + 
    							Integer.toString(bottom) + "\n" + 
    							Integer.toString(left));
    	this.pitch.setBuffers(top, right, bottom, left);
    }
    
    public void setPitchPosition(ObjectPosition position){
    	this.pitch.setPosition(position);
    }
    
    public void setOurGoal(ObjectPosition position){
    	this.ourGoal = position;
    }
    
    public void setOpponentsGoal(ObjectPosition position){
    	this.opponentsGoal = position;
    }
    
    public boolean isClickingDone(){
        return clickingDone;
    }
    
    public void setClickingDone(boolean yn){
        this.clickingDone = yn;
    }
    
}
