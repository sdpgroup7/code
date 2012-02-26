/**
 * 
 */
package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.util.Observable;
import java.util.Observer;

import uk.ac.ed.inf.sdp2012.group7.vision.Vision;

/**
 * @author s0955088
 * THE planning thread, so this will start off all the plan making
 *
 */
public class PlanningThread extends Observable implements Runnable{

	private boolean runFlag;
	private AllStaticObjects all_static_objects;
	//How do we set what plan to make?
	private int plan_type;
	private boolean isWorldStateNull = true;
	
	/**
	 * 
	 */
	public PlanningThread(Observer myWatcher, int plan_type) {
		//test if worldState is set...
		this.setIsWorldStateNull();
		
		// PlanningBuffer watches this thread
		this.addObserver(myWatcher);
		// Set while flag as true
		// Set plan type
		this.plan_type = plan_type;
	}

	@Override
	public void run() {
		
		if(!isWorldStateNull){
			this.all_static_objects = new AllStaticObjects();
		
			while(runFlag){
				synchronized(this){
					try {
						Plan temp_plan = new Plan(this.all_static_objects, this.plan_type);
						setChanged();
						notifyObservers(temp_plan);
						//This is here just because I can never remember how to do this
						//and I think it might be useful for testing...
						//I can imagine Tom finding this, and think "wtf!" - sorry Tom!
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					
					}
				}
			}
		
		}
		
	}
	
	public void switchRun() {
		this.runFlag = !runFlag;
	}
	
	public void setIsWorldStateNull (){
		this.isWorldStateNull = (Vision.worldState.getBall().getPosition().getCentre() == null);
	}


}
