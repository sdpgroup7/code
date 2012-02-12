package uk.ac.ed.inf.sdp2012.group7.control;

import java.awt.Point;

public class Tools {

	private Tools() {
	}

	public static double getAngleFrom0_0(Point pos) {
		// deals with cases where pos is on the x-axis
		if (pos.y == 0) {
			return (pos.x > 0 ? 0 : Math.PI);
		} else {
			if (pos.x > 0)
				return (Math.atan(((float) pos.y) / pos.x));
			else
				return (Math.PI + Math.atan(((float) pos.y) / pos.x));
		}
	}

	public static Point getRelativePos(Point startPoint, Point relativePoint) {
		return new Point(relativePoint.x - startPoint.x, relativePoint.y
				- startPoint.y);
	}

	public static double getAngleToFacePoint(Point ourCoor, double angle,
			Point target) {

		// first I want to find where the target is in relation to our robot
		Point targetRelativePos = Tools.getRelativePos(ourCoor, target);

		double targetFromNxt = Tools.getAngleFrom0_0(targetRelativePos);
		
		if (targetFromNxt < 0)
			targetFromNxt = 2 * Math.PI + targetFromNxt;

		// now find how much our robot has to turn to face target
		// (turning by negative getAngle returns it to face 0 then add on ball
		// Angle
		double howMuchToTurn = targetFromNxt - angle;

		// now adjust it so that it turns in the shortest direction (clockwise
		// or counter clockwise)
		if (howMuchToTurn < -Math.PI)
			howMuchToTurn = 2 * Math.PI + howMuchToTurn;
		else if (howMuchToTurn > Math.PI)
			howMuchToTurn = -(2 * Math.PI - howMuchToTurn);

		return howMuchToTurn;

	}
}
