package uk.ac.ed.inf.sdp2012.group7.testing.strategy;

import java.awt.Point;
import junit.framework.Assert;
import org.junit.Test;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.Node;
import uk.ac.ed.inf.sdp2012.group7.strategy.planning.TargetDecision;

public class BallPredictionTest {

	//width and height of pitch in nodes
	private double pitchWidthinNodes = 58;
	private double pitchHeightinNodes = 29;

	@Test
	public void ballPredictionCalculationTest1() {
		Node ball = new Node (0,0);
		double angle = 0;
		double velocity = 0;
		double time = 5;

		System.out.println("Running  ball prediction test 1");
		Point expected = new Point(0,0);
		Point actual = TargetDecision.ballPredictionCalculation(ball, angle, velocity, time, pitchWidthinNodes, pitchHeightinNodes);
		System.out.println("Expected = (" + expected.x+","+expected.y+")");
		System.out.println("Actual = (" + actual.x+","+actual.y+")");
		Assert.assertEquals(expected, actual);

	}

	@Test
	public void ballPredictionCalculationTest2() {
		Node ball = new Node(0,0);
		double angle = 0;
		double velocity = 1;
		double time =5;

		System.out.println("Running ball prediction test 2");
		Point expected = new Point(5,0);
		Point actual = TargetDecision.ballPredictionCalculation(ball, angle, velocity, time, pitchWidthinNodes, pitchHeightinNodes);
		System.out.println("Expected = (" + expected.x+","+expected.y+")");
		System.out.println("Actual = (" + actual.x+","+actual.y+")");
		Assert.assertEquals(expected, actual);

	}
	@Test
	public void ballPredictionCalculationTest3() {
		Node ball = new Node(0,0);
		double angle = 0;
		double velocity = 1;
		double time = 3*58 +1;

		System.out.println("Running ball prediction test 3");
		Point expected = new Point(57,0);
		Point actual = TargetDecision.ballPredictionCalculation(ball, angle, velocity, time, pitchWidthinNodes, pitchHeightinNodes);
		System.out.println("Expected = (" + expected.x+","+expected.y+")");
		System.out.println("Actual = (" + actual.x+","+actual.y+")");
		Assert.assertEquals(expected, actual);

	}

	@Test
	public void ballPredictionCalculationTest4() {
		Node ball = new Node(0,0);
		double angle = Math.PI/2;
		double velocity = 1;
		double time = 4*29 +1;

		System.out.println("Running ball prediction test 4");
		Point expected = new Point(0,1);
		Point actual = TargetDecision.ballPredictionCalculation(ball, angle, velocity, time, pitchWidthinNodes, pitchHeightinNodes);
		System.out.println("Expected = (" + expected.x+","+expected.y+")");
		System.out.println("Actual = (" + actual.x+","+actual.y+")");
		Assert.assertEquals(expected, actual);

	}

	@Test
	public void ballPredictionCalculationTest5() {
		Node ball = new Node(0,0);
		double angle = Math.PI;
		double velocity = 1;
		double time = 2*58+1;

		System.out.println("Running ball prediction test 5");
		Point expected = new Point(1,0);
		Point actual = TargetDecision.ballPredictionCalculation(ball, angle, velocity, time, pitchWidthinNodes, pitchHeightinNodes);
		System.out.println("Expected = (" + expected.x+","+expected.y+")");
		System.out.println("Actual = (" + actual.x+","+actual.y+")");
		Assert.assertEquals(expected, actual);

	}
	@Test
	public void ballPredictionCalculationTest6() {
		Node ball = new Node(0,0);
		double angle = Math.PI*3/2;
		double velocity = 1;
		double time = 2*29+1;

		System.out.println("Running ball prediction test 5");
		Point expected = new Point(0,1);
		Point actual = TargetDecision.ballPredictionCalculation(ball, angle, velocity, time, pitchWidthinNodes, pitchHeightinNodes);
		System.out.println("Expected = (" + expected.x+","+expected.y+")");
		System.out.println("Actual = (" + actual.x+","+actual.y+")");
		Assert.assertEquals(expected, actual);

	}
	@Test
	public void ballPredictionCalculationTest7() {
		Node ball = new Node(0,1);
		double angle = Math.PI;
		double velocity = 2;
		double time = 2*58+2;

		System.out.println("Running ball prediction test 7");
		Point expected = new Point(4,1);
		Point actual = TargetDecision.ballPredictionCalculation(ball, angle, velocity, time, pitchWidthinNodes, pitchHeightinNodes);
		System.out.println("Expected = (" + expected.x+","+expected.y+")");
		System.out.println("Actual = (" + actual.x+","+actual.y+")");
		Assert.assertEquals(expected, actual);

	}
	@Test
	public void ballPredictionCalculationTest8() {
		Node ball = new Node(1,1);
		double angle = Math.PI/4;
		double velocity = 1*Math.sqrt(2);
		double time = 3;

		System.out.println("Running ball prediction test 8");
		Point expected = new Point(4,4);
		Point actual = TargetDecision.ballPredictionCalculation(ball, angle, velocity, time, pitchWidthinNodes, pitchHeightinNodes);
		System.out.println("Expected = (" + expected.x+","+expected.y+")");
		System.out.println("Actual = (" + actual.x+","+actual.y+")");
		Assert.assertEquals(expected, actual);

	}

	@Test
	public void ballPredictionCalculationTest9() {
		Node ball = new Node(0,0);
		double angle = Math.PI/4 + Math.PI;
		double velocity = 1*Math.sqrt(2);
		double time = 3;

		System.out.println("Running ball prediction test 8");
		Point expected = new Point(3,3);
		Point actual = TargetDecision.ballPredictionCalculation(ball, angle, velocity, time, pitchWidthinNodes, pitchHeightinNodes);
		System.out.println("Expected = (" + expected.x+","+expected.y+")");
		System.out.println("Actual = (" + actual.x+","+actual.y+")");
		Assert.assertEquals(expected, actual);

	}

	@Test
	public void ballPredictionCalculationTest10() {
		Node ball = new Node(2,1);
		double angle = Math.PI/4 + Math.PI;
		double velocity = 1*Math.sqrt(2);
		double time = 3;

		System.out.println("Running ball prediction test 10");
		Point expected = new Point(1,2);
		Point actual = TargetDecision.ballPredictionCalculation(ball, angle, velocity, time, pitchWidthinNodes, pitchHeightinNodes);
		System.out.println("Expected = (" + expected.x+","+expected.y+")");
		System.out.println("Actual = (" + actual.x+","+actual.y+")");
		Assert.assertEquals(expected, actual);

	}

	@Test
	public void ballPredictionCalculationTest11() {
		Node ball = new Node(14,18);
		double angle = 0;
		double velocity = 0.01;
		double time =20;

		System.out.println("Running ball prediction test 11");
		Point expected = new Point(14,18);
		Point actual = TargetDecision.ballPredictionCalculation(ball, angle, velocity, time, pitchWidthinNodes, pitchHeightinNodes);
		System.out.println("Expected = (" + expected.x+","+expected.y+")");
		System.out.println("Actual = (" + actual.x+","+actual.y+")");
		Assert.assertEquals(expected, actual);

	}







}
