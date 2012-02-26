package uk.ac.ed.inf.sdp2012.group7.strategy;


import org.apache.log4j.Logger;
import uk.ac.ed.inf.sdp2012.group7.strategy.planning.*;

/**
 * 
 * Milestones so far
 *
 */
public class Strategy {

	public static final Logger logger = Logger.getLogger(Strategy.class);
	
	private PlanningBuffer planning_buffer;
	private PlanningThread planningthread;
	private ControlInterface control_interface;
	private Thread thread_for_planningthread;


	public Strategy() {
		
;
		
		//Start Control interface
		this.control_interface = new ControlInterface(5);
		
		//create the observer / buffer for the plans
		this.planning_buffer = new PlanningBuffer(control_interface);
		
		//Make runnable...
		this.planningthread = new PlanningThread(planning_buffer,1);
		
	}
	
	
	// need to read Tom's email on thread handling.. this is just a placeholder!
	
	public void startPlanningThread() {
		// TODO Auto-generated method stub
		this.thread_for_planningthread = new Thread(planningthread);
		this.planningthread.switchRun();
		this.thread_for_planningthread.start();
	}

	public void stopPlanningThread() {
		// TODO Auto-generated method stub
		this.planningthread.switchRun();
		this.thread_for_planningthread = null;
	}
	
	public ControlInterface getControlInterface(){
		return this.control_interface;
	}


}
