package uk.ac.ed.inf.sdp2012.group7.testing.planning;

import java.awt.Point;

import junit.framework.Assert;

import org.junit.Test;

import uk.ac.ed.inf.sdp2012.group7.strategy.planning.AllStaticObjects;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

public class AllStaticObjectsTest {

	private AllStaticObjects instance;
	
	public AllStaticObjectsTest() {
		WorldState.getInstance().setUpdatedTime();
		instance = new AllStaticObjects();
	}
	
	@Test
	public void testConvertToNode1() {
		Point p = new Point(44, 127);
		Point expected = new Point(1,1);
		System.out.println("Running test 1: ");
		Assert.assertEquals(expected,instance.convertToNode(p));
	}

	@Test
	public void testConvertToNode2() {
		Point p = new Point(24, 86);
		Point expected = new Point(0,0);
		System.out.println("Running test 2...");
		Assert.assertEquals(expected,instance.convertToNode(p));
	}
	
	@Test
	public void testConvertToNode3() {
		Point p = new Point(612, 402);
		Point expected = new Point(60,30);
		System.out.println("Running test 3...");
		Assert.assertEquals(expected,instance.convertToNode(p));
	}

}
