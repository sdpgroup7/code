package uk.ac.ed.inf.sdp2012.group7.strategy.newastar;


import java.awt.Point;


public class Node extends Point implements Comparable<Node> {

	
	private static final long serialVersionUID = -1084579547399653262L;
	private Point here; 
	private Node parent;
	private double obstacleCost;
	private boolean opposition;
	private boolean ball;
	private boolean isTarget;
	private boolean isStart;
	private double fCost;
	private double gCost;
	private double hcost;
	
	public Node(Point point, int i) {
		// TODO Auto-generated constructor stub
		this.here = point;
		this.obstacleCost = i;
	}

	@Override
	public int compareTo(Node o) {
		return (int) (this.fCost - o.getfCost());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((here == null) ? 0 : here.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof Node)) {
			return false;
		}
		Node other = (Node) obj;
		if (here == null) {
			if (other.here != null) {
				return false;
			}
		} else if (!here.equals(other.here)) {
			return false;
		}
		return true;
	}
	
	public int x(){
		return this.here.x; 
	}
	
	public int y(){
		return this.here.y; 
	}
	public double getfCost() {
		return fCost;
	}

	public void setfCost() {
		this.fCost = this.gCost + this.hcost + this.obstacleCost;
	}

	public double getgCost() {
		return gCost;
	}

	public void setgCost(double gCost) {
		this.gCost = gCost;
	}

	public double gethCost() {
		return hcost;
	}

	public void sethCost(double hcost) {
		this.hcost = hcost;
	}
	public boolean isOpposition() {
		return opposition;
	}

	public void setOpposition(boolean opposition) {
		this.opposition = opposition;
	}

	public boolean isBall() {
		return ball;
	}

	public void setBall(boolean ball) {
		this.ball = ball;
	}
	public boolean isTarget() {
		return isTarget;
	}

	public void setTarget(boolean isTarget) {
		this.isTarget = isTarget;
	}
	public double getObstacleCost() {
		return obstacleCost;
	}

	public void setObstacleCost(double obstacleCost) {
		this.obstacleCost = obstacleCost;
	}
	
	public boolean isStart() {
		return isTarget;
	}
	
	public void setStart(boolean start) {
		this.isStart = start;
	}

	public void setParent(Node parent){
		this.parent = parent;
	}

	public Node getParent(){
		return this.parent;
	}
	
	public Point getHere(){
		return this.here;
	}
}