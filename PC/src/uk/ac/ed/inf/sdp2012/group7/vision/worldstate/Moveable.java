package uk.ac.ed.inf.sdp2012.group7.vision.worldstate;

import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.ObjectPosition;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.Vector2;


public interface Moveable {
    
    ObjectPosition getPosition();
    Vector2 getVelocity();

}
