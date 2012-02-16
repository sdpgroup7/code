package uk.ac.ed.inf.sdp2012.group7.vision.worldstate;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class WorldState{

	volatile Color ourColor;
	volatile int room; //0 == main room, 1 == side room
	volatile boolean clickingDone = false;

	volatile Pitch pitch = new Pitch(); //All the data about the pitch dimensions
	volatile ObjectPosition ourGoal = new ObjectPosition(); //The position of our goal
	volatile ObjectPosition opponentsGoal = new ObjectPosition(); //The position of our opponents goal
    
	volatile MovingObject ourRobot = new MovingObject(); //our Robot including postion, orienation and eventually velocity
	volatile MovingObject opponentsRobot = new MovingObject(); //our opponents Robot including postion, orienation and eventually velocity
	volatile MovingObject ball = new MovingObject(); //same as robots but represents the ball
	volatile MovingObject ourGrey = new MovingObject(); //The grey circle on our robot
	volatile MovingObject opponentsGrey = new MovingObject(); //the grey circle on our opponents robot
    
	volatile ArrayList<Point> bluePixels = new ArrayList<Point>(); //Holds all the blue pixels (the T)
	volatile ArrayList<Point> yellowPixels = new ArrayList<Point>(); //Holds all the yellow pixels (the T)
	volatile Point ourKeyPoint = new Point();
	volatile Point opponentsKeyPoint = new Point();
	
	public Point getOurKeyPoint(){
		return this.ourKeyPoint;
	}
	public Point getOpponentsKeyPoint(){
		return this.opponentsKeyPoint;
	} 
	public void setOurKeyPoint(Point p){
		ourKeyPoint = p;
	}
	public void setOpponentsKeyPoint(Point p){
		opponentsKeyPoint = p;
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
    
    public MovingObject getOurRobot(){
    	//Returns an object representing our robot
        return this.ourRobot;
    }

    public MovingObject getOpponentsRobot(){
    	//Returns an object representing our opponents robot
        return this.opponentsRobot;
    }
    public MovingObject getOpponentsGrey(){
    	//Returns an object representing the grey circle on our opponents robot
        return this.opponentsGrey;
    }
    public MovingObject getOurGrey(){
    	//Returns an object representing the grey circle on our robot
        return this.ourGrey;
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
    
    public void setOurRobot(ObjectPosition position, double velocity){
    	//Lets you set our robot by providing a position and a velocity
    	this.ourRobot.set(position,velocity);
    }
    
    public void setOurRobotPosition(int x, int y){
    	//Set our robots centre point with the x and y values
    	//Vision.logger.debug(Integer.toString(x) + "," + Integer.toString(y));
    	this.ourRobot.setPosition(x,y);
    }
    public void setOurGreyPosition(int x, int y){
    	//Sets the position of our grey circle using the x and y values
    	this.ourGrey.setPosition(x, y);
    }
    public void setOurGreyPosition(Point p){
    	//Sets our grey position using a point
    	this.ourGrey.setPosition(p);
    }
    public void setOpponentsGreyPosition(int x, int y){
    	//Sets the position of our opponents grey circle using the x and y values
    	this.opponentsGrey.setPosition(x, y);
    }
    public void setOpponentsGreyPosition(Point p){
    	//Sets the position of our opponents grey cirlce using a point
    	this.opponentsGrey.setPosition(p);
    }
    public void setOurRobotPosition(Point p){
    	//Sets the centre of our robot using a point
    	this.ourRobot.setPosition(p);
    }
    
    public void setOurRobotPosition(ObjectPosition position){
    	//Sets our robot position by passing in the given ObjectPosition
    	this.ourRobot.setPosition(position);
    }
    
    public void setOpponentsRobot(ObjectPosition position, double velocity){
    	//Sets the opponents robot by giving a position and a velocity
    	this.opponentsRobot.set(position,velocity);
    }
    
    public void setOpponentsRobotPosition(int x, int y){
    	//Sets the opponents robot centre using the given x and y values
    	this.opponentsRobot.setPosition(x,y);
    }
    
    public void setOpponentsRobotPosition(Point p){
    	//Sets the opponents robot centre using the given point
    	this.opponentsRobot.setPosition(p);
    }
    
    public void setOpponentsRobotPosition(ObjectPosition position){
    	//Set the opponents robot given an ObjectPosition
    	this.opponentsRobot.setPosition(position);
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
