/**
 * 
 */
package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;




/**
 * @author s0955088
 *
 */
public class ConvertToNode {
	
	private static WorldState worldState = WorldState.getInstance();
	public static int nodeInPixels = worldState.getPitch().getWidthInPixels()/50;//width in pixels!
	public static int pitchTopBuffer = worldState.getPitch().getTopBuffer();
	public static int pitchLeftBuffer = worldState.getPitch().getLeftBuffer();
	
	//Compacts WorldState position point into "Node" center position
	public static Point convertToNode(Point p){
		int x = (int)Math.floor((p.x - ConvertToNode.pitchLeftBuffer)/nodeInPixels);
		int y = (int)Math.floor((p.y - ConvertToNode.pitchTopBuffer)/nodeInPixels);
		return new Point(x,y);
	}
	
	//Compacts WorldState position points into "Node" center positions
	public static ArrayList<Point> convertToNodes(ArrayList<Point> p){

		ArrayList<Point> nodePoints = new ArrayList<Point>();
		Iterator itr = p.iterator();

		while(itr.hasNext()){
			Point temp = (Point)itr.next();
			int x = (int)Math.floor((temp.x - ConvertToNode.pitchLeftBuffer)/nodeInPixels);
			int y = (int)Math.floor((temp.y - ConvertToNode.pitchTopBuffer)/nodeInPixels);
			nodePoints.add(new Point(x,y));
		}

		return nodePoints;
	}
	
	

}
