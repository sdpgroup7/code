/**
 * 
 */
package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

/**
 * 
 */

/**
 * @author twig
 *
 */
public class PlanningBuffer extends Observable implements Observer {

	private Plan held_plan;
	public static final Logger logger = Logger.getLogger(Plan.class);

	
	public PlanningBuffer(Observer myWatcher){
		   
		this.addObserver(myWatcher);
	}


	@Override
	public void update(Observable o, Object arg) {
		synchronized(this){
			logger.debug("Planning Buffer Updated");
			this.held_plan = (Plan)arg;;
			setChanged();
			notifyObservers(held_plan);
		}
		
	}
	
	public Plan getPlan(){
		return held_plan;
	}

}
