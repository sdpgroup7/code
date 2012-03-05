package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;
import java.util.ArrayList;

import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

public class AllStaticObjects {

	private Point inFrontOfTheirGoal;
	private Point centreOfTheirGoal;

	private int height;
	private int width;
	private int boundary;

	private double nodeInPixels;
	private double nodeHeightInPixels;
	private double nodeWidthInPixels;
	

	// worldstate getInstance
	private WorldState worldState = WorldState.getInstance();

	public AllStaticObjects() {
		while (worldState.getLastUpdateTime() == 0) {
		}

		this.height = 58;
		this.width = 116;

		// Boundary around the edges of the pitch, to prevent the robot from
		// hitting the walls
		// So this is dependent on the resolution..
		this.boundary = 2;

		this.nodeInPixels = (double) worldState.getPitch().getWidthInPixels() / (double) this.width;// width in pixels!
		this.nodeWidthInPixels = this.nodeInPixels;
		this.nodeWidthInPixels = (double) worldState.getPitch().getHeightInPixels() / (double) this.height;
	}

	// Compacts WorldState position point into "Node" centre position
	public Point convertToNode(Point p) {
		if (p.x < worldState.getPitch().getTopLeft().x || p.x > worldState.getPitch().getTopRight().x) {
			return new Point(-1,-1);
		} else {
			
		
		int x = (int) Math.round(((double) (p.x - (worldState.getPitch().getLeftBuffer() - 1)) / this.nodeInPixels));
		int y = (int) Math.round(((double) (p.y - (worldState.getPitch().getTopBuffer() - 1)) / this.nodeInPixels));

		return new Point(x, y);
		}
	}

	//Compacts WorldState position points into "Node" centre positions
	public ArrayList<Point> convertToNodes(ArrayList<Point> l){
		ArrayList<Point> nodePoints = new ArrayList<Point>();

		for (Point p : l) {
			nodePoints.add(convertToNode(p));
		}

		return nodePoints;
	}

	// Method for finding the centre point just in front of their goal...
	// Return this as a node!
	private void pointInfrontOfTheirGoal() {
		if (worldState.getShootingDirection() == -1) {
			this.inFrontOfTheirGoal = new Point(this.boundary, this.height / 2);

		} else {
			this.inFrontOfTheirGoal = new Point((this.width - this.boundary),
					this.height / 2);
		}
	}

	// Method for finding the centre point in their goal...
	// Return this as a node!
	private void centreOfTheirGoal() {
		if (worldState.getShootingDirection() == -1) {
			this.centreOfTheirGoal = new Point(1, this.height / 2);
		} else {
			this.centreOfTheirGoal = new Point(this.width - 2, this.height / 2);
		}
	}

	public Point getInFrontOfTheirGoal() {
		this.pointInfrontOfTheirGoal();
		return inFrontOfTheirGoal;
	}

	public Point getCentreOfTheirGoal() {
		this.centreOfTheirGoal();
		return centreOfTheirGoal;
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public double getNodeInPixels() {
		return this.nodeInPixels;
	}

	public double getNodeWidthInPixels() {
		return this.nodeWidthInPixels;
	}
	
	public double getNodeHeightInPixels() {
		return this.nodeHeightInPixels;
	}
}
