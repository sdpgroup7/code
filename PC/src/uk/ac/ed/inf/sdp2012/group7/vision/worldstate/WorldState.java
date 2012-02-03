package uk.ac.ed.inf.sdp2012.group7.vision.worldstate;

import uk.ac.ed.inf.sdp2012.group7.vision.VisualMethods;

public class WorldState implements VisualMethods{


    /*ObjectPosition pitch = new ObjectPosition();
    ObjectPosition ourGoal = new ObjectPosition();
    ObjectPosition opponentsGoal = new ObjectPosition();
    
    Robot ourRobot = new Robot();
    Robot opponentsRobot = new Robot();
    Ball ball = new Ball();*/
    
    private int direction; // 0 = right, 1 = left.
    private int colour; // 0 = yellow, 1 = blue
    private int pitch; // 0 = main, 1 = side room
    private int blueX;
    private int blueY;
    private int yellowX;
    private int yellowY;
    private int ballX;
    private int ballY;
    private float blueOrientation;
    private float yellowOrientation;
    private long counter;
  
    public WorldState() {
        /* control properties */
        this.direction = 0;
        this.pitch = 0;
    }
    
    /*public Robot getOurRobot(){
        return this.ourRobot;
    }

    public Robot getOpponentsRobot(){
        return this.opponentsRobot;
    }

    public Ball getBall(){
        return this.ball;
    }

    public ObjectPosition getPitch(){
        return this.pitch;
    }

    public ObjectPosition getOurGoal(){
        return this.ourGoal;
    }

    public ObjectPosition getOpponentsGoal(){
        return this.opponentsGoal;
    }*/
















    public void setYellowY(int yellowY) {
        this.yellowY = yellowY;
    }
    public void setYellowX(int yellowX) {
        this.yellowX = yellowX;
    }
    public void setBlueY(int blueY) {
        this.blueY = blueY;
    }
    public void setBlueX(int blueX) {
        this.blueX = blueX;
    }

    public int getBallX() {
        return ballX;
    }
    public void setBallX(int ballX) {
        this.ballX = ballX;
    }
    public int getBallY() {
        return ballY;
    }
    public void setBallY(int ballY) {
        this.ballY = ballY;
    }

    public float getBlueOrientation() {
        return blueOrientation;
    }

    public void setBlueOrientation(float blueOrientation) {
        this.blueOrientation = blueOrientation;
    }

    public float getYellowOrientation() {
        return yellowOrientation;
    }

    public void setYellowOrientation(float yellowOrientation) {
        this.yellowOrientation = yellowOrientation;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public ObjectPosition getBallPosition(){
        //gets the position of the ball
        //Pretty accurate. Gets confused occasionally if the ball has went into the shadows
        return new ObjectPosition(ballX,ballY);
    }
    
    public ObjectPosition getOurPosition(){
        //gets the position of our robot (return center?)
        //Currently finds the centroid of the blue T.
        if(this.colour == 0) return new ObjectPosition(yellowX,yellowY);
        return new ObjectPosition(blueX,blueY);
    }

    public ObjectPosition getOpponentPosition(){
        //gets the position of our oponents robot
        if(this.colour == 1) return new ObjectPosition(yellowX,yellowY);
        return new ObjectPosition(blueX,blueY);
    }

    public Vector2 getDistanceToOpponent(){
        //gets the distance and direction to our opponent
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Vector2 getDistanceToSide(){
        //finds distance to nearest side (specified maybe?)
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Vector2 getBallVelocity(){
        //gets the velocity of the ball
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Vector2 getOurVelocity(){
        //gets our velocity
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Vector2 getOpponentVelocity(){
        //gets our opponents velocity
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Vector2 getDistanceToBall(){
        //find distance to ball
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int whatDidWeHit(){
        //if you hit something we'll tell you what you hit, using a code 
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public boolean didWeScore(){
        //Check whether the ball is in opponents goal (then victory celebration)
        throw new UnsupportedOperationException("Not yet implemented");
    }

    
}
