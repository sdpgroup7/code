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


	public Strategy() {
		controller = new RobotControl();
		controller.startCommunications();
		
		
		//Thread testing and plan testing code::
		
		//create the observer / buffer for the plans
		PlanningBuffer planning_buffer = new PlanningBuffer();
		
		//Make runnable...
		Runnable planningthread = new PlanningThread(planning_buffer,1);
		
		//...becomes thread
		Thread thread_for_planningthread = new Thread(planningthread);
		
		//start it
		thread_for_planningthread.start();
	}

}
