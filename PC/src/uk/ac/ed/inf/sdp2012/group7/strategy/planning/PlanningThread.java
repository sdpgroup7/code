/**
 * 
 */
package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.util.Observable;
import java.util.Observer;

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
	
	/**
	 * 
	 */
	public PlanningThread(Observer myWatcher, int plan_type) {
		// We only need to create the static objects once, and this class provides one place to store them all
		this.all_static_objects = new AllStaticObjects();
		// PlanningBuffer watches this thread
		this.addObserver(myWatcher);
		// Set while flag as true
		// Set plan type
		this.plan_type = plan_type;
	}

	@Override
	public void run() {
		while(runFlag){
			synchronized(this){
				try {
					Plan temp_plan = new Plan(this.all_static_objects, this.plan_type);
					setChanged();
					notifyObservers(temp_plan);
					Thread.sleep(1);
				} catch (InterruptedException e) {
					
				}
			}
		}
		
	}
	
	public void switchRun() {
		this.runFlag = !runFlag;
	}


}
