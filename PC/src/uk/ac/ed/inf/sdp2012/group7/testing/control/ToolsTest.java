package uk.ac.ed.inf.sdp2012.group7.testing.control;

import static org.junit.Assert.assertTrue;

import java.awt.Point;

import org.junit.Test;

import uk.ac.ed.inf.sdp2012.group7.control.Tools;

/**
 * 
 * A few obvious tests of some helper functions to make sure they work.
 * 
 * @author TT
 *
 */
public class ToolsTest {

	@Test
	public void test1GetAngleFrom0_0() {
		double actual = Tools.getAngleFrom0_0(new Point(0,1));
		double expected = 3*Math.PI/2;
		System.out.println("Running the first test of getAngleFrom0_0, result: " + actual);
		assertTrue(actual == expected);
		
	}
	
	@Test
	public void test2GetAngleFrom0_0() {
		double actual = Tools.getAngleFrom0_0(new Point(1,0));
		double expected = 0.0;
		System.out.println("Running the second test of getAngleFrom0_0, result: " + actual);
		assertTrue(actual == expected);
	}
	
	@Test
	public void test3GetAngleFrom0_0() {
		double actual = Tools.getAngleFrom0_0(new Point(1,1));
		double expected = Math.PI / 4;
		System.out.println("Running the third test of getAngleFrom0_0, result: " + actual);
		assertTrue(actual == expected);
	}

	@Test
	public void test1GetRelativePos() {
		Point actual = Tools.getRelativePos(new Point(0,0), new Point(0,0));
		Point expected = new Point(0,0);
		System.out.println("Running the first test of getRelativePos, result: " + actual);
		assertTrue(actual.x == expected.x && actual.y == expected.y);
	}
	
	@Test
	public void test2GetRelativePos() {
		Point actual = Tools.getRelativePos(new Point(1,2), new Point(3,4));
		Point expected = new Point(2,2);
		System.out.println("Running the second test of getRelativePos, result: " + actual);
		assertTrue(actual.x == expected.x && actual.y == expected.y);
	}

	@Test
	public void test1GetAngleToFacePoint() {
		double actual = Tools.getAngleToFacePoint(new Point(1,1), 3*Math.PI/2, new Point(1,2));
		double expected = 0;
		System.out.println("Running the first test of getAngleToFacePoint, result: " + actual);
		assertTrue(actual == expected);
	}

	@Test
	public void test2GetAngleToFacePoint() {
		double actual = Tools.getAngleToFacePoint(new Point(1,1), Math.PI/2, new Point(2,1));
		double expected = -Math.PI/2;
		System.out.println("Running the second test of getAngleToFacePoint, result: " + actual);
		assertTrue(actual == expected);
	}	
	
}
