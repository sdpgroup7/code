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

	private AllMovingObjects all_moving_objects;
	private AllStaticObjects all_static_objects;
	//How do we set what plan to make?
	private int plan_type;
	private boolean worldStateIsPopulated = false;
	private WorldState worldState = WorldState.getInstance();
	public static final Logger logger = Logger.getLogger(PlanningThread.class);
	
	/**
	 * 
	 */
	public PlanningThread(Observer myWatcher, AllStaticObjects aSO) {
		
		// PlanningBuffer watches this thread
		this.addObserver(myWatcher);
		//this is here so we can use the plan_type variable in Strategy.java, which
		//commands which plan type we are creating
		this.all_static_objects = aSO;
		this.all_moving_objects = new AllMovingObjects();
	}

	@Override
	public void run() {
		boolean keepPlanning = true;
		while(keepPlanning || this.all_static_objects.getRunFlag()){
			if(worldStateIsPopulated){
				synchronized(this){
					if( this.plan_type == PlanTypes.PlanType.HALT.ordinal()){
						keepPlanning = false;
						logger.debug("Robot been asked to stop");
					} else {
						keepPlanning = true;
					}
					Strategy.logger.info("Planning Thread is running");
					Plan temp_plan = new Plan(this.all_static_objects, this.all_moving_objects);
					setChanged();
					notifyObservers(temp_plan);
					logger.debug("Plan type: " + this.plan_type);
					//This is here just because I can never remember how to do this
					//and I think it might be useful for testing...
					//I can imagine Tom finding this, and think "wtf!" - sorry Tom!
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						Strategy.logger.error("Thread sleeping in PlanningThread was interrupted.");
					}
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
