package uk.ac.ed.inf.sdp2012.group7.testing.strategy;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Point;
import java.util.ArrayList;
import math.geom2d.Point2D;


import uk.ac.ed.inf.sdp2012.group7.strategy.ControlInterface;


public class ControlInterfaceTest {
	
	private int look;
	private ControlInterface inter;
	private double expectedAngle;
	private Point2D expectedPoint;
	private Point2D position;
	private ArrayList<Point> path;
	
	
	/*
	 * Simple situation for finding the goal. Path has only one section and goal point is on that section 
	 */
	@Test public void goalTest1() {
		look = 3;
		inter = new ControlInterface(look);
		expectedPoint = new Point2D(0,3);
		position = new Point2D (0,0);
		path = new ArrayList<Point>();
		path.add(new Point(0,1));
		path.add(new Point(0,5));
		
		try {
			Point2D actual = inter.findGoalPoint(path, position);
			assertTrue(expectedPoint.equals(actual));
		} catch (Exception e) {
		}
		
	}
	
	/*
	 * Ensures that if the path cannot be found then it complains
	 */
	@Test(expected = Exception.class) public void goalTest2() throws Exception {
		look = 3;
		inter = new ControlInterface(look);
		position = new Point2D(0,3);
		path = new ArrayList<Point>();
		path.add(new Point(22,22));
		path.add(new Point(24,24));
		
		Point2D actual = inter.findGoalPoint(path, position);
	
	}
	
	/*
	 * Ensures that it can deal with the point at the end of the path
	 */
	@Test public void goalTest3() {
		look = 5;
		inter = new ControlInterface(look);
		expectedPoint = new Point2D(25,30);
		position = new Point2D(25,25);
		path = new ArrayList<Point>();
		path.add(new Point(22,25));
		path.add(new Point(25,26));
		path.add(new Point(25,28));
		path.add(new Point(25,32));
		
		try {
			Point2D actual = inter.findGoalPoint(path, position);
			assertTrue(expectedPoint.equals(actual));
		} catch (Exception e) {
		}
	}
	
	/*
	 * Ensure that it can deal with 2 points on the same path
	 */
	@Test public void goalTest4() {
		look = 3;
		inter = new ControlInterface(look);
		expectedPoint = new Point2D(25,28);
		position = new Point2D(25,25);
		path = new ArrayList<Point>();
		path.add(new Point(20,23));
		path.add(new Point(27,30));
		
		try {
			Point2D actual = inter.findGoalPoint(path, position);
			assertTrue(expectedPoint.equals(actual));
		} catch (Exception e){
		}
		
		
		}
	
	/*
	 * Checks the angle conventions
	 */
	@Test public void angleTest1() {
		look = 3;
		inter = new ControlInterface(look);
		expectedAngle = 0;
		double actualAngle = inter.convertAngle(0);
		assertTrue(actualAngle == expectedAngle);
		}
	
	@Test public void angleTest2() {
		look = 3;
		inter = new ControlInterface(look);
		expectedAngle = Math.PI/2;
		double actualAngle = inter.convertAngle(3*Math.PI/2);
		assertTrue(actualAngle == expectedAngle);
		}
	
	@Test public void angleTest3() {
		look = 3;
		inter = new ControlInterface(look);
		expectedAngle = Math.PI/2;
		double actualAngle = inter.convertAngle(3*Math.PI/2);
		assertTrue(actualAngle == expectedAngle);
		}
	
	@Test public void angleTest4() {
		look = 3;
		inter = new ControlInterface(look);
		expectedAngle = Math.PI;
		double actualAngle = inter.convertAngle(Math.PI);
		assertTrue(actualAngle == expectedAngle);
		}

}