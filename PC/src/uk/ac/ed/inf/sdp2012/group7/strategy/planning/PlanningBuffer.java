/**
 * 
 */
package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.strategy.planning.PlanMonitor;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

/**
 * 
 */

/**
 * @author twig
 *
 */
public class PlanningBuffer extends Observable implements Observer {

	private Plan heldPlan;
	
	public static final Logger logger = Logger.getLogger(Plan.class);
	private long timeStamp = System.currentTimeMillis();
	private PlanMonitor planMonitor= new PlanMonitor();
	private WorldState worldState = WorldState.getInstance();
	
	public PlanningBuffer(Observer myWatcher){
		this.addObserver(myWatcher);
	}


	@Override
	public void update(Observable o, Object arg) {
		synchronized(this){
			logger.debug("Planning Buffer Updated");
			this.heldPlan = (Plan)arg;
			setChanged();
			notifyObservers(heldPlan);
			worldState.addStrategyTime(System.currentTimeMillis() - timeStamp);
			planMonitor.setPlan(heldPlan);
			planMonitor.outputPlan();
			timeStamp = System.currentTimeMillis();
		}
	}
	
	public Plan getPlan(){
		return heldPlan;
	}
	

}
