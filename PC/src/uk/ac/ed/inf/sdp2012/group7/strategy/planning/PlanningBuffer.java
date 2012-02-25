/**
 * 
 */
package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.util.Observable;
import java.util.Observer;

/**
 * 
 */

/**
 * @author twig
 *
 */
public class PlanningBuffer extends Observable implements Observer {

	private Plan held_plan;
	
	public PlanningBuffer(Observer myWatcher){
		   
		
	}


	@Override
	public void update(Observable o, Object arg) {
		synchronized(this){
			this.held_plan = (Plan)arg;;
			setChanged();
			notifyObservers(held_plan);
		}
		
	}
	
	public Plan getPlan(){
		return held_plan;
	}

}
