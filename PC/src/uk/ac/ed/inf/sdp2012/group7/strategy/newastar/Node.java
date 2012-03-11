package uk.ac.ed.inf.sdp2012.group7.strategy.newastar;


import java.awt.Point;


public class Node extends Point implements Comparable<Node> {

	private Node parent = null;
	private double obstacleCost;
	private boolean opposition;
	private boolean ball;
	private boolean isTarget;
	private boolean isStart;
	private double fCost;
	private double gCost;
	private double hcost;
	
	public Node(Point point, int obstacleCost) {
		super(point.x,point.y);
		this.obstacleCost = obstacleCost;
	}
	
	public Node(Point point) {
		super(point.x,point.y);
		this.obstacleCost = 0;
	}
        

	@Override
	public int compareTo(Node o) {
		if(this.fCost > o.getfCost()){
                    return 1;
                } else if(this.fCost < o.getfCost()){
                    return -1;
                } else {
                    return 0;
                }
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + super.hashCode();
		return result;
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
		return isStart;
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
        
        public String toString(){
            return this.x + " " + this.y + " " + this.getfCost();
        }
}