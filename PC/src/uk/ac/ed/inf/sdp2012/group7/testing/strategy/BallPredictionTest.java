package uk.ac.ed.inf.sdp2012.group7.testing.strategy;
import static org.junit.Assert.assertTrue;

import java.awt.Point;

import junit.framework.Assert;

import org.junit.Test;

import uk.ac.ed.inf.sdp2012.group7.strategy.newastar.Node;
import uk.ac.ed.inf.sdp2012.group7.strategy.planning.TargetDecision;

public class BallPredictionTest {
	
	//width and height of pitch in nodes
	private double pitchWidthinNodes = 58;
	private double pitchHeightinNodes = 29;
	
	@Test
	public void ballPredictionCalculationTest1() {
		Node ball = new Node(new Point (1,1));
		double angle = 0;
		double velocity = 0;
		double time = 5;
		int direction = 1;
		
		Point expected = new Point(0,0);
		Point actual = TargetDecision.ballPredictionCalculation(ball, direction, angle, velocity, time, pitchWidthinNodes, pitchHeightinNodes);
		Assert.assertEquals(expected, actual);
		System.out.println("Running  ball prediction test 1");
	}
	
	@Test
	public void ballPredictionCalculationTest2() {
		Node ball = new Node(new Point (200,200));
		double angle = 0;
		double velocity = 1;
		double time = 5;
		int direction = 1;
		System.out.println(ball);
		Point expected = new Point(5,0);
		Point actual = TargetDecision.ballPredictionCalculation(ball, direction, angle, velocity, time, pitchWidthinNodes, pitchHeightinNodes);
		System.out.println("Actual = (" + actual.x+","+actual.y+")");
		Assert.assertEquals(expected, actual);
		System.out.println("Running ball prediction test 2");
	}
	@Test
	public void ballPredictionCalculationTest3() {
		Node ball = new Node(new Point (0,0));
		double angle = Math.PI;
		double velocity = 1;
		double time = 5;
		int direction = 1;
		System.out.println(ball);
		Point expected = new Point(53,0);
		Point actual = TargetDecision.ballPredictionCalculation(ball, direction, angle, velocity, time, pitchWidthinNodes, pitchHeightinNodes);
		System.out.println("Actual = (" + actual.x+","+actual.y+")");
		Assert.assertEquals(expected, actual);
		System.out.println("Running ball prediction test 2");
	}
	
	
	

	

}
