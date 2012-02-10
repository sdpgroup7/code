package uk.ac.ed.inf.sdp2012.group7.vision.worldstate;

import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.ObjectPosition;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.Vector2;
import java.awt.Point;


public class MovingObject {
    
	ObjectPosition position = new ObjectPosition();
	Vector2 velocity = new Vector2();
	
    public ObjectPosition getPosition(){
        return this.position;
    }

    public Vector2 getVelocity(){
        return this.velocity;
    }
    
    public void setPosition(ObjectPosition p){
    	this.position = p;
    }
    
    public void setPosition(Point p){
    	this.position.setCentre(p.x,p.y);
    }
    
    public void setPosition(int x, int y){
    	this.position.setCentre(x,y);
    }
    
    public void setVelocity(Vector2 v){
    	this.velocity = v;
    }
    
    public void set(ObjectPosition p, Vector2 v){
    	this.setPosition(p);
    	this.setVelocity(v);
    }

}
