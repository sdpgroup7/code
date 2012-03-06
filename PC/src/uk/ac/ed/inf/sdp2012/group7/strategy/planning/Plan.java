package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.AStarRun;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.Node;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.ObjectPosition;

public class Plan {

	private AllStaticObjects allStaticObjects;
	private AllMovingObjects allMovingObjects;

	private ArrayList<Point> getDefaultObstacles(){

		ArrayList<Point> obstacles = new ArrayList<Point>();
		double nodeInPixels = this.allStaticObjects.getNodeInPixels();

		//bounding box
		ObjectPosition position = this.allMovingObjects.getTheirPosition();
		int minX = position.getBottomLeft().x;
		int minY = position.getBottomLeft().y;
		int maxX = position.getTopRight().x;
		int maxY = position.getTopRight().y;
		Point[] rectangle = new Point[] {position.getBottomLeft(),position.getBottomRight(),position.getTopLeft(),position.getTopRight()};
		for (Point corner : rectangle) {
			if (corner.x > maxX) {
				maxX = corner.x;
			} else if (corner.x < minX) {
				minX = corner.x;
			}
			if (corner.y > maxY) {
				maxY = corner.y;
			} else if (corner.y < minY) {
				minY = corner.y;
			}
		}
		rectangle = null;
		int ratio = 7;
		
		for(int x = (int)(minX - (ratio*nodeInPixels)); x <= maxX + ratio*nodeInPixels; x = x + (int)nodeInPixels){
			for(int y = (int)(minY - (ratio*nodeInPixels)); y <= maxY + ratio*nodeInPixels; y = y + (int)nodeInPixels){
				Point p = new Point(x,y);
				obstacles.add(p);
			}
		}

	/*	Point positionBall = this.allMovingObjects.getBallPosition();
		//Possible problem with conversion back to Integers here..?
		for(int x = (int)(positionBall.x - (2*nodeInPixels)); x <= positionBall.x + 2*nodeInPixels; x = x + (int)nodeInPixels){
			for(int y = (int)(positionBall.y - (2*nodeInPixels)); y <= positionBall.y + 2*nodeInPixels; y = y + (int)nodeInPixels){
				Point p = new Point(x,y);
				obstacles.add(p);
			}
		}
*/
		return obstacles;
	}

	public Plan(AllStaticObjects allStaticObjects, AllMovingObjects allMovingObjects) {
		this.allMovingObjects = allMovingObjects;
		this.allStaticObjects = allStaticObjects;
	}

	public static Point chopPath(List<Node> path) {
		Point firstPoint;
		Point secondPoint;
		if(path.size() > 2) {

			firstPoint = path.get(0);
			secondPoint = path.get(1);

			//Find the furthest away point that is roughly in the same direction as the direction of p1 -> p2.
			//initialAngle = targetAngle
			//double initialAngle = Math.atan2((secondPoint.y - firstPoint.y),(secondPoint.x - firstPoint.x));
			Point initalVector = new Point(secondPoint.x-firstPoint.x,secondPoint.y-firstPoint.y);
			int index = 1;
			boolean takeNextPoint = true;

			while (takeNextPoint) {
				//nextAngle is the angle between p1 and the next point in the path
				//double nextAngle ;
				Point nextVector;
				if (path.size() > (index + 1)) {
					nextVector = new Point(path.get(index+1).x-path.get(index).x,path.get(index+1).y-path.get(index).y);
				//	nextAngle = Math.atan2((path.get(index+1).y - firstPoint.y),(path.get(index+1).x - firstPoint.x));
				} else {
					nextVector = initalVector;
					takeNextPoint = false;
				//	nextAngle = initialAngle;
				}

				//if (Math.abs(Math.toDegrees(initialAngle-nextAngle))>5) {
				if ((nextVector.x == initalVector.x) && (nextVector.y == initalVector.y)) {
					index++;
				} else {
					takeNextPoint = false;
				}
			}
			if (index >= path.size() ) {
				return path.get(path.size()-1);
			} else {
					return path.get(index);
				}
		} else {
			return null;
		}
	}

	private boolean isBallOnThePitch() {
		Point ballPosition = allStaticObjects.convertToNode(allMovingObjects.getBallPosition());
		Strategy.logger.debug("Ball position: " + ballPosition);
		return ((ballPosition.x >= 0) && (ballPosition.x <= allStaticObjects.getWidth()) &&
				(ballPosition.y >= 0) && (ballPosition.y <= allStaticObjects.getHeight()));
	}
	
		public Point getPointToGoTo() {
			if (isBallOnThePitch()) {
				TargetDecision td = new TargetDecision(allMovingObjects, allStaticObjects);
				Node navPoint = td.getNavPoint();
				if (navPoint == null) {
					return null;
				} else {
					AStarRun aStarNav = new AStarRun(this.allStaticObjects.getHeight(),
							this.allStaticObjects.getWidth(),
							this.allStaticObjects.convertToNode(navPoint),
							this.allStaticObjects.convertToNode(allMovingObjects.getOurPosition()),
							this.allStaticObjects.convertToNodes(this.getDefaultObstacles())
					);
					if (aStarNav.getPath() == null)
						return null;
					else
						return chopPath(aStarNav.getPath().getWayPoints());
				}
			} else {
				return null;
			}
		}
	}
