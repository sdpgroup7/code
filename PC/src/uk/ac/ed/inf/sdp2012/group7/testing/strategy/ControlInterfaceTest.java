package uk.ac.ed.inf.sdp2012.group7.testing.strategy;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.util.ArrayList;

import uk.ac.ed.inf.sdp2012.group7.strategy.ControlInterface;


public class ControlInterfaceTest {
	
	private int look;
	private ControlInterface inter;
	private Point expected;
	private Point position;
	private ArrayList<Point> path;
	
	/*
	 * Simple situation for finding the goal. Path has only one section and goal point is on that section 
	 */
	@Test public void goalTest1() {
		look = 3;
		inter = new ControlInterface(look);
		expected = new Point(0,3);
		position = new Point (0,0);
		path = new ArrayList<Point>();
		path.add(new Point(0,1));
		path.add(new Point(0,5));
		
		Point actual = inter.findGoalPoint(path, position);
		assertTrue(expected == actual);
	}
	
	/*
	 * Ensures that if the path cannot be found then it complains
	 */
	@Test(expected = Exception.class) public void goaltest2() {
		look = 3;
		inter = new ControlInterface(look);
		position = new Point(0,3);
		path = new ArrayList<Point>();
		path.add(new Point(22,22));
		path.add(new Point(24,24));
		
		Point actual = inter.findGoalPoint(path, position);
	}
}
	
	
	


