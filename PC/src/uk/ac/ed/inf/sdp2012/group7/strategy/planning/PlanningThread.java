/**
 * 
 */
package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.util.Observable;
import java.util.Observer;

/**
 * @author s0955088
 *
 */
public class PlanningThread extends Observable implements Runnable{

	private boolean run;
	
	/**
	 * 
	 */
	public PlanningThread(Observer myWatcher) {
		// Holding watches me
		this.addObserver(myWatcher);
		this.run = true;
	}

	@Override
	public void run() {
		while(run){
			synchronized(this){
				try {
					Plan temp_plan = new Plan();
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
