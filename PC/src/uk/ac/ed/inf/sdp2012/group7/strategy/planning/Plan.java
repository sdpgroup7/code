package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ed.inf.sdp2012.group7.strategy.astar.AStarRun;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.Node;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.ObjectPosition;

public class Plan {

	private AllStaticObjects allStaticObjects;
	private AllMovingObjects allMovingObjects;

	private ArrayList<Point> getDefaultObstacles(){

		ArrayList<Point> obstacles = new ArrayList<Point>();
		double nodeInPixels = this.allStaticObjects.getNodeInPixels();

		ObjectPosition position = this.allMovingObjects.getTheirPosition();
		
		for(int x = (int)(position.getBottomLeft().x - (5*nodeInPixels)); x <= position.getTopRight().x + 5*nodeInPixels; x = x + (int)nodeInPixels){
			for(int y = (int)(position.getBottomLeft().y - (5*nodeInPixels)); y <= position.getTopRight().y + 5*nodeInPixels; y = y + (int)nodeInPixels){
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

	private Point chopPath(List<Node> path) {
		Point firstPoint;
		Point secondPoint;
		if(path.size() > 2) {

			firstPoint = path.get(0);
			secondPoint = path.get(1);

			//Find the furthest away point that is roughly in the same direction as the direction of p1 -> p2.
			//initialAngle = targetAngle
			double initialAngle = Math.atan2((secondPoint.y - firstPoint.y),(secondPoint.x - firstPoint.x));
			int index = 1;
			boolean takeNextPoint = true;

			while (takeNextPoint) {
				//nextAngle is the angle between p1 and the next point in the path
				double nextAngle ;

				if (path.size() > (index + 1)) {
					nextAngle = Math.atan2((path.get(index+1).y - firstPoint.y),(path.get(index+1).x - firstPoint.x));
				} else {
					nextAngle = initialAngle;
				}

				if (Math.abs(Math.toDegrees(initialAngle-nextAngle))>5) {
					index++;
				} else {
					takeNextPoint = false;
				}
			}

			return path.get(index);
		} else {
			return null;
		}
	}

		public Point getPointToGoTo() {
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
		}
	}
