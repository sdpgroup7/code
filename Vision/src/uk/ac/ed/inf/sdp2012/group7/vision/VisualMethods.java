package uk.ac.ed.inf.sdp2012.group7.vision;
interface VisualMethods{

//Priority 1 - Vital

	ObjectPosition getBallPosition(); //gets the position of the ball
	ObjectPosition getOurPosition(); //gets the position of our robot (return center?)
	ObjectPosition getOpponentPosition(); //gets the position of our oponents robot
	ObjectPosition getOurOrientation(); //gets our orientation
	ObjectPosition getOpponentOrientation(); //gets our opponents orientation
	Vector2 getDistanceToOpponent(); //gets the distance and direction to our opponent


//Priority 2 - Needed to have any sort of success

	Vector2 getDistanceToSide(); //finds distance to nearest side (specified maybe?)
	Vector2 getBallVelocity(); //gets the velocity of the ball
	Vector2 getOurVelocity(); //gets our velocity
	Vector2 getOpponentVelocity(); //gets our opponents velocity
        Vector2 getDistanceToBall(); //find distance to ball
        int whatDidWeHit();  //if you hit something we'll tell you what you hit, using a code 



//Priority 3 - Could be useful

        boolean didWeScore(); //Check whether the ball is in opponents goal (then victory celebration)
        

}
