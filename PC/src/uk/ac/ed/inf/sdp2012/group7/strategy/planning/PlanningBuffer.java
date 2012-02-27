/**
 * 
 */
package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.AStar;
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
	private PlanMonitor planMonitor;

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
			if(counter > 1){
				planMonitor.setPlan(held_plan);
				planMonitor.outputPlan();
				counter = 0;
			}
			Strategy.logger.info("Current plan count: " + Integer.toString(counter));
			counter++;
		}
	}
	
	public Plan getPlan(){
		return held_plan;
	}
	

}
