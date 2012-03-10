package uk.ac.ed.inf.sdp2012.group7.strategy;


import org.apache.log4j.Logger;
import uk.ac.ed.inf.sdp2012.group7.strategy.planning.*;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

/**
 * 
 * Milestones so far
 *
 */
public class Strategy {

	public static final Logger logger = Logger.getLogger(Strategy.class);
	
	private static final int LOOKAHEAD = 10;
	
	private PlanningBuffer planning_buffer;
	private PlanningThread planningthread;
	private AllStaticObjects allStaticObjects;
	private ControlInterface control_interface;
	private Thread thread_for_planningthread;


	public Strategy() {
				
		//Start Control interface
		this.control_interface = ControlInterface.getInstance(LOOKAHEAD);
		
		//create the observer / buffer for the plans
		this.planning_buffer = new PlanningBuffer(control_interface);
		
		//create static objects and plan_type
		this.allStaticObjects = new AllStaticObjects();
		
		//Make runnable...
		this.planningthread = new PlanningThread(planning_buffer, this.allStaticObjects);
		
	}
	
	
	// need to read Tom's email on thread handling.. this is just a placeholder!
	
	//this function is used to send the plan_type
	//and then start the plan type
	public void startPlanningThread(int plan_type) {
			this.allStaticObjects.setPlanType(plan_type);
			this.thread_for_planningthread = new Thread(planningthread);
			this.allStaticObjects.startRun();
			this.thread_for_planningthread.start();
	}

	public void stopPlanningThread() {
			this.allStaticObjects.setPlanType(PlanTypes.PlanType.HALT.ordinal());
			this.allStaticObjects.stopRun();
			this.thread_for_planningthread = null;
	}
	
	public ControlInterface getControlInterface(){
		return this.control_interface;
	}


}
