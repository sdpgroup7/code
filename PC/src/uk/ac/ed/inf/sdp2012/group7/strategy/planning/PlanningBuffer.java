/**
 * 
 */
package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.strategy.astar.AreaMap;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.Node;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.Path;

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
	private long time_stamp = System.currentTimeMillis();

	//I would like to be able to read the plans created
	//offline; but I don't need EVERY plan, so I will
	//use a counter...
	private int counter = 0;
	
	public PlanningBuffer(Observer myWatcher){
		   
		this.addObserver(myWatcher);
	}


	@Override
	public void update(Observable o, Object arg) {
		synchronized(this){
			logger.debug("Planning Buffer Updated");
			this.held_plan = (Plan)arg;
			setChanged();
			notifyObservers(held_plan);
			if(counter < 50){
				counter++;
			} else {
				savePlan();
				counter = 0;
			}
			
		}
		
	}
	
	public Plan getPlan(){
		return held_plan;
	}
	
	public void savePlan(){
		AreaMap map = new AreaMap();
		
		Path path = held_plan.getNodePath();
		ArrayList<Node> waypoints = path.getWayPoints();
		for(Node n : waypoints){
			//Draw node onto map
		}
	}

}
