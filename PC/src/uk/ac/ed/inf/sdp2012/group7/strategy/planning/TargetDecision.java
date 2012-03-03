package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;

import uk.ac.ed.inf.sdp2012.group7.strategy.astar.Node;

public class TargetDecision {

	private AllMovingObjects allMovingObjects;
	private AllStaticObjects allStaticObjects;

	public TargetDecision(AllMovingObjects aMO, AllStaticObjects aSO) {
		//get all information required
		this.allMovingObjects = aMO;
		this.allStaticObjects = aSO;

	}

	private boolean isBallOnThePitch() {
		Point ballPosition = allStaticObjects.convertToNode(allMovingObjects.getBallPosition());
		return ((ballPosition.x >= 0) && (ballPosition.x <= allStaticObjects.getWidth()) &&
				(ballPosition.y >= 0) && (ballPosition.y <= allStaticObjects.getHeight()));
	}

	private boolean isOpenShotPossible(Point navPoint) {
		double b = 3;
		Point node1 = allStaticObjects.convertToNode(navPoint);
		return (node1.x > b) &&
		node1.x < (this.allStaticObjects.getWidth() - b) &&
		(node1.y < (this.allStaticObjects.getHeight() - b)&&
				node1.y > (b));
	}

	public Node getNavPoint() {
		if (!isBallOnThePitch())
			return null;
		else {
			Node navPoint = getNavPointOpen();
			if (isOpenShotPossible(navPoint)) {
				return navPoint;
			} else {
				return getNavPointAngular();
			}
		}
	}

	//set navPoint 7 nodes behind the ball
	private Node getNavPointOpen() {

		Point ballPosition = this.allStaticObjects.convertToNode(this.allMovingObjects.getBallPosition());
		Point ballReturnPosition = this.allMovingObjects.getBallPosition();
		Point centreGoal = this.allStaticObjects.getCentreOfTheirGoal();

		double angleBetweenBallAndGoal = Math.atan2((centreGoal.y - ballPosition.y),(centreGoal.x - ballPosition.x));


		int navX = ballReturnPosition.x - (int)(Math.cos(angleBetweenBallAndGoal)*7*allStaticObjects.getNodeInPixels());
		int navY = ballReturnPosition.y - (int)(Math.sin(angleBetweenBallAndGoal)*7*allStaticObjects.getNodeInPixels());

		return new Node(navX,navY);
	}

	//set navPoint 7 nodes behind the ball
	private Node getNavPointAngular(){

		Point ballPosition = this.allStaticObjects.convertToNode(this.allMovingObjects.getBallPosition());
		Point ballReturnPosition = this.allMovingObjects.getBallPosition();
		Point centreGoal = this.allStaticObjects.getCentreOfTheirGoal();

		double angleBetweenBallAndGoal = Math.atan2((centreGoal.y - ballPosition.y),(centreGoal.x - ballPosition.x));

		int navX = ballReturnPosition.x - (int)(Math.cos(angleBetweenBallAndGoal)*7*allStaticObjects.getNodeInPixels());
		int navY = ballReturnPosition.y + (int)(Math.sin(angleBetweenBallAndGoal)*7*allStaticObjects.getNodeInPixels());

		return new Node(navX,navY);
	}


}
