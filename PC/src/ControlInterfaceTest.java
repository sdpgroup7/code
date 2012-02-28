import static org.junit.Assert.*;

import java.awt.Point;
import java.util.ArrayList;

import math.geom2d.Point2D;

import org.junit.Test;

import uk.ac.ed.inf.sdp2012.group7.strategy.Arc;
import uk.ac.ed.inf.sdp2012.group7.strategy.ControlInterface;
import uk.ac.ed.inf.sdp2012.group7.strategy.planning.AllMovingObjects;
import uk.ac.ed.inf.sdp2012.group7.strategy.planning.AllStaticObjects;
import uk.ac.ed.inf.sdp2012.group7.strategy.planning.Plan;


public class ControlInterfaceTest {

	@Test
	public void testChooseArc1() {
		ArrayList<Point> path = new ArrayList<Point>();
		path.add(new Point(0,0));
		path.add(new Point(0,1));
		path.add(new Point(0,2));
		path.add(new Point(0,3));
		path.add(new Point(0,4));
		path.add(new Point(0,5));
		path.add(new Point(0,6));
		path.add(new Point(0,7));
		path.add(new Point(0,8));
		path.add(new Point(0,9));
		path.add(new Point(0,10));
		Arc generated = ControlInterface.generateArc(new Point2D(0,0),path,Math.PI,0,5);
		
		assertTrue(generated.getRadius() == Double.MAX_VALUE);
		System.err.println("Generated Radius: " + generated.getRadius());
	}

	
	
	
	
}
