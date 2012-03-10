package astar;


import java.awt.Point;
import java.util.ArrayList;


public class AStarRunTest {
	


		public static void main(String[] args) {
			
			
			
			// set start and end points of the path
			Node start = new Node(new Point(0,9), 0);
			Node end = new Node(new Point(9,0), 0);
			
			// set obstacles
			Node opposition = new Node(new Point(3,3), 1000);
			opposition.setOpposition(true);
			Node ball = new Node(new Point(6,6), 1000);
			ball.setBall(true);
			
			//Use Lists
			ArrayList<Node> balls = new ArrayList<Node>();
			ArrayList<Node> oppositions = new ArrayList<Node>();
			
			//boundary maker
			int boundary = 2;
			
			//Fill Arrays
			
			for(int i = ball.x - boundary; i < ball.x + boundary; i++){
				for(int j = ball.y - boundary; j < ball.y + boundary; j++){
					Node tempBall = new Node(new Point(i,j), 5);
					tempBall.setBall(true);
					//System.out.println(tempBall.getX() + " " + tempBall.getY());
					balls.add(tempBall);
				}
			}
			
			for(int i = opposition.x - 2*boundary; i < opposition.x + boundary; i++){
				for(int j = opposition.y - 2*boundary; j < opposition.y + boundary; j++){
					Node tempOpp = new Node(new Point(i,j), 5);
					tempOpp.setOpposition(true);
					//System.out.println(tempOpp.getX() + " " + tempOpp.getY());
					oppositions.add(tempOpp);
				}
			}
			
			System.out.println("going to start");
			
			// run the algorithm
			AStarRun run = new AStarRun(10, 10, start, end, balls, oppositions);
		}
}
