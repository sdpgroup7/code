package uk.ac.ed.inf.sdp2012.group7.vision.worldstate;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class WorldState{

	volatile Color ourColor = Color.blue;
	volatile int room; //0 == main room, 1 == side room
	volatile boolean clickingDone = false;

	volatile Pitch pitch = new Pitch(); //All the data about the pitch dimensions
	volatile ObjectPosition ourGoal = new ObjectPosition(); //The position of our goal
	volatile ObjectPosition opponentsGoal = new ObjectPosition(); //The position of our opponents goal
    
	volatile MovingObject blueRobot = new MovingObject(); //blue robot including postion, orienation and eventually velocity
	volatile MovingObject yellowRobot = new MovingObject(); //yellow robot including postion, orienation and eventually velocity
	volatile MovingObject ball = new MovingObject(); //same as robots but represents the ball
	volatile MovingObject blueGrey = new MovingObject(); //The grey circle on the blue robot
	volatile MovingObject yellowGrey = new MovingObject(); //the grey circle on the yellow robot
    
	volatile ArrayList<Point> bluePixels = new ArrayList<Point>(); //Holds all the blue pixels (the T)
	volatile ArrayList<Point> yellowPixels = new ArrayList<Point>(); //Holds all the yellow pixels (the T)
	volatile Point blueKeyPoint = new Point();
	volatile Point yellowKeyPoint = new Point();
	
	public MovingObject getOurRobot(){
		if(ourColor.equals(Color.blue)){
			return blueRobot;
		} else {
			return yellowRobot;
		}
	}
	
	public MovingObject getOppononentsRobot(){
		if(ourColor.equals(Color.blue)){
			return yellowRobot;
		} else {
			return blueRobot;
		}
	}
	
	public Point getBlueKeyPoint(){
		return this.blueKeyPoint;
	}
	public Point getYellowKeyPoint(){
		return this.yellowKeyPoint;
	} 
	public void setBlueKeyPoint(Point p){
		blueKeyPoint = p;
	}
	public void setYellowKeyPoint(Point p){
		yellowKeyPoint = p;
	}
    
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
    
    public MovingObject getBlueRobot(){
    	//Returns an object representing blue robot
        return this.blueRobot;
    }

    public MovingObject getYellowRobot(){
    	//Returns an object representing our yellow robot
        return this.yellowRobot;
    }
    public MovingObject getYellowGrey(){
    	//Returns an object representing the grey circle on our yellow robot
        return this.yellowGrey;
    }
    public MovingObject getBlueGrey(){
    	//Returns an object representing the grey circle on blue robot
        return this.blueGrey;
    }

    public MovingObject getBall(){
    	//Returns an object representing the ball
        return this.ball;
    }

    public Pitch getPitch(){
    	//Returns an object representing the pitch
        return this.pitch;
    }

    public ObjectPosition getOurGoal(){
    	//Returns an object representing our goal
        return this.ourGoal;
    }

    public ObjectPosition getOpponentsGoal(){
    	//Returns an object representing our opponents goal
        return this.opponentsGoal;
    }
    
    public void setBlueRobot(ObjectPosition position, double velocity){
    	//Lets you set blue robot by providing a position and a velocity
    	this.blueRobot.set(position,velocity);
    }
    
    public void setBlueRobotPosition(int x, int y){
    	//Set blue robots centre point with the x and y values
    	//Vision.logger.debug(Integer.toString(x) + "," + Integer.toString(y));
    	this.blueRobot.setPosition(x,y);
    }
    public void setBlueGreyPosition(int x, int y){
    	//Sets the position of blue grey circle using the x and y values
    	this.blueGrey.setPosition(x, y);
    }
    public void setBlueGreyPosition(Point p){
    	//Sets blue grey position using a point
    	this.blueGrey.setPosition(p);
    }
    public void setYellowGreyPosition(int x, int y){
    	//Sets the position of our yellow grey circle using the x and y values
    	this.yellowGrey.setPosition(x, y);
    }
    public void setYellowGreyPosition(Point p){
    	//Sets the position of our yellow grey cirlce using a point
    	this.yellowGrey.setPosition(p);
    }
    public void setBlueRobotPosition(Point p){
    	//Sets the centre of blue robot using a point
    	this.blueRobot.setPosition(p);
    }
    
    public void setBlueRobotPosition(ObjectPosition position){
    	//Sets blue robot position by passing in the given ObjectPosition
    	this.blueRobot.setPosition(position);
    }
    
    public void setYellowRobot(ObjectPosition position, double velocity){
    	//Sets the yellow robot by giving a position and a velocity
    	this.yellowRobot.set(position,velocity);
    }
    
    public void setYellowRobotPosition(int x, int y){
    	//Sets the yellow robot centre using the given x and y values
    	this.yellowRobot.setPosition(x,y);
    }
    
    public void setYellowRobotPosition(Point p){
    	//Sets the yellow robot centre using the given point
    	this.yellowRobot.setPosition(p);
    }
    
    public void setYellowRobotPosition(ObjectPosition position){
    	//Set the yellow robot given an ObjectPosition
    	this.yellowRobot.setPosition(position);
    }
    
    public void setBall(ObjectPosition position, double velocity){
    	//Sets the ball state given an ObjectPosition and a velocity
    	this.ball.set(position,velocity);
    }
    
    public void setBallPosition(int x, int y){
    	//Sets the ball position given and x and y value
    	this.ball.setPosition(x,y);
    }
    
    public void setBallPosition(Point p){
    	//Sets the ball position given a point
    	this.ball.setPosition(p);
    }
    
    public void setBallPosition(ObjectPosition position){
    	//Sets the ball position given an ObjectPosition
    	this.ball.setPosition(position);
    }
    
    public void setBallVelocity(double velocity){
    	//Sets the balls velocity 
    	this.ball.setVelocity(velocity);
    }
    
    public void setPitch(Pitch pitch){
    	//Used to set up the pitch initially.
    	/*System.out.println("PITCH: \n" + 
				pitch.getTopLeft() + "\n" + 
				pitch.getTopRight() + "\n" + 
				pitch.getBottomRight() + "\n" + 
				pitch.getBottomLeft());*/
    	this.pitch = pitch;
    }
    
    public void setPitchBuffers(int top, int right, int bottom, int left){
    	//Used to set the pitch buffers should they not already be set
    	//The pitch buffers are the buffers within which we process a given image.
    	//It is more likely useless for anyone except vision.
    	/*System.out.println("BUFFERS: \n" + 
    							Integer.toString(top) + "\n" + 
    							Integer.toString(right) + "\n" + 
    							Integer.toString(bottom) + "\n" + 
    							Integer.toString(left));*/
    	this.pitch.setBuffers(top, right, bottom, left);
    }
    
    public void setPitchPosition(ObjectPosition position){
    	//Lets you set the position of the pitch given an ObjectPosition
    	this.pitch.setPosition(position);
    }
    
    public void setOurGoal(ObjectPosition position){
    	//Lets you set our goal given an ObjectPosition
    	this.ourGoal = position;
    }
    
    public void setOpponentsGoal(ObjectPosition position){
    	//Lets you set our opponents goal given an ObjectPosition
    	this.opponentsGoal = position;
    }
    
    public boolean isClickingDone(){
    	//Lets us make sure we have done all possible setup of variables
    	//This is used in vision processing
        return clickingDone;
    }
    
    public void setClickingDone(boolean yn){
    	//Sets the above mentioned variable
        this.clickingDone = yn;
    }
    
}
