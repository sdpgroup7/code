package uk.ac.ed.inf.sdp2012.group7.vision;

public interface VisualMethods{

    ObjectPosition getBallPosition(); //gets the position of the ball
    ObjectPosition getOurPosition(); //gets the position of our robot (return center?)
    ObjectPosition getOpponentPosition(); //gets the position of our oponents robot
    Vector2 getDistanceToOpponent(); //gets the distance and direction to our opponent
    Vector2 getDistanceToSide(); //finds distance to nearest side (specified maybe?)
    Vector2 getBallVelocity(); //gets the velocity of the ball
    Vector2 getOurVelocity(); //gets our velocity
    Vector2 getOpponentVelocity(); //gets our opponents velocity
    Vector2 getDistanceToBall(); //find distance to ball
    int whatDidWeHit();  //if you hit something we'll tell you what you hit, using a code 
    boolean didWeScore(); //Check whether the ball is in opponents goal (then victory celebration)
        
}
