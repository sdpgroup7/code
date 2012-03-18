package uk.ac.ed.inf.sdp2012.group7.testing.vision;
import java.awt.Point;

import junit.framework.Assert;

import org.junit.Test;
import uk.ac.ed.inf.sdp2012.group7.vision.*;

public class PlateTesting {

	public Plate plate = new Plate();
	
	@Test public void testisPointInTriangle1(){
		Point a = new Point(1,1);
		Point b = new Point(5,1);
		Point c = new Point(5,5);
		Point p = new Point(2,3);
		Assert.assertFalse(plate.isPointInTriangle(a, b, c, p));
		
	}
	@Test public void testisPointInTriangle2(){
		Point a = new Point(1,1);
		Point b = new Point(5,1);
		Point c = new Point(5,5);
		Point p = new Point(4,2);
		Assert.assertTrue(plate.isPointInTriangle(a, b, c, p));
		
	}
	@Test public void testisPointInTriangle3(){
		Point a = new Point(1,1);
		Point b = new Point(5,1);
		Point c = new Point(5,5);
		Point p = new Point(2,3);
		Assert.assertFalse(plate.isPointInTriangle(a, b, c, p));
		
	}
	@Test public void testisPointInTriangle4(){
		Point a = new Point(300,300);
		Point b = new Point(350,300);
		Point c = new Point(336,373);
		Point p = new Point(336,375);
		Assert.assertFalse(plate.isPointInTriangle(a, b, c, p));
		
	}
}
