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

	private boolean run;
	private AllStaticObjects all_static_objects;
	
	/**
	 * 
	 */
	public PlanningThread(Observer myWatcher) {
		// We only need to create the static objects once, and this class provides one place to store them all
		this.all_static_objects = new AllStaticObjects();
		// PlanningBuffer watches this thread
		this.addObserver(myWatcher);
		// Set while flag as true
		this.run = true;
	}

	@Override
	public void run() {
		while(run){
			synchronized(this){
				try {
					Plan temp_plan = new Plan(this.all_static_objects);
					setChanged();
					notifyObservers(temp_plan);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					
				}
			}
		}
		
	}
	
	public void switchRun(){
		this.run = run && false;
	}

}
