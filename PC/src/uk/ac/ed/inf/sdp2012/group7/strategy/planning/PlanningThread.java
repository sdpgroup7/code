/**
 * 
 */
package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.strategy.PlanTypes;
import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

/**
 * @author s0955088
 * THE planning thread, so this will start off all the plan making
 *
 */
public class PlanningThread extends Observable implements Runnable{


	private AllMovingObjects allMovingObjects;
	private AllStaticObjects allStaticObjects;
	//How do we set what plan to make?
	private int planType;
	private boolean worldStateIsPopulated = false;
	private WorldState worldState = WorldState.getInstance();
	public static final Logger logger = Logger.getLogger(PlanningThread.class);
	
	/**
	 * 
	 */
	public PlanningThread(Observer myWatcher, AllStaticObjects aSO) {
		
		// PlanningBuffer watches this thread
		this.addObserver(myWatcher);
		//this is here so we can use the planType variable in Strategy.java, which
		//commands which plan type we are creating
		this.allStaticObjects = aSO;
		this.allMovingObjects = new AllMovingObjects(aSO);
	}

	@Override
	public void run() {
		boolean keepPlanning = true;
		while(keepPlanning || this.allStaticObjects.getRunFlag()){
			if(worldStateIsPopulated){
				synchronized(this){
					if( this.allStaticObjects.getPlanType() == PlanTypes.PlanType.HALT.ordinal()){
						keepPlanning = false;
						logger.debug("Robot been asked to stop");
					} else {
						keepPlanning = true;
					}
					Strategy.logger.info("Planning Thread is running");
					Plan temp_plan = new Plan(this.allStaticObjects, this.allMovingObjects);
					setChanged();
					notifyObservers(temp_plan);
					logger.debug("Plan type: " + this.planType);
					//This is here just because I can never remember how to do this
					//and I think it might be useful for testing...
					//I can imagine Tom finding this, and think "wtf!" - sorry Tom!
					
				
				}
			} else {
				while(!worldStateIsPopulated){
					Strategy.logger.info(worldState.getLastUpdateTime());
					setWorldStateIsPopulated();
				}
			}
			
		}
		
	}
	
	public void setWorldStateIsPopulated (){
		this.worldStateIsPopulated = (worldState.getLastUpdateTime() > 0);
	}


}
