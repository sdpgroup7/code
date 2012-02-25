package uk.ac.ed.inf.sdp2012.group7.strategy;

import java.awt.Point;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.control.RobotControl;
import uk.ac.ed.inf.sdp2012.group7.control.Tools;
import uk.ac.ed.inf.sdp2012.group7.strategy.planning.*;
import uk.ac.ed.inf.sdp2012.group7.vision.Vision;

/**
 * 
 * Milestones so far
 *
 */
public class Strategy {

	public static final Logger logger = Logger.getLogger(Strategy.class);
	
	private RobotControl controller;
	private PlanningBuffer planning_buffer;
	private PlanningThread planningthread;
	private Thread thread_for_planningthread;
	private boolean runFlag;


	public Strategy() {
		controller = new RobotControl();
		controller.startCommunications();
		this.runFlag = true;
		
		
		//Thread testing and plan testing code::
		//create the observer / buffer for the plans
		this.planning_buffer = new PlanningBuffer();
		
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
	
	
	//The below is just here as a quick fix for the GUI -- I don't have time to sort this out now
	//delete/modify later
	//Also kept so we can see how it was done
	
	public void drive(){
		controller.changeSpeed(30);
		controller.moveForward(60);
	}
	
	public void kick(){
		controller.kick();
	}




}
