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
	public static int pitch_top_buffer = worldState.getPitch().getTopBuffer();
	public static int pitch_left_buffer = worldState.getPitch().getLeftBuffer();
	
	//Compacts WorldState position point into "Node" center position
	public static Point convertToNode(Point p){
		int x = (int)Math.floor((p.x - ConvertToNode.pitch_left_buffer)/nodeInPixels);
		int y = (int)Math.floor((p.y - ConvertToNode.pitch_top_buffer)/nodeInPixels);
		Point grid_point = new Point(x,y);
		return grid_point;
	}
	
	//Compacts WorldState position points into "Node" center positions
	public static ArrayList<Point> convertToNodes(ArrayList<Point> p){

		ArrayList<Point> node_points = new ArrayList<Point>();
		Iterator itr = p.iterator();

		while(itr.hasNext()){
			Point temp = (Point)itr.next();
			int x = (int)Math.floor((temp.x - ConvertToNode.pitch_left_buffer)/nodeInPixels);
			int y = (int)Math.floor((temp.y - ConvertToNode.pitch_top_buffer)/nodeInPixels);
			Point grid_point = new Point(x,y);
			node_points.add(grid_point);
		}

		return node_points;
	}
	
	

}
